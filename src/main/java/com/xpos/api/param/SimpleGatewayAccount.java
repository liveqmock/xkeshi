package com.xpos.api.param;

import static com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;

public class SimpleGatewayAccount {
	private String registerMid;
	private int type;
	private String signKey;
	
	public SimpleGatewayAccount(String registerMid, POSGatewayAccountType type, String signKey){
		this.registerMid = registerMid;
		switch(type){
			case UMPAY: this.type = 1; break;
			case BOC: this.type = 2; this.signKey = signKey; break;
			case SHENGPAY: this.type = 3; break;
			case ALIPAY: this.type = 4; break;
			default: this.type = -1;
		}
	}
	public String getRegisterMid() {
		return registerMid;
	}
	public void setRegisterMid(String registerMid) {
		this.registerMid = registerMid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getSignKey() {
		return signKey;
	}
	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}
}
