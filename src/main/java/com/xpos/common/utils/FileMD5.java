package com.xpos.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 计算文件的MD5
 */
public class FileMD5 {
	protected static char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	protected static MessageDigest messageDigest = null;
	static{
		try{
			messageDigest = MessageDigest.getInstance("MD5");
		}catch (NoSuchAlgorithmException e) {
			System.err.println(FileMD5.class.getName()+"初始化失败，MessageDigest不支持MD5Util.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
	

	}
	
	/**
	 * 计算文件的MD5
	 * @param fileName 文件的绝对路径
	 * @return
	 * @throws IOException
	 */
	//public static String getFileMD5String(String fileName) throws IOException{
	//	File f = new File(fileName);
	//	return getFileMD5String(f);
	//}
	
	/**
	 * 计算文件的MD5，重载方法
	 * @param file 文件对象
	 * @return
	 * @throws IOException
	 */
	public static String getFileMD5String(File file) throws IOException{
		FileInputStream stream = new FileInputStream(file);
		FileChannel ch = stream.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		messageDigest.update(byteBuffer);
		String hex = bufferToHex(messageDigest.digest());
		stream.close();
		return hex;
	}
	
	
	public static String getFileMD5String(byte[] bytes) throws IOException{
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		messageDigest.update(byteBuffer);
		return bufferToHex(messageDigest.digest());
	}
	
	private static String bufferToHex(byte bytes[]) {
	   return bufferToHex(bytes, 0, bytes.length);
	}
	
	private static String bufferToHex(byte bytes[], int m, int n) {
	   StringBuffer stringbuffer = new StringBuffer(2 * n);
	   int k = m + n;
	   for (int l = m; l < k; l++) {
	    appendHexPair(bytes[l], stringbuffer);
	   }
	   return stringbuffer.toString();
	}
	
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
	   char c0 = hexDigits[(bt & 0xf0) >> 4];
	   char c1 = hexDigits[bt & 0xf];
	   stringbuffer.append(c0);
	   stringbuffer.append(c1);
	}
}