package com.xpos.common.service;

import java.util.Date;
import java.util.List;

import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Terminal;
import com.xpos.common.entity.example.TerminalExample;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.utils.Pager;

public interface TerminalService {

	List<Operator> findOperatorsByShopId(Long id);
	
	List<Terminal> findTerminalsByShopId(Long id);

	Terminal findTerminalByDevice(String deviceNumber);
	
	/**
	 * 显示POS机终端
	 * @param pager
	 * @param example
	 * @return
	 */
	public Pager<Terminal> findTerminalList(Pager<Terminal> pager, TerminalExample example);
	
	public String addTerminalByShopId(Long id,Terminal terminal) ;
	
	public boolean removeTerminalById(Terminal terminal);

	/** 更新最后登录时间 */
	boolean updateLastLoginDate(Long id, Date date);

	/** 记录POS客户端操作日志 */
	boolean appendOperationRecord(POSOperationLog operationLog);

	/***/
	Terminal findTerminalsByTerminalId(Long terminalId);
}
