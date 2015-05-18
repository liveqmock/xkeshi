package com.xkeshi.pojo.vo;

import java.math.BigDecimal;

/**
 * Created by david-y on 2015/1/10.
 */
public class MemberTypeVO {

    private Long id;
    private String name;
    private BigDecimal discount;
    private boolean isDefault;
    private String coverPicture;

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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getCoverPicture() {
		return coverPicture;
	}

	public void setCoverPicture(String coverPicture) {
		this.coverPicture = coverPicture;
	}
}
