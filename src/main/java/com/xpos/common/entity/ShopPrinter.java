package com.xpos.common.entity;


/**
 * 商户打印档口关联
 * @author snoopy
 */
public class ShopPrinter extends NewBaseEntity{

	private static final long serialVersionUID = -3822339065757674323L;

	/**
	 * 关联的商户ID
	 */
	private Long shopId;
	
	/**
	 * 打印档口的名称
	 */
	private String name;
	
	/**
	 * 打印档口的IP
	 */
	private String ip;
	
	/**
	 * 是否启用
	 */
	private boolean enable;

	

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	
	
}
