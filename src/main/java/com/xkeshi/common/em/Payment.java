package com.xkeshi.common.em;


import org.apache.commons.lang3.StringUtils;

/**
 * 支付结果接口枚举
 * Created by david-y on 2015/1/23.
 */
public enum Payment {

    SUCCESS("支付成功","0"),
    OTHER("支付失败","1000"),
    NON_ORDER("无订单","1001"),
    INVALID_ORDER_STATUS("当前订单状态不能进行支付","1002"),
    HAS_DEALING_TRANSACTION("订单有正在进行中的支付","1003"),
    OVER_RANGE("订单剩余支付金额小于提交金额","1004"),
    INVALID_PHYSICAL_COUPON("无效实体券","1005"),

    ALIPAY_SELLER_ACCOUNT_NOT_FOUND("商家支付宝账号未设置", "1006"),
    ALIPAY_INVALID_SELLER_ACCOUNT("支付宝账号错误", "1007"),
    ALIPAY_CREATE_SERIAL_FAILED("创建支付流水失败", "1008"),
    ALIPAY_SUBMIT_ORDER_TO_PLATFORM_FAILED("订单提交到支付宝平台失败", "1009"),
    ALIPAY_PLATFORM_CREATE_ORDER_FAILED("支付宝平台创建订单失败", "1010"),
    ALIPAY_TRADE_HAS_SUCCESS("该订单已经支付成功", "1011"),
    ALIPAY_TRADE_HAS_CLOSE("该订单已经关闭(超时或全额退款完成)", "1012"),
    ALIPAY_REASON_ILLEGAL_STATUS("交易状态不合法", "1013"),
    ALIPAY_BUYER_ENABLE_STATUS_FORBID("支付宝账户异常，无法继续交易", "1014"),
    ALIPAY_BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR("超出每日付款限额", "1015"),
    ALIPAY_CLIENT_VERSION_NOT_MATCH("钱包版本过低，请先升级到最新版", "1016"),
    ALIPAY_SOUNDWAVE_PARSER_FAIL("付款码错误，请重新输入", "1017"),
    ALIPAY_UNKNOWN_STATUS("支付状态未知", "1018"),
    ALIPAY_WAIT_BUYER_PAY("订单已创建，等待买家付款", "1019"),
    ALIPAY_QUERY_FAIL("订单查询失败，请重新查询", "1020"),
    ALIPAY_ORDER_CANCEL_SUCCESS("订单撤销成功", "1021"),
    ALIPAY_ORDER_CANCEL_FAILED("订单撤销失败", "1022"),
    ALIPAY_ORDER_CANCEL_UNKNOWN("订单撤销异常，请重试", "1023"),
    ALIPAY_ORDER_REFUND_SUCCESS("订单退款成功", "1024"),
    ALIPAY_ORDER_REFUND_FAILED("订单退款失败", "1025"),
    ALIPAY_ORDER_REFUND_UNKNOWN("订单退款异常，请重试", "1026"),
    ALIPAY_PULL_MOBILE_CASHIER_FAIL("支付码重复，请刷新条码", "1027"),
    ALIPAY_ORDER_NOT_FOUND("订单未找到", "1028"),
    
    POS_REGISTER_ACCOUNT_NOT_FOUND("第三方支付平台账号不存在", "1029"),
    POS_INVALID_REGISTER_ACCOUNT("第三方支付平台账号错误", "1030"),
    POS_CREATE_SERIAL_SUCCESS("创建POS支付流水成功", "1031"),
    POS_CREATE_SERIAL_FAILED("创建POS支付流水失败", "1032"),
    POS_UPLOAG_SIGNATURE_SUCCESS("上传客户签字成功", "1033"),
    POS_UPLOAG_SIGNATURE_FAILED("上传客户签字失败", "1034"),
    POS_UPLOAG_SIGNATURE_CONTENT_EMPTY("客户签字为空", "1035"),
    POS_INVALID_SIGNATURE("参数签名错误", "1036"),
    POS_STATUS_UPDATE_SUCCESS("流水状态更新成功", "1037"),
    POS_STATUS_UPDATE_FAILED("流水状态更新失败", "1038"),

    EXIST_PREPAID_CARD_PAYMENT_RECORD("存在预付卡支付记录", "1040"),


    NOT_EXIST_PREPAID_CARD("找不到相关预付卡", "1041"),
    PASSWORD_ERROR("密码出错", "1042"),
    PARTIAL_PREPAID_CHARGE("预付卡充值必须全额支付","1043"),
    NOT_ENOUGH_PREPAID_BALANCE("预付卡余额不足","1044"),


    //======客户端错误=====================
    CLIENT_PARAM_ERROR("客户端参数出错", "2000"),
    PREPAID_CARD_PASSWORD_DECODE_ERROR("预付卡支付密码解码出错","2001"),


    //=======以下用于内部状态，不返回给客户端===========

    FIRST_PAYMENT("首次支付","-1"),
    NOT_FIRST_PAYMENT("非首次支付","-2")
    ;


    private String name;
    private String code;

    private Payment(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(String code) {
        for (Payment c : Payment.values()) {
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
