package com.xpos.common.service;

import static com.xpos.common.entity.Refund.RefundStatus.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.service.AlipayWapDirectService;
import com.umpay.config.UmpayPaySourceConfig.UmpayPaySource;
import com.umpay.service.UmpaywapRefundService;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.CouponPayment.CouponPaymentSource;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.Refund;
import com.xpos.common.entity.Refund.RefundStatus;
import com.xpos.common.entity.RefundLog;
import com.xpos.common.entity.example.RefundExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.persistence.mybatis.RefundMapper;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.Pager;
import com.xpos.common.utils.UUIDUtil;

@Service
public class RefundServiceImpl implements RefundService{
	private final static Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);
	
	@Resource 
	private AlipayWapDirectService  alipayWapDirectService  ;
	@Resource
	private TenpayService tenpayService;
	@Resource
	private RefundLogService refundLogService;
	@Resource
	private RefundMapper refundMapper;
	@Resource
	private CouponService couponService;
	@Resource
	private UmpaywapRefundService umpaywapRefundService;

	
	@Override
	public Pager<Refund> findRefunds(Business business, RefundExample example, Pager<Refund> pager) {
		if(example == null){
			example = new RefundExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false);
		if(business != null)
			example.appendCriterion(BusinessSQLBuilder.getBusinessSQL(business.getSelfBusinessType(), business.getSelfBusinessId()));
		
		List<Refund> list = refundMapper.selectByExample(example, pager);
		int totalCount = refundMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	public Refund findRefundByCode(String code) {
		RefundExample example = new RefundExample();
		example.createCriteria().addCriterion("code = ", code).addCriterion("deleted = ", false);
		return refundMapper.selectOneByExample(example);
	}
	
	@Override
	public Refund findRefundBySerial(String serial) {
		RefundExample example = new RefundExample();
		example.createCriteria().addCriterion("serial = ", serial).addCriterion("deleted = ", false);
		return refundMapper.selectOneByExample(example);
	}
	
	@Override
	public Refund findRefundByCouponCode(String code) {
		RefundExample example = new RefundExample();
		example.createCriteria().addCriterion(" id in (select refund_id from Coupon where couponCode='"+code+"' and deleted=false) ")
								.addCriterion("deleted = ", false);
		return refundMapper.selectOneByExample(example);
	}
	
	@Override
	public Refund findRefundById(Long id) {
		RefundExample example = new RefundExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		return refundMapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public Refund createRefund(Coupon coupon) {
		Refund refund = new Refund();
		refund.setCoupon(coupon);
		refund.setBusinessId(coupon.getBusinessId());
		refund.setBusinessType(coupon.getBusinessType());
		refund.setRemark("用户提交退款申请");
		refund.setUser(coupon.getUser());
		refund.setCode(UUIDUtil.getRandomString(32));
		refund.setPayment(coupon.getPayment());
		refund.setStatus(RefundStatus.APPLY);
		BigDecimal total = coupon.getPayment().getSum();
		Integer quantity = coupon.getPayment().getQuantity();
		refund.setSum(total.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP)); //价格计算：原付款总价 / 购买份数，保留2位小数，四舍五入
		refund.setStatus(RefundStatus.APPLY); //创建默认“待审核”状态
		refundMapper.insert(refund);
		return refund;
	}

	@Override
	public boolean saveRefund(Refund refund) {
		refund.setStatus(RefundStatus.APPLY); //创建默认“待审核”状态
		return refundMapper.insert(refund) > 0;
	}

	@Override
	@Transactional
	public boolean updateRefund(Refund refund) {
		RefundLog log = new RefundLog();
		log.setRefund(refund);
		log.setStatus(refund.getStatus());
		log.setDescription(refund.getRemark());
		return refundMapper.updateByPrimaryKey(refund) == 1 && refundLogService.insert(log) == 1;
	}

	@Override
	@Transactional
	public boolean updateRefundByCode(Refund refund) {
		try{
			Refund originalRefund = findRefundByCode(refund.getCode());
			RefundStatus originalStatus = originalRefund.getStatus();
			RefundStatus curStatus = refund.getStatus();
			
			if(originalStatus.equals(APPLY)){
				//待审核，直接更新
				RefundLog log = new RefundLog();
				log.setRefund(refund);
				log.setStatus(refund.getStatus());
				log.setDescription(refund.getRemark());
				
				RefundExample example = new RefundExample();
				example.createCriteria().addCriterion("deleted = ", false)
										.addCriterion("code = ", refund.getCode());
				return refundMapper.updateByExample(refund, example) == 1 && refundLogService.insert(log) == 1;
			}else if(originalStatus.equals(AUTO_SUCCESS) ||
					originalStatus.equals(MANUAL_SUCCESS) ||
					originalStatus.equals(REJECTED)){
				//已成功或拒绝，直接返回
				return true;
			}else if(originalStatus.equals(AUTO_FAILED) || originalStatus.equals(MANUAL_FAILED) || originalStatus.equals(AUTO_EXECUTE)){
				if(curStatus.equals(AUTO_SUCCESS) || curStatus.equals(MANUAL_SUCCESS)){
					//退款失败、退款中的状态，只有通知退款成功才更新状态，否则忽略新的通知
					RefundLog log = new RefundLog();
					log.setRefund(refund);
					log.setStatus(refund.getStatus());
					log.setDescription(refund.getRemark());
					
					RefundExample example = new RefundExample();
					example.createCriteria().addCriterion("deleted = ", false)
								.addCriterion("code = ", refund.getCode());
					return refundMapper.updateByExample(refund, example) == 1 && refundLogService.insert(log) == 1;
				}else{
					return true;
				}
			}else if(originalStatus.equals(ACCEPTED)){
				if(curStatus.equals(AUTO_FAILED) || curStatus.equals(MANUAL_FAILED) 
						|| curStatus.equals(AUTO_EXECUTE) || curStatus.equals(MANUAL_EXECUTE)){
					//审核通过状态，可以更新为退款失败、自动退款中
					RefundLog log = new RefundLog();
					log.setRefund(refund);
					log.setStatus(refund.getStatus());
					log.setDescription(refund.getRemark());
					
					RefundExample example = new RefundExample();
					example.createCriteria().addCriterion("deleted = ", false)
											.addCriterion("code = ", refund.getCode());
					return refundMapper.updateByExample(refund, example) == 1 && refundLogService.insert(log) == 1;
				}else{
					return true;
				}
			}
		}catch(DataAccessException dae){
			logger.error("更新Refund信息失败", dae);
			throw dae;
		}
		return false;
	}

	@Override
	public boolean updateRefundCodeByPayments( Refund  refund ,RefundExample  refundExample ) {
		//return  refundMapper.updateRefundCodeBySerial(code, serials) >0 ;
		return refundMapper.updateByExample(refund, refundExample)>0;
	}

	@Override
	public List<Refund> findBatchNoRefundList(String batch_no) {
	     RefundExample refundExample = new RefundExample();
	     refundExample.appendCriterion("batchNo=", batch_no)
	                  .addCriterion("status=", RefundStatus.ACCEPTED.toString()) //退款中
	                  .addCriterion("deleted=", false);
		return refundMapper.selectByExample(refundExample, null);
	}

	@Override
	public Map<String, String> executeAutoRefunding(Refund refund) {
		Map<String, String> map = new HashMap<>();
		if(CouponPaymentType.WEI_XIN.equals(refund.getPayment().getType())){ //原订单通过微信方式支付
			//调用接口查询退款订单当前处理状态
			refund = tenpayService.queryRefundDetail(refund);
			if(RefundStatus.AUTO_FAILED.equals(refund.getStatus()) || RefundStatus.ACCEPTED.equals(refund.getStatus())){
				RefundLog log = new RefundLog();
				log.setRefund(refund);
				log.setStatus(refund.getStatus());
				log.setDescription("进入自动退款");
				refundLogService.insert(log);
				boolean result = tenpayService.refund(refund);
				map.put("result", ""+result);
			}
		}else if(CouponPaymentType.ALIPAY_WAP.equals(refund.getPayment().getType())){
			refund.setStatus(RefundStatus.ACCEPTED);  //重设审核通过状态
			PaySource paySource = null  ;
			if (CouponPaymentSource.YANGCHENGLAKE.equals(refund.getPayment().getSource())) {
				paySource = PaySource.YCLAKE_WAP_DIRECT;
			}
			Map<String, Refund> refundDate  = new HashMap<String, Refund>();
			refundDate.put(refund.getPayment().getSerial(), refund);
			if (refund.getBatchNo() != null) {
				refund.setRemark("再次尝试自动退款。");
			}else{
				refund.setRemark("符合自动退款条件，进入自动退款。");
			}
			String createRefundUrl = alipayWapDirectService.CreateRefundUrl(refundDate  ,paySource);
			map.put("refundForm", createRefundUrl);
		}else if(CouponPaymentType.UMPAY_WAP.equals(refund.getPayment().getType())){
			UmpayPaySource paySource = null  ;
			if (CouponPaymentSource.YANGCHENGLAKE.equals(refund.getPayment().getSource())) {
				paySource = UmpayPaySource.YCLAKE_WAP_U;
			}
			RefundLog log = new RefundLog();
			log.setRefund(refund);
			log.setStatus(refund.getStatus());
			if(StringUtils.isNotEmpty(refund.getSerial())) {
				log.setDescription("再次尝试自动退款。");
			}else {
				log.setDescription("符合自动退款条件，进入自动退款。");
			}
			refundLogService.insert(log);
			 try {
				umpaywapRefundService.requestRefund(refund, paySource);
			 } catch (Exception e) {
				e.printStackTrace();
			}
			map.put("result", ""+true);
		}
		return map;
	}
	
	
	public boolean updateRelatedCoupon(Refund refund, CouponStatus couponStatus){
		List<Coupon> couponList = new ArrayList<>();
		if(CouponInfoType.NORMAL.equals(refund.getCoupon().getType())){
			refund.getCoupon().setStatus(couponStatus);
			couponList.add(refund.getCoupon());
		}else if(CouponInfoType.CHILD.equals(refund.getCoupon().getType())){
			List<Coupon> couponPackage = couponService.findCouponPackageBySerial(refund.getCoupon().getPackageSerial());
			couponList.addAll(couponPackage);
			for(Coupon cou : couponPackage){
				cou.setStatus(couponStatus);
			}
		}
		return couponService.batchUpdateCoupon(couponList);
	}

}
