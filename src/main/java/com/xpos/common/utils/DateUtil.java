package com.xpos.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
 
public class DateUtil {
	public  static String getOrderNum(){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date);
	}
	//获取日期，格式：yyyy-MM-dd HH:mm:ss
	public  static String getDateFormatter(){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}
	
	
	public static String getDate(){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}
	
	public static String getDate(String format){
		Date date=new Date();
		DateFormat df=new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public static String getDate(Date date , String format){
		DateFormat df=new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public static Date getDateFormatter(String dateTime) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime)  ;
	}

	public static void main (String [] a) {
		System.out.println(getDate("yyyyMMddHHmmss"));
	}
}
