package com.xpos.api;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.xpos.api.param.POSOperation;
import com.xpos.api.result.ResCode;
import com.xpos.api.result.ValidateError;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.service.TerminalService;
import com.xpos.common.utils.BeanUtil;

public class TerminalOptResource extends BaseResource {
	private Logger logger = LoggerFactory.getLogger(TerminalOptResource.class);
	
	@Autowired
	private TerminalService terminalService;
	
	/**
	 * API 16: 记录客户端操作记录
	 * @param entity
	 * @return
	 */
	@Post("json")
	public Representation record(JsonRepresentation entity){
		if(entity == null){
			return new JsonRepresentation(new ValidateError("-2","post请求body的json参数不能为空"));
		}
		JsonRepresentation re = null;
		try {
			String jsonStr = entity.getText();
			POSOperation operation = JSON.parseObject(jsonStr, POSOperation.class);
			String validation = BeanUtil.validate(operation);
			if(validation != null){
				return new JsonRepresentation(new ValidateError("1601",validation));
			}
			
			//take care of device_number here...
			String deviceNumber = operation.getDeviceNumber();
			Terminal terminal = terminalService.findTerminalByDevice(deviceNumber);
			if(terminal == null){
				return new JsonRepresentation(new ValidateError("1604","设备未注册"));
			}else if(terminal.getShop() == null){
				return new JsonRepresentation(new ValidateError("1603","指定商户不存在/已删除"));
			}else if(!terminal.getShop().getId().equals(operation.getMidLong())){
				//return new JsonRepresentation(new ValidateError("1602","商户与设备不匹配"));
			}
			
			POSOperationLog operationLog = new POSOperationLog();
			operationLog.setDeviceNumber(operation.getDeviceNumber());
			operationLog.setAction(operation.getAct());
			operationLog.setType(operation.getType());
			operationLog.setVersion(operation.getVersion());
			terminalService.appendOperationRecord(operationLog);
			re = new JsonRepresentation(ResCode.General.OK);
		} catch (Exception e) {
			logger.error("Cannot update member info due to "+ e.getMessage(), e);
			re = new JsonRepresentation(ResCode.General.SERVER_INTERNAL_ERROR);
		}
		return re;
	}
}
