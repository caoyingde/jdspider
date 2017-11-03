package com.huilian.spider;

import java.math.BigDecimal;

public class BizConstant {
	// 免息等级
	public static final BigDecimal LEVEL_1 = BigDecimal.valueOf(0.03);
	public static final BigDecimal LEVEL_2 = BigDecimal.valueOf(0.0525);
	public static final BigDecimal LEVEL_3 = BigDecimal.valueOf(0.075);
	public static final BigDecimal LEVEL_4 = BigDecimal.valueOf(0.0975);
	public static final BigDecimal LEVEL_5 = BigDecimal.valueOf(0.12);
	// 基础价加成比例
	public static final BigDecimal LEVEL_BASE = BigDecimal.valueOf(0.0525);
	public static final BigDecimal COUPON_NO_PAYMENT = BigDecimal.valueOf(10);
	
	// 京东价减价金额
	public static final BigDecimal JD_PRICE_DERATE = BigDecimal.valueOf(10);
	// 上架商品最低供货价
	public static final BigDecimal ON_SELL_PRICE_MIN = BigDecimal.valueOf(99);
	
	
	public static final long PUTAWAY_SYNC_INTERVAL = 5 * 60 * 1000;//上架同步间隔5分钟
	public static final long SUPPLY_UPDATE_INTERVAL = 30 * 60 * 1000;//每30分钟更新供货价
	public static final long PRICE_SPIDER_INTERVAL = 2 * 60 * 60 * 1000;//京东商品价格爬取时间间隔，2小时内爬取完毕
	
	public static final long JD_SEARCH_PERIOD = 7 * 24 * 60 * 60 * 1000;// 京东top100商品每7天爬一次
}
