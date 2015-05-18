package com.xpos.common.entity;

import java.util.List;

import javax.persistence.Column;

public class PageTemplate extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@Column
	private String title;			// 标题
	@Column
	private String content;			// 内容
	
	private List<Page> pageList;	// 文章

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public List<Page> getPageList() {
		return pageList;
	}

	public void setPageList(List<Page> pageList) {
		this.pageList = pageList;
	}	
}