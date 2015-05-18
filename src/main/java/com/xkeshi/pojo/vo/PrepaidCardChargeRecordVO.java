package com.xkeshi.pojo.vo;


import java.math.BigDecimal;
import java.util.List;

/**
 * 预付卡充值记录VO
 * Created by david-y on 2015/1/20.
 */
public class PrepaidCardChargeRecordVO {

    private List<PrepaidCardChargeVO> chargeList;

    private Integer totalCount;
    private BigDecimal totalAmount;
    private Integer page;
    private Integer pageSize;
    private Boolean hasPrefix;
    private Boolean hasNext;

    public List<PrepaidCardChargeVO> getChargeList() {
        return chargeList;
    }

    public void setChargeList(List<PrepaidCardChargeVO> chargeList) {
        this.chargeList = chargeList;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getHasPrefix() {
        return hasPrefix;
    }

    public void setHasPrefix(Boolean hasPrefix) {
        this.hasPrefix = hasPrefix;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
