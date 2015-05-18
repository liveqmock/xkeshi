package com.xpos.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.Page;
import com.xpos.common.entity.PageCategory;
import com.xpos.common.entity.PageTemplate;
import com.xpos.common.entity.example.PageCategoryExample;
import com.xpos.common.entity.example.PageExample;
import com.xpos.common.entity.example.PageTemplateExample;
import com.xpos.common.entity.security.Account;
import com.xpos.common.searcher.PageCategorySearcher;
import com.xpos.common.searcher.PageSearcher;
import com.xpos.common.searcher.PageTemplateSearcher;
import com.xpos.common.service.PageService;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("pager")
public class PagerController extends BaseController{
    
	@Resource
	private PageService  pageService  ;
	
	/**
	 * page 列表
	 */
	@RequestMapping(value = "/pager/list" , method = RequestMethod.GET)
	public String pagerList( Pager<Page>  pager  , PageSearcher  searcher ,  Model model){
		pageService.findAllPager(pager, (PageExample) searcher.getExample());
		model.addAttribute("pager", pager);
		return "pager/pager_list";
	}
	
 
	/**
	 * page 进入添加
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value = "/pager/add" , method = RequestMethod.GET)
	public String pagerAdd(Model model){
		model.addAttribute("parentPageCategories", pageService.findAllPageCategory());
		model.addAttribute("pageTemplate", pageService.findAllPageTemplate());
		return "pager/pager_input";
	}
	
	
	/**
	 * page 保存
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/pager/pager/list")
	@RequestMapping(value = "/pager/add" , method = RequestMethod.POST)
	public String pagerAddSave( Page page,Model model ,RedirectAttributes redirectAttributes){
		Account account = super.getAccount();
		if (account==null) {
			page.setAuthor("");
		}else{
			String username = account.getUsername();
			page.setAuthor(username);
		}
		page.setCreateDate(new Date());
		if (pageService.savePage(page)) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "页面添加成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "页面添加失败");
		}
		return  "redirect:/pager/pager/list";
	}
	
	/**
	 * page urlname检查是否重复
	 */
	@RequestMapping(value = "/pager/name" , method = RequestMethod.GET)
	public HttpEntity<String> pagerUrlName( String name  ,Model model ){
		Page page = pageService.findPageByName(name);
		GrantResult grantResult = new GrantResult(STATUS_FAILD, "");
		if (page != null) {
			model.addAttribute("status", STATUS_FAILD);
		}else{
			grantResult = new GrantResult(STATUS_SUCCESS, "");
		}
		JSONResponse jsonResponse = new JSONResponse(grantResult);
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	
	
	/**
	 * page 详情页面
	 */
	@RequestMapping(value = "/pager/{id}" , method  =  RequestMethod.GET)
	public String pagerDetail(@PathVariable Long id , Model model){
		Page page = pageService.findPageById(id);
		model.addAttribute("page", page);
		model.addAttribute("parentPageCategories", pageService.findAllPageCategory());
		model.addAttribute("pageTemplate", pageService.findAllPageTemplate());
		return  "pager/pager_input";
	}
	
	/**
	 * page 修改
	 */
	@RequestMapping(value = "/pager/update"  , method  = RequestMethod.PUT)
	public String pagerUpdate( Page page  , Model model ,RedirectAttributes redirectAttributes){
		if (pageService.updatePage(page)) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "页面修改成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "页面修改失败");
		}
		return "redirect:/pager/pager/list";
	}
	
	/**
	 * page 删除
	 */
	@RequestMapping(value= "/pager/delete/{id}")
	public String pagerDelete(@PathVariable Long id ,Model model ,RedirectAttributes redirectAttributes){
		if (pageService.deletePage(id)) {
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "页面删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "页面 删除失败");
		}
		return "redirect:/pager/pager/list";
	}
	
	
	/**
	 * pageCategory 列表
	 */
	@RequestMapping(value = "/category/list" ,method  = RequestMethod.GET )
    public String pagerCategoryList( Pager<PageCategory>  pager  ,  PageCategorySearcher searcher , Model model){
		pager.setPageSize(5);	
		//暂时使用对大类进行假分页
		pager = pageService.findAllPageCategory(pager, (PageCategoryExample) searcher.getExample());
		model.addAttribute("pager", pager);
		return "pager/pager_category_list";
    }
	 
 
	/**
	 *  pageCategory 保存
	 * @return
	 */
	@RequestMapping(value= "/category/add" ,method = RequestMethod.POST)
	public String pagerCategoryAddSave( PageCategory category , Model model ,RedirectAttributes redirectAttributes){
		if (category.getId() == null) {
			category.setCreateDate(new Date());
			pageService.savePageCategory(category);
			redirectAttributes.addFlashAttribute("msg", "分类"+category.getName()+"添加成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "分类添加失败");
		}
		return  "redirect:/pager/category/list";
	}
	/**
	 *  pageCategory  修改
	 * @return
	 */
	@RequestMapping(value= "/category/update" ,method = RequestMethod.PUT)
	public String pagerCategoryUpdate( PageCategory category , Model model ,RedirectAttributes redirectAttributes){
		if (category.getId() != null){
			pageService.updatePageCategory(category);
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "分类"+category.getName()+"修改成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "分类添加失败");
		}
		return  "redirect:/pager/category/list";
	}
	
	/**
	 * pageTemplate列表
	 */
	@RequestMapping(value = "/template/list" , method = RequestMethod.GET)
	public String pagerTemplateList(Pager<PageTemplate>  pager  , PageTemplateSearcher  searcher  ,Model model ){
		pager  = pageService.findAllPageTemplate(pager, (PageTemplateExample) searcher.getExample());
		model.addAttribute("pager", pager);
		return "pager/pager_template_list";
	}
	
	
	/**
	 * pageTemplate 进入添加
	 */
	@AvoidDuplicateSubmission(addToken=true)
	@RequestMapping(value = "/template/add" , method = RequestMethod.GET)
	public String pagerTemplateAdd(Model model){
		return "pager/pager_template_input";
	}
	
	/**
	 * pageTemplate 保存
	 */
	@AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/pager/template/list")
	@RequestMapping(value = "/template/add" , method = RequestMethod.POST)
	public String pagerTemplateAddSave( PageTemplate pageTemplate, Model model ,RedirectAttributes redirectAttributes){
		pageTemplate.setCreateDate(new Date());
		if(pageService.savePageTemplate(pageTemplate)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "模板添加成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "模板添加失败");
		}
		return "redirect:/pager/template/list";
	}
	
	/**
	 * pageTemplate  编辑
	 */
	@RequestMapping(value = "/template/update" , method = RequestMethod.PUT)
	public String pagerTemplateUpdate( PageTemplate pageTemplate,  Model model ,RedirectAttributes redirectAttributes){
		if(pageService.updatePageTemplate(pageTemplate)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "模板修改成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "模板修改失败");
		}
		return "redirect:/pager/template/list";
	}
	/**
	 * pageTemplate 进入 详情
	 */
	@RequestMapping(value = "/template/{id}")
	public String pagerTemplate( @PathVariable  Long id, Model model){
		model.addAttribute("pageTemplate", pageService.findPageTemplateById(id));
		return  "pager/pager_template_input";
	}
	
	
	
}
