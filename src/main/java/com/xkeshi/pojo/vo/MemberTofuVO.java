package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * 会员豆腐块信息
 * <p/>
 * Created by david-y on 2015/1/19.
 */
public class MemberTofuVO {
    public MemberInfoVO memberInfo;
    public MemberTypeVO memberType;
    public Integer electronicCouponCount;
    public BigDecimal prepaidCardBalance;

    public MemberInfoVO getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfoVO memberInfo) {
        this.memberInfo = memberInfo;
    }

    public MemberTypeVO getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberTypeVO memberType) {
        this.memberType = memberType;
    }

    public Integer getElectronicCouponCount() {
        return electronicCouponCount;
    }

    public void setElectronicCouponCount(Integer electronicCouponCount) {
        this.electronicCouponCount = electronicCouponCount;
    }

    public BigDecimal getPrepaidCardBalance() {
        return prepaidCardBalance;
    }

    public void setPrepaidCardBalance(BigDecimal prepaidCardBalance) {
        this.prepaidCardBalance = prepaidCardBalance;
    }
}