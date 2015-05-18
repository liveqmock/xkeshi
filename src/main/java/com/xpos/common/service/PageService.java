package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.Page;
import com.xpos.common.entity.PageCategory;
import com.xpos.common.entity.PageTemplate;
import com.xpos.common.entity.example.PageCategoryExample;
import com.xpos.common.entity.example.PageExample;
import com.xpos.common.entity.example.PageTemplateExample;
import com.xpos.common.utils.Pager;

public interface PageService {
	
	/**page*/
	public boolean savePage(Page page);
	public boolean updatePage(Page page);
	public boolean deletePage(Long id);
	public Page findPageByName(String name);
	public Page findPageById(Long id);
	public String renderArticle(Page page);
	public Pager<Page> findAllPager(Pager<Page> pager ,PageExample  example);
	
	
	/**pageCategory*/
	public boolean savePageCategory(PageCategory pageCategory);
	public boolean updatePageCategory(PageCategory pageCategory);
	//pageCategory批量修改排序
	public boolean updateSequncePageCategory(List<PageCategory>  pageCategoryList);
	public Pager<PageCategory>  findAllPageCategory(Pager<PageCategory>  pager  , PageCategoryExample example);
	public PageCategory findPageCategoryById(Long id);
	public List<PageCategory>  findAllPageCategory();
	
	/**pageTemplate*/
	public boolean savePageTemplate(PageTemplate pageTemplate);
	public boolean updatePageTemplate(PageTemplate pageTemplate);
	public Pager<PageTemplate>  findAllPageTemplate(Pager<PageTemplate> pager  , PageTemplateExample  example);
	public PageTemplate findPageTemplateById(Long id);
	public List<PageTemplate>  findAllPageTemplate();
}
