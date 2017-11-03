package com.huilian.spider.service;

import java.util.List;

import com.huilian.spider.dao.BizMapper;
import com.huilian.spider.dao.JdTopSaleGoodsMapper;
import com.huilian.spider.model.JdTopSaleGoodsPOJO;

public class JdTopSaleGoodsService extends BizService {

	@Override
	protected Class<? extends BizService> getLoggerClass() {
		return JdTopSaleGoodsService.class;
	}

	@Override
	protected Class<? extends BizMapper> getMapperClass() {
		return JdTopSaleGoodsMapper.class;
	}

	/**
	 * 批量插入
	 * @param list
	 */
	public void batchInsertTop100Goods(List<JdTopSaleGoodsPOJO> list){
		try {
			List<JdTopSaleGoodsPOJO> newList = list;
			// 每次插入前200条，多余的不要
			if(list.size() > 200){
				newList = list.subList(0, 200);
			}
			((JdTopSaleGoodsMapper) super.getMapper()).batchInsertGoods(newList);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	public void truncateTable(){
		try {
			((JdTopSaleGoodsMapper) super.getMapper()).truncateTable();
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 查询最新爬取的top100商品，不在商品价格表（JdGoodsPrice）中的数据
	 * @return
	 */
	public List<String> findNewTopSaleGoods(){
		try {
			return ((JdTopSaleGoodsMapper) super.getMapper()).findNewTopSaleGoods();
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
}
