package com.xkeshi.pojo.vo.shopPrinter;


/**
 * @author snoopy
 *
 */
public class ShopPrinterVO{
	
	private Long id;
	
	private String name;
	
	private String ip;
	
	private int enable;
	
	private String remark;
	
	public ShopPrinterVO(){
		
	}
	
	public ShopPrinterVO(Long id , String name , String ip , int enable , String remark){
		this.id = id;
		this.name = name;
		this.ip = ip;
		this.enable = enable;
		this.remark = remark;
	}

	

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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	


}