package com.xkeshi.pojo.vo.param;

/**
 * Created by nt on 2015-04-03.
 */
public class POSGatewayAccountParam {

    private Long shopId;

    private String type;

    private String terminal;

    private String account;

    private String signKey;

    private Integer enable;

    private Integer enableZFB;

    private Integer enableWX;

    private Integer enableYHK;

    public Integer getEnableZFB() {
        return enableZFB;
    }

    public void setEnableZFB(Integer enableZFB) {
        this.enableZFB = enableZFB;
    }

    public Integer getEnableWX() {
        return enableWX;
    }

    public void setEnableWX(Integer enableWX) {
        this.enableWX = enableWX;
    }

    public Integer getEnableYHK() {
        return enableYHK;
    }

    public void setEnableYHK(Integer enableYHK) {
        this.enableYHK = enableYHK;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
