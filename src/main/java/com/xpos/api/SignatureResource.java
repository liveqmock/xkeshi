package com.xpos.api;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;
import com.xpos.common.service.UpYunServiceImpl;
import com.xpos.common.utils.Base64Coder;


public class SignatureResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(SignatureResource.class);
	@Autowired
	private UpYunServiceImpl upYunService;
	
//	<?xml version="1.0" encoding="GB2312" ?> 
//	<map> 
//	<F0  v=' 9000'/> 
//	<F3  v=' 920000'/> 
//	<F42  v=' 9996'/>  
//	<F45 v='ME-201312040001545,3343171548987941,3343171548987943'/>
//	<Signature v=''/></map>

//	<map>  
//		<F39 v='00'/>     
//	</map>
	
	/** 联动优势订单提交后，客户签字图片回调 */
	@Post("xml")
	public Representation accept(DomRepresentation entity) throws Exception {
		if(entity ==null){
			return result("11");
		}
		NodeList nodes;
		try {
			Element root = entity.getDocument().getDocumentElement();
//			logger.debug("==> SignatureResource " + root.getTextContent());
			nodes = root.getChildNodes();
			POSTransaction transaction = new POSTransaction();
			byte[] signatureBinary = null;
			for(int i = 0; i < nodes.getLength();i++){
				Node node = nodes.item(i);
				if(node instanceof Element){
					Element el = (Element)node;
					String value = StringUtils.trim(el.getAttribute("v"));
					switch(node.getNodeName()){
					case "F42":
						transaction.setGatewayAccount(value);
						transaction.setGatewayType(POSGatewayAccountType.UMPAY);
						break;
					case "F45":
						String[] array = StringUtils.split(value, ",");
						transaction.setCode(array[0]);
						break;
					case "Signature":
						signatureBinary = Base64Coder.decode(value);
						break;
					}
				}
			}
			boolean re = upYunService.uploadImg(StringUtils.join("/order/signature/",transaction.getGatewayAccount(),"/",transaction.getCode(),".png"), signatureBinary);
			if(re){
				return result("00");
			}else{
				logger.error("fail to save signature to upyun, order code:"+transaction.getCode());
				return result("12");
			}
		} catch (Exception e) {
			logger.error("Cannot save signature to upyun due to "+ e.getMessage(), e);
			return result("12");
		}
		
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
