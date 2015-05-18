package com.xpos.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xpos.common.entity.Category;
import com.xpos.common.entity.Picture;
import com.xpos.common.service.CategoryService;
import com.xpos.common.service.PictureService;

@Controller
@RequestMapping(value="category")
public class CategoryController extends BaseController {
	
	  @Resource
	  private CategoryService categoryService   ;
	
	  @Resource
	  private PictureService pictureService  ;
	  
	  /**
	        大类与小类查询列表
	   */
	  @RequestMapping(value="/list",method=RequestMethod.GET)
	  public String findCategorys( Model  model){
		   List<Category> findAllCategory = categoryService.findAllCountCategory();
		   model.addAttribute("categorys", findAllCategory);
		   return  "category/category_list";
	  }
	 /**
	     添加大类
	  */
	  @AvoidDuplicateSubmission(addToken=true)
	  @RequestMapping(value="/add",method=RequestMethod.GET)
	  public String addBigCategory(Model  model ){
		 return "category/category_input";
	  }
	  /**
	     添加小类
	  */
	  @AvoidDuplicateSubmission(addToken=true)
	  @RequestMapping(value="/add/{id}",method=RequestMethod.GET)
	  public String addlittleCategory(@PathVariable Long id  , Model  model){
		 Category parent = categoryService.findCategoryByIdWinthUnvisible(id);
		 Category category = new Category();
		 category.setParent(parent);
		 model.addAttribute("category", category);
		 return "category/category_input";
	  }
	  
	  /**
	   * 
	   * 保存大小类
	   */
	  @AvoidDuplicateSubmission(removeToken=true,errorRedirectURL="/category/list")
	  @RequestMapping(value="/add" , method=RequestMethod.POST)
	  public String addCategory( @Valid Category category ,  MultipartFile bannerFile ,  BindingResult result, 
			  					 Model  model , RedirectAttributes redirectAttributes){
		 if(result.hasErrors()){
			 	redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
				redirectAttributes.addFlashAttribute("msg", "操作失败");
				redirectAttributes.addFlashAttribute("error", result.getAllErrors());
				return findCategorys(model);
		}
		  category.setDeleted(false);
		if (bannerFile!=null && !bannerFile.isEmpty()) {
			  Picture defaultpicture = pictureService.getPictureFromMultipartFile(bannerFile);
              category.setBanner(defaultpicture);
		}
		if (category.getParent() == null) {
			// 大类
			 Category parentCategory = new Category();
			 parentCategory.setId(0L);
			 category.setParent(parentCategory);
		}
		boolean categoryStatus = false; 
		if (category.getId() != null){
			categoryStatus = categoryService.updateCategory(category);
		}else{
			categoryStatus = categoryService.saveCategory(category);
		}
		 if (categoryStatus) {
			    redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "操作成功");
			}else{
				redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
				redirectAttributes.addFlashAttribute("msg", "操作失败");
			}
		 return "redirect:/category/list";
	  }
	  
	  /**
	   *  编辑大/小类
	   */
	  @RequestMapping(value="/edit/{id}" ,method=RequestMethod.GET)
	  public String editBigCategory(@PathVariable  Long id , Model  model){
		  Category category = categoryService.findCategoryByIdWinthUnvisible(id);
		  model.addAttribute("category", category);
		  return "category/category_input";
	  }
	  /**
	   * 删除大小类
	   */
	  @RequestMapping(value="/delete/{id}" ,method=RequestMethod.DELETE)
	  public String deletelCategory(@PathVariable Long id  , Model model ,RedirectAttributes redirectAttributes){
		  boolean deleteCategory = categoryService.deleteCategory(id);
		 if (deleteCategory){
			redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "删除成功");
		}else{
			redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
			redirectAttributes.addFlashAttribute("msg", "删除失败");
		}
		 return "redirect:/category/list";
	  }
	  
	  /**
	   * 大类,小类排序
	   * @return
	   */
	  @RequestMapping(value="/sequence/{parentId}")
	  public String sequenceCategory(@PathVariable Long parentId , Model model){
		  List<Category> findAllParentCategory = categoryService.findAllParentCategory(parentId);
		  Category parent = new Category();
		  parent.setId(0L);
		  //小类覆盖parent
		  if (parentId!=null && parentId!=0L) {
			  for (Category category : findAllParentCategory) {
				   parent = category.getParent();
				   break;
			}
		}
		  model.addAttribute("categoryParent", parent);
		  model.addAttribute("categoryList", findAllParentCategory);
		  return "category/category_sequence";
	  }
	  
	  /**
	   * 修改大小类的排序
	   */
	  @RequestMapping(value = "update/sequence", method = RequestMethod.PUT)
	  public String updateSequenceCategory(@RequestParam(value = "sequence")Integer[] sequences , 
			  							   @RequestParam(value = "categoryId")Long[] categoryIds ,
			  							   @RequestParam(value = "parentId") Long parentId  ,
			  							   RedirectAttributes redirectAttributes,Model model){
		 List<Category> categoryList = new ArrayList<Category>();
		 	 redirectAttributes.addFlashAttribute("msg", "修改排序失败");
		 try{
			 for (int i = 0; i < sequences.length; i++) {
				 Category category = new Category();
				 category.setId(categoryIds[i]);
				 category.setSequence(sequences[i]);
				 categoryList.add(category);
			 }
		 }catch(Exception e){
			 redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		 }
		 if(categoryService.updateSequenceCategoryList(categoryList)){
			 redirectAttributes.addFlashAttribute("status",STATUS_SUCCESS);
			 redirectAttributes.addFlashAttribute("msg", "修改排序成功");
		 }
		 return "redirect:/category/list";
	  }
	  
	  
}
