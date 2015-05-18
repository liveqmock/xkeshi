package com.xpos.controller.api;

import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.entity.QRCodePaymentNotify;
import com.xkeshi.common.em.Payment;
import com.xkeshi.common.em.Refund;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodeCancelParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodePaymentParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodeQueryParam;
import com.xkeshi.pojo.vo.param.AlipayQRCodeRefundParam;
import com.xkeshi.service.payment.AlipayQRCodePaymentService;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.service.POSGatewayAccountService;
import com.xpos.common.service.ShopService;
import com.xpos.controller.BaseController;

/** 
 * 支付宝线下扫码支付
 * @author chengj
 */
@Controller
@RequestMapping("/api")
public class APIAlipayQRCodeController extends BaseController{
	private Logger logger = LoggerFactory.getLogger(APIAlipayQRCodeController.class);
	
	@Autowired
	private AlipayQRCodePaymentService alipayQRCodePaymentService;
	@Autowired
	private POSGatewayAccountService gatewayAccountService;
	@Autowired
	private ShopService shopService;
	
	
	/** 统一下单并支付 */
	@ResponseBody
	@RequestMapping(value="order/{orderNumber}/alipay/transaction", method=RequestMethod.POST)
	public Result alipayQRCodePayment(@ModelAttribute SystemParam systemParam,
									@RequestBody AlipayQRCodePaymentParam paymentRequestParam,
									@PathVariable("orderNumber") String orderNumber){
		//检查账户信息
		POSGatewayAccount account = getSellerAlipayAccountByShopId(systemParam.getMid());
		if(account == null){
			return new Result(Payment.ALIPAY_SELLER_ACCOUNT_NOT_FOUND.getName(), Payment.ALIPAY_SELLER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), paymentRequestParam.getSellerAccount())){
			return new Result(Payment.ALIPAY_INVALID_SELLER_ACCOUNT.getName(), Payment.ALIPAY_INVALID_SELLER_ACCOUNT.getCode());
		}
		
		//调用支付宝SDK接口进行下单&支付
		try{
			return alipayQRCodePaymentService.paymentForAlipayQRCode(paymentRequestParam, systemParam, orderNumber, account);
		}catch(Exception e){
			return new Result(Payment.ALIPAY_CREATE_SERIAL_FAILED.getName(), Payment.ALIPAY_CREATE_SERIAL_FAILED.getCode());
		}
	}

	/** 线下扫码支付异步回调(爱客仕订单类型) */
	@ResponseBody
	@RequestMapping(value="alipay/payment/qrcode_notify", method=RequestMethod.POST)
	public String processXPOSAlipayQRCodePaymentNotify(QRCodePaymentNotify notify,
													@RequestBody String body) {
		if(StringUtils.isBlank(body)){
			return "fail";
		}
		
		//logging
		try {
			String decodedBody = URLDecoder.decode(body, "UTF-8");
			logger.info("支付宝线下扫码支付【统一下单并支付】接口异步回调内容：【" + decodedBody + "】");
			boolean result = alipayQRCodePaymentService.processSubmitAndPayNotify(notify, decodedBody);
			return result ? "success" : "fail";
		} catch (Exception e) {
			logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调异常！", e);
		}
		
		return "fail";
	}
	
	/** 支付结果查询 */
	@ResponseBody
	@RequestMapping(value="order/{orderNumber}/alipay/transaction", method=RequestMethod.GET)
	public Result queryAlipayQRCodePayment(@ModelAttribute SystemParam systemParam,
									@ModelAttribute AlipayQRCodeQueryParam queryParam,
									@PathVariable("orderNumber") String orderNumber){
		POSGatewayAccount account = getSellerAlipayAccountByShopId(systemParam.getMid());
		if(account == null){
			return new Result(Payment.ALIPAY_SELLER_ACCOUNT_NOT_FOUND.getName(), Payment.ALIPAY_SELLER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), queryParam.getSellerAccount())){
			return new Result(Payment.ALIPAY_INVALID_SELLER_ACCOUNT.getName(), Payment.ALIPAY_INVALID_SELLER_ACCOUNT.getCode());
		}
		
		//调用支付宝SDK接口进行查询
		return alipayQRCodePaymentService.queryPaymentResult(queryParam, systemParam, orderNumber, account);
	}
	
	/** 撤销支付流水 */
	@ResponseBody
	@RequestMapping(value="order/{orderNumber}/alipay/transaction/cancel", method=RequestMethod.POST)
	public Result queryAlipayQRCodePayment(@ModelAttribute SystemParam systemParam,
									@RequestBody AlipayQRCodeCancelParam cancelParam,
									@PathVariable("orderNumber") String orderNumber){
		POSGatewayAccount account = getSellerAlipayAccountByShopId(systemParam.getMid());
		if(account == null){
			return new Result(Refund.SELLER_ACCOUNT_NOT_MATCH.getName(), Refund.SELLER_ACCOUNT_NOT_MATCH.getCode());
		}else if(!StringUtils.equals(account.getAccount(), cancelParam.getSellerAccount())){
			return new Result(Refund.SELLER_ACCOUNT_NOT_MATCH.getName(), Refund.SELLER_ACCOUNT_NOT_MATCH.getCode());
		}
		
		//调用支付宝SDK接口撤销支付
		return alipayQRCodePaymentService.cancelPayment(cancelParam, systemParam, orderNumber, account);
	}
	
	/** 支付流水退款 */
	@ResponseBody
	@RequestMapping(value="order/{orderNumber}/alipay/transaction/refund", method=RequestMethod.POST)
	public Result queryAlipayQRCodePayment(@ModelAttribute SystemParam systemParam,
			@RequestBody AlipayQRCodeRefundParam refundParam,
			@PathVariable("orderNumber") String orderNumber){
		POSGatewayAccount account = getSellerAlipayAccountByShopId(systemParam.getMid());
		if(account == null){
			return new Result(Payment.ALIPAY_SELLER_ACCOUNT_NOT_FOUND.getName(), Payment.ALIPAY_SELLER_ACCOUNT_NOT_FOUND.getCode());
		}else if(!StringUtils.equals(account.getAccount(), refundParam.getSellerAccount())){
			return new Result(Payment.ALIPAY_INVALID_SELLER_ACCOUNT.getName(), Payment.ALIPAY_INVALID_SELLER_ACCOUNT.getCode());
		}
		
		//调用支付宝SDK接口退款
		return alipayQRCodePaymentService.refundPayment(refundParam, systemParam, orderNumber, account);
	}
	
	private POSGatewayAccount getSellerAlipayAccountByShopId(long shopId){
		//校验卖家账户
		ShopInfo shopInfo = shopService.findShopInfoByShopId(shopId);
		List<POSGatewayAccount> posGatewayAccountList = shopInfo.getPosAccountList();
		POSGatewayAccount account = null;
		for(POSGatewayAccount _account : posGatewayAccountList){
			if(POSGatewayAccountType.ALIPAY.equals(_account.getType())){
				account = _account;
				break;
			}
		}
		return account;
	}
	
}
