package com.xpos.common.entity.face;

import java.math.BigDecimal;


public interface Business {
	
	public final static String SESSION_KEY = "_BUSINESS_";
	public final static String BUSINESS_CENTRAL = "_BUSINESS_CENTRAL_";//集团统一管理
	public final static String BUSINESS_CENTRAL_AVAILABLE = "_BUSINESS_CENTRAL_AVAILABLE_";//商户被统一管理，且预付卡规则适用
	public final static String BUSINESS_TYPE = "_BUSINESS_TYPE_";

	public enum BusinessType{
		MERCHANT, SHOP;
        @Override
        public String toString(){
            return this.name().toUpperCase();
        }
	}
	
	public enum BusinessModel{
		MEMBER, BALANCE, COUPON, MENU, ORDER, ACTIVITY, CONTACT, POS, ALBUM
	}
	
	public Long getAccessBusinessId(BusinessModel model);
	public BusinessType getAccessBusinessType(BusinessModel model);
	public Long getSelfBusinessId();
	public BusinessType getSelfBusinessType();
	public BigDecimal getBalance();

}
