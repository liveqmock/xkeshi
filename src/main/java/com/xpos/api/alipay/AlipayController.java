package com.xpos.api.alipay;

import com.alipay.entity.QRCodePaymentNotify;
import com.alipay.service.AlipayQRCodePaymentService;
import com.xpos.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.net.URLDecoder;

@Controller
@RequestMapping("api")
public class AlipayController extends BaseController{
	
	@Resource
	private AlipayQRCodePaymentService alipayQRCodePaymentOldService;
	
	/** 支付宝线下扫码支付异步回调 */
	@ResponseBody
	@RequestMapping(value="/alipay/qrcode/notify", method=RequestMethod.POST)
	public String processAlipayQRCodePaymentNotify(QRCodePaymentNotify notify, @RequestBody String body) {
		if(StringUtils.isBlank(body)){
			return "success";
		}
		
		//logging
		try {
			String decodedBody = URLDecoder.decode(body, "UTF-8");
			logger.info("支付宝线下扫码支付【统一下单并支付】接口异步回调内容：【" + decodedBody + "】");
			boolean result = alipayQRCodePaymentOldService.processSubmitAndPayNotify(notify, decodedBody);
			return result ? "success" : "fail";
		} catch (Exception e) {
			logger.error("支付宝线下扫码支付【统一下单并支付】接口异步回调异常！", e);
		}
		
		return "fail";
	}
	
}
