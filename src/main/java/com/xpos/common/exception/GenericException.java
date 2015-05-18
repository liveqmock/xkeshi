package com.xpos.common.exception;

public class GenericException  extends RuntimeException{
	
	private static final long serialVersionUID = -2520335086502231368L;
	
	private static String DEFUALT_ERROR_MSG = "系统错误";
	
	public GenericException(){
		super(DEFUALT_ERROR_MSG);
	}

	public GenericException(String msg) {  
	    super(msg);  
    }  
      
    public GenericException(String msg,Throwable e) {  
        super(msg,e);  
    }  

}