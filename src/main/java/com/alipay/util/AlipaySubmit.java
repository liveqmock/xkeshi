package com.alipay.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.sign.MD5;
import com.alipay.sign.RSA;
import com.alipay.util.httpClient.HttpProtocolHandler;
import com.alipay.util.httpClient.HttpRequest;
import com.alipay.util.httpClient.HttpResponse;
import com.alipay.util.httpClient.HttpResultType;


/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit { 

	   /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
	
    /**
     * 生成签名结果
     * @param sPara 要签名的数组
     * @return 签名结果字符串
     */
	public static String buildRequestMysign(Map<String, String> sPara ,PaySource paySource) {
		String signKey = sPara.remove("sign_key");
    	String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
    	String mysign = "";
    	PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        if(payConfig.getSignType().equals("MD5") ) {
        	if(StringUtils.isBlank(signKey)){
        		mysign = MD5.sign(prestr, payConfig.getKey(),  payConfig.getCharSet());
        	}else{
        		mysign = MD5.sign(prestr, signKey,  payConfig.getCharSet());
        	}
        }else if(payConfig.getSignType().equals("0001") ){
        	mysign = RSA.sign(prestr,  payConfig.getPrivateKey(),  payConfig.getCharSet());
        }
        return mysign;
    }
	
    /**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp ,PaySource  paySource) {
    	PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        //除去数组中的空值和签名参数
        Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
        //生成签名结果
        String mysign = buildRequestMysign(sPara,paySource);

        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        if(! sPara.get("service").equals("alipay.wap.trade.create.direct") && ! sPara.get("service").equals("alipay.wap.auth.authAndExecute")) {
        	sPara.put("sign_type", payConfig.getSignType());
        }

        return sPara;
    }

    /**
     * 建立请求，以表单HTML形式构造（默认）
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public static String buildRequest(  Map<String, String> sParaTemp, 
    								 String strMethod, String strButtonName ,PaySource paySource) {
    	PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp,paySource);
        List<String> keys = new ArrayList<String>(sPara.keySet());

        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\" action=\"" + ALIPAY_GATEWAY_NEW
                      + "_input_charset=" +  payConfig.getCharSet() + "\" method=\"" + strMethod
                      + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sPara.get(name);

            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");

        return sbHtml.toString();
    }
    
    /**
     * 建立请求，以表单HTML形式构造，带文件上传功能
     * @paramALIPAY_GATEWAY_NEW 支付宝网关地址
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @param strParaFileName 文件上传的参数名
     * @return 提交表单HTML文本
     */
    public static String buildRequest(String ALIPAY_GATEWAY_NEW, Map<String, String> sParaTemp, String strMethod
    								 , String strButtonName, String strParaFileName ,PaySource paySource) {
    	PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp,paySource);
        List<String> keys = new ArrayList<String>(sPara.keySet());

        StringBuffer sbHtml = new StringBuffer();

        sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\"  enctype=\"multipart/form-data\" action=\"" + ALIPAY_GATEWAY_NEW
                      + "_input_charset=" +  payConfig.getCharSet() + "\" method=\"" + strMethod
                      + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sPara.get(name);

            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }
        
        sbHtml.append("<input type=\"file\" name=\"" + strParaFileName + "\" />");

        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");

        return sbHtml.toString();
    }
    
    /**
     * 建立请求，以模拟远程HTTP的POST请求方式构造并获取支付宝的处理结果
     * 如果接口中没有上传文件参数，那么strParaFileName与strFilePath设置为空值
     * 如：buildRequest("", "",sParaTemp)
     * @paramALIPAY_GATEWAY_NEW 支付宝网关地址
     * @param strParaFileName 文件类型的参数名
     * @param strFilePath 文件路径
     * @param sParaTemp 请求参数数组
     * @return 支付宝处理结果
     * @throws Exception
     */
    public static String buildRequest(String ALIPAY_GATEWAY_NEW, String strParaFileName, String strFilePath,
    								 Map<String, String> sParaTemp ,PaySource paySource) throws Exception {
        //待请求参数数组
        Map<String, String> sPara = buildRequestPara(sParaTemp,paySource);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();

        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        
        PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
        
        //设置编码集
        request.setCharset( payConfig.getCharSet());

        request.setParameters(AlipayBase.generatNameValuePair(sPara));
        if(StringUtils.isBlank(ALIPAY_GATEWAY_NEW)){
        	ALIPAY_GATEWAY_NEW = AlipaySubmit.ALIPAY_GATEWAY_NEW;
        }
        request.setUrl(ALIPAY_GATEWAY_NEW+"_input_charset="+ payConfig.getCharSet());

        HttpResponse response = httpProtocolHandler.execute(request,strParaFileName,strFilePath);
        if (response == null) {
            return null;
        }
        
        String strResult = response.getStringResult(paySource);

        return strResult;
    }
  
    
     
}
