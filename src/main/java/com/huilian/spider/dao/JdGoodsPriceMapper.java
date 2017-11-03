package com.huilian.spider.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huilian.spider.model.JdGoodsPricePOJO;
import com.huilian.spider.model.JdGoodsInfoPOJO;

public interface JdGoodsPriceMapper extends BizMapper{
	public List<JdGoodsPricePOJO> findSkuList(long start, long limit);
	public List<JdGoodsPricePOJO> findForUpdateSupplyPrice(long start, long limit);
	public void batchUpdatePrice(@Param("list")List<JdGoodsPricePOJO> list, @Param("now")Date now);
	public void batchUpdateSupplyPrice(@Param("list")List<JdGoodsInfoPOJO> list, @Param("now")Date now);
	public void batchInsertTopSaleGoods(@Param("list")List<String> list);
	public List<String> findNewSku();
}
