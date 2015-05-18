package com.xpos.common.utils;

public class IDUtil {
	
	private final static String CODE = "9214035678";
	
	public static Long decode(String eid){
		try{
			Long.parseLong(eid);
		}catch(NumberFormatException e){
			return null;
		}
		
		try{
			eid = eid.substring(2, eid.length());
			String result = "";
			for(int i=0;i<eid.length();i++){
				result+=CODE.indexOf(Integer.valueOf(eid.charAt(i)));
			}
			Long v = Long.valueOf(result);
			return (v-183729)/37;
		}catch(Exception e){
			return null;
		}
		
	}

	public static String encode(Long id){
		String eid = String.valueOf(id*37+183729);
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<eid.length();i++){
			builder.append(CODE.charAt(Integer.valueOf(String.valueOf(eid.charAt(i)))));
		}
		return "10"+builder.toString();
	}
	
	public static  void  main(String[] args){
		System.out.println(IDUtil.encode(100L));
		System.out.println(IDUtil.decode("10276018"));
	}

}
