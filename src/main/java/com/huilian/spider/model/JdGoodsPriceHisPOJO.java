package com.huilian.spider.model;


/**
 * @version 1.0
 * @author zyx
 */
public class JdGoodsPriceHisPOJO{

	private Long id;
	
	private String sku;
	
	private java.math.BigDecimal price;
	
	private java.util.Date updateTime;

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
	
	public void setUpdateTime(java.util.Date value) {
		this.updateTime = value;
	}
	
	public java.util.Date getUpdateTime() {
		return this.updateTime;
	}
	
}
