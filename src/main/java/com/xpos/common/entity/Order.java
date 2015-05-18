package com.xpos.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xpos.common.entity.ShopInfo.ConsumeType;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.pos.POSTransaction;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Table(name="Orders")
public class Order extends BaseEntity{

	private static final long serialVersionUID = 6869670513829553246L;
	
	@Column
	private String orderNumber;	//订单号
	
	@Column
	private Long businessId;
	
	@Column
	private Business.BusinessType businessType;
	
	@Column
	@DecimalMin("0")
	private BigDecimal totalAmount;   //订单金额
	
	@Column
	@DecimalMin("0")
	private BigDecimal actuallyPaid;  //实收金额
	
	@Column
	private Type type; //付款方式
	
	@Column
	private Status status;
	
	@Column
	private Member member;
	
	@Column
	private String identifier;
	
	@Column
	private Integer peoples; //人数
	
	@Column
	private Boolean takeAway; //外卖
	
	@Column
	@DecimalMin("0")
	@DecimalMax("10")
	private BigDecimal discount; //折扣;
	
	@Column
	private Operator operator; //收银员
	
	@Transient
	private List<OrderItem> items;
	
	@Column
	private POSTransaction posTransaction;
	
	@Column
	private ConsumeType consumeType;
	
	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date tradeDate;//支付成功时间
	
	/*当班会话session*/
	@Column(name="operator_session_code")
	private String operatorSessionCode;
	
	@Column
	private int counter;
	
	@Column(name="device_number")
	private String deviceNumber;
	
	public enum Status{
		UNPAID, //未付款
		SUCCESS, //支付成功
		FAILED, //支付失败
		CANCEL, //撤销订单
		TIMEOUT, //超时
		PARTIAL_REFUND, //部分退款
		REFUND, //退款
		PARTIAL_PAYMENT; //部分支付
		
		public static Status findByName(String name) {
			for(Status status : values()){
				if(StringUtils.equalsIgnoreCase(status.name(), name)){
					return status;
				}
			}
			return null;
		}
	}
	
	public enum Type{
		CASH("现金", 1L),
		BANKCARD("刷卡", 2L),
		ALIPAY_QRCODE("支付宝扫码", 3L),
		WXPAY_QRCODE ("微信扫码", 4L),
		PREPAID("预付卡", 5L),
		BANK_NFC_CARD("电子现金", 6L);
		
		private String desc;
		private Long code;
		
		Type(String desc, Long code){
			this.desc = desc;
			this.code = code;
		}
		public String getDesc(){
			return desc;
		}
		public Long getCode(){
			return code;
		}
		public static Type findByCode(Long code){
			for(Type type : values()){
				if(type.getCode().equals(code)){
					return type;
				}
			}
			return null;
		}
	}
	
	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}
	

	public Business.BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(Business.BusinessType businessType) {
		this.businessType = businessType;
	}
	
	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(BusinessModel.ORDER);
		this.businessType = business.getAccessBusinessType(BusinessModel.ORDER);
	}


	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getActuallyPaid() {
		return actuallyPaid;
	}

	public void setActuallyPaid(BigDecimal actuallyPaid) {
		this.actuallyPaid = actuallyPaid;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Integer getPeoples() {
		return peoples;
	}

	public void setPeoples(Integer peoples) {
		this.peoples = peoples;
	}

	public Boolean getTakeAway() {
		return takeAway;
	}

	public void setTakeAway(Boolean takeAway) {
		this.takeAway = takeAway;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public POSTransaction getPosTransaction() {
		return posTransaction;
	}

	public void setPosTransaction(POSTransaction posTransaction) {
		this.posTransaction = posTransaction;
	}

	public ConsumeType getConsumeType() {
		return consumeType;
	}

	public void setConsumeType(ConsumeType consumeType) {
		this.consumeType = consumeType;
	}
	
	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	
	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}

	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}
	

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	@JsonSerialize(using = JsonDateSerializer.class)
	@Override
	public Date getCreateDate(){
		return super.getCreateDate();
	}
	
	public class JsonDateSerializer  extends JsonSerializer<Date> {
		  
	    @Override  
	    public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
	        String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);  
	        jsonGenerator.writeString(dateStr);  
	    }  
	  
	}  
	
}
