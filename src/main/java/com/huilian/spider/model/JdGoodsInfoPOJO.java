package com.huilian.spider.model;

import java.math.BigDecimal;

public class JdGoodsInfoPOJO {

	private String sku;

	private java.math.BigDecimal supplyPrice;

	private Integer state;// 上架状态

	public JdGoodsInfoPOJO() {
	}

	public JdGoodsInfoPOJO(String sku, BigDecimal supplyPrice) {
		super();
		this.sku = sku;
		this.supplyPrice = supplyPrice;
	}
	
	public JdGoodsInfoPOJO(String sku, Integer state) {
		super();
		this.sku = sku;
		this.state = state;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public java.math.BigDecimal getSupplyPrice() {
		return supplyPrice;
	}

	public void setSupplyPrice(java.math.BigDecimal supplyPrice) {
		this.supplyPrice = supplyPrice;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "JdGoodsInfoPOJO [sku=" + sku + ", supplyPrice=" + supplyPrice + ", state=" + state + "]";
	}

}
