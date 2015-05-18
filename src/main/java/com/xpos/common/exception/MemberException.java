package com.xpos.common.exception;

public class MemberException  extends RuntimeException{
 
	private static final long serialVersionUID = 8810287088799397403L;
	
	public MemberException(){
		super("会员操作出错");
	}
	
	public MemberException(String msg){
		super(msg);
	}
}
