package com.xpos.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class MobileEncodor {
	private final static String MOBILENUM_REGEX = "^(1(([35][0-9])|(47)|[8][0-9]))\\d{8}$";

	private static byte[] base64Decode(String input) throws Exception{  
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");  
        Method mainMethod= clazz.getMethod("decode", String.class);  
        mainMethod.setAccessible(true);  
         Object retObj=mainMethod.invoke(null, input);  
         return (byte[])retObj;  
    }  
	
	private static String encodeBase64(byte[]input) throws Exception{  
        Class clazz=Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");  
        Method mainMethod= clazz.getMethod("encode", byte[].class);  
        mainMethod.setAccessible(true);  
         Object retObj=mainMethod.invoke(null, new Object[]{input});  
         return (String)retObj;  
    }  
	
	public static String encode(String mobile) throws Exception{
		String str = "phone:" + encodeBase64(mobile.getBytes());
		return encodeBase64(str.getBytes());
	}
	
	public static String decode(String mobile) throws Exception{
		if(StringUtils.isNotBlank(mobile)){
			String str = new String(base64Decode(mobile));
			str = str.replace("phone:", "");
			String phone = new String(base64Decode(str));
			if(Pattern.matches(MOBILENUM_REGEX, phone))
				return phone;
		}
		
		return null;
	}
}
