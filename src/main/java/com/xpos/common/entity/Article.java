package com.xpos.common.entity;

import javax.persistence.Column;

import com.xpos.common.entity.security.Account;


public class Article extends BaseEntity{
	
	private static final long serialVersionUID = 716079782656904409L;
	@Column
	private Account author;  //作者
	
	@Column
	private String title;   //标题
	
	@Column
	private String memo;    //简介
	
	@Column
	private String content;  //内容
	
	@Column
	private Shop shop; //
	
	public Account getAccount() {
		return author;
	}
	public void setAuthor(Account author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Shop getShop() {
		return shop;
	}
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	
	
	
}
