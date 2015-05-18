package com.xpos.common.exception;

public class ScopeException  extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8810287088799397403L;
	
	public ScopeException(){
		super("参数不在指定范围");
	}
	
	public ScopeException(String msg){
		super(msg);
	}
}
