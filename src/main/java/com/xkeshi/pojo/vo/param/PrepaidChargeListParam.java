package com.xkeshi.pojo.vo.param;

/**
 * 预付卡充值统计参数
 *
 * Created by david-y on 2015/1/30.
 */
public class PrepaidChargeListParam extends BaseParam {
    //需要设置排序默认参数
    private static final String ORDER_BY = "t.id";
    private static final int ORDER_TYPE = 2; //desc

    private String beginTime;
    private String endTime;
    private Integer initialCharge;
    private String chargeAmount;
    private Long chargeChannelId;

    private Long businessId;
    private String businessType;

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getInitialCharge() {
        return initialCharge;
    }

    public void setInitialCharge(Integer initialCharge) {
        this.initialCharge = initialCharge;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public Long getChargeChannelId() {
        return chargeChannelId;
    }

    public void setChargeChannelId(Long chargeChannelId) {
        this.chargeChannelId = chargeChannelId;
    }

    @Override
    public String getOrderBy() {
        if (super.orderBy == null) {

            return ORDER_BY;
        }
        return super.orderBy;
    }

    @Override
    public int getOrderType() {
        if (super.orderType == 0) {
            return ORDER_TYPE;
        }
        return super.orderType;
    }
}
