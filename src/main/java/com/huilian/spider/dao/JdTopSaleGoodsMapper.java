package com.huilian.spider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huilian.spider.model.JdTopSaleGoodsPOJO;

public interface JdTopSaleGoodsMapper extends BizMapper{
	public void batchInsertGoods(@Param("list")List<JdTopSaleGoodsPOJO> list);

	public void truncateTable();
	
	public List<String> findNewTopSaleGoods();
}
