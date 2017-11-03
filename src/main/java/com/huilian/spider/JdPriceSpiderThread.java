package com.huilian.spider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.huilian.hlej.base.utils.SendEmailUtil;
import com.huilian.spider.model.JdGoodsPricePOJO;
import com.huilian.spider.service.JdGoodsPriceHisService;
import com.huilian.spider.service.JdGoodsPriceService;
import com.huilian.spider.service.JdSpiderSettingService;

/**
 * 京东商品售价爬虫
 * @author Administrator
 *
 */
public class JdPriceSpiderThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(JdPriceSpiderThread.class);
	private static final String URL_FORMAT = "http://p.3.cn/prices/mgets?skuIds=J_%s";
	
	private volatile boolean runnable = true;
	private JdGoodsPriceService service = new JdGoodsPriceService();
	private JdSpiderSettingService settingService = new JdSpiderSettingService();
	private JdGoodsPriceHisService hisService = new JdGoodsPriceHisService();
	private ExecutorService pool = Executors.newFixedThreadPool(5);
	
	private Random random = new Random();
	
	public JdPriceSpiderThread() {
		super("jd-price-spider-thread");
	}
	
	@Override
	public void run() {
		logger.info("京东商品售价爬虫已启动.");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			logger.error("线程异常",e);
		}
		// 最后一次执行时间
		long lastUpdate = 0;
		while(runnable){
			// 数据库配置状态为1，或者时间间隔合适则执行
			boolean timeSuitable = ((System.currentTimeMillis() - lastUpdate) > BizConstant.PRICE_SPIDER_INTERVAL) ? true : false;
			
			if(!timeSuitable){
				try {
					Thread.sleep(20000);
					continue;
				} catch (InterruptedException e) {
					logger.error("线程异常",e);
				}
			}
			
			try {
				SpiderConstant.lock.lock();
				
				logger.info("京东商品爬虫开始工作.");
				
				lastUpdate = System.currentTimeMillis();
				
				long index = 0;
				List<JdGoodsPricePOJO> entityList = service.findSkuList(index, SpiderConstant.FETCH_PER_COUNT);
				boolean emptyData = false;
				if(entityList.size() == 0){
					emptyData = true;
				}

				for (; entityList.size() > 0; index = index + SpiderConstant.FETCH_PER_COUNT, entityList = service.findSkuList(index,
						SpiderConstant.FETCH_PER_COUNT)) {

					List<String> skus = new ArrayList<String>(entityList.size());
					for (JdGoodsPricePOJO pojo : entityList) {
						skus.add(pojo.getSku());
					}
					
					String url = String.format(URL_FORMAT, StringUtils.join(skus, ",J_"));
					
					String result = null;
					try {
						Object obj = null;
						Date now = null;
						while(true){
							try {
								result = this.fetcContent(url);
								now = new Date();
								
								obj = JSON.parse(result);
								if(obj instanceof JSONObject && ((JSONObject)obj).containsKey("error")){
									logger.error("执行爬取京东价格数据异常,需重试,返回结果:"+result);
									Thread.sleep(20000);
									continue;
								}
								else{
									break;
								}
							} catch (Exception e) {
								logger.error("socket请求超时,重新发送HTTP请求",e);
								Thread.sleep(20000);
								continue;
							}
							
						}
						
						JSONArray arrays = (JSONArray) obj;
						this.handleResult(arrays, entityList, now);
						
					} catch (JSONException | ClassCastException e) {
						logger.error("执行爬取京东价格数据异常,返回结果:"+result, e);
					} catch (Exception e) {
						logger.error("查询异常", e);
					}
				}
				
				if(!emptyData){
					settingService.markGoodsSyncTrue();
				}
				logger.info("数据已全部跑完,总共花费时间：{} 秒,等待进入下一轮价格爬取",(System.currentTimeMillis() - lastUpdate)/1000);
			} catch (Exception e) {
				logger.error("京东商品售价爬虫线程异常", e);
			}finally{
				SpiderConstant.lock.unlock();
			}
		}
	}

	private String fetcContent(String url) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpRequestBase request = this.assemble(url);
		try {
			return httpclient.execute(request, new HttpResponseHandler());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("httpclient关闭连接失败", e);
			}
		}
	}
	
	private HttpRequestBase assemble(String url){
		HttpGet httpget = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom()
	    .setConnectionRequestTimeout(SpiderConstant.CONNECTION_TIMEOUT)
	    .setConnectTimeout(SpiderConstant.CONNECTION_TIMEOUT)
	    .setSocketTimeout(SpiderConstant.CONNECTION_TIMEOUT)
	    .build();
		httpget.setConfig(requestConfig);
		httpget.addHeader("Host", httpget.getURI().getHost());
		httpget.addHeader("User-Agent", SpiderConstant.USER_AGENTS[generateRandom(3,0)]);
		for (Header header : SpiderConstant.REQUEST_HEADERS) {
			httpget.addHeader(header);
		}
		return httpget;
	}
	
	private class HttpResponseHandler implements ResponseHandler<String>{
		@Override
		public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		}
	}

	public void setRunnable(boolean runnable) {
		this.runnable = runnable;
	}

	/**
	 * 随机生成介于 max(不包括) ~ min 之间的数字
	 * @param max 最大值
	 * @param min 最小值
	 * @return 随机数
	 */
	private int generateRandom(int max,int min){
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * 结果集的业务逻辑处理
	 * @param arrays
	 * @param entityList
	 * @param now
	 */
	private void handleResult(JSONArray arrays, List<JdGoodsPricePOJO> entityList, Date now){
		List<JdGoodsPricePOJO> priceHisList = new ArrayList<JdGoodsPricePOJO>();
		
		List<JdGoodsPricePOJO> _list = new ArrayList<JdGoodsPricePOJO>(arrays.size());
		for (Object t : arrays) {
			JSONObject json = (JSONObject)t;
			String price = json.getString("p");
			String sku = json.getString("id").replace("J_", "");
			JdGoodsPricePOJO temp = null;
			for (JdGoodsPricePOJO entity : entityList) {
				if(entity.getSku().equals(sku)){
					temp = entity;
					break;
				}
			}
			
			JdGoodsPricePOJO pojo = new JdGoodsPricePOJO(sku,new BigDecimal(price),temp.getSupplyPrice());
			_list.add(pojo);
			
			if(!new BigDecimal(price).equals(temp.getPrice())){
				priceHisList.add(pojo);
			}
		}
		
		if(_list != null && _list.size() > 0){
			service.batchUpdatePrice(_list,now);
		}
		else{
			// json格式已变动，需邮件通知本人
			try {
				SendEmailUtil.getInstance().doSendEmail("京东价格爬虫异常", "爬虫获取到的json格式异常，数据如下：\n"+arrays.toJSONString(), "zhouyx@flyingfinancial.hk");
			} catch (MessagingException e) {
				logger.error("邮件发送异常", e);
			}
		}
		
		final Date tempNow = now;
		if(priceHisList.size() > 0){
			pool.submit(new Runnable() {
				@Override
				public void run() {
					hisService.batchInsertTopSaleGoods(priceHisList, tempNow);
				}
			});
		}
	}
}