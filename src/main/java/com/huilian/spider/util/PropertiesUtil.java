package com.huilian.spider.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	
	private static String filename = "/config.properties";
	private static Properties pro = new Properties();
	static {
		try {
			pro.load(PropertiesUtil.class.getResourceAsStream(filename));
		} catch (Exception e) {
			logger.error("config.properties配置文件加载出错", e);
		}
	}

	public static int getInt(String key) {
		int i = 0;
		try {
			i = Integer.parseInt(getString(key));
		} catch (Exception e) {
		}
		return i;
	}

	public static String getString(String key) {
		return pro == null ? null : pro.getProperty(key);
	}

}