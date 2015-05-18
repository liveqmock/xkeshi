package com.xpos.common.persistence.mybatis;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xpos.common.entity.Coupon;
import com.xpos.common.persistence.BaseMapper;
import com.xpos.common.searcher.CouponPaymentSearcher;
import com.xpos.common.utils.Pager;

public interface CouponMapper extends BaseMapper<Coupon>{
   
	@ResultMap("DetailMap")
	@Select("select c.* from Coupon c,CouponInfo ac where c.mobile=#{mobile} and  ( c.status='EXPIRED' or (c.status='AVAILABLE' and ac.endDate<now()))  and  ac.id = c.couponInfo_id  and c.deleted = false and  ac.deleted = false order by c.id desc limit #{pager.startNumber} ,#{pager.pageSize}")
	public List<Coupon> selectByExpired(@Param("mobile")Long mobile, @Param("pager")Pager<Coupon> pager);
	
	@Select("select count(1) from Coupon c,CouponInfo ac where c.mobile=#{mobile} and ( c.status='EXPIRED' or (c.status='AVAILABLE' and ac.endDate<now()))  and  ac.id = c.couponInfo_id  and c.deleted = false and  ac.deleted = false")
	public int selectByExpiredCount(@Param("mobile")Long mobile, @Param("pager")Pager<Coupon> pager);
	
	@ResultMap("DetailMap")
	@Select("select c.* from Coupon c,CouponInfo ac where c.mobile=#{mobile} and c.status='AVAILABLE' and ac.endDate>now() and  ac.id = c.couponInfo_id  and c.deleted = false and  ac.deleted = false order by c.id desc limit #{pager.startNumber} ,#{pager.pageSize}")
	public List<Coupon> selectByAvailable(@Param("mobile")Long mobile, @Param("pager")Pager<Coupon> pager);
	
	@Select("select count(1) from Coupon c,CouponInfo ac where c.mobile=#{mobile} and c.status='AVAILABLE' and ac.endDate>now() and  ac.id = c.couponInfo_id  and c.deleted = false and  ac.deleted = false")
	public int selectByAvailableCount(@Param("mobile")Long mobile, @Param("pager")Pager<Coupon> pager);
	
	@Update("update Coupon set mobile=#{newmobile} where mobile=#{oldmobile}")
	public int updateMobile(@Param(value = "oldmobile")String oldmobile, @Param(value ="newmobile") String newmobile);
	
	@Update("update Coupon a set a.status='EXPIRED' where a.status='AVAILABLE' and a.deleted=false and EXISTS (SELECT 1 from CouponInfo b where b.id=a.couponInfo_id and UNIX_TIMESTAMP(b.endDate)<UNIX_TIMESTAMP(NOW()))")
	public int updateStatus();

	public List<Coupon> selectConsumeList(@Param("searcher")CouponPaymentSearcher searcher, @Param("pager")Pager<Coupon> pager);

	public int countConsumeList(@Param("searcher")CouponPaymentSearcher searcher);

	public BigDecimal countConsumeStatistics(@Param("searcher")CouponPaymentSearcher searcher);
	
}