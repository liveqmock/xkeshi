package com.xpos.controller.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.POSPaymentRequestParam;
import com.xkeshi.pojo.vo.param.POSPaymentResultParam;
import com.xkeshi.pojo.vo.param.POSRefundResultParam;
import com.xkeshi.pojo.vo.param.POSSignatureUploadParam;
import com.xkeshi.pojo.vo.param.POSUpdateInfoParam;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.ShopService;
import com.xpos.controller.BaseController;

/** 
 * 中行刷卡支付
 * @author chengj
 */
@Controller
@RequestMapping("/api/order")
public class APIPOSPaymentController extends BaseController{
	@Autowired
	private POSTransactionService posTransactionService;
	@Autowired
	private POSGatewayAccountService gatewayAccountService;
	@Autowired
	private ShopService shopService;
	
	/** 创建支付流水 */
	@ResponseBody
	@RequestMapping(value="/{orderNumber}/pos/transaction", method=RequestMethod.POST)
	public Result applyPOSTransactionSerial(@ModelAttribute SystemParam systemParam,
									@RequestBody POSPaymentRequestParam posPaymentParam,
									@PathVariable("orderNumber") String orderNumber){
		POSGatewayAccount account = getGatewayAccount(systemParam, posPaymentParam.getRegisterMid(), posPaymentParam.getChannel());
		if(account == null){
			return new Result(Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getName(), Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), posPaymentParam.getRegisterMid())){
			return new Result(Payment.POS_INVALID_REGISTER_ACCOUNT.getName(), Payment.POS_INVALID_REGISTER_ACCOUNT.getCode());
		}
		
		try{
			return posTransactionService.paymentForPOS(systemParam, posPaymentParam, orderNumber, account);
		}catch(Exception e){
			return new Result(Payment.POS_CREATE_SERIAL_FAILED.getName(), Payment.POS_CREATE_SERIAL_FAILED.getCode());
		}
		
	}
	
	/** 支付操作的同步回调 */
	@ResponseBody
	@RequestMapping(value="/{orderNumber}/pos/transaction/{serial}", method=RequestMethod.PUT)
	public Result processPaymentResultCallback(@ModelAttribute SystemParam systemParam,
											@RequestBody POSPaymentResultParam paymentResultParam,
											@PathVariable("orderNumber")String orderNumber,
											@PathVariable("serial")String serial){
		
		POSGatewayAccount account = getGatewayAccount(systemParam, paymentResultParam.getRegisterMid(), paymentResultParam.getChannel());
		if(account == null){
			return new Result(Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getName(), Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), paymentResultParam.getRegisterMid())){
			return new Result(Payment.POS_INVALID_REGISTER_ACCOUNT.getName(), Payment.POS_INVALID_REGISTER_ACCOUNT.getCode());
		}
		
		return posTransactionService.processPaymentResultCallback(systemParam, paymentResultParam, orderNumber, serial, account);
		
	}
	
	/** 退款操作的同步回调 */
	@ResponseBody
	@RequestMapping(value="/{orderNumber}/pos/transaction/{serial}/cancel", method=RequestMethod.POST)
	public Result processRevocationResultCallback(@ModelAttribute SystemParam systemParam,
			@RequestBody POSRefundResultParam refundResultParam,
			@PathVariable("orderNumber")String orderNumber,
			@PathVariable("serial")String serial){
		
		POSGatewayAccount account = getGatewayAccount(systemParam, refundResultParam.getRegisterMid(), refundResultParam.getChannel());
		if(account == null){
			return new Result(Refund.SELLER_ACCOUNT_NOT_MATCH.getName(), Refund.SELLER_ACCOUNT_NOT_MATCH.getCode());
		}else if(!StringUtils.equals(account.getAccount(), refundResultParam.getRegisterMid())){
			return new Result(Refund.SELLER_ACCOUNT_NOT_MATCH.getName(), Refund.SELLER_ACCOUNT_NOT_MATCH.getCode());
		}
		
		return posTransactionService.processRefundResultCallback(systemParam, refundResultParam, orderNumber, serial, account);
		
	}
	
	/** 上传客户签字 */
	@ResponseBody
	@RequestMapping(value="/{orderNumber}/pos/transaction/signature", method=RequestMethod.POST)
	public Result uploadSignature(@ModelAttribute SystemParam systemParam,
									@RequestBody POSSignatureUploadParam signatureParam,
									@PathVariable("orderNumber") String orderNumber){
		POSGatewayAccount account = getGatewayAccount(systemParam, signatureParam.getRegisterMid(), signatureParam.getChannel());
		if(account == null){
			return new Result(Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getName(), Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), signatureParam.getRegisterMid())){
			return new Result(Payment.POS_INVALID_REGISTER_ACCOUNT.getName(), Payment.POS_INVALID_REGISTER_ACCOUNT.getCode());
		}
		
		return posTransactionService.uploadSignature(systemParam, signatureParam, orderNumber, account);
	}
	
	/** 上传手机号，发送短信 */
	@ResponseBody
	@RequestMapping(value="/{orderNumber}/pos/transaction/{serial}", method=RequestMethod.POST)
	public Result updateConsumerMobile(@ModelAttribute SystemParam systemParam,
										@RequestBody POSUpdateInfoParam updateInfoParam,
										@PathVariable("orderNumber")String orderNumber,
										@PathVariable("serial")String serial){
		POSGatewayAccount account = getGatewayAccount(systemParam, updateInfoParam.getRegisterMid(), updateInfoParam.getChannel());
		if(account == null){
			return new Result(Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getName(), Payment.POS_REGISTER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), updateInfoParam.getRegisterMid())){
			return new Result(Payment.POS_INVALID_REGISTER_ACCOUNT.getName(), Payment.POS_INVALID_REGISTER_ACCOUNT.getCode());
		}
		
		return posTransactionService.updateConsumerMobile(systemParam, updateInfoParam, orderNumber, serial);
	}
	
	private POSGatewayAccount getGatewayAccount(SystemParam systemParam, String registerMid, int channel){
		//校验商户账号
		ShopInfo shopInfo = shopService.findShopInfoByShopId(systemParam.getMid());
		List<POSGatewayAccount> posGatewayAccountList = shopInfo.getPosAccountList();
		POSGatewayAccount account = null;
		for(POSGatewayAccount _account : posGatewayAccountList){
			if(_account.getType().getState() == channel && StringUtils.equals(_account.getAccount(), registerMid)){
				account = _account;
				break;
			}
		}
		return account;
	}
	
}
