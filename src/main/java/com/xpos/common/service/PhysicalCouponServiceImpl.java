package com.xpos.common.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.pojo.vo.physicalCoupon.PhysicalCouponWriteOffVO;
import com.xpos.common.entity.physicalCoupon.PhysicalCoupon;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponOrder;
import com.xpos.common.entity.physicalCoupon.PhysicalCouponShop;
import com.xpos.common.persistence.mybatis.physicalCoupon.PhysicalCouponMapper;
import com.xpos.common.persistence.mybatis.physicalCoupon.PhysicalCouponOrderMapper;
import com.xpos.common.persistence.mybatis.physicalCoupon.PhysicalCouponShopMapper;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponOrderSearcher;
import com.xpos.common.searcher.physicalCoupon.PhysicalCouponSearcher;
import com.xpos.common.utils.Pager;

@Service
public class PhysicalCouponServiceImpl implements PhysicalCouponService{
	
	@Autowired
	private PhysicalCouponMapper physicalCouponMapper;
	
	@Autowired
	private PhysicalCouponOrderMapper physicalCouponOrderMapper;

	@Autowired
	private PhysicalCouponShopMapper physicalCouponShopMapper;
	
	@Override
	public List<PhysicalCoupon> findAvailablePhysicalCouponListByShopId(Long shopId) {
		if(shopId != null) {
			return physicalCouponMapper.findAvailablePhysicalCouponListByShopId(shopId);
		}else {
			return null;
		}
	}

	@Override
	public Pager<PhysicalCouponOrder> findOrderPhysicalCouponList(PhysicalCouponOrderSearcher physicalCouponOrderSearcher,
			Pager<PhysicalCouponOrder> pager) {
		if(physicalCouponOrderSearcher == null) {
			physicalCouponOrderSearcher = new PhysicalCouponOrderSearcher();
		}
		if(pager == null) {
			pager = new Pager<PhysicalCouponOrder>();
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager.setTotalCount(physicalCouponOrderMapper.findOrderPhysicalCouponList(physicalCouponOrderSearcher,null).size());
		pager.setList(physicalCouponOrderMapper.findOrderPhysicalCouponList(physicalCouponOrderSearcher,pager));
		return pager;
	}
	

	@Override
	public int findCountOrderPhysicalCoupon(PhysicalCouponOrderSearcher physicalCouponOrderSearcher) {
		return physicalCouponOrderMapper.countOrderPhysicalCoupon(physicalCouponOrderSearcher);
	}

	@Override
	public BigDecimal orderPhysicalCouponTotalAmount(PhysicalCouponOrderSearcher physicalCouponOrderSearcher) {
		return physicalCouponOrderMapper.orderPhysicalCouponTotalAmount(physicalCouponOrderSearcher);
	}

	@Override
	public Pager<PhysicalCoupon> findPhysicalCouponList(PhysicalCouponSearcher searcher, Pager<PhysicalCoupon> pager) {
		if(searcher == null) {
			searcher = new PhysicalCouponSearcher();
		}
		if(pager == null) {
			pager = new Pager<PhysicalCoupon>();
			pager.setPageSize(Integer.MAX_VALUE); 
		}
		pager.setTotalCount(physicalCouponMapper.findPhysicalCouponList(searcher,null).size());
		pager.setList(physicalCouponMapper.findPhysicalCouponList(searcher,pager));
		return pager;
	}
	
	@Transactional
	@Override
	public boolean add(PhysicalCoupon physicalCoupon, Long[] shopList) {
		if(physicalCoupon.getAmount()==null || physicalCoupon.getBusiness_type() == null
				|| physicalCoupon.getBusiness_id()==null || StringUtils.isBlank(physicalCoupon.getName())) {
			return false; 
		}else {
			if(physicalCoupon.getWeight() == null) {
				physicalCoupon.setWeight(50);
			}
			boolean result = physicalCouponMapper.insert(physicalCoupon)>0;
			return  result && physicalCouponShopMapper.insert(physicalCoupon.getId(), shopList)>0;
		}
	}

	@Override
	public PhysicalCoupon findPhysicalCouponById(Long id) {
		return physicalCouponMapper.selectById(id);
	}

	@Override
	public boolean update(PhysicalCoupon physicalCoupon, Long[] shopList) {
		if(physicalCoupon == null || physicalCoupon.getId() == null 
				|| physicalCoupon.getAmount()==null || physicalCoupon.getBusiness_type() == null 
				|| physicalCoupon.getBusiness_id()==null || StringUtils.isBlank(physicalCoupon.getName())) {
			return false; 
		}else {
			if(physicalCoupon.getWeight() == null || physicalCoupon.getWeight() <= 0){
				physicalCoupon.setWeight(50);
			}
			boolean result = physicalCouponMapper.update(physicalCoupon)>0;
			result = result && physicalCouponShopMapper.delete(physicalCoupon.getId())>0;
			return result && physicalCouponShopMapper.insert(physicalCoupon.getId(), shopList)>0;
		}
	}

	@Override
	public List<PhysicalCouponShop> findShopListByPhysicalCouponId(Long id) {
		return physicalCouponShopMapper.findShopListByPhysicalCouponId(id);
	}

	@Override
	public boolean update(PhysicalCoupon pc) {
		return physicalCouponMapper.update(pc)>0;
	}
	
	/**
	 * 通过orderNum来查询订单实体券的信息  PhysicalCouponOrder
	 */
	@Override
	public List<PhysicalCouponOrder> findPhysicalCouponOrderByOrderNum(
			String orderNumber) {
		return physicalCouponOrderMapper.findPhysicalCouponOrderByOrderNum(orderNumber);
	}
	/**
	 * 根据ordernumber从physical_coupon_order表中计算出实体券的总金额
	 */
	@Override
	public BigDecimal calculatePhyAmount(String orderNum) {
		return physicalCouponOrderMapper.calculatePhyAmount(orderNum);
	}

	@Override
	public Pager<PhysicalCouponWriteOffVO> getOrderPhysicalCouponList(PhysicalCouponOrderSearcher searcher,Pager<PhysicalCouponWriteOffVO> pager) {
		if(searcher == null) {
			searcher = new PhysicalCouponOrderSearcher();
		}
		if(pager == null) {
			pager = new Pager<PhysicalCouponWriteOffVO>();
			pager.setPageSize(Integer.MAX_VALUE);
		}
		pager.setTotalCount(physicalCouponOrderMapper.getOrderPhysicalCouponListCount(searcher));
		List<PhysicalCouponWriteOffVO> orderPhysicalCouponList =physicalCouponOrderMapper.getOrderPhysicalCouponList(searcher,pager);
		
		pager.setList(orderPhysicalCouponList);
		return pager;
	}
	
}
