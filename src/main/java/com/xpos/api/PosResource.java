package com.xpos.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.service.PrepaidService;
import com.xkeshi.service.XShopService;
import com.xpos.api.param.POS;
import com.xpos.api.param.SimpleGatewayAccount;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.ShopInfo;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.service.OperatorService;
import com.xpos.common.service.OperatorShiftService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.BeanUtil;
import com.xpos.common.utils.TokenUtil;
import com.xpos.common.utils.UUIDUtil;


public class PosResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(PosResource.class);
	private String TOKEN_SEPERATOR = "\\|\\|\\|";

	@Autowired
	private TerminalService terminalService;
	
	@Autowired
	private OperatorService operatorService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private OperatorShiftService  operatorShiftService ;
	
	@Autowired
	private PrepaidService  prepaidService ;
	
	@Autowired
	private XShopService   xShopService  ;
	
	/**
	 * POS终端操作员登陆
	 * /api/pos/login
	 * @return
	 */
	@Post("json")
	public Representation login(JsonRepresentation entity){
				
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-3","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			POS pos = JSON.parseObject(jsonStr, POS.class);
			String validation = BeanUtil.validate(pos);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("0101",validation));
			}else if(StringUtils.isBlank(pos.getDeviceNumber())){
				return new JsonRepresentation(new ValidateError("0102","deviceNumber不能为空"));
			}
			
			String plainText = TokenUtil.decrypt(pos.getToken());
			String[] array = plainText.split(TOKEN_SEPERATOR);
			if(array.length != 2){
				return new JsonRepresentation(ResCode.General.PARAMS_NOT_MATCHED);
			}else{
				//take care of device_number here...
				String deviceNumber = pos.getDeviceNumber();
				Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
				if(terminal == null){
					return new JsonRepresentation(new ValidateError("0103","设备未注册"));
				}
				
				String userName = array[0];
				String passwd = array[1];
				Shop shop = terminal.getShop();
				if(shop == null){
					return new JsonRepresentation(new ValidateError("0103","设备未注册"));
				}
				
				try {
					//verify username and password here...
					Operator operator = new Operator();
					operator.setUsername(userName);
					operator.setPassword(passwd);
					operator.setShop(shop);
					if(!operatorService.login(operator)){
						return new JsonRepresentation(new ValidateError("0110","操作员账号或密码错误"));
					}
					
					ShopInfo shopInfo = shopService.findShopInfoByShopId(shop.getId());
					if(shopInfo == null || shopInfo.getConsumeType() == null){
						return new JsonRepresentation(new ValidateError("0105","消费类型未设置，请在后台设置后重新登录"));
					}
					com.xkeshi.pojo.po.Shop shopPo = xShopService.findShopByShopId(shop.getId());
					/**交接班*/
					if (shopPo.getEnableShift()) {
						operatorShiftService.isOperatorShifted(deviceNumber , operator.getId(),pos.getOperatorSessionCode());
					}
					terminalService.updateLastLoginDate(terminal.getId(), new Date());
					List<POSGatewayAccount> posGatewayAccounts = shopInfo.getPosAccountList();
					List<SimpleGatewayAccount> simpleGatewayAccountList = convertToSimpleList(posGatewayAccounts);
					boolean hasPrepaidCardChargeRules = prepaidService.hasPrepaidCardChargeRules(
																	GlobalSource.getIDByName(GlobalSource.metaBusinessTypeList, "商户"),
																	shop.getId());
					
					re = new JsonRepresentation(ResCode.General.OK);
					JSONObject json = re.getJsonObject();
					json.put("mid", shop.getId());
					json.put("merchantName", shop.getName());
					json.put("shopName", shop.getName());
					json.put("shopAddress", shop.getAddress());
					json.put("shopContact", shop.getContact());
					json.put("externalMid", simpleGatewayAccountList);
					json.put("operator", operator.getUsername());
					json.put("operatorName", operator.getUsername());
					json.put("operatorId", operator.getId());
					json.put("serverTimestamp", System.currentTimeMillis()/1000);
					json.put("hasPrepaidCardChargeRules", hasPrepaidCardChargeRules);
					String csrf_token = UUIDUtil.getRandomString(32);
					json.put("csrfToken", csrf_token);
					json.put("consumeType", shopInfo.getConsumeType());
					json.put("enableShift", shopPo.getEnableShift());
					json.put("visibleShiftReceivableData", shopPo.getVisibleShiftReceivableData());
					json.put("deviceCode", terminal.getCode());
					json.put("enableMultiplePayment", shop.isEnableMultiplePayment());
				} catch (Exception e) {
					logger.error("POS收银员账户登录失败", e);
					re = new JsonRepresentation(new ValidateError("0104","账号或密码错误"));
				}
				return re;
			}
		}	catch (RuntimeException e) {
			return new JsonRepresentation(new ValidateError("0101","参数校验失败"));
		} catch (Exception e) {
			logger.error("Merchant device logon error. "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
	
	private List<SimpleGatewayAccount> convertToSimpleList(List<POSGatewayAccount> posGatewayAccounts) {
		List<SimpleGatewayAccount> list = new ArrayList<>();
		if(!CollectionUtils.isEmpty(posGatewayAccounts)){
			for(POSGatewayAccount account : posGatewayAccounts){
				list.add(new SimpleGatewayAccount(account.getAccount(), account.getType(), account.getSignKey()));
			}
		}
		return list;
	}

}
