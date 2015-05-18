package com.xpos.common.entity.pos;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;

/**
 * 
 * POS刷卡流水(可与银行对账)
 * @author Johnny
 *
 */
public class POSTransaction extends BaseEntity implements EncryptId{

	private static final long serialVersionUID = -2120018528100675414L;
	
	@Column(name="order_number")
	private String orderNumber; //爱客仕内部订单号
	@Column(name="prepaid_card_charge_order_code")
	private String prepaidCardChargeOrderCode; //爱客仕预付卡充值订单号
	@Column(name="third_order_code")
	private String thirdOrderCode; //外部系统订单号
	@Column
	private Long businessId;
	@Column
	private BusinessType businessType;
	@Column
	private String mobile;
	@Column
	private BigDecimal sum; //订单金额
	@Column
	private String code; //内部订单Id
	@Column
	private String serial;//外部系统订单流水
	@Column
	private POSTransactionStatus status; //订单状态
	@Column
	private POSTransactionType type; //订单类型
	@Column
	private String operator;//操作员账号
	@Column
	private String cardNumber; //卡号
	@Column
	private String responseCode; //交易返回码
	@Column
	private String terminal; //刷卡终端号
	@Column
	private String location;
	@Column
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date tradeDate;
	@Column
	private String gatewayAccount;
	@Column
	private POSGatewayAccountType gatewayType;
	@Column
	private String remark;
	@Column
	private String refNo; //交易参考号
	@Column
	private String batchNo; //交易批次号
	@Column
	private String traceNo; //交易跟踪号
	@Column
	private String authCode; //授权码
	@Column
	private String cardOrg; //发卡机构
	@Column
	private String issueCode; //发卡行代码
	@Column
	private String issueName; //发卡行名称
	
	private String password;
	
	public enum POSTransactionStatus{
		PAID_FAIL(-1),//付款失败
		UNPAID(0), //等待付款
		PAID_SUCCESS(1),//付款成功
		PAID_REFUND(2),//退款历史成功订单
		PAID_TIMEOUT(3),//交易超时订单关闭
		PAID_REVOCATION(4),//撤销当天成功的订单
		PRE_AUTHORIZATION(5), //预授权
		PRE_AUTHORIZATION_COMPLETION(6), //预授权完成
		OFFLINE(7),   //离线交易
		REVERSAL(8),  //充正
		SALES_RETURN(9); //退货
		private int state;
		POSTransactionStatus(int state){
			this.state = state;
		}
		
		public int getState() {
			return state;
		}
		
		public static final POSTransactionStatus queryByState(int state){
			for(POSTransactionStatus status : POSTransactionStatus.values()){
				if(status.getState() == state){
					return status;
				}
			}
			return null;
		}
		
	}
	
	public enum POSTransactionType{
		BANK_CARD(1), //刷银行卡消费
		//PREPAID_CARD(2), //充值卡消费
		//MEMBER_POINT(3), //会员积分消费
		CMCC_TICKET(4), //移动电子券
		WEI_XIN(5), //微信
		ALIPAY(6), //支付宝
		ELECTRONIC_CASH(7); //电子现金(NFC)
		
		private int type;
		
		POSTransactionType(int type){
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPrepaidCardChargeOrderCode() {
		return prepaidCardChargeOrderCode;
	}

	public void setPrepaidCardChargeOrderCode(String prepaidCardChargeOrderCode) {
		this.prepaidCardChargeOrderCode = prepaidCardChargeOrderCode;
	}

	public String getThirdOrderCode() {
		return thirdOrderCode;
	}

	public void setThirdOrderCode(String thirdOrderCode) {
		this.thirdOrderCode = thirdOrderCode;
	}
	
	public String getOrderCodeByType(String orderType){
		if(StringUtils.equals("XPOS_ORDER", orderType)
			|| StringUtils.isBlank(orderType)){ //TODO 兼容老接口暂时保留（老接口没有orderType类型，默认XPOS_ORDER类型）
			return getOrderNumber();
		}else if(StringUtils.equals("XPOS_PREPAID", orderType)){
			return getPrepaidCardChargeOrderCode();
		}else if(StringUtils.equals("THIRD_ORDER", orderType)){
			return getThirdOrderCode();
		}
		return null; 
	}
	
	public void setOrderCodeByType(String orderCode, String orderType){
		if(StringUtils.equals("XPOS_ORDER", orderType)
			|| StringUtils.isBlank(orderType)){ //TODO 兼容老接口暂时保留（老接口没有orderType类型，默认XPOS_ORDER类型）
			setOrderNumber(orderCode);
		}else if(StringUtils.equals("XPOS_PREPAID", orderType)){
			setPrepaidCardChargeOrderCode(orderCode);
		}else if(StringUtils.equals("THIRD_ORDER", orderType)){
			setThirdOrderCode(orderCode);
		}
	}
	
	public Long getBusinessId() {
		return businessId;
	}
	
	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	
	public BusinessType getBusinessType() {
		return businessType;
	}
	
	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
	
	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(BusinessModel.POS);
		this.businessType = business.getAccessBusinessType(BusinessModel.POS);
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public String getMaskedMobile(){
		if(StringUtils.isNotBlank(mobile)){
			StringBuilder sb = new StringBuilder(mobile);
			sb.replace(3, 7, "****");
			return sb.toString();
		}
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	
	public BigDecimal getSum() {
		return sum;
	}

	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getSerial() {
		return serial;
	}
	
	public void setSerial(String serial) {
		this.serial = serial;
	}
	
	public POSTransactionStatus getStatus() {
		return status;
	}
	
	public void setStatus(POSTransactionStatus status) {
		this.status = status;
	}
	
	public POSTransactionType getType() {
		return type;
	}
	
	public void setType(POSTransactionType type) {
		this.type = type;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCardNumber() {
		return cardNumber;
	}
	
	public String getMaskedCardNumber(){
		if(StringUtils.isNotBlank(cardNumber)){
			switch(this.gatewayType){
				case BOC:
					return StringUtils.left(cardNumber, 6) + "******" + StringUtils.right(cardNumber, 4);//中行提供完整银行卡号，隐藏中间的几位
				case UMPAY: case SHENGPAY: default: //联动优势、盛付通只提供末4位，无需隐藏
					return cardNumber;
			}
		}
		return cardNumber;
	}
	
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getGatewayAccount() {
		return gatewayAccount;
	}

	public void setGatewayAccount(String gatewayAccount) {
		this.gatewayAccount = gatewayAccount;
	}

	public POSGatewayAccountType getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(POSGatewayAccountType gatewayType) {
		this.gatewayType = gatewayType;
	}
	
	public void setPOSGatewayAccount(POSGatewayAccount posGatewayAccount){
		this.gatewayAccount = posGatewayAccount.getAccount();
		this.gatewayType = posGatewayAccount.getType();
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		if(StringUtils.isNotBlank(remark)){
			this.remark = getRemark() + "|||" + remark;
		}
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getCardOrg() {
		return cardOrg;
	}

	public void setCardOrg(String cardOrg) {
		this.cardOrg = cardOrg;
	}

	public String getIssueCode() {
		return issueCode;
	}

	public void setIssueCode(String issueCode) {
		this.issueCode = issueCode;
	}

	public String getIssueName() {
		return issueName;
	}

	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}
	
}
