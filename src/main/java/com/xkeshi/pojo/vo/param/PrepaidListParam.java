package com.xkeshi.pojo.vo.param;

/**
 * <br>Author: David <br>
 * 2015/1/6.
 */
public class PrepaidListParam extends BaseParam {

    //需要设置排序默认参数
    private static final String ORDER_BY = "t.id";
    private static final int ORDER_TYPE = 2; //desc



    private Long memberTypeId;

    private String beginDate;

    private String endDate;

    private Integer enable;

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

    public Long getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(Long memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
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
