package com.xpos.common.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.xpos.common.entity.Page;
import com.xpos.common.entity.PageCategory;
import com.xpos.common.entity.PageTemplate;
import com.xpos.common.entity.example.PageCategoryExample;
import com.xpos.common.entity.example.PageExample;
import com.xpos.common.entity.example.PageTemplateExample;
import com.xpos.common.persistence.mybatis.PageCategoryMapper;
import com.xpos.common.persistence.mybatis.PageMapper;
import com.xpos.common.persistence.mybatis.PageTemplateMapper;
import com.xpos.common.utils.Pager;

@Service
public class PageServiceImpl implements PageService{
	
	@Resource
	private FreeMarkerConfig freemarkerConfig;

	@Resource
	private PageMapper pageMapper;
	
	@Resource
	private PageCategoryMapper pageCategoryMapper;
	
	@Resource
	private PageTemplateMapper pageTemplateMapper;
	
	@Override
	public Page findPageById(Long id) {
		return pageMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public Page findPageByName(String name) {
		PageExample example = new PageExample();
		example.createCriteria().addCriterion("name=", name)
								.addCriterion("deleted=", false);
		return pageMapper.selectOneByExample(example);
	}

	@Override
	public String renderArticle(Page page) {
		return getHtml(page);
	}
	
	
	public String getHtml(Page page) {
		if (page == null) {
			return null;
		}
		if (page.getPageTemplate() == null) {
			return page.getContent();
		}

		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("page", page);
		parameterMap.put("templateContent", page.getPageTemplate()
				.getContent());
		return resolvedAsHtml(parameterMap);
	}
	
	
	public String resolvedAsHtml(Map<String, Object> parameterMap) {
		if (parameterMap == null || parameterMap.get("templateContent") == null) {
			return null;
		}
		try {

			String templateContent = parameterMap.get("templateContent")
					.toString();

			StringWriter writer = new StringWriter();
			new freemarker.template.Template("", new StringReader(
					templateContent), freemarkerConfig.getConfiguration())
					.process(parameterMap, writer);
			return writer.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	@Transactional
	public boolean savePage(Page page) {
		return pageMapper.insert(page)>0;
	}

	@Override
	@Transactional
	public boolean updatePage(Page page) {
		return pageMapper.updateByPrimaryKey(page)>0;
	}

	@Override
	public boolean deletePage(Long id) {
		 Page page = pageMapper.selectByPrimaryKey(id);
		 if (page != null) {
			 page.setDeleted(true);
			 return pageMapper.updateByPrimaryKey(page)>0;
		}
		return false;
	}
	@Override
	public Pager<Page> findAllPager(Pager<Page> pager, PageExample example) {
		if (example == null) 
			example  =  new PageExample();
		  example.appendCriterion("deleted = ", false);
	      pager.setList(pageMapper.selectByExample(example, pager));
	      pager.setTotalCount(pageMapper.countByExample(example));
		return pager;
	}

	@Override
	@Transactional
	public boolean savePageCategory(PageCategory pageCategory) {
		return pageCategoryMapper.insert(pageCategory)>0;
	}

	@Override
	@Transactional
	public boolean updatePageCategory(PageCategory pageCategory) {
		return pageCategoryMapper.updateByPrimaryKey(pageCategory)>0;
	}

	@Override
	@Transactional
	public boolean updateSequncePageCategory(List<PageCategory> pageCategoryList) {
	 
		return false;
	}

	@Override
	@Transactional
	public boolean savePageTemplate(PageTemplate page) {
		return pageTemplateMapper.insert(page)>0;
	}

	@Override
	@Transactional
	public boolean updatePageTemplate(PageTemplate page) {
		return pageTemplateMapper.updateByPrimaryKey(page)>0;
	}



	@Override
	public Pager<PageCategory> findAllPageCategory(Pager<PageCategory> pager,
			PageCategoryExample example) {
		if (example == null) 
			example  =  new PageCategoryExample();
		List<PageCategory> pageCategoryList = pageCategoryMapper.selectPageCategoryCount();
		//只对大类进行分页
		Map<Long, PageCategory> categoryMap = this.handlePageCategory(pageCategoryList);
		List<PageCategory> arrayList =   new ArrayList<PageCategory>(categoryMap.values());
		int listSize = arrayList.size();
		int fromIndex  = pager.getStartNumber();
		int toIndex    = pager.getStartNumber()+pager.getEndNumber();
		if (fromIndex>listSize) 
			return  pager;
		if (toIndex > listSize) {
			toIndex = listSize;
		}
	     pager.setList(arrayList.subList(fromIndex, toIndex));
	     pager.setTotalCount(categoryMap.size());
		return pager;
	}
	
	private Map<Long, PageCategory> handlePageCategory(List<PageCategory> pageCategoryList) {
		Map<Long, PageCategory> categoryMap  = new LinkedHashMap<Long, PageCategory>();
		Iterator<PageCategory> iterator = pageCategoryList.iterator();
		//暂时获取大类
		while ( iterator.hasNext()) {
			PageCategory category = iterator.next();
			if (category.getParent()==null) {
				categoryMap.put(category.getId(), category);
				iterator.remove();
			}			
		}
		//统计大类的数量，并向大类中注入所属的小类
		for (PageCategory category : pageCategoryList) {
			PageCategory categoryLittle = categoryMap.get(category.getParent().getId());
			if (categoryLittle != null) {
				categoryLittle.setCount(categoryLittle.getCount()+category.getCount());
				List<PageCategory> categorys = categoryLittle.getChildren();
				categorys.add(category);
			}
		} 
		return  categoryMap;
	}

	@Override
	public Pager<PageTemplate> findAllPageTemplate(Pager<PageTemplate> pager,
			PageTemplateExample example) {
		if (example == null) 
			example  =  new PageTemplateExample();
	      pager.setList(pageTemplateMapper.selectByExample(example, pager));
	      pager.setTotalCount(pageTemplateMapper.countByExample(example));
		return pager;
	}



	@Override
	public PageCategory findPageCategoryById(Long id) {
		return pageCategoryMapper.selectByPrimaryKey(id);
	}



	@Override
	public PageTemplate findPageTemplateById(Long id) {
		return pageTemplateMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<PageCategory> findAllPageCategory() {
		List<PageCategory> pageCategoryList = pageCategoryMapper.selectPageCategoryCount();
		Map<Long, PageCategory> categoryMap = this.handlePageCategory(pageCategoryList);
		return new ArrayList<>(categoryMap.values());
	}

	@Override
	public List<PageTemplate> findAllPageTemplate() {
		return (List<PageTemplate>) pageTemplateMapper.selectByExample(new PageTemplateExample(), null);
	}

}
