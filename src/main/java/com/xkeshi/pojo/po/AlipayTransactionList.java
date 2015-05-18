package com.xkeshi.pojo.po;

import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.utils.IDUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by szw on 2015/3/11.
 */
public class AlipayTransactionList implements EncryptId {

    private Long id;
    private String orderNumber;
    private BigDecimal amount;
    private Date tradeTime;
    private Date createdTime;
    private String buyerId;
    private Long businessId;
    private Long shopId;
    private String shopName;
    private String merchantId;
    private String memberName;
    private String mobile;
    private String statusCode;
    private String statusName;
    private Date startDateTime;
    private Date endDateTime;
    private String[] status;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String[] getStatus() {
        return status;
    }

    public void setStatus(String[] status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getEid(){
        if(this instanceof EncryptId){
            return getId() != null ? IDUtil.encode(getId()) : null;
        }else
            return String.valueOf(getId());
    }

    public boolean getFilterParameter() {
        return  startDateTime != null || endDateTime != null ||  StringUtils.isNotBlank(shopName) || status!=null ;
    }

    public String getFilterStatus() {
        if(status != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i<status.length;i++) {
                if (i == 0) {
                    sb.append(status[i]);
                } else {
                    sb.append(","+status[i]);
                }
            }
            return sb.toString();
        }
        return null;
    }
}
