package com.xpos.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.drongam.hermes.entity.SMS;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;
import com.xpos.common.service.POSTransactionService;
import com.xpos.common.service.SMSService;
import com.xpos.common.service.ShopService;



//<?xml version="1.0" encoding="GB2312" ?> 
//<map> 
//<F2  v='6161'/> 
//<F4  v='1'/> 
//<F12 v='111534'/>
//<F13 v='0927'/>
//<F39 v='00'/>
//<F41 v='122010000001'/> 
//<F42 v='001075548160000'/> 
//<F45 v='20130303015519811111, 3270111534447804'/> 
//<MerSignv ='64e1c1d4beb931167523c813bd1654fd31659ed835a7711907d3f833b3eaef0a50862cab986055c5745578db2b0b9d96e6d8e2576666a4850c4bfc202ab12a953bebc1700fe66b2848d0d2453bde4ab4e8671c4231332f043c775bf83357e76d7563dc4a08b7e7e966603061d0bcab039528f69ec2fac23fe7367e8381d7aa6e'/>   
//</map>

//<map>  
//	<F39 v='00'/>     
//</map>



public class OrderPaymentXMLResource extends BaseResource{
	private Logger logger = LoggerFactory.getLogger(OrderPaymentXMLResource.class);

	@Autowired
	private POSTransactionService transactionService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private SMSService smsService;
	
	
//	@Autowired
//	private ShortUrlService shortUrlService;
	
	@Post("xml")
	public Representation updateOrderStatus(DomRepresentation entity){
		if(entity ==null){
			return result("11");
		}
		NodeList nodes;
		try {
			logger.info("==> OrderPaymentXML: ["+entity.getText()+"]");
			Element root = entity.getDocument().getDocumentElement();
			nodes = root.getChildNodes();
			POSTransaction transaction = new POSTransaction();
			StringBuffer date = new StringBuffer();
			for(int i = 0; i < nodes.getLength();i++){
				Node node = nodes.item(i);
				if(node instanceof Element){
					Element el = (Element)node;
					String value = el.getAttribute("v").trim();
					switch(node.getNodeName()){
					case "F2":
						//value is cardNum;
						transaction.setCardNumber(value);
						break;
					case "F4":
						//value is sum
						transaction.setSum(BigDecimal.valueOf(0.01d).multiply(BigDecimal.valueOf(Integer.valueOf(value))).setScale(2, RoundingMode.HALF_UP));
						break;
					case "F8":
						//value is location
						transaction.setLocation(value);
						break;
					case "F12":
						//value is time
						date.append(value);
						break;
					case "F13":
						//value is date
						DateTime dateTime = new DateTime();
						date.insert(0, value).insert(0, dateTime.getYear());
						break;
					case "F39":
						//value is response code
						transaction.setResponseCode(value);
						break;
					case "F41":
						//value is terminalId
						transaction.setTerminal(value);
						break;
					case "F42":
						//value is pos gateway account
						POSGatewayAccount account = new POSGatewayAccount();
						account.setAccount(value);
						transaction.setPOSGatewayAccount(account);
						break;
					case "F45":
						//value is orderId+serial
						String[] array = value.split(",");
						String orderId = array[0];
						String serial = array[1];
						transaction.setCode(orderId.trim());
						transaction.setSerial(serial.trim());
						break;
					case "MerSign":
						//do nothing
						
						break;
					}
				}
				
			}
			
			//parse payment timestamp
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMddHHmmss");
			try{
				DateTime paymentTs = fmt.parseDateTime(date.toString());
				transaction.setTradeDate(paymentTs.toDate());
			}catch(Exception e){
				throw new IllegalArgumentException("wrong payment date time:"+date.toString());
			}
			
			transaction.setStatus(POSTransactionStatus.PAID_SUCCESS);
			boolean result = transactionService.updatePOSTransactionByCode(transaction);
			if(!result){
				logger.error("fail to update order data, order code:"+transaction.getCode());
				return result("12");
			}else{
				POSTransaction _transaction = transactionService.findTransactionByCode(transaction.getCode());
				String mobile = _transaction.getMobile();
				//如果订单手机号不为空，则下发电子账单短信。短信内容格式：您于【订单支付完成时间，格式xx月xx日xx时xx分】在【商家简称】消费人民币【消费金额】元，查看账单详情【账单链接地址】[爱客仕xPos]
				if(StringUtils.isNotBlank(mobile)){
					String url = "http://xka.me/ebill/"+_transaction.getCode(); //电子账单地址
					Shop shop = shopService.findShopByIdIgnoreVisible(_transaction.getBusinessId());
					StringBuffer content = new StringBuffer();
					content.append("您于").append(new DateTime(_transaction.getTradeDate()).toString("MM月dd日HH时mm分")).append("在 ").append(shop.getName())
					.append(" 消费人民币：").append(_transaction.getSum()).append("元，查看账单详情：").append(url);
					SMS sms = new SMS();
					sms.setMobile(mobile);
					sms.setMessage(content.toString());
					smsService.sendSMSAndDeductions(shop.getId() ,BusinessType.SHOP,sms,null,"发送电子账单" );
				}
			}
		} catch (Exception e) {
			logger.error("Cannot add order due to "+ e.getMessage(), e);
			return result("12");
		}
		
		return result("00");
	}
	
	
	private DomRepresentation result(String code) {
		DomRepresentation re = null;
		try {
			re = new DomRepresentation();
			Document doc = re.getDocument();
			Element map = doc.createElement("map");
			doc.appendChild(map);
			Element F39 = doc.createElement("F39");
			F39.setAttribute("v", code);
			map.appendChild(F39);
			return re;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return re;

	}
	
}
