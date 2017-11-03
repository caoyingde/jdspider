package com.huilian.spider.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface JdSpiderSettingMapper extends BizMapper{
	public String findByKey(@Param("key") String key);
	public void update(@Param("key")String key,@Param("value")String value);
	
	public List<String> getGoodsCategoryList();
	
}
