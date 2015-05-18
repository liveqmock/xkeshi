package com.xpos.api;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xkeshi.service.XShopService;
import com.xkeshi.utils.Tools;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Configuration;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.Terminal.TerminalType;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.entity.security.Account;
import com.xpos.common.service.AccountService;
import com.xpos.common.service.ConfigurationService;
import com.xpos.common.service.OperatorShiftService;
import com.xpos.common.service.ShopService;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.TokenUtil;


public class MerchantDeviceResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(OrderResource.class);
	private String TOKEN_SEPERATOR = "\\|\\|\\|";
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private ShopService shopService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ConfigurationService confService;
	
	@Autowired
	private XShopService  xShopService  ;
	
	@Autowired
	private OperatorShiftService  operatorShiftService ;
	
	/**
	 * merchant/device/binding
	 * @return
	 */
	@Put("json")
	public Representation deviceBinding(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-3","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			JSONObject json = entity.getJsonObject();
			String adminToken = json.getString("token");
			String deviceNumber = json.getString("deviceNumber");
			if(StringUtils.isBlank(adminToken)){
				return new JsonRepresentation(new ValidateError("2201","token不能为空"));
			}
			if(StringUtils.isBlank(deviceNumber)){
				return new JsonRepresentation(new ValidateError("2201","deviceNumber不能为空"));
			}
			
			String plainText = TokenUtil.decrypt(adminToken);
			String[] array = plainText.split(TOKEN_SEPERATOR);
			if(array.length != 2){
				return new JsonRepresentation(ResCode.General.PARAMS_NOT_MATCHED);
			}else{
				String userName = array[0];
				String password = array[1];
				
				try {
					
					//1.校验商户绑定的账号
					if(!accountService.verifyAccount(userName,password)) {
						return new JsonRepresentation(new ValidateError("2202","商户绑定设备的账号或密码错误"));
					}
					
					//2.根据登陆账号获取对应商户
					Account account = accountService.findAccountByUsername(userName);
					Shop shop = (Shop)accountService.findBusinessByAccount(account);
					
					//3.校验POS机终端是否已存在
					Terminal _terminal = terminalService.findTerminalByDevice(deviceNumber);
					if(_terminal != null && !_terminal.getShop().getId().equals(shop.getId())){
						return new JsonRepresentation(new ValidateError("2203","设备号已被使用"));
					}else if(_terminal != null && _terminal.getShop().getId().equals(shop.getId())){
						//可能第一次绑定时终端未收到绑定成功消息
						re = new JsonRepresentation(ResCode.General.OK);
						JSONObject obj = re.getJsonObject();
						obj.putOnce("merchantName", shop.getFullName());
						obj.putOnce("mid", shop.getId());
						obj.putOnce("deviceSecret", _terminal.getDeviceSecret());
						return re;
					}
					
					//4.绑定终端
					Terminal terminal = new Terminal();
					terminal.setDeviceNumber(deviceNumber);
					terminal.setDeviceSecret(Tools.getUUID());
					terminal.setShop(shop);
					terminal.setTerminalType(TerminalType.CASHIER);  //TODO 先写死成“收银台”类型，后续需要改接口，传入参数设备类型
					terminalService.addTerminalByShopId(shop.getId(), terminal);
					re = new JsonRepresentation(ResCode.General.OK);
					JSONObject obj = re.getJsonObject();
					obj.putOnce("merchantName", shop.getFullName());
					obj.putOnce("mid", shop.getId());
					obj.putOnce("deviceSecret", terminal.getDeviceSecret());
					
				} catch (Exception e) {
					re = new JsonRepresentation(new ValidateError("2202","商户绑定设备的账号或密码错误"));
				}
				return re;
			
			}
		}catch (Exception e) {
			logger.error("Binding device to merchant error. "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
	
	/**
	 * API 33: 终端解绑
	 */
	@Delete("json")
	public Representation merchantDeviceUnbind(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			JSONObject json = entity.getJsonObject();
			String deviceNumber = json.getString("deviceNumber");
			Long shopId = json.getLong("mid");
			if(StringUtils.isBlank(deviceNumber)){
				return new JsonRepresentation(new ValidateError("3301","deviceNumber不能为空"));
			}
			
			try {
				//1.校验POS机终端是否已存在
				Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
				if(terminal == null){
					return new JsonRepresentation(new ValidateError("3303","未找到相应设备"));
				}else if(!terminal.getShop().getId().equals(shopId)){
					return new JsonRepresentation(new ValidateError("3305","设备与商户不匹配"));
				}
				com.xkeshi.pojo.po.Shop shop = xShopService.findShopByShopId(shopId);
				//2.检查是否已经交接班
				if (shop != null && shop.getEnableShift()) {
					POSOperationLog operationLog = operatorShiftService.findOperatorSessionByDeviceNumber(terminal.getDeviceNumber());
					if (operationLog != null ) {
						return new JsonRepresentation(new ValidateError("3306","解绑失败,该设备未完成交接"));
					}
				}
				//3.解绑终端
				if(terminalService.removeTerminalById(terminal)){
					re = new JsonRepresentation(ResCode.General.OK);
				}else{
					re = new JsonRepresentation(new ValidateError("-1","解绑失败，请稍后再试"));
				}
			} catch (Exception e) {
				re = new JsonRepresentation(new ValidateError("3302","账号或密码错误"));
			}
			return re;
			
		}catch(JSONException je){
			return new JsonRepresentation(new ValidateError("3304","参数格式错误"));
		}catch (Exception e) {
			logger.error("Unbind device to merchant error. "+ e.getMessage(), e);
			return new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
	}
	
	
	/**
	 * API 17: 验证客户端版本
	 * @return
	 */
	@Get("json")
	public Representation check(){
		String clientType = getQuery().getFirstValue("type");
		String posVersion = getQuery().getFirstValue("version");
		
		boolean skipUpdate = false;
		String latestVersion = null; //最新的版本号
		String updateURL = null; //安装包下载地址
		String packageDesc = null; //新版本更新描述
		String packageSize = null; //安装包大小
		
		Configuration conf = confService.findByName("terminalVersion." + clientType);
		if(conf == null){
			return new JsonRepresentation(new ValidateError("1001", "无此客户端类型"));
		}
		latestVersion = conf.getValue();
		skipUpdate = compareVersion(latestVersion, posVersion);
		
		if(!skipUpdate){
			updateURL = confService.findByName("terminalDownloadUrl." + clientType).getValue();
			
			Configuration descConf = confService.findByName("terminalPackageDescription." + clientType);
			if(descConf != null){ //可能部分包没有该配置项，做空判断
				packageDesc = descConf.getValue();
			}
			
			Configuration sizeConf = confService.findByName("terminalPackageSize." + clientType);
			if(sizeConf != null){ //可能部分包没有该配置项，做空判断
				int byteSize = NumberUtils.toInt(sizeConf.getValue(), 0);
				if(byteSize < 1024 * 1024){ //小于1M，以K为单位
					packageSize = byteSize/1024 + "K";
				}else if(byteSize < 1024 * 1024 * 1024){ //小于1G，以M为单位
					packageSize = byteSize/(1024 * 1024) + "M";
				}
			}
		}
		
		JsonRepresentation re = null;
		if(skipUpdate){
			re = new JsonRepresentation(ResCode.General.OK);
		}else{
			re = new JsonRepresentation(new ValidateError("1", "有新版本"));
			JSONObject json = re.getJsonObject();
			json.put("version", latestVersion);
			json.put("url", updateURL+"?t="+new Random().nextInt(100000));
			if(StringUtils.isNotBlank(packageDesc)){
				json.put("description", packageDesc);
			}
			if(StringUtils.isNotBlank(packageSize)){
				json.put("size", packageSize);
			}
		}
		return re;
	}
}
