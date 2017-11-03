package com.huilian.spider.service;

import java.util.Date;
import java.util.List;

import com.huilian.spider.InterestCalStrategy;
import com.huilian.spider.dao.BizMapper;
import com.huilian.spider.dao.HshSelfGoodsPriceMapper;
import com.huilian.spider.model.JdGoodsPricePOJO;

public class HshSelfGoodsPriceService extends BizService {

	@Override
	protected Class<? extends BizService> getLoggerClass() {
		return HshSelfGoodsPriceService.class;
	}

	@Override
	protected Class<? extends BizMapper> getMapperClass() {
		return HshSelfGoodsPriceMapper.class;
	}

	/**
	 * 分批查询
	 * @param start 开始行（0开始）
	 * @param limit 查询数量
	 * @return
	 */
	public List<JdGoodsPricePOJO> findSelfGoodsList(){
		try {
			List<JdGoodsPricePOJO> list = ((HshSelfGoodsPriceMapper) super.getMapper()).findSelfGoodsList();
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 批量更新免息计算结果
	 * @param list
	 * @param now 更新时间
	 */
	public void batchUpdatePrice(List<JdGoodsPricePOJO> list,Date now){
		try {
			for (JdGoodsPricePOJO pojo : list) {
				InterestCalStrategy.cal(pojo);
			}
			((HshSelfGoodsPriceMapper) super.getMapper()).batchUpdateGoods(list,now);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 同步hsh_goods中的自营商品到本地表中
	 * @param list
	 */
	public void syncSelfGoodsToLocal(){
		try {
			((HshSelfGoodsPriceMapper) super.getMapper()).syncSelfGoodsToLocal();
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
}
