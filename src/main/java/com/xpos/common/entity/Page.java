package com.xpos.common.entity;

import java.util.Date;

import javax.persistence.Column;


public class Page extends BaseEntity {

	private static final long serialVersionUID = 1475773294701585482L;

	public static final int MAX_RECOMMEND_ARTICLE_LIST_COUNT = 20; // 推荐文章列表最大文章数
	public static final int MAX_HOT_ARTICLE_LIST_COUNT = 20;       // 热点文章列表最大文章数
	public static final int MAX_NEW_ARTICLE_LIST_COUNT = 20;       // 最新文章列表最大文章数
	public static final int MAX_PAGE_CONTENT_COUNT = 2000;         // 内容分页每页最大字数
	public static final int DEFAULT_ARTICLE_LIST_PAGE_SIZE = 20;   // 文章列表默认每页显示数
	
    @Column
	private String title;					// 标题
    @Column
	private String name;					// url名称
    @Column
	private String author;					// 作者
    @Column
	private String content;					// 内容
    @Column
	private String metaKeywords;			// 页面关键词
    @Column
	private String metaDescription;			// 页面描述
    @Column
	private Boolean published = false;	    // 是否发布
	@Column
	private String html;					// 生成网页
	@Column
	private PageCategory pageCategory;		// 文章分类
	@Column
	private PageTemplate pageTemplate;		// 应用模板
	

	private Boolean isTop = false;			// 是否置顶
	private Boolean isRecommend = false;	// 是否为推荐文章
	private Integer pageCount;				// 文章页数
	private String  htmlFilePath;			// HTML静态文件路径（首页）
	private Integer hits;					// 点击数
	private String  picture; 
	private Date    publishDate; 
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	public Boolean getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Boolean isRecommend) {
		this.isRecommend = isRecommend;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public String getHtmlFilePath() {
		return htmlFilePath;
	}

	public void setHtmlFilePath (String htmlFilePath) {
		this.htmlFilePath = htmlFilePath;
	}
	
	public Integer getHits() {
		return hits;
	}

	public void setHits(Integer hits) {
		this.hits = hits;
	}
	
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public PageCategory getPageCategory() {
		return pageCategory;
	}

	public void setPageCategory(PageCategory pageCategory) {
		this.pageCategory = pageCategory;
	}
	
	public PageTemplate getPageTemplate(){
		return pageTemplate;
	}
	
	public void setPageTemplate(PageTemplate pageTemplate) {
		this.pageTemplate = pageTemplate;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

}