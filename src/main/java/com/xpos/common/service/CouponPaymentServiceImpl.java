package com.xpos.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.drongam.hermes.entity.SMS;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.Coupon.CouponStatus;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentSource;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.example.CouponPaymentExample;
import com.xpos.common.entity.security.User;
import com.xpos.common.persistence.mybatis.CouponInfoMapper;
import com.xpos.common.persistence.mybatis.CouponMapper;
import com.xpos.common.persistence.mybatis.CouponPaymentMapper;
import com.xpos.common.searcher.CouponPaymentSearcher;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.CouponUtil;
import com.xpos.common.utils.Pager;
import com.xpos.common.utils.UUIDUtil;

@Service
public class CouponPaymentServiceImpl implements CouponPaymentService{
	private final static Logger logger = LoggerFactory.getLogger(CouponPaymentServiceImpl.class);
	
	@Autowired
	private CouponPaymentMapper couponPaymentMapper;

	@Autowired
	private CouponInfoMapper couponInfoMapper;
	
	@Autowired
	private CouponMapper couponMapper;
	
	@Autowired
	private CouponService  couponService  ;
	
	@Autowired
	private UserService userService  ;
	
	@Autowired
	private SMSService smsService  ;
	
	@Autowired
	private ShopService shopService  ;
	
	@Autowired
	private MemberService memberService  ;
	
	
	
	@Override
	public Pager<CouponPayment> findPayments(CouponPaymentExample example, Pager<CouponPayment> pager) {
		if(example == null){
			example = new CouponPaymentExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false);
		List<CouponPayment> list = couponPaymentMapper.selectByExample(example, pager);
		int totalCount = couponPaymentMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	public CouponPayment findPaymentByCode(String code) {
		CouponPaymentExample example = new CouponPaymentExample();
		example.createCriteria().addCriterion("code = ", code).addCriterion("deleted = ", false);
		return couponPaymentMapper.selectOneByExample(example);
	}
	
	@Override
	public CouponPayment findPaymentById(Long id) {
		CouponPaymentExample example = new CouponPaymentExample();
		example.createCriteria().addCriterion("id = ", id).addCriterion("deleted = ", false);
		List<CouponPayment> list = couponPaymentMapper.selectByExample(example, null);
		if(list.size() == 1)
			return list.get(0);
		return null;
	}

	@Override
	public boolean saveCouponPayment(CouponPayment couponPayment) {
		if(couponPayment.getStatus() == null){
			couponPayment.setStatus(CouponPaymentStatus.UNPAID); //创建默认“待付款”状态
		}
		return couponPaymentMapper.insert(couponPayment) > 0;
	}

	@Override
	public boolean updateCouponPayment(CouponPayment payment) {
		return couponPaymentMapper.updateByPrimaryKey(payment) > 0;
	}

	@Override
	public boolean updateCouponPaymentByCode(CouponPayment payment) {
		try{
			CouponPayment originalPayment = findPaymentByCode(payment.getCode());
			CouponPaymentStatus originalStatus = originalPayment.getStatus();
			CouponPaymentStatus curStatus = payment.getStatus();
			
			if(originalStatus.equals(CouponPaymentStatus.UNPAID)||originalStatus.equals(CouponPaymentStatus.PAID_TIMEOUT)){
				//等待付款状态或(付款途中已失效状态)，直接更新
				CouponPaymentExample example = new CouponPaymentExample();
				example.createCriteria().addCriterion("deleted = ", false)
										.addCriterion("code = ", payment.getCode());
				return couponPaymentMapper.updateByExample(payment, example) == 1;
			}else if(originalStatus.equals(CouponPaymentStatus.PAID_SUCCESS)){
				//付款成功的状态
				if(curStatus.equals(CouponPaymentStatus.PAID_REVOCATION) || curStatus.equals(CouponPaymentStatus.PAID_REFUND)) {
					//新状态为撤销或退款
					CouponPaymentExample example = new CouponPaymentExample();
					example.createCriteria().addCriterion("deleted = ", false)
					.addCriterion("code = ", payment.getCode());
					return couponPaymentMapper.updateByExample(payment, example) == 1;
				}else{
					//否则忽略，直接返回
					return true;
				}
			}else if(originalStatus.equals(CouponPaymentStatus.PAID_FAIL)){
				if(curStatus.equals(CouponPaymentStatus.PAID_SUCCESS) || curStatus.equals(CouponPaymentStatus.PAID_REVOCATION) || curStatus.equals(CouponPaymentStatus.PAID_REFUND)){
					//付款失败的状态，只有通知付款成功、撤销、退款才更新状态，否则忽略新的通知
					CouponPaymentExample example = new CouponPaymentExample();
					example.createCriteria().addCriterion("deleted = ", false)
					.addCriterion("code = ", payment.getCode());
					return couponPaymentMapper.updateByExample(payment, example) == 1;
				}else{
					return true;
				}
			}
		}catch(DataAccessException dae){
			logger.error("更新CouponPayment订单信息失败", dae);
			throw dae;
		}
		return false;
	}

	@Override
	public void updateCouponPaymentStatus() {
		List<CouponPayment> paymentList = couponPaymentMapper.selectNoPaylimitList();
		if(paymentList!=null && paymentList.size()>0) {
			for(CouponPayment payment:paymentList) {
				//状态置为
				CouponPayment couponPayment = couponPaymentMapper.selectByPrimaryKey(payment.getId());
				couponPayment.setStatus(CouponPaymentStatus.PAID_TIMEOUT);
				couponPaymentMapper.updateByPrimaryKey(couponPayment);
				CouponInfo ci = couponPayment.getCouponInfo();
				if(ci!=null && ci.getReceived()!=null && ci.getAllowContinueSale()==true) {
					ci.setReceived(ci.getReceived()==0||ci.getReceived()-couponPayment.getQuantity()<0?0:ci.getReceived()-couponPayment.getQuantity());
					couponInfoMapper.updateByPrimaryKey(ci);
				}
			}
		}
		//System.out.println("------更新用户优惠券订单状态"+new Date()+"-------");
	}

	@Override
	public Pager<CouponPayment> salesList(CouponPaymentSearcher searcher, Pager<CouponPayment> pager) {
		List<CouponPayment> list = couponPaymentMapper.selectSalesList(searcher, pager);
		int totalCount = couponPaymentMapper.countSalesList(searcher);
		
		pager.setList(list);
		pager.setTotalCount(totalCount);
		return pager;
	}

	@Override
	public String[] priceStatistic(CouponPaymentSearcher searcher) {
		Map<String, BigDecimal> map = couponPaymentMapper.countSalesStatistics(searcher);
		String[] total = new String[2];
		total[0] = map != null && map.get("priceSum") != null ? map.get("priceSum").toString() : "0.00";
		total[1] = map != null && map.get("paymentSum") != null ? map.get("paymentSum").toString() : "0.00";
		return total;
	}

	@Override
	public Pager<Coupon> consumeStatisticsList(CouponPaymentSearcher searcher, Pager<Coupon> pager) {
		List<Coupon> list = couponMapper.selectConsumeList(searcher, pager);
		int totalCount = couponMapper.countConsumeList(searcher);
		
		pager.setList(list);
		pager.setTotalCount(totalCount);
		return pager;
	}

	@Override
	public String paymentStatistic(CouponPaymentSearcher searcher) {
		BigDecimal total = couponMapper.countConsumeStatistics(searcher);
		return total == null ? "0.00" : total.toString();
	}

	@Override
	@Transactional
	public boolean paymentByCreateCoupon(CouponPayment payment, boolean isMarkReceived) {
		boolean result = true;
		//创建优惠券
		CouponInfo couponInfo = payment.getCouponInfo();
		if(couponInfo == null){
			logger.error("订单支付成功，但优惠活动已无法购买！couponPayment code:"+payment.getCode());
			return false;
		}
		User user = userService.findUserByMobile(payment.getMobile());
		if(CouponInfoType.NORMAL.equals(couponInfo.getType())){
			for(int i = 0; i < payment.getQuantity(); i++){
				Coupon coupon = new Coupon();
				coupon.setBusinessId(couponInfo.getBusinessId());
				coupon.setBusinessType(couponInfo.getBusinessType());
				coupon.setType(CouponInfoType.NORMAL);
				coupon.setCouponInfo(couponInfo);
				coupon.setMobile(payment.getMobile());
				coupon.setPayment(payment);
				if(user !=  null){
					coupon.setUser(user);
				}
				if(couponInfo.getBusinessId() !=null && couponInfo.getBusinessType()!=null) {
//FIXME 集团创建优惠券，如果非会员统一管理，如何set member信息
//					Member member = memberService.findMemberByMobile(couponInfo.getBusinessId(),couponInfo.getBusinessType(), payment.getMobile());
//					if(member!=null) {
//						coupon.setMember(member);
//					}
				}
				result &= couponService.saveCoupon(coupon, isMarkReceived);//coupon表添加记录
				//发送短信(短信推送、第三方获取除外)
				if(StringUtils.isNotBlank(coupon.getMobile()) 
						&& !(CouponPaymentType.SMS_PUSH.equals(payment.getType()) || CouponPaymentType.EXTERNAL_APPLY.equals(payment.getType()))){
					SMS sms = new SMS();
					sms.setMobile(coupon.getMobile());
					sms.setMessage("您的优惠券兑换码是"+coupon.getCouponCode()+"，点击查看详情：http://coupon.xka.me/"+coupon.getUniqueCode());
					smsService.sendSMSAndDeductions(couponInfo.getBusinessId() ,couponInfo.getBusinessType(),sms,null," 下发优惠券短信" );
				}
			}
		}else if(CouponInfoType.PACKAGE.equals(couponInfo.getType()) && !CollectionUtils.isEmpty(couponInfo.getItems())){
			for(int i = 0; i < payment.getQuantity(); i++){
				String packageSerial = UUIDUtil.getRandomString(32);
				for(CouponInfo item : couponInfo.getItems()){
					for(int j = 0; j < item.getQuantity(); j++){
						item.setStartDate(couponInfo.getStartDate());
						item.setEndDate(couponInfo.getEndDate());
						Coupon coupon = new Coupon();
						coupon.setBusinessId(couponInfo.getBusinessId());
						coupon.setBusinessType(couponInfo.getBusinessType());
						coupon.setType(CouponInfoType.CHILD);
						coupon.setParent(couponInfo);
						coupon.setPackageSerial(packageSerial);
						coupon.setCouponInfo(item);
						coupon.setMobile(payment.getMobile());
						coupon.setPayment(payment);
						if(user !=  null){
							coupon.setUser(user);
						}
						if(couponInfo.getBusinessId() !=null && couponInfo.getBusinessType()!=null) {
//FIXME 集团创建优惠券，如果非会员统一管理，如何set member信息
//							Member member = memberService.findMemberByMobile(couponInfo.getBusinessId(),couponInfo.getBusinessType(), payment.getMobile());
//							if(member!=null) {
//								coupon.setMember(member);
//							}
						}
						result &= couponService.saveCoupon(coupon, isMarkReceived);//coupon表添加记录
						//发送短信(短信推送、第三方获取除外)
						if(StringUtils.isNotBlank(coupon.getMobile())
								&& !(CouponPaymentType.SMS_PUSH.equals(payment.getType()) || CouponPaymentType.EXTERNAL_APPLY.equals(payment.getType()))){
							SMS sms = new SMS();
							sms.setMobile(coupon.getMobile());
							sms.setMessage("您的优惠券兑换码是"+coupon.getCouponCode()+"，点击查看详情：http://coupon.xka.me/"+coupon.getUniqueCode());
							smsService.sendSMSAndDeductions(couponInfo.getBusinessId() ,couponInfo.getBusinessType(),sms,null," 下发优惠券短信" );
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean getUserBuyQualification(Integer num,  Integer userLimitCount, Long couponInfoId,
			String mobile) {
		return couponPaymentMapper.findUserPayCount(mobile, couponInfoId,userLimitCount,num)<=0;
		
	}

	@Override
	public boolean createCouponByPayment(CouponPayment payment) {
		boolean result = true;
		//创建优惠券
		CouponInfo couponInfo = payment.getCouponInfo();
		if(couponInfo == null){
			return false;
		}
		List<Coupon> couponList = null;
		Coupon coupon = null;
		//限制用户只能领取1份时
		if(couponInfo.getUserLimitCount()!= null && couponInfo.getUserLimitCount().equals(1))  {
			Pager<Coupon> pager = new Pager<Coupon>();
			CouponExample example = new CouponExample ();
			example.createCriteria().addCriterion("mobile=",payment.getMobile())
									.addCriterion("couponInfo_id=", couponInfo.getId());
			couponList = couponService.findCoupons(example, pager).getList();
			if(couponList.size()>0) {
				coupon = couponList.get(0);
			}
		}
		if(coupon == null) {
			BigDecimal sum = couponInfo.getPrice()==null?BigDecimal.valueOf(0L):couponInfo.getPrice().multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP);
			payment.setSum(sum);
			payment.setSource(CouponPaymentSource.XKESHI_WAP);
			payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
			payment.setType(CouponPaymentType.EXTERNAL_APPLY);
			payment.setCode(CouponUtil.newCode(32));
			payment.setTradeDate(new Date());
			couponPaymentMapper.insert(payment);
			coupon = new Coupon();
			coupon.setBusinessId(couponInfo.getBusinessId());
			coupon.setBusinessType(couponInfo.getBusinessType());
			coupon.setType(CouponInfoType.NORMAL);
			coupon.setCouponInfo(couponInfo);
			coupon.setMobile(payment.getMobile());
			coupon.setPayment(payment);
			coupon.setStatus(CouponStatus.AVAILABLE);
			// 添加关联用户会员信息 writed by snoopy @2014-11-21
			/*if(couponInfo.getBusinessId() !=null && couponInfo.getBusinessType()!=null) {
				Business business = null;
				if(business !=null) {
					Member member = memberService.findMemberByMobile(business, payment.getMobile());
					if(member!=null) {
						coupon.setMember(member);
					}
				}
			}*/
			result &= couponService.saveCoupon(coupon, true);//coupon表添加记录
		}
		//发送短信(短信推送、第三方获取除外)
		/*if(StringUtils.isNotBlank(coupon.getMobile())){
			SMSMessage message = new SMSMessage();
			message.setMobile(coupon.getMobile());
			message.setContent("【爱客仕xpos】您的 "+coupon.getCouponInfo().getName()+" 编号是："+coupon.getCouponCode()+"，点击查看详情：http://coupon.xka.me/"+coupon.getUniqueCode());
			smsService.send(message, "xpos");
		}*/
		return result;
	}
	
	@Override
	public boolean createCouponByPayment(CouponPayment payment ,String sms) {
		boolean result = true;
		//创建优惠券
		CouponInfo couponInfo = payment.getCouponInfo();
		if(couponInfo == null){
			return false;
		}
		List<Coupon> couponList = null;
		Coupon coupon = null;
		//限制用户只能领取1份时
		if(couponInfo.getUserLimitCount()!= null && couponInfo.getUserLimitCount().equals(1))  {
			Pager<Coupon> pager = new Pager<Coupon>();
			CouponExample example = new CouponExample ();
			example.createCriteria().addCriterion("mobile=",payment.getMobile())
			.addCriterion("couponInfo_id=", couponInfo.getId());
			couponList = couponService.findCoupons(example, pager).getList();
			if(couponList.size()>0) {
				coupon = couponList.get(0);
			}
		}
		if(coupon == null) {
			BigDecimal sum = couponInfo.getPrice()==null?BigDecimal.valueOf(0L):couponInfo.getPrice().multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP);
			payment.setSum(sum);
			payment.setSource(CouponPaymentSource.XKESHI_WAP);
			payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
			payment.setType(CouponPaymentType.EXTERNAL_APPLY);
			payment.setCode(CouponUtil.newCode(32));
			payment.setTradeDate(new Date());
			couponPaymentMapper.insert(payment);
			coupon = new Coupon();
			coupon.setBusinessId(couponInfo.getBusinessId());
			coupon.setBusinessType(couponInfo.getBusinessType());
			coupon.setType(CouponInfoType.NORMAL);
			coupon.setCouponInfo(couponInfo);
			coupon.setMobile(payment.getMobile());
			coupon.setPayment(payment);
			coupon.setStatus(CouponStatus.AVAILABLE);
			// 添加关联用户会员信息 writed by snoopy @2014-11-21
			/*if(couponInfo.getBusinessId() !=null && couponInfo.getBusinessType()!=null) {
				Business business = null;
				if(business !=null) {
					Member member = memberService.findMemberByMobile(business, payment.getMobile());
					if(member!=null) {
						coupon.setMember(member);
					}
				}
			}*/
			result &= couponService.saveCoupon(coupon, true);//coupon表添加记录
		}
		//发送短信(短信推送、第三方获取除外)
		if(StringUtils.isNotBlank(coupon.getMobile())){
			SMS smsMessage = new SMS();
			smsMessage.setMobile(coupon.getMobile());
			smsMessage.setMessage(sms+"http://coupon.xka.me/"+coupon.getUniqueCode()); //v2版uuid coupon code
			smsService.sendSMSAndDeductions(couponInfo.getBusinessId() ,couponInfo.getBusinessType(),smsMessage,null,"用户获取优惠券" );
		}
		return result;
	}
	

}
