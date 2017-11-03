package com.huilian.spider.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huilian.spider.model.JdGoodsPricePOJO;

public interface HshSelfGoodsPriceMapper extends BizMapper{
	public void syncSelfGoodsToLocal();
	public List<JdGoodsPricePOJO> findSelfGoodsList();
	public void batchUpdateGoods(@Param("list")List<JdGoodsPricePOJO> list, @Param("now")Date now);
}
