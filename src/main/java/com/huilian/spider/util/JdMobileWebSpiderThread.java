package com.huilian.spider.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huilian.spider.SpiderConstant;

public class JdMobileWebSpiderThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(JdMobileWebSpiderThread.class);
	private String electric = "https://so.m.jd.com/category/list.action?_format_=json&catelogyId=737";//家电
	private String computer = "https://so.m.jd.com/category/list.action?_format_=json&catelogyId=670";//电脑办公
	private String mobile = "https://so.m.jd.com/category/list.action?_format_=json&catelogyId=9987";// 手机数码
	private String imageFolderPath = "e:/jd_mobile";

	@Override
	public void run() {
		List<JdGoodsImgPOJO> el = this.parseJson(electric);
		List<JdGoodsImgPOJO> co = this.parseJson(computer);
		List<JdGoodsImgPOJO> mo = this.parseJson(mobile);
		List<JdGoodsImgPOJO> all = new ArrayList<>(el.size() + co.size() + mo.size());
		all.addAll(el);
		all.addAll(co);
		all.addAll(mo);

		for (JdGoodsImgPOJO t : all) {
			try {
				String fileName = t.getName() + t.getUrl().substring(t.getUrl().lastIndexOf('.'));

				File folder = new File(imageFolderPath + "/" + t.getParentCat());
				if (!folder.exists()) {
					folder.mkdirs();
				}

				File file = new File(folder.getAbsolutePath() + "/" + fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				this.fetchFile(t.getUrl(), file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<JdGoodsImgPOJO> parseJson(String url) {
		List<JdGoodsImgPOJO> list = new ArrayList<JdGoodsImgPOJO>();
		try {
			String result = this.fetchHtml(url);
			JSONObject arrays = (JSONObject) JSON.parse(result);
			JSONObject oo = (JSONObject) JSON.parse((String) arrays.get("catalogBranch"));
			JSONArray datas = (JSONArray) oo.get("data");
			datas.remove(0);

			for (Object t : datas) {
				JSONArray arr = ((JSONObject) t).getJSONArray("catelogyList");
				String parentCat = ((JSONObject) t).getString("name");
				for (Object obj : arr) {
					JSONObject jo = (JSONObject) obj;
					String img = (String) jo.get("icon");
					String name = ((String) jo.get("name")).replace('/', '_');
					JdGoodsImgPOJO entity = new JdGoodsImgPOJO(img, name, parentCat);
					list.add(entity);
					System.out.println(entity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private String fetchHtml(String url) throws Exception {
		HttpRequestBase request = this.assembleFetchHtml(url);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			return httpclient.execute(request, new ResponseHandler<String>() {
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
			});
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				logger.error("httpclient关闭连接失败", e);
			}
		}
	}

	private File fetchFile(String url, File file) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try {
				HttpResponse httpResponse = httpclient.execute(this.assembleFetchImage(url));
				InputStream in = (InputStream) httpResponse.getEntity().getContent();

				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				byte b[] = new byte[4096];
				int len = 0;
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
				out.flush();
				in.close();
				out.close();
			} finally {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("httpclient关闭连接失败", e);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	private HttpRequestBase assembleFetchHtml(String url) {
		HttpPost httpget = new HttpPost(url);// 家用电器
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(SpiderConstant.CONNECTION_TIMEOUT)
				.setConnectTimeout(SpiderConstant.CONNECTION_TIMEOUT).setSocketTimeout(SpiderConstant.CONNECTION_TIMEOUT).build();
		httpget.setConfig(requestConfig);
		httpget.addHeader("Accept", "application/json");
		httpget.addHeader("Accept-Encoding", "gzip, deflate, sdch, br");
		httpget.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpget.addHeader("Connection", "keep-alive");
		httpget.addHeader("Referer", "https://so.m.jd.com/category/all.html");
		httpget.addHeader("x-requested-with", "XMLHttpRequest");
		httpget.addHeader("User-Agent",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
		return httpget;
	}

	private HttpRequestBase assembleFetchImage(String url) {
		HttpGet httpget = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(SpiderConstant.CONNECTION_TIMEOUT)
				.setConnectTimeout(SpiderConstant.CONNECTION_TIMEOUT).setSocketTimeout(SpiderConstant.CONNECTION_TIMEOUT).build();
		httpget.setConfig(requestConfig);
		httpget.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpget.addHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpget.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpget.addHeader("Host", "m.360buyimg.com");
		httpget.addHeader("User-Agent",
				"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
		return httpget;
	}
	
	class JdGoodsImgPOJO {
		private String url;
		private String name;
		private String parentCat;

		public JdGoodsImgPOJO(String url, String name, String parentCat) {
			super();
			this.url = url;
			this.name = name;
			this.parentCat = parentCat;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getParentCat() {
			return parentCat;
		}

		public void setParentCat(String parentCat) {
			this.parentCat = parentCat;
		}

		@Override
		public String toString() {
			return "JdGoodsImgPOJO [url=" + url + ", name=" + name + ", parentCat=" + parentCat + "]";
		}

	}

	public static void main(String[] args) {
		new JdMobileWebSpiderThread().start();
	}
}
