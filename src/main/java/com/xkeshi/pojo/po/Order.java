package com.xkeshi.pojo.po;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by david-y on 2015/1/22.
 */
public class Order {
    private Long id;
    private String orderNumber;
    private String deviceNumber;
    private int counter;
    private String businessType;
    private Long businessId;
    private BigDecimal totalAmount;
    private BigDecimal actuallyPaid;
    private String type;
    private String status;
    private Long operatorId;
    private Long managerId;
    private Long memberId;
    private String operatorSessionCode;
    private String identifier;
    private Integer peopleCount;
    private Boolean takeAway;
    private BigDecimal discount;
    private Long posTranscationId;
    private Date createdDate;
    private Date modifyDate;
    
    
    
    public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getActuallyPaid() {
        return actuallyPaid;
    }

    public void setActuallyPaid(BigDecimal actuallyPaid) {
        this.actuallyPaid = actuallyPaid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getOperatorSessionCode() {
        return operatorSessionCode;
    }

    public void setOperatorSessionCode(String operatorSessionCode) {
        this.operatorSessionCode = operatorSessionCode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(Integer peopleCount) {
        this.peopleCount = peopleCount;
    }

    public Boolean getTakeAway() {
        return takeAway;
    }

    public void setTakeAway(Boolean takeAway) {
        this.takeAway = takeAway;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Long getPosTranscationId() {
        return posTranscationId;
    }

    public void setPosTranscationId(Long posTranscationId) {
        this.posTranscationId = posTranscationId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
