package com.xkeshi.pojo.vo.offline;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by dell on 2015/4/11.
 * @Description 离线上传操作员交接数据
 */
public class OffLineOperatorShiftVO {

    private Long operatorId               ;
    private String operatorSessionCode    ;
    private int totalOrderCount           ;
    private int totalOrderItemCount       ;
    private int totalCouponConsumeCount   ;
    private int totalMemberCount          ;
    private BigDecimal totalOrderAmount   ;
    private BigDecimal totalPhysicalCouponAmount ;
    private BigDecimal totalReceivableAmount     ;
    private BigDecimal totalActuallyAmount       ;
    private BigDecimal totalDifferenceCashAmount ;
    private String shiftStartTime                  ;
    private String shiftEndTime                    ;
    private boolean shiftCompleted               ;
    private List<OffLinePhysicalCouponVO>  physicalCoupons ;

    //预付赠送总额
    private BigDecimal prepaidcardTotalPresentedAmount            ;
    //预付卡实充金额
    private BigDecimal prepaidcardtotalRealityRechargeAmount      ;
    //预付卡充值次数
    private int prepaidcardRechargeAmountCount                    ;

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorSessionCode() {
        return operatorSessionCode;
    }

    public void setOperatorSessionCode(String operatorSessionCode) {
        this.operatorSessionCode = operatorSessionCode;
    }

    public List<OffLinePhysicalCouponVO> getPhysicalCoupons() {
        return physicalCoupons;
    }

    public void setPhysicalCoupons(List<OffLinePhysicalCouponVO> physicalCoupons) {
        this.physicalCoupons = physicalCoupons;
    }

    public boolean isShiftCompleted() {
        return shiftCompleted;
    }

    public void setShiftCompleted(boolean shiftCompleted) {
        this.shiftCompleted = shiftCompleted;
    }

    public String getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public String getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public BigDecimal getTotalActuallyAmount() {
        return totalActuallyAmount;
    }

    public void setTotalActuallyAmount(BigDecimal totalActuallyAmount) {
        this.totalActuallyAmount = totalActuallyAmount;
    }

    public int getTotalCouponConsumeCount() {
        return totalCouponConsumeCount;
    }

    public void setTotalCouponConsumeCount(int totalCouponConsumeCount) {
        this.totalCouponConsumeCount = totalCouponConsumeCount;
    }

    public BigDecimal getTotalDifferenceCashAmount() {
        return totalDifferenceCashAmount;
    }

    public void setTotalDifferenceCashAmount(BigDecimal totalDifferenceCashAmount) {
        this.totalDifferenceCashAmount = totalDifferenceCashAmount;
    }

    public int getTotalMemberCount() {
        return totalMemberCount;
    }

    public void setTotalMemberCount(int totalMemberCount) {
        this.totalMemberCount = totalMemberCount;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public int getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(int totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public int getTotalOrderItemCount() {
        return totalOrderItemCount;
    }

    public void setTotalOrderItemCount(int totalOrderItemCount) {
        this.totalOrderItemCount = totalOrderItemCount;
    }

    public BigDecimal getTotalPhysicalCouponAmount() {
        return totalPhysicalCouponAmount;
    }

    public void setTotalPhysicalCouponAmount(BigDecimal totalPhysicalCouponAmount) {
        this.totalPhysicalCouponAmount = totalPhysicalCouponAmount;
    }

    public BigDecimal getTotalReceivableAmount() {
        return totalReceivableAmount;
    }

    public void setTotalReceivableAmount(BigDecimal totalReceivableAmount) {
        this.totalReceivableAmount = totalReceivableAmount;
    }

    public BigDecimal getPrepaidcardTotalPresentedAmount() {
        return prepaidcardTotalPresentedAmount;
    }

    public void setPrepaidcardTotalPresentedAmount(BigDecimal prepaidcardTotalPresentedAmount) {
        this.prepaidcardTotalPresentedAmount = prepaidcardTotalPresentedAmount;
    }

    public BigDecimal getPrepaidcardtotalRealityRechargeAmount() {
        return prepaidcardtotalRealityRechargeAmount;
    }

    public void setPrepaidcardtotalRealityRechargeAmount(BigDecimal prepaidcardtotalRealityRechargeAmount) {
        this.prepaidcardtotalRealityRechargeAmount = prepaidcardtotalRealityRechargeAmount;
    }

    public int getPrepaidcardRechargeAmountCount() {
        return prepaidcardRechargeAmountCount;
    }

    public void setPrepaidcardRechargeAmountCount(int prepaidcardRechargeAmountCount) {
        this.prepaidcardRechargeAmountCount = prepaidcardRechargeAmountCount;
    }
}
