package com.huilian.spider;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时启动"京东 top 100 商品信息爬虫
 * @author Administrator
 *
 */
public class JdSearchSpiderScheduledThread extends Thread {
	private static Logger logger = LoggerFactory.getLogger(JdSearchSpiderScheduledThread.class);
	private static final int RUN_TIME = 7;
	private static final Timer timer = new Timer();
	private static TimerTask task;

	@Override
	public void run() {
		task = new JdSearchSpiderThread();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, RUN_TIME); 
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime(); // 第一次执行定时任务的时间
		// 避免发布时立即执行
		if (date.before(new Date())) {
			date = this.addDay(date, 1);
		}
		// 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
		timer.schedule(task, date, BizConstant.JD_SEARCH_PERIOD);
		logger.info("京东 top 100 商品信息爬虫已设定为定时启动.");
	}

	// 增加或减少天数
	private Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
}
