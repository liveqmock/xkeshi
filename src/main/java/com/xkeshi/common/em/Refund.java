package com.xkeshi.common.em;


import org.apache.commons.lang3.StringUtils;

/**
 * 退款结果接口枚举
 * Created by david-y on 2015/1/23.
 */
public enum Refund {

    SUCCESS("退款成功","0"),
    OTHER("退款失败","1000"),
    NON_TRANSACTION("不存在该支付","1001"),
    TRANSACTION_UNMATCHED("订单和支付流水不匹配","1002"),
    ORDER_UNABLE_REFUND("当前订单不允许退款", "1003"),
    REFUND_TRANSACTION_FAILED("支付退款失败", "1004"),
    REFUND_RESULT_UNKNOW("退款结果未知", "1005"),
    SELLER_ACCOUNT_NOT_MATCH("卖家账号不匹配", "1006"),

    //======客户端错误=====================
    CLIENT_PARAM_ERROR("客户端参数出错", "2000");


    private String name;
    private String code;

    private Refund(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(String code) {
        for (Refund c : Refund.values()) {
            if (StringUtils.equals(c.getCode(), code)) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
