package com.xkeshi.pojo.vo.param;

/**
 * Created by nt on 2015-04-02.
 */
public class ShopDiscountSettingParam {

    private Long shopId;

    private Integer enable;

    private Integer enableHYK;  //会员卡是否开通

    private Integer enableSTQ;  //实体券是否开通

    private Integer enableDZQ;  //电子券是否开通

    public Integer getEnableHYK() {
        return enableHYK;
    }

    public void setEnableHYK(Integer enableHYK) {
        this.enableHYK = enableHYK;
    }

    public Integer getEnableSTQ() {
        return enableSTQ;
    }

    public void setEnableSTQ(Integer enableSTQ) {
        this.enableSTQ = enableSTQ;
    }

    public Integer getEnableDZQ() {
        return enableDZQ;
    }

    public void setEnableDZQ(Integer enableDZQ) {
        this.enableDZQ = enableDZQ;
    }

    private Long discountWayNameId;

    private Integer enablePrepaidCard;

    public Integer getEnablePrepaidCard() {
        return enablePrepaidCard;
    }

    public void setEnablePrepaidCard(Integer enablePrepaidCard) {
        this.enablePrepaidCard = enablePrepaidCard;
    }

    public Long getDiscountWayNameId() {
        return discountWayNameId;
    }

    public void setDiscountWayNameId(Long discountWayNameId) {
        this.discountWayNameId = discountWayNameId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }
}
