package com.xpos.common.exception;

public class SignEncException  extends Exception{
	
	private static final long serialVersionUID = -2520654586502231368L;
	
	private static String DEFUALT_ERROR_MSG = "签名校验错误";
	
	public SignEncException(){
		super(DEFUALT_ERROR_MSG);
	}

	public SignEncException(String msg) {  
	    super(msg);  
    }  
      
    public SignEncException(String msg,Throwable e) {  
        super(msg,e);  
    }  

}