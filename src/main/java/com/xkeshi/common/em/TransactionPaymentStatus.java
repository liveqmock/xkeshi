package com.xkeshi.common.em;

/**
 * 支付流水结果枚举
 * Created by david-y on 2015/1/23.
 */
public enum TransactionPaymentStatus {
    SUCCESS(1), //支付成功
    UNPAID(2), //未付款
    FAILED(3), //支付失败
    TIMEOUT(4), //超时
    CANCEL(5), //取消支付(买家主动放弃支付)
    REVOCATION(6), //撤销(当天成功的付款)
    REFUND(7), //退款(历史成功的付款)
    REVERSAL(8); //冲正
    
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;

    private TransactionPaymentStatus(int value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return name();
    }
    
    public static final TransactionPaymentStatus findByValue(int value){
    	for(TransactionPaymentStatus status : TransactionPaymentStatus.values()){
			if(status.getValue() == value){
				return status;
			}
		}
		return null;
    }

}
