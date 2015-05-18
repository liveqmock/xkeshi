package com.xkeshi.pojo.vo.shopPrinter;


/**
 * @author snoopy
 */
public class ShopPrinterServiceVO{
	
	
	private String ip;
	
	private String port;
	
	private int enable;
	
	public ShopPrinterServiceVO(){
		
	}
	
	public ShopPrinterServiceVO(String ip , String port ,  int enable) {
		this.ip = ip;
		this.port = port;
		this.enable = enable;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	

	

	


}