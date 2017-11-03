package com.huilian.spider.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.huilian.spider.model.JdGoodsPricePOJO;

public interface JdGoodsPriceHisMapper extends BizMapper{
	public void batchInsertHis(@Param("list")List<JdGoodsPricePOJO> list, @Param("now")Date now);
}
