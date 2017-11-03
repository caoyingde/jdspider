package com.huilian.spider.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huilian.spider.model.JdGoodsInfoPOJO;

/**
 * 获取资源文件键值对.
 * 
 * @author gjl
 * 
 */
public final class JdApiUtil {
	private static Logger logger = LoggerFactory.getLogger(JdApiUtil.class);
	
	/**
	 * 1.2.1 批量查询价格
	 * @param sku(多个以,逗号隔开)
	 * @return
	 * @throws Exception
	 */
	public static List<JdGoodsInfoPOJO> getLastestSupplyPrice(String skus) throws Exception {
		List<JdGoodsInfoPOJO> result = new ArrayList<JdGoodsInfoPOJO>();
		Map<String, Object> urlParm = JdApiUtil.getUrlParamMap();
		urlParm.put("method", "biz.price.sellPrice.get");
		urlParm.put("access_token", JdApiUtil.jdGetToken());
		JSONObject paramJson = new JSONObject();
		paramJson.put("sku", skus);
		urlParm.put("param_json", paramJson);//业务参数put到map
//		System.out.println(urlParm);
		String urlPath = JdApiUtil.getJdApiUrl(urlParm);//获取访问地址
//		logger.info("*****************访问地址为：" + urlPath + "***********************************");
		if (StringUtils.isEmpty(urlPath)) {
			throw new RuntimeException("访问地址为空");
		}
		String vistResult = JdApiUtil.httpPostRequestJD(urlPath, "");
//		logger.info("*****************批量查询价格  结果为：" + vistResult + "***********************************");
		JSONObject jsonObject = JSONObject.parseObject(vistResult);
		if (null != jsonObject && null != jsonObject.getString("biz_price_sellPrice_get_response")) {
			JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("biz_price_sellPrice_get_response"));
			boolean success = jsonData.getBooleanValue("success");
			if (success) {
				JSONArray array = jsonData.getJSONArray("result");
				if (null != array && array.size() > 0) {
					for (Object t : array) {
						JSONObject jo = (JSONObject) t;
						JdGoodsInfoPOJO pojo = new JdGoodsInfoPOJO(String.valueOf(jo.get("skuId")),jo.getBigDecimal("price"));
						result.add(pojo);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 1.1.4	查询商品上下架状态接口
	 * @param sku(多个以,逗号隔开)
	 * @return
	 * @throws Exception
	 */
	public static List<JdGoodsInfoPOJO> checkGoodsSellStatus(String skus) throws Exception {
		List<JdGoodsInfoPOJO> result = new ArrayList<JdGoodsInfoPOJO>();
		
		JSONObject skuParams = new JSONObject();
		skuParams.put("sku", skus);
		
		Map<String, Object> statusQueryParm = JdApiUtil.getUrlParamMap();
		statusQueryParm.put("method", "biz.product.state.query");
		statusQueryParm.put("access_token", JdApiUtil.jdGetToken());
		statusQueryParm.put("param_json", skuParams);
		
		String statusQueryUrl = JdApiUtil.getJdApiUrl(statusQueryParm);
		
		if (StringUtils.isEmpty(statusQueryUrl)) {
			throw new RuntimeException("访问地址为空");
		}
		
		String vistResult = JdApiUtil.httpPostRequestJD(statusQueryUrl, "");
		
		JSONObject jsonObject = JSONObject.parseObject(vistResult);
		
		if (null != jsonObject && null != jsonObject.getString("biz_product_state_query_response")) {
			JSONObject jsonData = JSONObject.parseObject(jsonObject.getString("biz_product_state_query_response"));
			boolean success = jsonData.getBooleanValue("success");
			if (success) {
				JSONArray array = jsonData.getJSONArray("result");
				if (null != array && array.size() > 0) {
					for (Object t : array) {
						JSONObject jo = (JSONObject) t;
						JdGoodsInfoPOJO pojo = new JdGoodsInfoPOJO(String.valueOf(jo.get("sku")),jo.getIntValue("state"));
						result.add(pojo);
					}
				}
			}
		}
		return result;
	}
	
	private static HashMap<String, Object> getUrlParamMap() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("app_key", PropertiesUtil.getString("app_key"));
		param.put("timestamp", new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(new Date()));
		return param;
	}
	
	private static String jdGetToken() {
		String accessToken = "";
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("app_key", PropertiesUtil.getString("app_key"));
			map.put("app_secret", PropertiesUtil.getString("app_secret"));
			map.put("username", PropertiesUtil.getString("username"));
			map.put("password", PropertiesUtil.getString("password"));
			String path = getJdTokenApiUrl(map);
			String data = httpPostRequestJD(path, "");
//			logger.info("jdGetToken() result：" + data);
			if (!StringUtils.isEmpty(data)) {
				JSONObject obj = JSONObject.parseObject(data);
				if(obj.getString("code").equals("1004")){//token过期,刷新token
					return jdGetRefreshToken();
				}else{
					if (obj.get("access_token") != null && !"".equals(obj.get("access_token"))) {
						accessToken = obj.getString("access_token");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}
	
	private static String getJdTokenApiUrl(Map<String, Object> map) {
		String path = PropertiesUtil.getString("jdTokenUrl");
		if (!StringUtils.isEmpty(path)) {
			for (String key : map.keySet()) {
				path = path.replaceAll("(" + key + "=[^&]*)", key + "=" + map.get(key));
			}
		}
		return path;
	}
	
	/**
	 * 京东使用的http post请求方法
	 * 
	 * @param url 地址
	 * @param postContent post内容格式为param1=value&param2=value2&param3=value3
	 * @return
	 * @throws IOException
	 */
	private static String httpPostRequestJD(String urlPath, String postContent) throws Exception {
		OutputStream outputstream = null;
		BufferedReader in = null;
		URL url = new URL(urlPath);
		try {
			URLConnection httpurlconnection = url.openConnection();
			httpurlconnection.setConnectTimeout(10 * 1000);
			httpurlconnection.setReadTimeout(10 * 1000);
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setUseCaches(false);
			OutputStreamWriter out = new OutputStreamWriter(httpurlconnection.getOutputStream(), "UTF-8");
			out.write(postContent);
			out.flush();

			StringBuffer result = new StringBuffer();
			in = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			return result.toString();
		} catch (Exception ex) {
			throw new Exception("post请求异常：" + ex.getMessage());
		} finally {
			if (outputstream != null) {
				try {
					outputstream.close();
				} catch (IOException e) {
					outputstream = null;
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					in = null;
				}
			}
		}
	}
	
	
	/**
	 * https://kploauth.jd.com/oauth/token?grant_type=refresh_token&app_key=test 
	 * &app_secret=e6127721abca41cf84639d5e90e24adf&state=xxx&username= 
	 * username=kepler_test&password=e10adc3949ba59abbe56e057f20f883e
	 */
	//刷新token的方法
	private static String jdGetRefreshToken(){
		String accessToken = "";
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("grant_type", "refresh_token");
			map.put("app_key", PropertiesUtil.getString("app_key"));
			map.put("app_secret", PropertiesUtil.getString("app_secret"));
			map.put("username", PropertiesUtil.getString("username"));
			map.put("password", PropertiesUtil.getString("password"));
			String path = getJdTokenApiUrl(map);
			String data = httpPostRequestJD(path, "");
//			logger.info("jdGetRefreshToken() result：" + data);
			if (!StringUtils.isEmpty(data)) {
				JSONObject obj = JSONObject.parseObject(data);
				if (obj.get("access_token") != null && !"".equals(obj.get("access_token"))) {
					accessToken = obj.getString("access_token");
				}
			}
		} catch (Exception e) {
			logger.error("刷新token出错", e);
		}
		return accessToken;
	}
	
	private static String getJdApiUrl(Map<String, Object> map) {
		String path = PropertiesUtil.getString("jdApiUrlModel");
		if (!StringUtils.isEmpty(path)) {
			for (String key : map.keySet()) {
				path = path.replaceAll("(" + key + "=[^&]*)", key + "=" + map.get(key));
			}
		}
		return path;
	}
	
	public static void main(String[] args) {
		try {
			List<JdGoodsInfoPOJO> list = JdApiUtil.checkGoodsSellStatus("1226115");
			for (JdGoodsInfoPOJO t : list) {
				System.out.println(t);
			}
			List<JdGoodsInfoPOJO> prices = JdApiUtil.getLastestSupplyPrice("1226115");
			for (JdGoodsInfoPOJO t : prices) {
				System.out.println(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}