package com.xkeshi.pojo.vo;

/**
 * 会员基本信息VO
 * <p/>
 * Created by david-y on 2015/1/19.
 */
public class MemberInfoVO {
    public Long id;
    public String name;
    public String gender;
    public String mobileNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
