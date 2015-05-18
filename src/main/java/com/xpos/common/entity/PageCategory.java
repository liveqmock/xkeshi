package com.xpos.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;



public class PageCategory extends BaseEntity {

	private static final long serialVersionUID = -5132652107151648662L;

	public static final String PATH_SEPARATOR = ",";// 树路径分隔符
	@Column
	private String name;						// 分类名称
	@Column
	private Integer sequnce;					// 排序
	@Column
	private PageCategory parent;				// 上级分类
	@Column
	private Integer  count  ; 					// 统计一共有多少page使用
	
	private List<PageCategory> children  =  new ArrayList<PageCategory>();		// 下级分类
	private List<Page> pageList;				// 文章

	private String metaKeywords;				// 页面关键词
	private String metaDescription;				// 页面描述
	private String path;						// 树路径
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public Integer getSequnce() {
		return sequnce;
	}

	public void setSequnce(Integer sequnce) {
		this.sequnce = sequnce;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public PageCategory getParent() {
		return parent;
	}

	public void setParent(PageCategory parent) {
		this.parent = parent;
	}

	public List<PageCategory> getChildren() {
		return children;
	}

	public void setChildren(List<PageCategory> children) {
		this.children = children;
	}

	public List<Page> getPageList() {
		return pageList;
	}

	public void setPageList(List<Page> pageList) {
		this.pageList = pageList;
	}

	// 获取分类层级（顶级分类：0）
	@Transient
	public Integer getLevel() {
		// 由于Path 保存路径，最后一个是自己的ID比较麻烦，先简单实现
		Integer i = 0;
		PageCategory articleCategory = this;
		while(true){
			if(articleCategory.parent==null||articleCategory.parent.getId()==null||articleCategory.getParent().getId()<1){
				return i;
			}
			articleCategory=articleCategory.getParent();
			i++;
		}
//		return path.split(PATH_SEPARATOR).length - 1;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}