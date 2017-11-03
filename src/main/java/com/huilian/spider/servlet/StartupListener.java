package com.huilian.spider.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huilian.spider.JdPriceSpiderThread;
import com.huilian.spider.JdSearchSpiderScheduledThread;
import com.huilian.spider.JdSupplyPriceSpiderThread;
import com.huilian.spider.TopSaleGoodsSyncThread;

public class StartupListener implements ServletContextListener {
	static Logger logger = LoggerFactory.getLogger(StartupListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("start spider thread.");
		new JdPriceSpiderThread().start();
		new TopSaleGoodsSyncThread().start();
		new JdSupplyPriceSpiderThread().start();
		new JdSearchSpiderScheduledThread().start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}