package com.xpos.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RequestHelper {
	
	//get
	public static String sendGet(String url) {	
		String result = "";
		BufferedReader in = null;
		URLConnection conn =null;
		try {
			URL realUrl = new URL(url);

			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			// 建立实际的连接
			conn.connect();

			// 获取所有响应头字段
			java.util.Map<String, java.util.List<String>> map = conn.getHeaderFields();

			// 遍历所有的响应头字段
			/*for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}*/
			
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
				conn=null;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
