package com.xpos.common.utils;

import java.util.Random;
import java.util.UUID;

public class UUIDUtil {
	
	public static String getRandomString(int length){
		return getRandomString(length, false);
	}
	
	public static String getRandomString(int length, Boolean upper){
		String string = UUID.randomUUID().toString();
		string = string.replaceAll("-", "");
		if(string.length() >= length){
			string = string.substring(0, length);
		}else{
			while(string.length() < length){
				int i = new Random().nextInt(36);
				string+= i>25?(char)('0'+(i-26)):(char)('a'+i);
			}
		}
		if(upper)
			string = string.toUpperCase();
		
		return string;
	}
	
	public static void main(String args[]) throws Exception{
		System.out.println(UUIDUtil.getRandomString(32));
	}
}
