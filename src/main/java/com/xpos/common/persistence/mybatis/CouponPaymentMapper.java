package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xpos.common.entity.CouponPayment;
import com.xpos.common.persistence.BaseMapper;
import com.xpos.common.searcher.CouponPaymentSearcher;
import com.xpos.common.utils.Pager;

public interface CouponPaymentMapper extends BaseMapper<CouponPayment>{

	@Update("update CouponPayment set mobile=#{newmobile} where mobile=#{oldmobile}")
	public int updateMobile(@Param(value = "oldmobile")String oldmobile, @Param(value ="newmobile") String newmobile);

	public List<CouponPayment> selectSalesList(@Param("searcher")CouponPaymentSearcher searcher, @Param("pager")Pager<CouponPayment> pager);

	public int countSalesList(@Param("searcher")CouponPaymentSearcher searcher);

	public Map<String, BigDecimal> countSalesStatistics(@Param("searcher")CouponPaymentSearcher searcher);
	
	@Select("select #{num} +  IFNULL(sum(quantity),0) - IFNULL(SUM(refundCount),0) - #{userLimitCount} from("
			+ "SELECT a.id ,a.quantity, COUNT(b.id) as refundCount FROM CouponPayment a left JOIN Refund b "
			+ "on b.payment_id = a.id  and b.deleted=false where  a.couponInfo_id = #{couponInfoId} AND "
			+ "( a. STATUS = 'PAID_SUCCESS' OR a. STATUS = 'UNPAID' ) AND a.mobile = #{mobile} "
			+ "and a.deleted=FALSE group by a.id ,a.quantity ) T ")
	public int findUserPayCount(@Param(value = "mobile")String  mobile, @Param(value ="couponInfoId") Long couponInfoId ,@Param(value ="userLimitCount") int userLimitCount ,@Param(value ="num") int num );
	
	@Select("select DISTINCT a.* from CouponPayment  a , CouponInfo b "
			+ "where a.couponInfo_id=b.id and a.deleted=false and b.deleted=false and a.`status`='UNPAID' "
			+ " and b.allowContinueSale is not null and b.limitPayTime is not null "
			+ " and UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(a.createDate)>= (b.limitPayTime*60)")
	public List<CouponPayment> selectNoPaylimitList();
}