package com.alipay.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.sign.MD5;
import com.alipay.sign.RSA;


@SuppressWarnings({ "rawtypes" })
public class AlipayNotify {
	  /**
     * 支付宝消息验证地址
     */
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";
    
    /**
     * 验证消息是否是支付宝发出的合法消息，验证callback
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verifyReturn(Map<String, String> params,PaySource paySource) {
	    String sign = "";
	    //获取返回时的签名验证结果
	    if(params.get("sign") != null) {sign = params.get("sign");}
	    //验证签名
	    boolean isSign = getSignVeryfy(params, sign, true, paySource);

        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
	    //AlipayCore.logResult(sWord);

        //判断isSign是否为true
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        if (isSign) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证消息是否是支付宝发出的合法消息，验证服务器异步通知
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verifyNotify(Map<String, String> params,PaySource paySource) throws Exception {
    	
    	//获取是否是支付宝服务器发来的请求的验证结果
    	String responseTxt = "true";
    	try {
        	//XML解析notify_data数据，获取notify_id
	    	Document document = DocumentHelper.parseText(params.get("notify_data"));
	    	String notify_id = document.selectSingleNode( "//notify/notify_id" ).getText();
			responseTxt = verifyResponse(notify_id,paySource);
    	} catch(Exception e) {
    		responseTxt = e.toString();
    	}
    	
    	//获取返回时的签名验证结果
	    String sign = "";
	    if(params.get("sign") != null) {sign = params.get("sign");}
	    boolean isSign = getSignVeryfy(params, sign,false, paySource);

        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
	    //AlipayCore.logResult(sWord);

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * 支付宝退款异步接收消息
     * 验证消息是否是支付宝发出的合法消息
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params   ,PaySource paySource) {

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
    	String responseTxt = "true";
		if(params.get("notify_id") != null) {
			String notify_id = params.get("notify_id");
			responseTxt = verifyResponse(notify_id ,paySource);
		}
	    String sign = "";
	    if(params.get("sign") != null) {sign = params.get("sign");}
	    boolean isSign = getSignVeryfy(params, sign ,true  ,paySource );

        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
	    //AlipayCore.logResult(sWord);

        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 解密
     * @param inputPara 要解密数据
     * @param PaySource  支付类型
     * @return 解密后结果
     */
    public static Map<String, String> decrypt(Map<String, String> inputPara ,PaySource paySource) throws Exception {
    	PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
    	inputPara.put("notify_data", RSA.decrypt(inputPara.get("notify_data"), payConfig.getPrivateKey(), payConfig.getCharSet()));
    	return inputPara;
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @param isSort 是否排序
     * @return 生成的签名结果
     */
	private static boolean getSignVeryfy(Map<String, String> Params, String sign, boolean isSort ,PaySource paySource ) {
    	//过滤空值、sign与sign_type参数
    	Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        //获取待签名字符串
    	String preSignStr = "";
    	if(isSort) {
    		preSignStr = AlipayCore.createLinkString(sParaNew);
    	} else {
    		preSignStr = AlipayCore.createLinkStringNoSort(sParaNew);
    	}
        //获得签名验证结果
        boolean isSign = false;
        PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        if(payConfig.getSignType().equals("MD5") ) {
        	isSign = MD5.verify(preSignStr, sign, payConfig.getKey(), payConfig.getCharSet());
        }
        if(payConfig.getSignType().equals("0001")){
        	isSign = RSA.verify(preSignStr, sign, payConfig.getAliPublicKey(), payConfig.getCharSet());
        }
        return isSign;
    }

    /**
    * 获取远程服务器ATN结果,验证返回URL
    * @param notify_id 通知校验ID
    * @return 服务器ATN结果
    * 验证结果集：
    * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
    * true 返回正确信息
    * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
    */
    private static String verifyResponse(String notify_id,PaySource paySource) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
         PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        String partner = payConfig.getPartnerID();
        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;
        return checkUrl(veryfy_url);
    }

    /**
    * 获取远程服务器ATN结果
    * @param urlvalue 指定URL路径地址
    * @return 服务器ATN结果
    * 验证结果集：
    * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
    * true 返回正确信息
    * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
    */
    private static String checkUrl(String urlvalue) {
        String inputLine = "";

        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
                .getInputStream()));
            inputLine = in.readLine().toString();
        } catch (Exception e) {
            e.printStackTrace();
            inputLine = "";
        }

        return inputLine;
    }

	
	/**
	 * *功能：根据反馈回来的信息，生成签名结果
	 * @param Params 通知返回来的参数数组
	 * @param key 安全校验码
	 * @return 生成的签名结果
	 */
	public static String GetMysign(Map Params, String key, PaySource paySource){
				
		Map sParaNew = AlipayBase.ParaFilter(Params);//过滤空值、sign与sign_type参数
		String mysign = AlipayBase.BuildMysign(sParaNew, key,paySource);//获得签名结果
		
		return mysign;
	}
	
	/**
	* *功能：获取远程服务器ATN结果,验证返回URL
	* @param notify_id 通知校验ID
	* @return 服务器ATN结果
	* 验证结果集：
	* invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
	* true 返回正确信息
	* false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	*/
	public static String Verify(String notify_id ,PaySource paySource){
		//获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		String transport = payConfig.getTransPort();
		String partner = payConfig.getPrivateKey();
		String veryfy_url = "";
		if(transport.equalsIgnoreCase("https")){
			veryfy_url = "https://www.alipay.com/cooperate/gateway.do?service=notify_verify";
		} else{
			veryfy_url = "http://notify.alipay.com/trade/notify_query.do?";
		}
		veryfy_url = veryfy_url + "&partner=" + partner + "&notify_id=" + notify_id;
		
		String responseTxt = CheckUrl(veryfy_url);
		
		return responseTxt;
	}
	
	/**
	* *功能：获取远程服务器ATN结果
	* @param urlvalue 指定URL路径地址
	* @return 服务器ATN结果
	* 验证结果集：
	* invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
	* true 返回正确信息
	* false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	*/
	public static String CheckUrl(String urlvalue){
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
	 * 验证消息是否是支付宝发出的合法消息
	 * @param params 通知返回来的参数数组
	 * @return 验证结果
	 */
	public static boolean verify(Map<String, String> params) {

		//判断responsetTxt是否为true，isSign是否为true
		//responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
		//isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
		String responseTxt = "true";
		if(params.get("notify_id") != null) {
			String notify_id = params.get("notify_id");
			String partner = params.remove("partner");
			responseTxt = verifyResponse(notify_id, partner);
		}
		String sign = "";
		if(params.get("sign") != null) {sign = params.get("sign");}
		boolean isSign = getSignVerify(params, sign);
		
		if (isSign && "true".equals(responseTxt)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取远程服务器ATN结果,验证返回URL
	 * @param notify_id 通知校验ID
	 * @return 服务器ATN结果
	 * 验证结果集：
	 * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 
	 * true 返回正确信息
	 * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 */
	private static String verifyResponse(String notify_id, String partner) {
		//获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
		String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;
		return checkUrl(veryfy_url);
	}
	
	/**
	 * 根据反馈回来的信息，生成签名结果
	 * @param Params 通知返回来的参数数组
	 * @param sign 比对的签名结果
	 * @return 生成的签名结果
	 */
	private static boolean getSignVerify(Map<String, String> Params, String sign) {
		String signKey = Params.remove("sign_key");
		//过滤空值、sign与sign_type参数
		Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
		//获取待签名字符串
		String preSignStr = AlipayCore.createLinkString(sParaNew);
		//获得签名验证结果
		boolean isSign = false;
		if(Params.get("sign_type").equals("MD5") ) {
			isSign = MD5.verify(preSignStr, sign, signKey, "UTF-8");
		}
		return isSign;
	}


}
