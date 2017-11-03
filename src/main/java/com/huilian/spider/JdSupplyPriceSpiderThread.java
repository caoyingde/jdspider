package com.huilian.spider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huilian.hlej.base.utils.SendEmailUtil;
import com.huilian.spider.model.JdGoodsInfoPOJO;
import com.huilian.spider.model.JdGoodsPricePOJO;
import com.huilian.spider.service.JdGoodsPriceService;
import com.huilian.spider.service.JdSpiderSettingService;
import com.huilian.spider.util.BigDecimalUtil;
import com.huilian.spider.util.JdApiUtil;

/**
 * 更新京东供货价线程
 * @author Administrator
 *
 */
public class JdSupplyPriceSpiderThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(JdSupplyPriceSpiderThread.class);

	private static final int MAX_FETCH_COUNT = 100;
	private volatile boolean runnable = true;
	private JdGoodsPriceService service = new JdGoodsPriceService();
	private JdSpiderSettingService settingService = new JdSpiderSettingService();

	private Random random = new Random();
	
	public JdSupplyPriceSpiderThread() {
		super("jd-supply-spider-thread");
	}
	
	@Override
	public void run() {
		// 最后一次执行时间
		long lastUpdate = 0;
		logger.info("京东供货价更新线程已启动.");
		while (runnable) {
			boolean runnable = settingService.isUpdateSupplyPrice();
			
			// 数据库配置状态为1，或者时间间隔合适则执行
			boolean timeSuitable = ((System.currentTimeMillis() - lastUpdate) > BizConstant.SUPPLY_UPDATE_INTERVAL) ? true : false;
			
			if(!runnable && !timeSuitable){
				try {
					Thread.sleep(10000);
					continue;
				} catch (InterruptedException e) {
					logger.error("线程异常",e);
				}
			}
			
			try {
				SpiderConstant.lock.lock();
				
				logger.info("京东供货价更新线程开始工作.");
				
				lastUpdate = System.currentTimeMillis();
				
				long index = 0;
				List<JdGoodsPricePOJO> entityList = service.findForUpdateSupplyPrice(index, MAX_FETCH_COUNT);
				
				for (; entityList.size() > 0; index = index + MAX_FETCH_COUNT, entityList = service.findForUpdateSupplyPrice(index,MAX_FETCH_COUNT)) {

					List<String> skus = new ArrayList<String>(entityList.size());
					for (JdGoodsPricePOJO pojo : entityList) {
						skus.add(pojo.getSku());
					}
					
					try {
						List<JdGoodsInfoPOJO> statusList = JdApiUtil.checkGoodsSellStatus(StringUtils.join(skus, ","));
						List<String> waitQueryList = new ArrayList<>(); 
						for (JdGoodsInfoPOJO st : statusList) {
							if(st.getState() == 1){
								waitQueryList.add(st.getSku());
								st.setState(1);
							}
							else{
//								st.setSupplyPrice(BigDecimal.valueOf(-1));// 可用于冲掉原来的供货价
								st.setState(0);// 商品下架状态
							}
						}
						
						Thread.sleep(1000);
						
						if(waitQueryList.size() > 0){
							List<JdGoodsInfoPOJO> results = JdApiUtil.getLastestSupplyPrice(StringUtils.join(waitQueryList, ","));
							
							for (JdGoodsInfoPOJO st : statusList) {
								for (JdGoodsInfoPOJO r : results) {
									if(st.getSku().equals(r.getSku())){
										st.setSupplyPrice(r.getSupplyPrice());
										break;
									}
								}
							}
						}
						
						for (JdGoodsInfoPOJO st : statusList) {
							if(st.getSupplyPrice() == null){
								st.setState(-1);// 不在商品池状态
							}
							else if(BigDecimalUtil.lessThan(st.getSupplyPrice(),BizConstant.ON_SELL_PRICE_MIN)){
								st.setState(0);
							}
						}
						
						service.batchUpdateSupplyPrice(statusList,new Date(lastUpdate));
						
						// 重新计算免息数据
						List<JdGoodsPricePOJO> waitForCal = new ArrayList<JdGoodsPricePOJO>();
						for (JdGoodsInfoPOJO p : statusList) {
							for (JdGoodsPricePOJO e : entityList) {
								if(e.getSku().equals(p.getSku()) && p.getState().equals(1) && e.getPrice() != null){
									e.setSupplyPrice(p.getSupplyPrice());
									waitForCal.add(e);
									break;
								}
							}
						}
						if(waitForCal.size() > 0){
							service.batchUpdatePrice(waitForCal, new Date());
						}
						
					} catch (Exception e) {
						logger.error("获取京东供货价异常", e);
					}
					Thread.sleep(generateRandom(3500,1500));
				}
				
				List<String> skus = service.findNewSku();
				if(skus.size() > 0){
					// 未入京东商品池的sku发邮件
					try {
						SendEmailUtil.getInstance().doSendEmail("有新京东top100热销商品未入商品池", StringUtils.join(skus, '\n'), "zhouyx@flyingfinancial.hk");
					} catch (MessagingException e) {
						logger.error("邮件发送异常", e);
					}
				}
				
				settingService.markUpdateSupplyPriceFalse();
				
				long count = (System.currentTimeMillis() - lastUpdate)/1000;
				logger.info("京东供货价更新线程,数据已全部跑完,总共花费时间：{} 秒,等待进入下一轮价格爬取",count);
			} catch (Exception e) {
				logger.error("京东供货价更新线程异常",e);
			} finally {
				SpiderConstant.lock.unlock();
			}
			
			
		}
	}
	
	private int generateRandom(int max, int min) {
		return random.nextInt(max - min) + min;
	}
	
	public void setRunnable(boolean runnable) {
		this.runnable = runnable;
	}
}