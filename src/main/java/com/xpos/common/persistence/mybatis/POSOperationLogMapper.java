package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.vo.shift.ShiftInfoVO;
import com.xkeshi.pojo.vo.shift.ShiftItemVO;
import com.xkeshi.pojo.vo.shift.SummarizeInfoResultVO;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.persistence.BaseMapper;

public interface POSOperationLogMapper extends BaseMapper<POSOperationLog>{


	
	int insertPOSOperationLog (@Param("operationLog")  POSOperationLog  operationLog) ;
	
	
	/**
	 * 获取操作员的operatorSession
	 * @param deviceNumber
	 * @param operatorId
	 * @return
	 */
	POSOperationLog findOperatorSession(@Param("deviceNumber")String deviceNumber,@Param("operatorId") Long operatorId);

 
	/**
	 * 获取操作员的最近operatorSession
	 * @param deviceNumber
	 * @param operatorId
	 * @return
	 */
	POSOperationLog findLastOperatorSession(@Param("deviceNumber")String deviceNumber,@Param("operatorId") Long operatorId);
	
	
	/**
	 * 获取官方Order交接班清单
	 * @param operatorSessionCode
	 * @Param shopId
	 * @return
	 */
	SummarizeInfoResultVO getOrderSummarizeInfoByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);
	
	/**
	 * 获取第三方Order交接班清单
	 * @param operatorSessionCode
	 * @Param shopId
	 */
	SummarizeInfoResultVO getThirdOrderSummarizeInfoByOperatorSessionCode(@Param("operatorSessionCode")String operatorSessionCode);

	/**
	 * 获取销售商品汇总
	 * @param operatorSessionCode
	 */
	 List<ShiftItemVO> findOperatorShiftItems(@Param("operatorSessionCode")String operatorSessionCode);

	
	/**
	 * 当前操作员的当班信息
	 * @param operatorSessionCode
	 */
	ShiftInfoVO findOperatorShiftInfo(@Param("operatorSessionCode") String operatorSessionCode);

	/**
	 * 获取当前设备，最后一条交班状态为登录的操作员的operatorSessionCode
	 * @param deviceNumber
	 */
	POSOperationLog findOperatorSessionByDeviceNumber(@Param("deviceNumber") String deviceNumber);

	

}