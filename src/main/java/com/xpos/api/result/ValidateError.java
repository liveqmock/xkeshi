package com.xpos.api.result;

public class ValidateError {
	private String res;
	private String description;
	
	public ValidateError(String code, String desc){
		setRes(code);
		setDescription(desc);
	}
	

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	public String getRes() {
		return res;
	}


	public void setRes(String res) {
		this.res = res;
	}
}
