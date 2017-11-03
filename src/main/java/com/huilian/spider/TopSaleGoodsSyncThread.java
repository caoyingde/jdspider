package com.huilian.spider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huilian.spider.service.JdGoodsPriceService;
import com.huilian.spider.service.JdSpiderSettingService;
import com.huilian.spider.service.JdTopSaleGoodsService;

/**
 * top 100 热销商品上架信息同步线程 
 * @author Administrator
 *
 */
public class TopSaleGoodsSyncThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(TopSaleGoodsSyncThread.class);
	
	private JdSpiderSettingService settingService = new JdSpiderSettingService();
	private JdTopSaleGoodsService goodsService = new JdTopSaleGoodsService();
	private JdGoodsPriceService priceService = new JdGoodsPriceService();
	
	private volatile boolean runnable = true;
	
	public TopSaleGoodsSyncThread() {
		super("top-sale-goods-sync-thread");
	}
	
	@Override
	public void run() {
		logger.info("top 100 热销商品上架信息同步线程已启动.");
		while(runnable){
			boolean isTopSaleUpdate = settingService.isTopSaleSyncRunnable();
			if(isTopSaleUpdate){
				long curr = System.currentTimeMillis();
				List<String> list = goodsService.findNewTopSaleGoods();
				if(list.size() > 0){
					priceService.batchInsertTopSaleGoods(list);
				}
				settingService.markTopSaleSyncFalse();
				settingService.markUpdateSupplyPriceTrue();
				logger.info("top100热销商品已同步上架完,总共花费时间：{} 秒,等待进入下一轮",(System.currentTimeMillis() - curr));
			}
			try {
				Thread.sleep(BizConstant.PUTAWAY_SYNC_INTERVAL);
			} catch (InterruptedException e) {
				logger.error("线程异常", e);
			}
		}
	}
	
	public void setRunnable(boolean runnable) {
		this.runnable = runnable;
	}
}
