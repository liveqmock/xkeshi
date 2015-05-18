package com.xkeshi.interceptor.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 *  防止重复提交注解，方法级
 *  步骤一:  为响应防止重复提交的页面之前,在controller方法级添加一个注解(@AvoidDuplicateSubmission(addToken = true))，
 *           此时拦截器会在Session中保存一个token，再通过controller返回给页面 
 *  步骤二:  为防止提交的页面里加入<form method='POST'><input type="hidden" name="submissionToken" value="${submissionToken}"></form>
 *  步骤三:  为防止重复提交的方法级上添加一个注解(@AvoidDeplicateSubmission(removeToken = true)),此时会在拦截器中验证是否重复提交
 *  
 *  备注：     
 *         如果为防止重复提交的方法级上添加一个注解(@AvoidDeplicateSubmission(removeAndAddToken = true)),
 *         先验证是否重复提交，验证通过会重新生成一个新的token存在session中给Controller调用。
 *         session中key为‘submissionToken’。
 *  扩展：
 *    A. 出错重定向地址: 
 *       a. errorRedirectURL = ""   //默认
 *       b. errorRedirectURL = "/shop/list"   //固定重定向地址
 *       c. errorRedirectURL = "/shop/{pathVarName}" @PathVariable("pathVarName") //匹配重定向地址
 *       d. <form method='POST'><input type="hidden" name="redirectURL" value="/shop/list"></form>  (不建议)
 * @author xk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AvoidDuplicateSubmission {
  
	/**  注解, 先移除token, 再添加一个新token */
	boolean removeAndAddToken() default  false ;
	
	/**  注解，添加token  */
	boolean addToken() default  false ;
	
    /**  注解，移除token  */
	boolean removeToken()  default   false;
	
	/** 注解，重复提交时，重定向地址*/
	public String  errorRedirectURL() default "";
	
}
