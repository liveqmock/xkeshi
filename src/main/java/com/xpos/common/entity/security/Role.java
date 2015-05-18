package com.xpos.common.entity.security;

public enum Role {

	ROLE_ADMIN,
	ROLE_SHOP_ADMIN,
	ROLE_MERCHANT_ADMIN;

    @Override
    public String toString() {
        return this.name().toUpperCase();
    }

}
