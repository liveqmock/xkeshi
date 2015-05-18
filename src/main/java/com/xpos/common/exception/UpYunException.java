package com.xpos.common.exception;

public class UpYunException extends Exception{

	private static final long serialVersionUID = 3854772125385537971L;
	
	public int code;
	public String message;
	public String url;
	public long time;
	public String signString;
	public boolean isSigned;
	
	public UpYunException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String toString() {
		return "UpYunException [code=" + code + ", " + (message != null ? "message=" + message + ", " : "")
				+ (url != null ? "url=" + url + ", " : "") + "time=" + time + ", "
				+ (signString != null ? "signString=" + signString + ", " : "") + "isSigned=" + isSigned + "]";
	}

}