package com.xpos.common.exception;

/**
 * 
 * 删除时候当含有关联entity的时候抛出该异常
 * @author Johnny
 *
 */
public class AssociationException extends RuntimeException{

	private static final long serialVersionUID = -8284410723524585440L;

	public AssociationException(){
		super("包含关联");
	}
	
	public AssociationException(String msg){
		super(msg);
	}
	
}
