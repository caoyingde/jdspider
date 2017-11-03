package com.huilian.spider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import javax.mail.MessagingException;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huilian.hlej.base.utils.SendEmailUtil;
import com.huilian.spider.model.JdTopSaleGoodsPOJO;
import com.huilian.spider.service.JdSpiderSettingService;
import com.huilian.spider.service.JdTopSaleGoodsService;

/**
 * 京东 top 100 商品信息爬虫
 * @author zyx
 *
 */
public class JdSearchSpiderThread extends TimerTask {
	private Logger logger = LoggerFactory.getLogger(JdSearchSpiderThread.class);
	
	private JdTopSaleGoodsService service = new JdTopSaleGoodsService();
	private JdSpiderSettingService settingService = new JdSpiderSettingService();

	private Random random = new Random();
	
	private String referer;
	
	private boolean isPaging = false;

	@Override
	public void run() {
		if(SpiderConstant.isJdTop100SpiderRunning){
			return;
		}
		logger.info("京东 top 100 商品信息爬虫已启动");
		SpiderConstant.isJdTop100SpiderRunning = true;
		
		long curr = System.currentTimeMillis();
		int count=0;
		
		List<String> list = settingService.getGoodsCategoryList();
		service.truncateTable();

		for (String key : list) {
			try {
				count = this.bizHandle(key) + count;
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.error("京东 top 100 商品信息爬虫执行出错",e);
			}
		}
		
		if(count == 0){
			// 京东html样式已变动，需邮件通知本人
			try {
				SendEmailUtil.getInstance().doSendEmail("京东top100热销商品爬虫异常", "爬虫获取到的html，经目前样式解释后数据为空，请检查京东商城页面是否变动。", "zhouyx@flyingfinancial.hk");
			} catch (MessagingException e) {
				logger.error("邮件发送异常", e);
			}
		}
		
		settingService.markTopSaleSyncTrue();
		logger.info("爬取京东热销商品完成，共{}个商品，总花费{}秒", count, (System.currentTimeMillis() - curr) / 1000f);
		SpiderConstant.isJdTop100SpiderRunning = false;
	}
	
	private int bizHandle(String searchKey) throws UnsupportedEncodingException{
		String searchEncode = URLEncoder.encode(searchKey, "utf-8");
		List<JdTopSaleGoodsPOJO> list = new ArrayList<>();
		
		/**
		 * i:第几页，j:页面条数起始点
		 */
		for (int i = 1,j=1; i <= 7; i++,j=j+30) {
//			String format = SpiderConstant.URL_FORMAT_MAP.get(search)[0];
			String format = SpiderConstant.COMMON_URL_FORMAT;
			if(i % 2 == 0){
//				format = SpiderConstant.URL_FORMAT_MAP.get(search)[1];
				format = SpiderConstant.COMMON_URL_FORMAT_PAGE;
				isPaging = true;
			}else{
				isPaging = false;
			}
			
			String url = format.replaceAll("@kw@", searchEncode).replace("@page@", i+"").replace("@count@", j+"");
			
			if(i % 2 != 0){
				referer = url;
			}
			
			String result = null;
			try {
				Date now = null;
				while (true) {
					try {
						result = this.fetcContent(url);
						now = new Date();
						break;
					} catch (Exception e) {
						logger.error("socket请求超时,重新发送HTTP请求", e);
						Thread.sleep(1000);
						continue;
					}

				}
				List<JdTopSaleGoodsPOJO> _list = this.parse(result);
				if (_list.size() > 0) {
					for (JdTopSaleGoodsPOJO pojo : _list) {
						pojo.setCreateTime(now);
						pojo.setKeyword(searchKey);
					}
					list.addAll(_list);
				}
			} catch (Exception e) {
				logger.error("执行爬取京东{}热销商品异常,返回结果:{},异常信息:{}", searchKey, result, e);
			}
		}
		if(list.size() > 0){
			service.batchInsertTop100Goods(list);
		}
		logger.info("爬取京东'{}'热销商品完成，共{}个商品", searchKey, list.size());
		return list.size();
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

	private HttpRequestBase assemble(String url) {
		HttpGet httpget = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(SpiderConstant.CONNECTION_TIMEOUT)
				.setConnectTimeout(SpiderConstant.CONNECTION_TIMEOUT).setSocketTimeout(SpiderConstant.CONNECTION_TIMEOUT).build();
		httpget.setConfig(requestConfig);
		httpget.addHeader("Host", httpget.getURI().getHost());
		httpget.addHeader("User-Agent", SpiderConstant.USER_AGENTS[generateRandom(3, 0)]);
		for (Header header : SpiderConstant.REQUEST_HEADERS) {
			httpget.addHeader(header);
		}
		if(isPaging){
			httpget.addHeader("Referer", referer);
			httpget.addHeader("X-Requested-With", "XMLHttpRequest");
		}
		return httpget;
	}

	private class HttpResponseHandler implements ResponseHandler<String> {
		@Override
		public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity, "utf-8") : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		}
	}

	private List<JdTopSaleGoodsPOJO> parse(String html) {
		//logger.info(html);
		Document doc = Jsoup.parse(html);
		Elements elements = doc.select("li[class=\"gl-item\"] > div[class=\"gl-i-wrap\"]");
		
		List<JdTopSaleGoodsPOJO> list = new ArrayList<>();
		for (Element t : elements) {
			// 非自营
			if(t.select("div[class=\"p-icons\"] > img[class^=\"goods-icons-img J-picon-tips\"]").size() == 0){
				continue;
			}
			Element strong = t.select("div[class=\"p-price\"] > strong").first();
			// 广告位 & 价格低于99
			if(!strong.hasAttr("data-price") || Float.parseFloat(strong.attr("data-price")) < 99){
				continue;
			}
			Element a = t.select("div[class^=\"p-name p-name-type-2\"] > a").first();
			String sku = strong.attr("class").replace("J_", "");
			String title = a.attr("title");
			BigDecimal price = new BigDecimal(strong.attr("data-price"));
			JdTopSaleGoodsPOJO pojo = new JdTopSaleGoodsPOJO(sku, title, price);
			list.add(pojo);
		}
		
		return list;
	}

	/**
	 * 随机生成介于 max(不包括) ~ min 之间的数字
	 * @param max 最大值
	 * @param min 最小值
	 * @return 随机数
	 */
	private int generateRandom(int max, int min) {
		return random.nextInt(max - min) + min;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		new Thread(new JdSearchSpiderThread()).start();
		
	}
}