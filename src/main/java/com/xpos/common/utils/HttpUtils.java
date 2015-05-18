package com.xpos.common.utils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pwan on 14-2-7.
 */
public class HttpUtils {
	static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	private static int defaultTimeout = 5;

	private static RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(defaultTimeout*1000)
            .setConnectTimeout(defaultTimeout*1000)
            .setConnectionRequestTimeout(defaultTimeout*1000)
            .build();
	
	public static void setTimeout(int timeout){
        timeout = timeout < 3 ? 3 :timeout;
		requestConfig = RequestConfig.custom()
	            .setSocketTimeout(timeout*1000)
	            .setConnectTimeout(timeout*1000)
	            .setConnectionRequestTimeout(timeout*1000)
	            .build();
	}
	
	private static void setDefaultTimeout(){
		requestConfig = RequestConfig.custom()
	            .setSocketTimeout(defaultTimeout*1000)
	            .setConnectTimeout(defaultTimeout*1000)
	            .setConnectionRequestTimeout(defaultTimeout*1000)
	            .build();
	}

    public static String simpleGet(String uri){
    	logger.debug("Send HTTP/GET to "+ uri);
        if(StringUtils.isBlank(uri)){
            return "Invalid http uri provided.";
        }
        String responseStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(uri);
            httpget.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseStr = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            setDefaultTimeout();
        }
        return responseStr;
    }

    public static String jsonPost(String uri, String jsonStr){
    	logger.debug("Send HTTP/POST to "+ uri + ", with body: "+ jsonStr);
        return post(uri, jsonStr, ContentType.APPLICATION_JSON, "UTF-8");
    }

    public static String xmlPost(String uri, String xmlStr, String reqEncoding, String respEncoding){
    	logger.debug("Send HTTP/POST to "+ uri + ", with body: "+ xmlStr);
    	ContentType type = ContentType.create("application/xml", Charset.forName(reqEncoding));
        return post(uri, xmlStr, type, respEncoding);
    }
    
    private static String post(String uri, String rawStr, ContentType type, String respEncoding ){
        if(StringUtils.isBlank(uri)){
            return "Invalid http uri provided.";
        }
        String responseStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(uri);
            httppost.setConfig(requestConfig);
            HttpEntity reqEntity = new StringEntity(rawStr, type);
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseStr = EntityUtils.toString(entity, Charset.forName(respEncoding));
                }
            } finally {
                HttpClientUtils.closeQuietly(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
            setDefaultTimeout();
        }
        return responseStr;
    }
    
    public static String keyValuePost(String uri, Map<String, String> paramMap, String reqEncoding, String respEncoding ){
    	if(StringUtils.isBlank(uri)){
    		return "Invalid http uri provided.";
    	}
    	String responseStr = null;
    	CloseableHttpClient httpclient = HttpClients.createDefault();
    	try {
    		HttpPost httppost = new HttpPost(uri);
    		httppost.setConfig(requestConfig);
    		List<NameValuePair> paramList = convertMapParams(paramMap);
    		HttpEntity reqEntity = new UrlEncodedFormEntity(paramList, reqEncoding);
    		httppost.setEntity(reqEntity);
    		CloseableHttpResponse response = httpclient.execute(httppost);
    		try {
    			HttpEntity entity = response.getEntity();
    			if (entity != null) {
    				responseStr = EntityUtils.toString(entity, Charset.forName(respEncoding));
    			}
    		} finally {
    			HttpClientUtils.closeQuietly(response);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		HttpClientUtils.closeQuietly(httpclient);
    		setDefaultTimeout();
    	}
    	return responseStr;
    }

	private static List<NameValuePair> convertMapParams(Map<String, String> paramMap) {
		List<NameValuePair> paramList = new ArrayList<>();
		if(paramMap != null && paramMap.size() > 0){
			for(Entry<String, String> entry : paramMap.entrySet()){
				paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return paramList;
	}
	
	@SuppressWarnings({ "resource", "deprecation", "rawtypes", "unchecked" })
	public static String httpsGet(String reqURL, Map<String, String> paramMap){
		String responseContent = null;
		
		HttpClient httpClient = new DefaultHttpClient(); //创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { //创建TrustManager 
			public void checkClientTrusted(X509Certificate[] chain,  String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() { return null; }
		};
		try {
			//TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			//使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用 
			ctx.init(null, new TrustManager[] { xtm }, null);
			//创建SSLSocketFactory 
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上 
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			
			if(paramMap != null && paramMap.size() > 0){
				StringBuilder sb = new StringBuilder(reqURL).append("?");
				int i = 0;
				for(Entry<String, String> entry : paramMap.entrySet()){
					if(i++ > 0){
						sb.append("&");
					}
					sb.append(entry.getKey()).append("=").append(entry.getValue());
				}
				reqURL = sb.toString();
			}
			HttpGet httpget = new HttpGet(reqURL);
			logger.debug("call https get:" + httpget.getURI());
			ResponseHandler responseHandler = new BasicResponseHandler();
			responseContent = (String)httpClient.execute(httpget, responseHandler);
		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
			logger.error("call https get error", e);
		} finally {
			httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
		}
		return responseContent;
	}

	public static String jsonHttpsPost(String reqURL, String jsonString){
		return httpsPost(reqURL, jsonString, ContentType.APPLICATION_JSON);
	}
	
	private static String httpsPost(String reqURL, String rawString, ContentType type) {
		String responseContent = null;
		
		HttpClient httpClient = new DefaultHttpClient(); //创建默认的httpClient实例
		X509TrustManager xtm = new X509TrustManager() { //创建TrustManager 
			public void checkClientTrusted(X509Certificate[] chain,  String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() { return null; }
		};
		try {
			//TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			//使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用 
			ctx.init(null, new TrustManager[] { xtm }, null);
			//创建SSLSocketFactory 
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上 
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			
			HttpPost httpPost = new HttpPost(reqURL); //创建HttpPost
			HttpEntity reqEntity = new StringEntity(rawString, type);
			httpPost.setEntity(reqEntity);
			logger.debug("call https POST:" + httpPost.getURI());
			ResponseHandler responseHandler = new BasicResponseHandler();
			responseContent = (String)httpClient.execute(httpPost, responseHandler);
		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
			logger.error("call https POST error", e);
		} finally {
			httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源 
		}
		return responseContent;
	}

}
