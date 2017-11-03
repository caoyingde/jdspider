package com.huilian.spider.model;

import java.math.BigDecimal;

public class JdGoodsPricePOJO {

	private Long id;

	private String sku;

	private java.math.BigDecimal price;

	private java.math.BigDecimal supplyPrice;
	private java.math.BigDecimal sellPrice;
	private Integer chargeFree;
	private Integer periods;
	private java.math.BigDecimal couponAmount;
	private Integer sellStatus;
	private Integer bizType;

	private java.util.Date createTime;

	private java.util.Date lastUpdate;
	
	private java.util.Date spiderTime;

	public JdGoodsPricePOJO() {
	}

	public JdGoodsPricePOJO(String sku, BigDecimal price, BigDecimal supplyPrice) {
		super();
		this.sku = sku;
		this.price = price;
		this.supplyPrice = supplyPrice;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public void setSku(String value) {
		this.sku = value;
	}

	public String getSku() {
		return this.sku;
	}

	public void setPrice(java.math.BigDecimal value) {
		this.price = value;
	}

	public java.math.BigDecimal getPrice() {
		return this.price;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public void setLastUpdate(java.util.Date value) {
		this.lastUpdate = value;
	}

	public java.util.Date getLastUpdate() {
		return this.lastUpdate;
	}

	public java.math.BigDecimal getSupplyPrice() {
		return supplyPrice;
	}

	public void setSupplyPrice(java.math.BigDecimal supplyPrice) {
		this.supplyPrice = supplyPrice;
	}

	public java.math.BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(java.math.BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Integer getChargeFree() {
		return chargeFree;
	}

	public void setChargeFree(Integer chargeFree) {
		this.chargeFree = chargeFree;
	}

	public Integer getPeriods() {
		return periods;
	}

	public void setPeriods(Integer periods) {
		this.periods = periods;
	}

	public java.math.BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(java.math.BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public Integer getBizType() {
		return bizType;
	}

	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}

	public java.util.Date getSpiderTime() {
		return spiderTime;
	}

	public void setSpiderTime(java.util.Date spiderTime) {
		this.spiderTime = spiderTime;
	}

	public Integer getSellStatus() {
		return sellStatus;
	}

	public void setSellStatus(Integer sellStatus) {
		this.sellStatus = sellStatus;
	}

}
