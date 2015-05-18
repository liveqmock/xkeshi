package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.RefundLog;


public interface RefundLogService { 
	
	public int insert(RefundLog  refundLog);
	
	public List<RefundLog>  refundLogs(String refundCode);
}
