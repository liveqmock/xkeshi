package com.xkeshi.pojo.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import java.util.Date;

/**
 * Created by dell on 2015/4/10.
 */
public class Operator {

    @Column
    private Long id;// ID

    @JsonIgnore
    @Column
    private Date createDate;// 创建日期

    @JsonIgnore
    @Column
    private Date modifyDate;// 修改日期

    @JsonIgnore
    @Column
    private Boolean deleted;

    public final static String SESSION_KEY = "_OPERATOR_";


    @Column
    @NotBlank(message="账号不能为空")
    @Length(max=32)
    private String username;


    @Column
    @NotBlank(message="姓名不能为空")
    @Length(max=32)
    private String realName;

    @Column
    @NotBlank(message="登陆密码不能为空")
    private String password;

    @Column
    private com.xpos.common.entity.Shop shop;

    @Column
    private Long shopId;

    @Column(name = "level")
    private Level level;

    private enum Level {
        OPERATOR,MANAGER
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public com.xpos.common.entity.Shop getShop() {
        return shop;
    }

    public void setShop(com.xpos.common.entity.Shop shop) {
        this.shop = shop;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
