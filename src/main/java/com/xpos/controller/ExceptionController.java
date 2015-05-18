package com.xpos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 
 * @author xk
 * 异常控制
 */
@Controller
public class ExceptionController extends  BaseController {
   
	/**
	 * 处理请求连接不存在，web.xml中配置的错误
	 */
	@RequestMapping(value={"/404"}, method= RequestMethod.GET)
	public String errorNotFound(Model  model  ,RedirectAttributes  redirectAttributes){
		 model.addAttribute("status", FAILD);
	     model.addAttribute("msg", "你访问的页面不存在");
		return "error/error";
	}
	
	@RequestMapping(value ={"/500"},method= RequestMethod.GET)
	public String errorInternalServerError(Model  model){
		model.addAttribute("status", FAILD);
		model.addAttribute("msg", "请联系管理员");
		return  "error/error";
	}
	
	@RequestMapping(value ={"/503"},method= RequestMethod.GET)
	public String errorServiceUnavailable(Model  model){
		model.addAttribute("status", FAILD);
		model.addAttribute("msg", "服务器拒绝了你的请求");
		return  "error/error";
	}
}
