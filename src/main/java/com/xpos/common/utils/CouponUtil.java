package com.xpos.common.utils;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import com.fasterxml.uuid.Generators;

public class CouponUtil {
	
	
	/**
	 * 优惠码生成工具类
	 * 生产10位的随机数字，经过大量测试目前重复率是在0.025%左右
	 * @return
	 */
	public static String newCode(){
		String random = RandomStringUtils.randomNumeric(9);
		StringBuffer bf = new StringBuffer(10);
		bf.append(String.valueOf(new Random().nextInt(8)+1)).append(random);
		return bf.toString();
	}

	public static String newCode(int length){
		String random = RandomStringUtils.randomNumeric(length);
		return (new Random().nextInt(8)+1) + random;
	}

	public static String getUniqueCode(){
		return  new String(Base64Coder.encode(Generators.timeBasedGenerator().generate().toString().getBytes()));
	}
	 
}
