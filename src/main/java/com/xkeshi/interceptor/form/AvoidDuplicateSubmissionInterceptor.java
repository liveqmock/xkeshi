package com.xkeshi.interceptor.form;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/***
 * @author xk
 * 防止重复提交拦截器
 */
public class AvoidDuplicateSubmissionInterceptor extends HandlerInterceptorAdapter {

	public static final  String SUBMISSION_TOKEN   = "submissionToken";
	
    private static final Logger LOG = Logger.getLogger(AvoidDuplicateSubmissionInterceptor.class);
   
    /**获取重复提交，重定向出错地址*/
    @SuppressWarnings("unchecked")
    private String resolveArgument(HttpServletRequest request , HandlerMethod handlerMethod  ,AvoidDuplicateSubmission annotation) {
    	//方式一：获取请求重定向地址
    	String  errorRedirectURL = request.getParameter("redirectURL");
    	if (StringUtils.isBlank(errorRedirectURL)) {
    		//方式二：获取注解重定向地址
    		errorRedirectURL = annotation.errorRedirectURL() ;
    		MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            for (MethodParameter methodParameter : methodParameters) {
            	Annotation[] annotations = methodParameter.getParameterAnnotations();
            	for (Annotation paramAnn : annotations) {
            		 if (PathVariable.class.isInstance(paramAnn)) { 
						Map<String, String> uriTemplateVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
      					if (uriTemplateVariables == null) 
      							throw new IllegalStateException( "Could not find @PathVariable  value in @RequestMapping");
      					for (Entry<String, String> pathVariable : uriTemplateVariables.entrySet()) {
      						//通配替换注解重定向地址
      						errorRedirectURL = StringUtils.replace(errorRedirectURL,"{"+ pathVariable.getKey()+"}", pathVariable.getValue());
      					}
      				 }
				}
			}
    	}
    	return  errorRedirectURL;
    }
    
	@Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            AvoidDuplicateSubmission annotation = method.getAnnotation(AvoidDuplicateSubmission.class);
            if (annotation != null) {
            	//销毁token , 添加新token
            	boolean needRemoveAndSaveToken = annotation.removeAndAddToken();
            	if (needRemoveAndSaveToken) {
            		 if (isRepeatSubmit(request)) {
                     	 LOG.warn("please don't repeat submit!");
                     	 //获取error重定向地址
                    	 String  errorRedirectURL = resolveArgument(request, handlerMethod, annotation);
                     	 //判断请求头是否是ajax,ajax请求的返回json数据
                   	     response.sendRedirect(StringUtils.isNotBlank(request.getHeader("X-Requested-With")) ? "/error/ajax/submission?redirectURL="+errorRedirectURL :"/error/submission?redirectURL="+errorRedirectURL);
                    	 request.getSession(false).removeAttribute(SUBMISSION_TOKEN);
                         return false;
                     }
                    request.getSession(false).removeAttribute(SUBMISSION_TOKEN);
                 	String generateToken = TokenProcessor.getInstance().generateToken(request);
                	request.getSession(false).setAttribute(SUBMISSION_TOKEN, generateToken);
				}
            	//销毁token
                boolean needRemoveSession = annotation.removeToken();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                    	LOG.warn("please don't repeat submit!");
                    	//获取error重定向地址
                    	String  errorRedirectURL = resolveArgument(request, handlerMethod, annotation);
                    	//判断请求头是否是ajax,ajax请求的返回json数据
                    	response.sendRedirect(StringUtils.isNotBlank(request.getHeader("X-Requested-With")) ? "/error/ajax/submission?redirectURL="+errorRedirectURL :"/error/submission?redirectURL="+errorRedirectURL);
                    	return false;
                    }
                    request.getSession(false).removeAttribute(SUBMISSION_TOKEN);
                }
                //添加token
                boolean needSaveSession = annotation.addToken();
                if (needSaveSession) {
                	String generateToken = TokenProcessor.getInstance().generateToken(request);
                	request.getSession(false).setAttribute(SUBMISSION_TOKEN, generateToken);
                }
            }
        return true;
    }
    
   /** 验证后，移除token */
    private boolean isRepeatSubmit(HttpServletRequest request) {
    	String clinetToken = request.getParameter(SUBMISSION_TOKEN);
    	HttpSession session = request.getSession(false);
    	if (clinetToken == null || session == null) 
    		return true;
        String serverToken = (String) session.getAttribute(SUBMISSION_TOKEN);
        if (serverToken == null) 
            return true;
        if (!serverToken.equals(clinetToken)) 
            return true;
        return false;
    }
    
     
}
