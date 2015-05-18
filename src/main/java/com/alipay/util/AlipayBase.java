package com.alipay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.sign.MD5;
import com.alipay.sign.Md5Encrypt;
import com.alipay.sign.RSA;
/**
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.alipay.config.AlipayConfig;
**/
/**
 *功能：支付宝接口公用函数
 *详细：该页面是请求、通知返回两个文件所调用的公用函数核心处理文件，不需要修改
 *版本：3.0
 *修改日期：2010-07-16
 '说明：
 '以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 '该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

*/
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AlipayBase {
	/** 
	 * 功能：生成签名结果
	 * @param sArray 要加密的数组
	 * @param key 安全校验码
	 * @return 签名结果字符串
	 */
	public static String BuildMysign(Map sArray, String key,PaySource paySource) {
		String prestr = CreateLinkString(sArray);  //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		prestr = prestr + key;                     //把拼接后的字符串再与安全校验码直接连接起来
		String mysign = Md5Encrypt.md5(prestr,paySource);
		return mysign;
	}
	 /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestMysign(Map<String, String> sPara ,PaySource paySource) {
    	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
    	String mysign = "";
    	PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        if(payConfig.getSignType().equals("MD5") ) {
        	mysign = MD5.sign(prestr, payConfig.getKey(),  payConfig.getCharSet());
        }
        if(payConfig.getSignType().equals("0001") ){
        	mysign = RSA.sign(prestr,  payConfig.getPrivateKey(),  payConfig.getCharSet());
        }
        return mysign;
    }
	
	/** 
	 * 功能：除去数组中的空值和签名参数
	 * @param sArray 加密参数组
	 * @return 去掉空值与签名参数后的新加密参数组
	 */
	
	public static Map ParaFilter(Map sArray){
		List keys = new ArrayList(sArray.keySet());
		Map sArrayNew = new HashMap();
		
		for(int i = 0; i < keys.size(); i++){
			String key = (String) keys.get(i);
			String value = "";
			if(sArray.get(key) instanceof String)
				value = (String) sArray.get(key);
			else if(sArray.get(key) instanceof String[])
				value = ((String[]) sArray.get(key))[0];
			
			if(value.equals("") || value == null || 
					key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type")){
				continue;
			}
			
			sArrayNew.put(key, value);
		}
		
		return sArrayNew;
	}
	
	/** 
	 * 功能：把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String CreateLinkString(Map params){
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
	
	/** 
	 * 功能：把数组所有元素按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 应用场景：使用场景：GET方式请求时，对URL的中文进行编码
	 * @param params 需要排序并参与字符拼接的参数组
	 * @param input_charset 编码格式
	 * @return 拼接后字符串
	 */
	public static String CreateLinkString_urlencode(Map params, String input_charset){
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			try {
				prestr = prestr + key + "=" + URLEncoder.encode(value,input_charset) + "&";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return prestr;
	}
	
	/** 
	 * 功能：写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
	 * @param sWord 要写入日志里的文本内容
	 */
	public static void LogResult(String sWord){
		// 该文件存在于和应用服务器 启动文件同一目录下，文件名是alipay log加服务器时间
		try {
			/*FileWriter writer = new FileWriter("D:\\alipay_log" + System.currentTimeMillis() + ".txt");
			writer.write(sWord);
			writer.close();*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数
     * 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
     * @return 时间戳字符串
     * @throws IOException
     * @throws DocumentException
     * @throws MalformedURLException
     */
	public static String query_timestamp(PaySource paySource) throws MalformedURLException,
                                                        DocumentException, IOException {
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        //构造访问query_timestamp接口的URL串
        String strUrl = payConfig.getAlipayGateway()+"service=query_timestamp&partner=" + payConfig.getPartnerID();
        StringBuffer result = new StringBuffer();

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new URL(strUrl).openStream());

        List<Node> nodeList = doc.selectNodes("//alipay/*");

        for (Node node : nodeList) {
            // 截取部分不需要解析的信息
            if (node.getName().equals("is_success") && node.getText().equals("T")) {
                // 判断是否有成功标示
                List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
                for (Node node1 : nodeList1) {
                    result.append(node1.getText());
                }
            }
        }

        return result.toString();
    }
	
    /**
     * MAP类型数组转换成NameValuePair类型
     * @param properties  MAP类型数组
     * @return NameValuePair类型数组
     */
    public static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }

        return nameValuePair;
    }
    /**
     * 解析远程模拟提交后返回的信息，获得token
     * @param text 要解析的字符串
     * @return 解析结果
     * @throws Exception 
     */
    public static String getRequestToken(String text , PaySource paySource) throws Exception {
    	String request_token = "";
    	//以“&”字符切割字符串
    	String[] strSplitText = text.split("&");
    	//把切割后的字符串数组变成变量与数值组合的字典数组
    	Map<String, String> paraText = new HashMap<String, String>();
        for (int i = 0; i < strSplitText.length; i++) {

    		//获得第一个=字符的位置
        	int nPos = strSplitText[i].indexOf("=");
        	//获得字符串长度
    		int nLen = strSplitText[i].length();
    		//获得变量名
    		String strKey = strSplitText[i].substring(0, nPos);
    		//获得数值
    		String strValue = strSplitText[i].substring(nPos+1,nLen);
    		//放入MAP类中
    		paraText.put(strKey, strValue);
        }
         PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        
    	if (paraText.get("res_data") != null) {
    		String res_data = paraText.get("res_data");
    		//解析加密部分字符串（RSA与MD5区别仅此一句）
    		if(payConfig.getSignType().equals("0001")) {
    			res_data = RSA.decrypt(res_data,  payConfig.getPrivateKey(),  payConfig.getCharSet());
    		}
    		
    		//token从res_data中解析出来（也就是说res_data中已经包含token的内容）
    		Document document = DocumentHelper.parseText(res_data);
    		request_token = document.selectSingleNode( "//direct_trade_create_res/request_token" ).getText();
    	}
    	return request_token;
    }
}
