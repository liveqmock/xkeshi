package com.xkeshi.pojo.vo;

/**
 * API系统参数
 * <p/>
 * Created by david-y on 2015/1/19.
 */
public class SystemParam {
    private String deviceNumber;
    private Long mid;
    private Long operatorId;

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}