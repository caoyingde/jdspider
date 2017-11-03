package com.huilian.spider.model;

import java.math.BigDecimal;

public class JdTopSaleGoodsPOJO {

	private Long id;

	private String keyword;

	private String sku;
	private String title;

	private java.math.BigDecimal price;

	private java.util.Date createTime;

	public JdTopSaleGoodsPOJO() {
	}

	public JdTopSaleGoodsPOJO(String sku, String title, BigDecimal price) {
		super();
		this.sku = sku;
		this.title = title;
		this.price = price;
	}

	public void setId(Long value) {
		this.id = value;
	}

	public Long getId() {
		return this.id;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
