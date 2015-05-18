package com.xpos.common.service;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Refund.RefundAccountType;
import com.xpos.common.entity.Refund.RefundStatus;

@Service
public class TenpayServiceImpl implements TenpayService{
	private final static Logger logger = LoggerFactory.getLogger(TenpayServiceImpl.class);

	@Resource
	private CouponService couponService;
	@Resource
	private ExternalHttpInvokeService externalHttpInvokeService;
	@Resource
	private RefundService refundService;
	
	@Override
	public Refund queryRefundDetail(Refund refund) {
		//调用接口查询订单是否已退款成功（例如已发起过退款请求，但是正在处理中或接收响应失败）
		Map<String, String> responseMap = externalHttpInvokeService.queryTenpayRefundDetail(refund);
		
		if(responseMap != null && responseMap.size() > 0){
			if(StringUtils.equalsIgnoreCase(responseMap.get("retcode"), "0")){ //查询到退款记录
				int refundState = Integer.valueOf(responseMap.get("refund_state_0"));
				int refundChannel = Integer.valueOf(responseMap.get("refund_channel_0"));
				String receiverAccount = responseMap.get("recv_user_id_0");
				switch(refundState){
					case 4: case 10: //退款成功
						refund.setStatus(RefundStatus.AUTO_SUCCESS);
						refund.setSerial(responseMap.get("refund_id_0"));
						if(refundChannel == 0){
							refund.setType(RefundAccountType.TENPAY);
							refund.setAccount(receiverAccount); //接收退款的财付通帐号
						}else if(refundChannel == 1){
							refund.setType(RefundAccountType.DEBIT_CARD);
						}
						refund.setRemark("自动退款成功");
						refundService.updateRefundByCode(refund);
						refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_SUCCESS);
						return refund;
					case 8: case 9 : case 11: //退款处理中
						if(RefundStatus.AUTO_FAILED.equals(refund.getStatus()) || RefundStatus.ACCEPTED.equals(refund.getStatus())){
							refund.setStatus(RefundStatus.AUTO_EXECUTE);
							refund.setSerial(responseMap.get("refund_id_0"));
							if(refundChannel == 0){
								refund.setType(RefundAccountType.TENPAY);
								refund.setAccount(receiverAccount); //接收退款的财付通帐号
							}else if(refundChannel == 1){
								refund.setType(RefundAccountType.DEBIT_CARD);
							}
							refund.setRemark("自动退款处理中");
							refundService.updateRefundByCode(refund);
						}
						return refund;
					case 1: case 2: //未确定，需要商户原退款单号重新发起
					case 3: case 5: case 6: //退款失败
					case 7: //转入代发，客户银行卡异常，需人工干预
						refund.setStatus(RefundStatus.AUTO_FAILED);
						refund.setSerial(responseMap.get("refund_id_0"));
						if(refundChannel == 0){
							refund.setType(RefundAccountType.TENPAY);
							refund.setAccount(receiverAccount); //接收退款的财付通帐号
						}else if(refundChannel == 1){
							refund.setType(RefundAccountType.DEBIT_CARD);
						}
						String remark = null;
						if(refundState == 1 || refundState == 2){
							remark = "自动退款失败(原因：财付通退款失败，需重新发起退款操作)";
						}else if(refundState == 7){
							remark = "自动退款失败(原因：用户银行卡异常，无法退款。请尝试人工退款)";
						}else{
							remark = "自动退款失败";
						}
						refund.setRemark(remark);
						refundService.updateRefundByCode(refund);
						refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_FAIL);
						return refund;
				}
			} else if(StringUtils.equalsIgnoreCase(responseMap.get("retcode"), "88222014")){ //该笔订单未退款
				//nothing to do here
			} else {
				logger.error("财付通退款详情接口查询失败，retmsg：【"+responseMap.get("retmsg")+"】");
			}
		}
		
		return refund;
	}

	@Override
	public boolean refund(Refund refund) {
		//调用财付通退款接口
		Map<String, String> responseMap = externalHttpInvokeService.executeTenpayRefund(refund);
		
		if(responseMap != null && responseMap.size() > 0){
			if(StringUtils.equalsIgnoreCase(responseMap.get("retcode"), "0")){ //退款操作正常返回
				int refundState = Integer.valueOf(responseMap.get("refund_status"));
				int refundChannel = Integer.valueOf(responseMap.get("refund_channel"));
				String receiverAccount = responseMap.get("recv_user_id");
				String serial = responseMap.get("refund_id");
				switch(refundState){
					case 4: case 10: //退款成功
						refund.setStatus(RefundStatus.AUTO_SUCCESS);
						refund.setSerial(serial);
						if(refundChannel == 0){
							refund.setType(RefundAccountType.TENPAY);
							refund.setAccount(receiverAccount); //接收退款的财付通帐号
						}else if(refundChannel == 1){
							refund.setType(RefundAccountType.DEBIT_CARD);
						}
						refund.setRemark("自动退款成功");
						refundService.updateRefundByCode(refund);
						refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_SUCCESS);
						return true;
					case 8: case 9 : case 11: //退款处理中
						if(RefundStatus.AUTO_FAILED.equals(refund.getStatus()) || RefundStatus.ACCEPTED.equals(refund.getStatus())){
							refund.setStatus(RefundStatus.AUTO_EXECUTE);
							refund.setSerial(serial);
							if(refundChannel == 0){
								refund.setType(RefundAccountType.TENPAY);
								refund.setAccount(receiverAccount); //接收退款的财付通帐号
							}else if(refundChannel == 1){
								refund.setType(RefundAccountType.DEBIT_CARD);
							}
							refund.setRemark("自动退款处理中");
							refundService.updateRefundByCode(refund);
						}
						return false;
					case 1: case 2: //未确定，需要商户原退款单号重新发起
					case 3: case 5: case 6: //退款失败
					case 7: //转入代发，客户银行卡异常，需人工干预
						refund.setStatus(RefundStatus.AUTO_FAILED);
						refund.setSerial(serial);
						if(refundChannel == 0){
							refund.setType(RefundAccountType.TENPAY);
							refund.setAccount(receiverAccount); //接收退款的财付通帐号
						}else if(refundChannel == 1){
							refund.setType(RefundAccountType.DEBIT_CARD);
						}
						String remark = null;
						if(refundState == 1 || refundState == 2){
							remark = "自动退款失败(原因：财付通退款失败，需重新发起退款操作)";
						}else if(refundState == 7){
							remark = "自动退款失败(原因：用户银行卡异常，无法退款。请尝试人工退款)";
						}else{
							remark = "自动退款失败";
						}
						refund.setRemark(remark);
						refundService.updateRefundByCode(refund);
						refundService.updateRelatedCoupon(refund, CouponStatus.REFUND_FAIL);
						return false;
				}
			} else {
				logger.error("财付通退款操作接口执行失败，retmsg：【"+responseMap.get("retmsg")+"】");
			}
		}
		return false;
	}
	
	
}
