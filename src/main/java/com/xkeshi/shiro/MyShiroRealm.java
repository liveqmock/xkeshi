package com.xkeshi.shiro;


import javax.annotation.Resource;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;

import com.xkeshi.pojo.po.Account;
import com.xkeshi.pojo.po.Merchant;
import com.xkeshi.pojo.po.Shop;
import com.xkeshi.service.XAccountService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopService;
import com.xpos.common.exception.CodeErrorException;
import com.xpos.common.exception.AccountDeleteException;
import com.xpos.common.exception.MerchantDeleteException;
import com.xpos.common.exception.ShopDeleteException;



 
public class MyShiroRealm extends JdbcRealm {


	@Resource
	private XAccountService accountService;
	
	@Resource
	private XShopService shopService;
	
	@Resource
	private XMerchantService  xMerchantService;
	
	@Resource
	private HttpSession session;
	
	@Resource
	private HttpServletRequest request;
  
   /**
    * 认证回调函数,登录时调用.用来检验登录的手机号和密码
    */
   @Override  
   protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken){  
	   Integer errCount=(Integer) session.getAttribute("errCount");
	   if(errCount!=null && errCount>=3){
	       	String _code=(String) session.getAttribute("image");
	       	String code=(String) request.getParameter("j_code");
	       	if(StringUtils.isBlank(code))
	       		throw new CodeErrorException("验证码不能为空");
	       	if(!(StringUtils.equalsIgnoreCase(code, _code)))
	       		throw new CodeErrorException("验证码错误");
       }
	   
	   UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;    
	   String username = token.getUsername();
	   Account account = accountService.selectByName(username);
	   if (account != null) {
		   if(StringUtils.equalsIgnoreCase(account.getBusinessType(), "SHOP")){
			   Shop shop = shopService.selectShopByShopId(account.getBusinessId());
			   if (shop.getDeleted()) {
				   throw new ShopDeleteException();
			   }else if(shop.getMerchantId() != null){
				   Merchant merchant = xMerchantService.selectMerchantByMerchantId(shop.getMerchantId());
				   if (merchant.getDeleted()) {
					   throw new MerchantDeleteException();
				   }
			   }
		   } else  if(StringUtils.equalsIgnoreCase(account.getBusinessType(), "MERCHANT")){
			   Merchant merchant = xMerchantService.selectMerchantByMerchantId(account.getBusinessId());
			   if (merchant.getDeleted()) {
				   throw new MerchantDeleteException();
			   }
		   }
	   }
	   if(account.isDeleted()){
		   throw new AccountDeleteException();
	   }
	   return new SimpleAuthenticationInfo(account.getUsername(), account.getPassword(),getName());   
   }  
}  