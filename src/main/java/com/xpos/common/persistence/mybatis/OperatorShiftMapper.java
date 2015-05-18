package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.vo.shift.OperatorShiftVO;
import com.xkeshi.pojo.vo.shift.ShiftItemVO;
import com.xpos.common.entity.OperatorShift;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.searcher.OperatorShiftSearcher;
import com.xpos.common.utils.Pager;

public interface OperatorShiftMapper {

	Integer  insertOperatorShift(@Param("operatorShift") OperatorShift  operatorShift );

	Integer findOperatorConsumeCoupon(@Param("operatorSessionCode") String operatorSessionCode);
	/*新增会员数*/
	Integer findOperatorMemberCount(@Param("operatorSessionCode")String operatorSessionCode);
	
	Integer getTotalOrderItemCount(@Param("operatorSessionCode")String operatorSessionCode);

	Integer getTotalThirdOrderGoodsCount(@Param("operatorSessionCode") String operatorSessionCode);

	POSOperationLog findOperatorShiftInfo(@Param("deviceNumber")String deviceNumber);

	Integer findOperatorShiftCount(@Param("businessId") Long businessId,@Param("businessType") String businessType,
									@Param("operatorShiftSearcher") OperatorShiftSearcher operatorShiftSearcher);

	List<OperatorShiftVO> findOperatorShiftList(@Param("businessId") Long businessId,@Param("businessType") String businessType,@Param("pager") Pager<OperatorShiftVO> pager,
											  @Param("operatorShiftSearcher") OperatorShiftSearcher operatorShiftSearcher);

	BigDecimal getOrderPhysicalCouponAmount( @Param("operatorSessionCode")String operatorSessionCode);

	List<ShiftItemVO> findOperatorShiftOrderItemList(@Param("pager")Pager<ShiftItemVO> pager,@Param("operatorSessionCode")String operatorSessionCode);

	Integer findOperatorOrderItemCount(@Param("operatorSessionCode")String operatorSessionCode);

	OperatorShiftVO findOperatorShift(@Param("operatorSessionCode") String operatorSessionCode);


	
}
