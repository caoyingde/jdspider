package com.huilian.spider.service;

import java.util.Date;
import java.util.List;

import com.huilian.spider.dao.BizMapper;
import com.huilian.spider.dao.JdGoodsPriceHisMapper;
import com.huilian.spider.model.JdGoodsPricePOJO;

public class JdGoodsPriceHisService extends BizService {

	@Override
	protected Class<? extends BizService> getLoggerClass() {
		return JdGoodsPriceHisService.class;
	}

	@Override
	protected Class<? extends BizMapper> getMapperClass() {
		return JdGoodsPriceHisMapper.class;
	}

	/**
	 * 批量插入京东价格变动记录
	 * @param list
	 */
	public void batchInsertTopSaleGoods(List<JdGoodsPricePOJO> list,Date now){
		try {
			((JdGoodsPriceHisMapper) super.getMapper()).batchInsertHis(list,now);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
}
