package com.xkeshi.common.em.result;


import org.apache.commons.lang3.StringUtils;

/**
 * 预付卡支付结果
 * Created by david-y on 2015/1/23.
 */
public enum PrepaidCardChargeResult {

    SUCCESS("充值成功","0"),
    OTHER("充值失败","1000"),

    //=====客户端参数出错=======
    CLIENT_PARAM_ERROR("客户端参数出错","2000"),


    //=====非客户端错误========
    NOT_FOUND_RULE("没有找到相应充值规则","3001")

    ;


    private String name;
    private String code;

    private PrepaidCardChargeResult(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(String code) {
        for (PrepaidCardChargeResult c : PrepaidCardChargeResult.values()) {
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
