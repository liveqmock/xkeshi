package com.xpos.controller.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.xkeshi.pojo.vo.SystemParam;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.service.OperatorShiftService;


public abstract class BaseAPIController {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private OperatorShiftService  operatorShiftService   ;
	
	/** 获取操作员的当班会话  * */
	protected  String  getOperatorSession(SystemParam systemParam){
		try{
			String deviceNumber = systemParam.getDeviceNumber();
			Long operatorId = systemParam.getOperatorId();
			POSOperationLog posOperationLog = operatorShiftService.getOperatorSession(deviceNumber, operatorId);
			return posOperationLog != null ?  posOperationLog.getOperatorSessionCode() :  null;
		}catch (Exception e){
			logger.error(e.toString());
		}
		return  null;
	}
}
