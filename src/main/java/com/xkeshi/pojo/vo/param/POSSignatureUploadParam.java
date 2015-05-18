package com.xkeshi.pojo.vo.param;

/**
 * 客户签字图片上传
 * @author chengj
 */
public class POSSignatureUploadParam {
	private String orderType;
	private String registerMid; //第三方支付平台注册的商户号
	private int channel; //支付通道
	private String serial;
	private String content;
	
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
