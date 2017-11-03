package com.huilian.spider.service;

import java.util.Date;
import java.util.List;

import com.huilian.spider.InterestCalStrategy;
import com.huilian.spider.dao.BizMapper;
import com.huilian.spider.dao.JdGoodsPriceMapper;
import com.huilian.spider.model.JdGoodsPricePOJO;
import com.huilian.spider.model.JdGoodsInfoPOJO;

public class JdGoodsPriceService extends BizService {

	@Override
	protected Class<? extends BizService> getLoggerClass() {
		return JdGoodsPriceService.class;
	}

	@Override
	protected Class<? extends BizMapper> getMapperClass() {
		return JdGoodsPriceMapper.class;
	}

	/**
	 * 分批查询
	 * @param start 开始行（0开始）
	 * @param limit 查询数量
	 * @return
	 */
	public List<JdGoodsPricePOJO> findSkuList(long start, long limit){
		try {
			List<JdGoodsPricePOJO> list = ((JdGoodsPriceMapper) super.getMapper()).findSkuList(start, limit);
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 分批查询
	 * @param start 开始行（0开始）
	 * @param limit 查询数量
	 * @return
	 */
	public List<JdGoodsPricePOJO> findForUpdateSupplyPrice(long start, long limit){
		try {
			List<JdGoodsPricePOJO> list = ((JdGoodsPriceMapper) super.getMapper()).findForUpdateSupplyPrice(start, limit);
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 批量更新抓取的价格
	 * @param list
	 * @param now 更新时间
	 */
	public void batchUpdatePrice(List<JdGoodsPricePOJO> list,Date now){
		try {
			for (JdGoodsPricePOJO pojo : list) {
				InterestCalStrategy.cal(pojo);
			}
			((JdGoodsPriceMapper) super.getMapper()).batchUpdatePrice(list,now);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 批量更新供货价
	 * @param list
	 */
	public void batchUpdateSupplyPrice(List<JdGoodsInfoPOJO> list, Date now){
		try {
			((JdGoodsPriceMapper) super.getMapper()).batchUpdateSupplyPrice(list,now);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 批量插入top 100 热销商品信息
	 * @param list
	 */
	public void batchInsertTopSaleGoods(List<String> list){
		try {
			((JdGoodsPriceMapper) super.getMapper()).batchInsertTopSaleGoods(list);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 查询未在京东商品池的sku
	 * @return
	 */
	public List<String> findNewSku(){
		try {
			return ((JdGoodsPriceMapper) super.getMapper()).findNewSku();
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
}
