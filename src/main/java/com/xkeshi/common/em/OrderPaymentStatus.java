package com.xkeshi.common.em;


/**
 * 订单支付结果枚举
 * Created by david-y on 2015/1/23.
 */
public enum OrderPaymentStatus {
    SUCCESS(1), //支付成功
    UNPAID(2), //未付款
    FAILED(3), //支付失败
    TIMEOUT(4), //超时
    CANCEL(5), //撤销订单
    PARTIAL_PAYMENT(6), //部分支付成功
    REFUND(7); //退货

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;

    private OrderPaymentStatus(int value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return name();
    }
    
    public static OrderPaymentStatus getByValue(int value){
    	for(OrderPaymentStatus status : values()){
    		if(status.getValue() == value){
    			return status;
    		}
    	}
    	return null;
    }

}
