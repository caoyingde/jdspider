package com.huilian.spider.util;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.huilian.spider.SpiderConstant;

public class Main {

	public static void main(String[] args) throws Exception, IOException {
		HttpGet httpget = new HttpGet("http://newhouse.sz.fang.com/house/s/futian/");
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(SpiderConstant.CONNECTION_TIMEOUT)
				.setConnectTimeout(SpiderConstant.CONNECTION_TIMEOUT).setSocketTimeout(SpiderConstant.CONNECTION_TIMEOUT).build();
		httpget.setConfig(requestConfig);
		httpget.addHeader("Host", httpget.getURI().getHost());
		httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36");
		for (Header header : SpiderConstant.REQUEST_HEADERS) {
			httpget.addHeader(header);
		}
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			String result = httpclient.execute(httpget, new ResponseHandler<String>(){
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
			System.out.println(result);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
