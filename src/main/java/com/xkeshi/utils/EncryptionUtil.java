package com.xkeshi.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;


/**
 * 加密处理类
 * @author David
 *
 */
public class EncryptionUtil {
	
	
	/**
	 * 获得随机salt
	 */
	public static String getSalt() {
		return new SecureRandomNumberGenerator().nextBytes().toBase64();
	}

	/**
	 * 哈希加密并Base64处理密码
	 * @param rawPassword
	 * @param salt
	 * @return
	 */
	public static String encodePassword(String rawPassword, String salt) {
		return new Sha512Hash(rawPassword, salt,512).toBase64();
	}


    public static String md5(String str) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest messageDigest = null;

        messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.reset();

        messageDigest.update(str.getBytes("UTF-8"));

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }
    
    public static boolean isStrongPassword(String rawPassword){
        if(StringUtils.isNotBlank(rawPassword)){
            Pattern p1 = Pattern.compile("^[0-9a-zA-Z]{6,32}$"); //数字或大小写字母，6~32位
            Pattern p2 = Pattern.compile("[0-9]+");
            Pattern p3 = Pattern.compile("[a-zA-Z]+");
            Matcher matcher = p1.matcher(rawPassword);
            if(matcher.find()){
                matcher.reset().usePattern(p2);
                if(matcher.find()){
                	matcher.reset().usePattern(p3);
                	if(matcher.find()){
                		return true;
                	}
                }
            }
        }
    	return false;
    }
   
    /**  
     * 生成随机含有数字和字母的字符串  
     * @param len 随机字符串长度 (长度需大于1)  
     */  
    public static String generateRandomCharAndNumber(int count) { 
    	 if (count < 2) {
             throw new IllegalArgumentException("Requested random string length " + count + " is less than 1.");
         }
    	 int numberCount  =  RandomUtils.nextInt(count-1);
    	 numberCount =  numberCount  == 0 ?  1 : numberCount ; 
    	 StringBuffer  stringBuffer   = new StringBuffer() ;
    	 //生成随机数字
    	 stringBuffer.append(RandomStringUtils.randomNumeric(numberCount)); 
		 //生成随机字符(剔除'l','o')
    	 stringBuffer.append(RandomStringUtils.random((count - numberCount),"abcdefghijkmnpqrstuvwxyz" )); 
    	 //打乱
    	 char[] c = stringBuffer.toString().toCharArray();
         List<Character> lst = new ArrayList<Character>();
         for (int i = 0; i < c.length; i++) 
             lst.add(c[i]);
         Collections.shuffle(lst);
         stringBuffer   = new StringBuffer() ;
         for (int i = 0; i < lst.size(); i++) 
        	 stringBuffer.append(lst.get(i));
         return stringBuffer.toString();
    }  
}
