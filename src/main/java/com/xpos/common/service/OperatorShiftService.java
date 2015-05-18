package com.xpos.common.service;

import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.shift.OperatorShiftVO;
import com.xkeshi.pojo.vo.shift.ShiftItemResultVO;
import com.xkeshi.pojo.vo.shift.ShiftItemVO;
import com.xkeshi.pojo.vo.shift.ShiftVO;
import com.xkeshi.pojo.vo.shift.SummarizeInfoResultVO;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.searcher.OperatorShiftSearcher;
import com.xpos.common.utils.Pager;

public interface OperatorShiftService {
	
	/**
	 * 交接班操作
	 * @description 操作员在一台app上执行交接，退出登录
	 * @return  交接是否成功
	 */
	public boolean executeOperatorShift(SystemParam  systemParam ,ShiftVO  shiftVO  );

	/**
	 * 交接班清单
	 * @param operatorSessionCode 操作员的操作会话
	 * @return
	 */
	public SummarizeInfoResultVO  getSummarizeInfoResultVO(String operatorSessionCode);
	
	/**
	 * 交班期间销售商品汇总
	 * @param  operatorSessionCode
	 */
	public  ShiftItemResultVO  getShiftItem(String operatorSessionCode);
	
	/**
	 * 获取操作员的当前当班的会话
	 * @param deviceNumber
	 * @param operatorId
	 * @return
	 */
	public POSOperationLog  getOperatorSession(String  deviceNumber , Long operatorId);
	/**
	 * 获取操作员的最近当班的会话
	 * @param deviceNumber
	 * @param operatorId
	 * @return
	 */
	public POSOperationLog  getLastOperatorSession(String  deviceNumber , Long operatorId);
	/**
	 * 客户端是否交接班
	 * @param deviceNumber
	 * @param operatorId
	 * @param 当班session
	 * @return  前一个未交班的操作员的姓名
	 */
	public String isOperatorShifted(String deviceNumber, Long operatorId, String operatorSessionCode);
	
	/**
	 * 获取操作员当班记录
	 * @param business
	 * @param pager
	 * @param operatorShiftSearcher
	 * @return
	 */
	public Pager<OperatorShiftVO> findOpeatorShiftList(Business business, Pager<OperatorShiftVO> pager, OperatorShiftSearcher operatorShiftSearcher);

	/**
	 * 交接班详情
	 * @param business
	 * @param pager
	 * @return
	 */
	public Pager<ShiftItemVO> findOpeatorShiftDetail(Pager<ShiftItemVO> pager  , String operatorSessionCode);

	/**
	 * 获取交接班
	 * @param operatorSessionCode
	 * @return
	 */
	public OperatorShiftVO getOpeatorShiftDetail(String operatorSessionCode);

	public POSOperationLog findOperatorSessionByDeviceNumber(String deviceNumber);
    
	
}
