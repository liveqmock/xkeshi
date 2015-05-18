package com.xpos.common.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

public class TokenUtil {
	
	/**
	 * The salt must be 128 bit, saying 16 characters
	 */
	private static String SALT = "xpos1qazZSE$xpos"; 
	
	private static final String key = "whos1qazZSE$fsos";

	/**
	 * Encrypt the raw string to token
	 * @param rawStr
	 * @return
	 */
	public static String encrypt(String rawStr) {
		if(StringUtils.isNotBlank(rawStr)){
			 try {
		         Key aesKey = new SecretKeySpec(SALT.getBytes(), "AES");
		         Cipher cipher = Cipher.getInstance("AES");
		         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		         byte[] encrypted = cipher.doFinal(rawStr.getBytes());
		         return new String(Base64Coder.encode(encrypted));
		      
		      }catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
		    	  throw new RuntimeException("Error happened when encrypting String = " + rawStr + " due to " + e.getMessage());
		      }
		}else{
			throw new RuntimeException("Original String to be encrypted is null");
		}
	     
	}
	
	
	/**
	 * Decrypt the token to raw string
	 * @param encryptedStr
	 * @return
	 */
	public static String decrypt(String encryptedStr){
		if(StringUtils.isNotBlank(encryptedStr)){
			try {
				Key aesKey = new SecretKeySpec(SALT.getBytes(), "AES");
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, aesKey);
				byte[] baseDecrypted = Base64Coder.decode(encryptedStr);
				byte[] decrypted = cipher.doFinal(baseDecrypted);
				return new String(decrypted);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				 throw new RuntimeException("Error happened when decrypting String = " + encryptedStr + " due to " + e.getMessage());
			}
		}else{
			throw new RuntimeException("Original String to be decrypted is null");
		}
		
		
	}
	
	//refactor ,allow to input encript key values;
	public static String encrypt(String rawStr,String key) {
		if(StringUtils.isNotBlank(rawStr)){
			 try {
		         Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
		         Cipher cipher = Cipher.getInstance("AES");
		         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		         byte[] encrypted = cipher.doFinal(rawStr.getBytes());
		         return new String(Base64Coder.encode(encrypted));
		      
		      }catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
		    	  throw new RuntimeException("Error happened when encrypting String = " + rawStr + " due to " + e.getMessage());
		      }
		}else{
			throw new RuntimeException("Original String to be encrypted is null");
		}
	     
	}
	
	public static String decrypt(String encryptedStr,String key){
		if(StringUtils.isNotBlank(encryptedStr)){
			try {
				Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, aesKey);
				byte[] baseDecrypted = Base64Coder.decode(encryptedStr);
				byte[] decrypted = cipher.doFinal(baseDecrypted);
				return new String(decrypted);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				 throw new RuntimeException("Error happened when decrypting String = " + encryptedStr + " due to " + e.getMessage());
			}
		}else{
			throw new RuntimeException("Original String to be decrypted is null");
		}
		
		
	}
	public static void main(String[] args) {
		String en = TokenUtil.encrypt("123456");
		
		System.out.println(en);

		System.out.println(TokenUtil.decrypt(en));

	}
}
