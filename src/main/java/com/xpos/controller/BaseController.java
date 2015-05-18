package com.xpos.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.security.Account;
import com.xpos.common.entity.security.Role;


public abstract class BaseController {
	
	protected Log logger = LogFactory.getLog(getClass());

	
	
	public final static String CITY_CODE_SESSION_KEY = "_city_code_";
	
	public  final static String SUCCESS = "success";
	public  final static String FAILD = "faild";
	public  final static String STATUS_SUCCESS = "success";
	public  final static String STATUS_FAILD = "faild";
		
	public void storeSession(String key, Object value){
		SecurityUtils.getSubject().getSession().setAttribute(key, value); 
	}
	
	public String getCityCode(){
		return "330100";
		//return (String)getSession().getAttribute(CITY_CODE_SESSION_KEY);
	}
	
	public Business getBusiness(){
		Business business = (Business)SecurityUtils.getSubject().getSession().getAttribute(Business.SESSION_KEY);		
		return business;
	}

    public  Long getBusinessId() {
        Business business = getBusiness();
        if (business == null) {
            return null;
        }
        return business.getSelfBusinessId();
    }

    public String getBusinessType() {
        Business business = getBusiness();
        if (business == null) {
            return null;
        }
        return business.getSelfBusinessType().toString();
    }
	
	public Account getAccount(){
		//Account account = (Account)session.getAttribute(Account.SESSION_KEY);	
		Account account = (Account)SecurityUtils.getSubject().getSession().getAttribute(Account.SESSION_KEY);
		return account;
	}

    public boolean checkBusinessCentral(){
        return (boolean) SecurityUtils.getSubject().getSession().getAttribute(Business.BUSINESS_CENTRAL);
    }
	
	@InitBinder
	protected void dateBinder(WebDataBinder binder) {
	    //The date format to parse or output your dates
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    //Create a new CustomDateEditor
	    CustomDateEditor editor = new CustomDateEditor(dateFormat, true);
	    //Register it as custom editor for the Date type
	    binder.registerCustomEditor(Date.class, editor);
	}
	
	
	/**
     * 检查单一角色
     * Check role.
     *
     * @param role the role
     * @return the boolean
     */
    protected boolean checkRole(Role role){
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser.hasRole(role.toString());
    }
    

	/**
	 * 检查当前用户是否具有任一角色
	 * @param roleNameArray 权限名称数组
	 * @return
	 */
	protected boolean checkRoleAny(Role... roleNameArray){
		Subject subject = SecurityUtils.getSubject();
		for (Role role : roleNameArray) {
			if (subject.hasRole(role.toString())) {
				return true;
			}
		}
		return false; 
	}

	/**
	 * 检查当前用户是否具有所有角色
	 * @param roleNameArray 权限名称数组
	 * @return
	 */
	protected boolean checkRoleAll(Role...  roleNameArray){
		Subject subject = SecurityUtils.getSubject();
        if (ArrayUtils.isEmpty(roleNameArray)) {
            return false;
        }
        for (Role role : roleNameArray) {
			if (!subject.hasRole(role.toString())) {
				return false;
			}
		}
		return true;
	}


	
}
