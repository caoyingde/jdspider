package com.huilian.spider;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huilian.spider.model.JdGoodsPricePOJO;
import com.huilian.spider.service.HshSelfGoodsPriceService;

/**
 * 自营商品免息计算任务
 * @author Administrator
 *
 */
public class HshSelfGoodsThread extends Thread {
	private Logger logger = LoggerFactory.getLogger(TopSaleGoodsSyncThread.class);
	private HshSelfGoodsPriceService service = new HshSelfGoodsPriceService();
	
	public HshSelfGoodsThread() {
		super("hsh-self-goods-thread");
	}

	@Override
	public void run() {
		logger.info("自营商品免息计算任务已启动.");
		if (!SpiderConstant.isSelfGoodsCalTaskRunning) {
			SpiderConstant.isSelfGoodsCalTaskRunning = true;
			Date now = new Date();
			service.syncSelfGoodsToLocal();
			List<JdGoodsPricePOJO> list = service.findSelfGoodsList();
			if (list.size() > 0) {
				service.batchUpdatePrice(list, now);
			}
			SpiderConstant.isSelfGoodsCalTaskRunning = false;
		}
	}

	public static void main(String[] args) {
		new HshSelfGoodsThread().start();
	}
}
