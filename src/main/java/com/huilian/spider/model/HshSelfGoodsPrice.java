package com.huilian.spider.model;

/**
 * @version 1.0
 * @author zyx
 */
public class HshSelfGoodsPrice {

	private Long id;
	
	private java.math.BigDecimal goodsId;
	
	private java.math.BigDecimal supplyPrice;
	
	private java.math.BigDecimal sellPrice;
	
	private Boolean chargeFree;
	
	private Integer periods;
	
	private java.math.BigDecimal couponAmount;
	
	private java.util.Date lastUpdate;

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setGoodsId(java.math.BigDecimal value) {
		this.goodsId = value;
	}
	
	public java.math.BigDecimal getGoodsId() {
		return this.goodsId;
	}
	
	public void setSupplyPrice(java.math.BigDecimal value) {
		this.supplyPrice = value;
	}
	
	public java.math.BigDecimal getSupplyPrice() {
		return this.supplyPrice;
	}
	
	public void setSellPrice(java.math.BigDecimal value) {
		this.sellPrice = value;
	}
	
	public java.math.BigDecimal getSellPrice() {
		return this.sellPrice;
	}
	
	public void setChargeFree(Boolean value) {
		this.chargeFree = value;
	}
	
	public Boolean getChargeFree() {
		return this.chargeFree;
	}
	
	public void setPeriods(Integer value) {
		this.periods = value;
	}
	
	public Integer getPeriods() {
		return this.periods;
	}
	
	public void setCouponAmount(java.math.BigDecimal value) {
		this.couponAmount = value;
	}
	
	public java.math.BigDecimal getCouponAmount() {
		return this.couponAmount;
	}
	
	public void setLastUpdate(java.util.Date value) {
		this.lastUpdate = value;
	}
	
	public java.util.Date getLastUpdate() {
		return this.lastUpdate;
	}
	
}
