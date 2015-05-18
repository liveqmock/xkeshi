package com.xpos.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;

/**
 * 
 * @author xk
 * 错误控制
 */
@Controller
@RequestMapping("/error")
public class ErrorController extends BaseController {
	
	/** html 重复提交表单 */
	@RequestMapping(value="/submission" )
	public String errorSubmission (Model  model,  String redirectURL){
		model.addAttribute("status", FAILD);
		model.addAttribute("msg", "请勿重复提交");
		model.addAttribute("redirectURL", redirectURL);
		return "error/errorSubmission";
	}
	
	/** ajax重复提交表单 */
	@RequestMapping(value="/ajax/submission")
	public HttpEntity<String> errorAjaxSubmission (Model  model,  String redirectURL){
		  GrantResult grantResult = new GrantResult(FAILD, "请勿重复提交");
		  Map<String, String >  result  =  new HashMap<String, String>();
		  result.put("redirectURL",redirectURL);
		  grantResult.setResult(result);
		  JSONResponse jsonResponse = new JSONResponse(grantResult);
		 return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	/**集团相关的错误*/
	@RequestMapping(value = "/business")
	public String  errorBusiness(){
		return "error/error";
	}
	
	/** ajax json 未登录错误 */
	@RequestMapping(value="/ajax/json/nologin")
	public HttpEntity<String> errorAjaxJsonNoLogin (Model  model){
		JSONResponse jsonResponse = new JSONResponse(new GrantResult(FAILD, "登录失效，请重新登录"));
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
	
	
}
