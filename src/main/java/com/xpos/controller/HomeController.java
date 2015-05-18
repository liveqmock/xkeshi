package com.xpos.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.pojo.po.Account;
import com.xkeshi.service.XAccountService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xkeshi.utils.RandomValidateCode;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.exception.CodeErrorException;
import com.xpos.common.exception.AccountDeleteException;
import com.xpos.common.exception.MerchantDeleteException;
import com.xpos.common.exception.ShopDeleteException;
import com.xpos.common.service.AccountService;
import com.xpos.common.utils.FileMD5;
@Controller
public class HomeController   extends  BaseController {
	
	@Autowired
	private AccountService accountService;

    @Autowired
    private XMerchantService xMerchantService;
	
    @Autowired
    private XShopService   xShopService  ;
	
	@Autowired
	private XAccountService AccountService;
	
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String home(){
		Business business = super.getBusiness();
		String name=super.getAccount().getUsername();
		Account account = AccountService.selectByName(name);
		if (account == null) {
			return  "redirect:/404";
		}
		if(account.isIsInitPassword()){
			return "/modify_pwd/modify_pwd";
		}else{
			if(business instanceof  Merchant){   
				return "redirect:/shop/list";
			}else if ( business instanceof Shop) {
				Shop shop  = (Shop)business;
				return "redirect:/shop/"+shop.getId();
			}
			return "index";
		}
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(HttpServletRequest request , HttpServletResponse response ,Model  model  ){
		 
		HttpSession session = request.getSession();
    	Enumeration rnames=request.getHeaderNames();
    	List<String> list = new ArrayList<>();
    	for (Enumeration e = rnames ; e.hasMoreElements() ;) {
    	         String thisName=e.nextElement().toString();
    	        String thisValue=request.getHeader(thisName);
    	        list.add(thisName+"="+thisValue);
    	}
    	
    	/*Object object = request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
    	if (object != null ) {
    		model.addAttribute("status", FAILD);
    		if (object  instanceof AuthenticationServiceException ) {
    			if(((AuthenticationServiceException) object).getMessage()=="codeNull")
	    			{
    					model.addAttribute("msg", "验证码不能为空");
	    			}else {
	    				model.addAttribute("msg", "验证码错误");
	    			}
    		} else if ( object  instanceof BadCredentialsException) {
    			if(((BadCredentialsException) object).getMessage()=="usernameOrpasswordNull"){
    				model.addAttribute("msg","帐号或密码不能为空");
    			}else{
    				model.addAttribute("msg","帐号或密码错误");
    			}
    		}
    		Integer errCount=(Integer) session.getAttribute("errCount");
    		if(errCount==null){
    			errCount=1;
    		}else{
    			errCount=errCount+1;
    		}
    		request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
    		session.setAttribute("errCount", errCount);
		}*/
    	session.setAttribute("list", list);
		return "login";
	}
	
	/**登录提交验证
	 * @throws ServletException */
	@RequestMapping(value="/login" , method= RequestMethod.POST)
	public String login( Account  userForm ,  Model model ,
			              HttpServletRequest  request  , HttpServletResponse  response ,
			              ServletRequest requests ){
		HttpSession session = request.getSession();
		boolean userLoginSecurity = userLoginSecurity(userForm, model);
		if (userLoginSecurity) { 
			//认证通过
			onAuthenticationSuccess(userForm.getUsername());
			return "redirect:/";
		}
		Integer errCount=(Integer) session.getAttribute("errCount");
		if(errCount==null){
			errCount=1;
		}else{
			errCount=errCount+1;
		}
		session.setAttribute("errCount", errCount);
		model.addAttribute("status", FAILD);
		return "login";
	}
	
	/**
	 *  登录验证
	 * @param userForm   包含用户名和登录密码(密码已加密)
	 * @param redirectModel
	 * @return
	 */
	private boolean userLoginSecurity(Account userForm, Model model) throws AccountDeleteException {
		UsernamePasswordToken token;
		Subject subject = SecurityUtils.getSubject();
		try {
			userForm.setPassword(FileMD5.getFileMD5String(userForm.getPassword().getBytes()));
			token = new  UsernamePasswordToken(userForm.getUsername(), userForm.getPassword());
			token.setRememberMe(true);
			subject.login(token);
			if (subject.isAuthenticated()) 
				 return true ;
        } catch (UnknownAccountException uae) {
            model.addAttribute("msg", "请输入正确的用户名或密码");
        } catch (IncorrectCredentialsException ice) {
        	model.addAttribute("msg", "请输入正确的用户名或密码");
        } catch (LockedAccountException lae) {
        	model.addAttribute("msg", "用户被锁定,请联系客服专员");
        }catch (AccountDeleteException e) {
        	model.addAttribute("msg", "用户已经被删除");
		}catch (MerchantDeleteException e) {
        	model.addAttribute("msg", "集团已经被删除");
		}catch (ShopDeleteException e) {
        	model.addAttribute("msg", "商户已经被删除");
		}catch(CodeErrorException e){
			model.addAttribute("msg","验证码错误");
		}catch (AuthenticationException e) {
        	model.addAttribute("msg", "请输入正确的用户名或密码");
        } catch (Exception e) {
        	model.addAttribute("msg", "验证失败");
		}
		return false;
	}
	
	 
	/** 注销 */
	@RequestMapping(value="/logout" ,method = RequestMethod.GET)
	public String mLogout( HttpServletResponse  response ,  Model  model){
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			subject.logout();
		}
		return "redirect:/login";
	}
	
	@RequestMapping(value="/image/code",method=RequestMethod.GET)
	public void getLoginCode(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
            randomValidateCode.getRandcode(request, response);//输出图片方法
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@RequestMapping(value="/setpwd",method=RequestMethod.PUT)
	public String setpwd(HttpServletRequest request,RedirectAttributes  redirect, @RequestParam("pwd") String password) throws IOException{
		String name=super.getAccount().getUsername();
		if (StringUtils.isNotBlank(name)  && StringUtils.isNotBlank(password)) {
			Account account=AccountService.selectByName(name);
			account.setPassword(FileMD5.getFileMD5String(password.getBytes()));
			account.setIsInitPassword(false);
			if(AccountService.updateAccount(account))
				return "/modify_pwd/modify_pwd_second";
		}
		redirect.addFlashAttribute("status",FAILD);
		redirect.addFlashAttribute("msg","修改失败");
		return "redirect:/";
	}
	
	private void onAuthenticationSuccess(String username){
		com.xpos.common.entity.security.Account account = accountService.findAccountByUsername(username);
		if(account != null){
			storeSession(Account.SESSION_KEY, account);
			Business business = accountService.findBusinessByAccount(account);
			if(business != null){
				storeSession(Business.SESSION_KEY, business);
				if (business.getSelfBusinessType().equals(Business.BusinessType.MERCHANT)){
					boolean central = xMerchantService.checkMemberCentralManagementByMerchantId(business.getSelfBusinessId());
					storeSession(Business.BUSINESS_CENTRAL, central);
					storeSession(Business.BUSINESS_TYPE, business.getSelfBusinessType().toString());
					//判断集团下的商户是否存在交接班
					storeSession(Shop.SHOP_ENABLE_SHIFT, xMerchantService.presenceEnableShiftShopByMerchantId(business.getSelfBusinessId()));
				} else if (business.getSelfBusinessType().equals(Business.BusinessType.SHOP)) {
					//检查是否是被统一管理的商户且未被适用
					boolean available = xMerchantService.checkMemberCentralManagementPrepaidCardAvailableByShopId(business.getSelfBusinessId());
					storeSession(Business.BUSINESS_CENTRAL_AVAILABLE, available);
		            boolean central = xMerchantService.checkMemberCentralManagementByShopId(business.getSelfBusinessId());
		            storeSession(Business.BUSINESS_CENTRAL, central);
		            storeSession(Business.BUSINESS_TYPE, business.getSelfBusinessType().toString());
		            storeSession(Shop.SHOP_ENABLE_SHIFT, xShopService.findShopByShopId(business.getSelfBusinessId()).getEnableShift());
		        }
			}
		}
	}
	
	
}

