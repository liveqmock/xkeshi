package com.xkeshi.common.em.result;


import com.xkeshi.pojo.vo.result.CreateChargeOrderResultVO;
import org.apache.commons.lang3.StringUtils;

/**
 * 预付卡充值订单结果
 * Created by david-y on 2015/1/23.
 */
public enum PrepaidCardChargeOrderResult {

    SUCCESS("创建充值订单成功","0"),
    OTHER("创建充值订单失败","1000"),

    //=====客户端参数出错=======
    CLIENT_PARAM_ERROR("客户端参数出错","2000"),


    //=====非客户端错误========
    NOT_FOUND_RULE("没有找到相应充值规则","3001"),
    EXIST_ORDER("已存在充值记录，首充失败","3002"),
    NOT_EXIST_ORDER("不存在充值记录，续充失败","3003")
    ;
    private CreateChargeOrderResultVO result;


    private String name;
    private String code;

    private PrepaidCardChargeOrderResult(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getName(String code) {
        for (PrepaidCardChargeOrderResult c : PrepaidCardChargeOrderResult.values()) {
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


    public CreateChargeOrderResultVO getResult() {
        return result;
    }

    public void setResult(CreateChargeOrderResultVO result) {
        this.result = result;
    }
}
