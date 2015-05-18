package com.alipay.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.sign.Md5Encrypt;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class AlipayFuction {
	

	public static String sign(Map params, String privateKey,PaySource paySource) {
		Properties properties = new Properties();

		for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			Object value = params.get(name);

			if (name == null || name.equalsIgnoreCase("sign")
					|| name.equalsIgnoreCase("sign_type")) {
				continue;
			}

			properties.setProperty(name, value.toString());
			
		}
		String content = getSignatureContent(properties);

		if (privateKey == null) {
			return null;
		}
		String signBefore = content + privateKey;
		//System.out.print("signBefore===" + signBefore);
		// *****************************************************************
		// ��alipay�յ���Ϣ����ѽ��ܵ���Ϣд����־
		// ���ļ������ں�Ӧ�÷����� ���ļ�ͬһĿ¼�£��ļ�����alipay log�ӷ�����ʱ��
		try {
			FileWriter writer = new FileWriter("D:\\alipay_log"
					+ System.currentTimeMillis() + ".txt");
			writer.write(signBefore);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// *********************************************************************
			return Md5Encrypt.md5(signBefore,paySource);

	}

	/** 
	 * @param properties
	 * @return
	 */
	public static String getSignatureContent(Properties properties) {
		StringBuffer content = new StringBuffer();
		List keys = new ArrayList(properties.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = properties.getProperty(key);

			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}
		
		return content.toString();
	}

	/** 
	 * @param privateKey
	 */
	public static String getContent_public(Map params, String privateKey) {
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		

		return prestr + privateKey;
	}

	/**
	 * @param urlvalue
	 * @return
	 */

	public static String checkurl(String urlvalue) {

		String inputLine = "";

		try {
			URL url = new URL(urlvalue);

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			inputLine = in.readLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return inputLine;
	}

	/**
	 * @return
	 * @throws Exception 
	 */
	 
	public String creatteInvokeUrl(PaySource paySource) throws Exception {
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		String key = payConfig.getKey();
		
		Map params = new HashMap();
		params.put("service", "query_timestamp");
		params.put("partner", payConfig.getPartnerID());
		params.put("_input_charset", payConfig.getCharSet());

		String prestr = "";
		prestr = prestr + key;
		String sign = com.alipay.sign.Md5Encrypt.md5(getContent_public(params,
				key),paySource);

		String parameter = payConfig.getAlipayGateway();
		List keys = new ArrayList(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			String value = (String) params.get(keys.get(i));
			if (value == null || value.trim().length() == 0) {
				continue;
			}
			try {
				parameter = parameter + keys.get(i) + "="
						+ URLEncoder.encode(value, payConfig.getCharSet()) + "&";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}// for end
		parameter = parameter + "sign=" + sign + "&sign_type=" + "MD5";
		
		return parameter;
	}


	


}
