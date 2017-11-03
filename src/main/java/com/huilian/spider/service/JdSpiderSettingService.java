package com.huilian.spider.service;

import java.util.List;

import com.huilian.spider.dao.BizMapper;
import com.huilian.spider.dao.JdSpiderSettingMapper;

public class JdSpiderSettingService extends BizService {

	@Override
	protected Class<? extends BizService> getLoggerClass() {
		return JdSpiderSettingService.class;
	}

	@Override
	protected Class<? extends BizMapper> getMapperClass() {
		return JdSpiderSettingMapper.class;
	}

	private void update(String key,String value){
		try {
			((JdSpiderSettingMapper) super.getMapper()).update(key, value);
			super.commit();
		} catch (Exception e) {
			super.rollback();
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	private String findByKey(String key){
		try {
			String updatable = ((JdSpiderSettingMapper) super.getMapper()).findByKey(key);
			return updatable;
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 获取需要更新的商品分类
	 * @return
	 */
	public List<String> getGoodsCategoryList(){
	    try {
	    	return ((JdSpiderSettingMapper) super.getMapper()).getGoodsCategoryList();
		} catch (Exception e) {
			throw e;
		} finally {
			super.closeSession();
		}
	}
	
	/**
	 * 是否可同步并上架京东top 100商品表中的数据
	 * @param start 开始行（0开始）
	 * @param limit 查询数量
	 * @return
	 */
	public boolean isTopSaleSyncRunnable(){
		String updatable = this.findByKey("top_sale_goods_sync");
		if("1".equals(updatable)){
			return true;
		}
		return false;
	}
	
	/**
	 * 标记可以同步并上架京东top 100商品表中的数据
	 * @param list
	 */
	public void markTopSaleSyncTrue(){
		this.update("top_sale_goods_sync", "1");
	}
	
	/**
	 * 标记不可同步并上架京东top 100商品表中的数据
	 * @param list
	 */
	public void markTopSaleSyncFalse(){
		this.update("top_sale_goods_sync", "0");
	}
	
	/**
	 * top 100 热销商品上架信息同步是否完成
	 * @param start 开始行（0开始）
	 * @param limit 查询数量
	 * @return
	 */
	public boolean isUpdateSupplyPrice(){
		String updatable = this.findByKey("update_supply_price");
		if("1".equals(updatable)){
			return true;
		}
		return false;
	}
	
	/**
	 * 标示是否更新上架商品的供货价为可更新
	 * @param list
	 */
	public void markUpdateSupplyPriceTrue(){
		this.update("update_supply_price", "1");
	}
	
	/**
	 * 标示是否更新上架商品的供货价为不更新
	 * @param list
	 */
	public void markUpdateSupplyPriceFalse(){
		this.update("update_supply_price", "0");
	}
	
	/**
	 * 商品信息同步标志为可同步状态
	 * @param list
	 */
	public void markGoodsSyncTrue(){
		this.update("sync_flag", "1");
	}
	
	/**
	 * 商品信息同步标志为不可同步状态
	 * @param list
	 */
	public void markGoodsSyncFalse(){
		this.update("sync_flag", "0");
	}
}
