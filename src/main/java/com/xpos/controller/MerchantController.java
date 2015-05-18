package com.xpos.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xkeshi.endpoint.GrantResult;
import com.xkeshi.endpoint.JSONResponse;
import com.xkeshi.interceptor.form.AvoidDuplicateSubmission;
import com.xkeshi.utils.EncryptionUtil;
import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.example.MerchantExample;
import com.xpos.common.entity.security.Account;
import com.xpos.common.searcher.MerchantSearcher;
import com.xpos.common.service.AccountService;
import com.xpos.common.service.MerchantService;
import com.xpos.common.service.PictureService;
import com.xpos.common.service.ShopService;
import com.xpos.common.utils.Pager;

@Controller
@RequestMapping("merchant")
public class MerchantController extends BaseController{

	@Resource
	private ShopService shopService;

	@Resource
	private MerchantService  merchantService  ;
	
	@Resource
	private AccountService  accountService ;
	
	@Resource
	private PictureService pictureService ;
	
	@AvoidDuplicateSubmission(addToken = true)
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String findMerchants( Pager<Merchant>  pager , MerchantSearcher  merchantSearcher, Model model){
		pager = merchantService.findMerchants((MerchantExample) merchantSearcher.getExample(), pager);
		List<Account> accounts = accountService.findAccountByBusiness(new Merchant());
		model.addAttribute("accounts", accounts);
		model.addAttribute("pager", pager);
		model.addAttribute("searcher", merchantSearcher);
		return "merchant/merchant_list";
	}
     
	@AvoidDuplicateSubmission(removeToken = true,errorRedirectURL="/merchant/list")
	@RequestMapping(value = "/save" ,method =  RequestMethod.POST)
	public String saveMerchant ( Merchant  merchant , Account  account ,MultipartFile avatarFile, RedirectAttributes redirectAttributes){
		redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "创建集团失败");
		
		if(!EncryptionUtil.isStrongPassword(account.getPassword())){
			redirectAttributes.addFlashAttribute("msg", "密码长度为6-32位字符， 须同时包含字母和数字");
			return "redirect:/merchant/list";
		}
		
		if(avatarFile !=null && !avatarFile.isEmpty()){
			Picture avatar = pictureService.getPictureFromMultipartFile(avatarFile);
			merchant.setAvatar(avatar);
		}

		try {
			if (merchantService.saveMerchant(merchant ,account)) {
				redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				redirectAttributes.addFlashAttribute("msg", "创建集团成功");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/merchant/list";
	}
	
	@AvoidDuplicateSubmission(removeToken = true,errorRedirectURL="/merchant/list")
	@RequestMapping(value = "/{merchantId}/update"  ,method = RequestMethod.POST)
	public String updateMerchant (@PathVariable("merchantId") Long merchantId  ,  Merchant merchant  ,MultipartFile avatarFile, com.xkeshi.pojo.po.Account account  , RedirectAttributes redirectAttributes){
		redirectAttributes.addFlashAttribute("stauts", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "修改失败");
		merchant.setId(merchantId);
		if(avatarFile !=null && !avatarFile.isEmpty()){
			Picture avatar = pictureService.getPictureFromMultipartFile(avatarFile);
			merchant.setAvatar(avatar);
		}
		try {
			account.setIsInitPassword(true);
			if(!EncryptionUtil.isStrongPassword(account.getPassword())){
				redirectAttributes.addFlashAttribute("status", STATUS_FAILD);
				 redirectAttributes.addFlashAttribute("msg", "修改失败,密码长度为6-32位字符， 须同时包含字母和数字");
				 return "redirect:/merchant/list";
			 }else  if(merchantService.updateMerchant(merchant, account)){
				  redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
				  redirectAttributes.addFlashAttribute("msg", "修改成功");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  "redirect:/merchant/list";
	}
	
	@RequestMapping(value = "/publish/{visible}"  ,method = RequestMethod.PUT)
	public String updatePublishMerchant (@PathVariable("visible") Boolean visible  , Long[] merchantIds,  RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("stauts", STATUS_FAILD);
		redirectAttributes.addFlashAttribute("msg", "修改失败");
		if(merchantService.batchSetVisible(merchantIds, visible)){
			redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			redirectAttributes.addFlashAttribute("msg", "修改成功");
		}
		return  "redirect:/merchant/list";
	}
	
	@RequestMapping(value = "/delete" ,method = RequestMethod.DELETE)
	public String deleteMerchant( Long[] merchantIds ,    RedirectAttributes  redirectAttributes){
		if (merchantService.deleteMerchants(merchantIds)) {
			 redirectAttributes.addFlashAttribute("status", STATUS_SUCCESS);
			 redirectAttributes.addFlashAttribute("msg", "删除成功");
		}else{
			 redirectAttributes.addFlashAttribute("stauts", STATUS_FAILD);
			 redirectAttributes.addFlashAttribute("msg", "删除失败");
		}
		return  "redirect:/merchant/list";
	}
	
	@RequestMapping(value = "/account/{userName}" , method  = RequestMethod.GET )
	public HttpEntity<String> checkUserName( @PathVariable("userName") String userName , Model  model){
		Account account = accountService.findAccountByUsername(userName);
		JSONResponse  jsonResponse = new JSONResponse(account == null ? new GrantResult(SUCCESS, "账户名有效") : new GrantResult(FAILD, "账户名已存在"));;
		return new ResponseEntity<String>( jsonResponse.getBody(), jsonResponse.getHttpStatus()) ;
	}
}
