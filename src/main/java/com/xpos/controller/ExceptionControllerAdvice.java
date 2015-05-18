package com.xpos.controller;



import javax.servlet.ServletException;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;
 

/**
 * 
 * @author xk
 * 抛出异常统一管理
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends  BaseController {
	
	    /** 401  未授权的  */
	    @ExceptionHandler({UnauthorizedException.class})
	    @ResponseStatus(HttpStatus.UNAUTHORIZED)
	    public ModelAndView processUnauthenticatedException(NativeWebRequest request, UnauthorizedException e) {
	        ModelAndView mv = new ModelAndView();
	        mv.addObject("msg", e.getMessage());
	        mv.addObject("status", FAILD);
	        mv.setViewName("error/error");
	        return mv;
	    }
	    
	    /** 404  请求的资源不存在  */
	    @ExceptionHandler({ServletException.class})
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    public ModelAndView  processNotFoundException (){
	    	ModelAndView mv = new ModelAndView();
	    	mv.addObject("msg", "请求的资源不存在");
	    	mv.addObject("status", FAILD);
	    	mv.setViewName("error/error");
	    	return mv ;
	    }
	    
	    /**400 请求参数错误*/
	    @ExceptionHandler({IllegalStateException.class})
	    @ResponseStatus(HttpStatus.BAD_REQUEST)
	    public ModelAndView   processBadRequestByPathVariable (){
	    	ModelAndView  mv   = new ModelAndView();
	    	mv.addObject("msg", "请求出错");
	    	mv.addObject("status",FAILD);
	    	mv.setViewName("error/error");
	    	return  mv ;
	    }
	     
	    
	    
}
