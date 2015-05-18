package com.xpos.common.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.CashTransactionDAO;
import com.xkeshi.dao.OperatorShiftConsumedPhysicalCouponDAO;
import com.xkeshi.dao.OrderMemberDiscountDAO;
import com.xkeshi.dao.PhysicalCouponDAO;
import com.xkeshi.dao.PhysicalCouponOrderDAO;
import com.xkeshi.dao.PrepaidCardChargeOrderDAO;
import com.xkeshi.dao.WXPayTransactionDAO;
import com.xkeshi.pojo.po.OperatorShiftConsumedPhysicalCoupon;
import com.xkeshi.pojo.po.PhysicalCoupon;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.shift.AlipayTransactionVO;
import com.xkeshi.pojo.vo.shift.BankNFCPayTransactionVO;
import com.xkeshi.pojo.vo.shift.CashPayTransactionVO;
import com.xkeshi.pojo.vo.shift.ConsumedPhysicalCouponVO;
import com.xkeshi.pojo.vo.shift.MemberDiscountVO;
import com.xkeshi.pojo.vo.shift.OperatorShiftVO;
import com.xkeshi.pojo.vo.shift.OrderPayVO;
import com.xkeshi.pojo.vo.shift.OrderPhysicalCouponVO;
import com.xkeshi.pojo.vo.shift.OrderPreferentialVO;
import com.xkeshi.pojo.vo.shift.POSPayTransactionVO;
import com.xkeshi.pojo.vo.shift.PrepaidCardPayTransactionVO;
import com.xkeshi.pojo.vo.shift.ShiftInfoVO;
import com.xkeshi.pojo.vo.shift.ShiftItemResultVO;
import com.xkeshi.pojo.vo.shift.ShiftItemVO;
import com.xkeshi.pojo.vo.shift.ShiftVO;
import com.xkeshi.pojo.vo.shift.SummarizeInfoResultVO;
import com.xkeshi.pojo.vo.shift.WXPayTransactionVO;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.OperatorShift;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.entity.pos.POSTransaction.POSTransactionType;
import com.xpos.common.persistence.mybatis.AlipayTransactionMapper;
import com.xpos.common.persistence.mybatis.BankNFCTransactionMapper;
import com.xpos.common.persistence.mybatis.OperatorShiftMapper;
import com.xpos.common.persistence.mybatis.POSOperationLogMapper;
import com.xpos.common.persistence.mybatis.POSTransactionMapper;
import com.xpos.common.searcher.OperatorShiftSearcher;
import com.xpos.common.utils.DateUtil;
import com.xpos.common.utils.Pager;
@Service
public class OperatorShiftServiceImpl   implements  OperatorShiftService{
	
	@Resource
	private POSOperationLogMapper posOperationLogMapper;

	@Resource
	private OperatorShiftMapper  operatorShiftMapper  ;

	@Resource
	private AlipayTransactionMapper   alipayTransactionMapper  ;

	@Autowired(required = false)
	private BankNFCTransactionMapper  bankNFCTransactionMapper  ;

	@Autowired(required  = false)
	private WXPayTransactionDAO  wxPayTransactionDAO  ;
	
	@Autowired(required  = false)
	private PrepaidCardChargeOrderDAO  prepaidCardChargeOrderDAO  ;
	
	@Autowired(required  = false)
	private PhysicalCouponOrderDAO   physicalCouponOrderDAO  ;
	
	@Autowired(required  = false)
	private PhysicalCouponDAO   physicalCouponDAO   ;
	
	@Resource
	private OperatorService  operatorService   ;
	
	@Autowired(required  = false)
	private OrderMemberDiscountDAO  orderMemberDiscountDAO  ;
	
	@Autowired(required  = false)
	private POSTransactionMapper   posTransactionMapper   ;

	@Autowired(required  = false)
	private CashTransactionDAO  cashTransactionDAO  ;
	
	@Autowired(required = false)
	private OperatorShiftConsumedPhysicalCouponDAO  operatorShiftConsumedPhysicalCouponDAO ;
	
	/**执行交接班操作*/
	@Override
	@Transactional
	public boolean executeOperatorShift(SystemParam  systemParam ,ShiftVO  shiftVO  ) {
		 String deviceNumber  = systemParam.getDeviceNumber();
		 Long operatorId  =  systemParam.getOperatorId();
		 BigDecimal totalActuallyAmount = shiftVO.getTotalCashPaidAmount();
		 POSOperationLog operatorSession = this.getOperatorSession(deviceNumber  , operatorId  );
		if (operatorSession == null  || operatorSession.getLogined() == 0 || totalActuallyAmount  == null) 
			return false;
		 String operatorSessionCode = operatorSession.getOperatorSessionCode();
		 //步骤一：保存交接班操作记录
		 POSOperationLog posOperationLog = new POSOperationLog();
		 posOperationLog.setOperatorId(operatorId);
		 posOperationLog.setDeviceNumber(deviceNumber);
		 posOperationLog.setOperatorSessionCode(operatorSessionCode);
		 posOperationLog.setLogined(0); //退出登录
		 posOperationLog.setShift(1);   //交接班
		 posOperationLog.setCreateDate(new Date());
		if (posOperationLogMapper.insertPOSOperationLog(posOperationLog) >0) {
			OperatorShift operatorShift = new OperatorShift();
			operatorShift.setOperatorId(operatorId);
			operatorShift.setOperatorSessionCode(operatorSessionCode);
			ShiftInfoVO shiftInfoVO = posOperationLogMapper.findOperatorShiftInfo(operatorSessionCode);
			if (shiftInfoVO == null) 
				return false;
			operatorShift.setOperatorRealName(shiftInfoVO.getOperatorName());
			try {
				operatorShift.setShiftedStartTime(DateUtil.getDateFormatter(shiftInfoVO.getStartTime()));
				operatorShift.setShiftedEndTime(DateUtil.getDateFormatter(shiftInfoVO.getEndTime()));
			} catch (ParseException e) { }
			operatorShift.setCreatedTime(new Date());
			Integer  totalConsumeCount =  operatorShiftMapper.findOperatorConsumeCoupon(operatorSessionCode);
			operatorShift.setTotalConsumeCount(totalConsumeCount); //核销电子券
			Integer totalMemberCount = operatorShiftMapper.findOperatorMemberCount(operatorSessionCode);
			operatorShift.setTotalMemberCount(totalMemberCount);   //新增会员数
		    boolean isXPOSOrder  = true;   //爱客仕官方订单
	        //官方order 
	        // SummarizeInfoResultVO summarizeInfoResultVO = posOperationLogMapper.getOrderSummarizeInfoByOperatorSessionCode(operatorSessionCode);
	        //TODO 第三方订单，暂时不用考虑
	        /*if (summarizeInfoResultVO.getTotalOrderCount()  == 0) {
	        	//第三方order
	        	summarizeInfoResultVO = posOperationLogMapper.getThirdOrderSummarizeInfoByOperatorSessionCode(operatorSessionCode);
	        	if (summarizeInfoResultVO.getTotalOrderCount() == 0)
	        		return true ;
	        	isXPOSOrder  = false;   //第三方订单
	        }  */
	        SummarizeInfoResultVO summarizeInfoResultVO = this.getSummarizeInfoResultVO(operatorSessionCode);
			operatorShift.setTotalOrderCount(summarizeInfoResultVO.getTotalOrderCount());
			
			/**订单总销量*/
			Integer  totalOrderItemCount  = isXPOSOrder  ? operatorShiftMapper.getTotalOrderItemCount(operatorSessionCode)
					                                     : operatorShiftMapper.getTotalThirdOrderGoodsCount(operatorSessionCode);
			operatorShift.setTotalOrderItemCount(totalOrderItemCount);
			
			/**点单总金额*/
			operatorShift.setTotalOrderAmount(summarizeInfoResultVO.getTotalOrderAmount());
	
			/**核销实体券*/
			if (isXPOSOrder) 
				operatorShift.setTotalPhysicalCouponAmount(operatorShiftMapper.getOrderPhysicalCouponAmount(operatorSessionCode));

			/**应收金额*/
			operatorShift.setTotalReceivableAmount(summarizeInfoResultVO.getTotalReceivableCashAmount() );
			
			/**实收现金(金额由操作员在确认交接班前，输入)*/
			totalActuallyAmount  =  totalActuallyAmount == null ? new BigDecimal(0) : totalActuallyAmount  ;
			operatorShift.setTotalActuallyAmount(totalActuallyAmount);
			
			/**实收现金差额*/
			BigDecimal totalReceivableCashAmount = summarizeInfoResultVO.getTotalReceivableCashAmount();
			totalReceivableCashAmount  =  totalReceivableCashAmount == null  ?  new BigDecimal(0) : totalReceivableCashAmount  ;
			operatorShift.setTotalDifferenceCashAmount(totalActuallyAmount.subtract(totalReceivableCashAmount));
			
			Operator operator = operatorService.findById(operatorId);
			if (operator  == null  || operator.getShop() == null ) 
				throw new RuntimeException("操作员或商户错误");
			    operatorShift.setShopId(operator.getShop().getId());
			    List<ConsumedPhysicalCouponVO> physicalCoupons = shiftVO.getPhysicalCoupons();
			    if ( operatorShiftMapper.insertOperatorShift(operatorShift ) == 1  && physicalCoupons != null) {
			    	OperatorShiftVO operatorShiftVO = operatorShiftMapper.findOperatorShift(operatorSessionCode);
			    	for (ConsumedPhysicalCouponVO consumedPhysicalCouponVO : physicalCoupons) {
			    		PhysicalCoupon physicalCoupon = physicalCouponDAO.getByID(PhysicalCoupon.class, consumedPhysicalCouponVO.getId());
			    		OperatorShiftConsumedPhysicalCoupon shiftCoupon = new OperatorShiftConsumedPhysicalCoupon();
			    		shiftCoupon.setOperatorShiftId(operatorShiftVO.getId());
			    		shiftCoupon.setPhysicalCouponId(physicalCoupon.getId());
			    		shiftCoupon.setPhysicalCouponName(physicalCoupon.getName());
			    		shiftCoupon.setPhysicalCouponAmount(physicalCoupon.getAmount());
			    		shiftCoupon.setTotalConsumedCount(consumedPhysicalCouponVO.getCount());
						operatorShiftConsumedPhysicalCouponDAO.insertOperatorShiftConsumedPhysicalCoupon(shiftCoupon);
					}
			    }
			}
		return true;
	}
	/**
	 * 获取交接班清单
	 * @description 订单包括系统订单或第三方订单(两者不并存)
	 */
	@Override
	public SummarizeInfoResultVO getSummarizeInfoResultVO( String operatorSessionCode) {
        if (StringUtils.isBlank(operatorSessionCode)) 
        	return null;
        boolean isXPOSOrder  = true;   //爱客仕官方订单
        //官方order 
        SummarizeInfoResultVO summarizeInfoResultVO = posOperationLogMapper.getOrderSummarizeInfoByOperatorSessionCode(operatorSessionCode);
        //TODO 第三方订单，暂时不用考虑
       /* if (summarizeInfoResultVO.getTotalOrderCount()  == 0) {
        	//第三方order
        	summarizeInfoResultVO = posOperationLogMapper.getThirdOrderSummarizeInfoByOperatorSessionCode(operatorSessionCode);
        	if (summarizeInfoResultVO.getTotalOrderCount() == 0) 
        		return  new SummarizeInfoResultVO();
        	isXPOSOrder  = false;   //第三方订单
        }  */
        //当班信息
        ShiftInfoVO  shiftInfo  = posOperationLogMapper.findOperatorShiftInfo(operatorSessionCode);
        summarizeInfoResultVO.setShiftInfo(shiftInfo);
		
        //===========1.订单优惠
        OrderPreferentialVO orderPreferential = new OrderPreferentialVO();
        //优惠一:实体券
		List<OrderPhysicalCouponVO> physicalCouponList =  isXPOSOrder ?  physicalCouponOrderDAO.getOrderUsePhysicalCouponByOperatorSessionCode(operatorSessionCode)
				                                                       : physicalCouponOrderDAO.getThirdOrderUsePhysicalCouponByOperatorSessionCode(operatorSessionCode) ;
		orderPreferential.setOrderPhysicalCouponList(physicalCouponList);

		if (isXPOSOrder) {
			//优惠二：会员折扣
			MemberDiscountVO   memberDiscount  = orderMemberDiscountDAO.getOrderUseMemberDiscountByOperatorSessionCode(operatorSessionCode);
			orderPreferential.setMemberDicount(memberDiscount);
		}
		summarizeInfoResultVO.setOrderPreferential(orderPreferential );
		
		//============2.订单支付
		OrderPayVO orderPayVo = new OrderPayVO();
		//支付宝支付
		AlipayTransactionVO alipayTotalAmount  = isXPOSOrder ?  alipayTransactionMapper.getOrderTotalAmountByOperatorSessionCode(operatorSessionCode)
													         :  alipayTransactionMapper.getThirdOrderTotalAmountByOperatorSessionCode(operatorSessionCode)  ;
		orderPayVo.setAlipayTransaction(alipayTotalAmount);
	    //微信支付
		WXPayTransactionVO weixinTotalAmount  = isXPOSOrder  ? wxPayTransactionDAO.getOrderTotalAmountByOperatorSessionCode(operatorSessionCode)
															 : wxPayTransactionDAO.getThirdOrderTotalAmountByOperatorSessionCode(operatorSessionCode);
		orderPayVo.setWxPayTransaction(weixinTotalAmount);
		//pos刷卡 
		POSPayTransactionVO posPayTransaction = isXPOSOrder  ? posTransactionMapper.getOrderTotalAmountByOperatorSessionCode(operatorSessionCode, POSTransactionType.BANK_CARD)
														     : posTransactionMapper.getThirdOrderTotalAmountByOperatorSessionCode(operatorSessionCode, POSTransactionType.BANK_CARD);
		orderPayVo.setPosPayTransaction(posPayTransaction);
		//NFC 
		BankNFCPayTransactionVO electronitcTotalAmount =  isXPOSOrder  ? bankNFCTransactionMapper.getOrderTotalAmountByOperatorSessionCode(operatorSessionCode)
																	   : bankNFCTransactionMapper.getThirdOrderTotalAmountByOperatorSessionCode(operatorSessionCode);
		orderPayVo.setBankNFCPayTransaction(electronitcTotalAmount);
		
		if (isXPOSOrder) {
			//预付卡消费
			PrepaidCardPayTransactionVO prepaidCardPayTransaction  = isXPOSOrder   ?  prepaidCardChargeOrderDAO.getOrderTotalAmountByOperatorSessionCode(operatorSessionCode)
													   :  prepaidCardChargeOrderDAO.getThirdOrderTotalAmountByOperatorSessionCode(operatorSessionCode);
			orderPayVo.setPrepaidCardPayTransaction(prepaidCardPayTransaction);
		}
		
		//现金支付
		CashPayTransactionVO   cashPayTransaction   = isXPOSOrder ?  cashTransactionDAO.getOrderTotalAmountByOperatorSessionCode(operatorSessionCode)
																  :  cashTransactionDAO.getThirdOrderTotalAmountByOperatorSessionCode(operatorSessionCode);
		orderPayVo.setCashPayTransaction(cashPayTransaction);
		summarizeInfoResultVO.setOrderPay(orderPayVo);
	
		//订单应收现金
		BigDecimal totalReceivableCashAmount  =   new BigDecimal(0) ;
		if (cashPayTransaction != null && cashPayTransaction.getTotalAmount() != null) 
			totalReceivableCashAmount  =  cashPayTransaction.getTotalAmount()  ;
		summarizeInfoResultVO.setTotalReceivableCashAmount(totalReceivableCashAmount  );
		return  summarizeInfoResultVO;
	}

	@Override
	public  ShiftItemResultVO getShiftItem(String operatorSessionCode) {
		  if (StringUtils.isBlank(operatorSessionCode)) 
	        	return null;
		  List<ShiftItemVO> shiftItemVO = posOperationLogMapper.findOperatorShiftItems(operatorSessionCode);
	       ShiftInfoVO  shiftInfo  = posOperationLogMapper.findOperatorShiftInfo(operatorSessionCode);
		 return new ShiftItemResultVO(shiftInfo, shiftItemVO);
	}

	@Override
	public POSOperationLog getOperatorSession(String deviceNumber, Long operatorId) {
		 return  posOperationLogMapper.findOperatorSession(deviceNumber ,operatorId);
	}
	
	@Override
	public POSOperationLog getLastOperatorSession(String deviceNumber, Long operatorId) {
		return  posOperationLogMapper.findLastOperatorSession(deviceNumber ,operatorId);
	}
	
	@Override
	public String isOperatorShifted(String deviceNumber, Long operatorId,String operatorSessionCode) {
		createPOSOperationLog(deviceNumber, operatorId,operatorSessionCode);
		return null;
	}
	
	@Transactional
	private void createPOSOperationLog(String deviceNumber, Long operatorId,String operatorSessionCode) {
		 POSOperationLog posOperationLog = new POSOperationLog();
		 posOperationLog.setOperatorId(operatorId);
		 posOperationLog.setDeviceNumber(deviceNumber);
		 posOperationLog.setOperatorSessionCode(operatorSessionCode);
		 posOperationLog.setLogined(1);
		 posOperationLog.setShift(1);
		 posOperationLog.setCreateDate(new Date());
		 posOperationLogMapper.insertPOSOperationLog(posOperationLog);
	}
	@Override
	public Pager<OperatorShiftVO> findOpeatorShiftList(Business business,
			Pager<OperatorShiftVO> pager,
			OperatorShiftSearcher operatorShiftSearcher) {
		Integer operatorShiftCount = operatorShiftMapper.findOperatorShiftCount(business.getSelfBusinessId(),business.getSelfBusinessType().toString(),operatorShiftSearcher);
		pager.setTotalCount(operatorShiftCount);
		List<OperatorShiftVO>   opeartorShift = operatorShiftMapper.findOperatorShiftList(business.getSelfBusinessId(),business.getSelfBusinessType().toString(), pager, operatorShiftSearcher);
		pager.setList(opeartorShift);
		return pager;
	}
	
	@Override
	public Pager<ShiftItemVO> findOpeatorShiftDetail(Pager<ShiftItemVO> pager,String operatorSessionCode) {
		Integer operatorShiftOrderItemCount = operatorShiftMapper.findOperatorOrderItemCount(operatorSessionCode);
		pager.setTotalCount(operatorShiftOrderItemCount);
		List<ShiftItemVO> itemList = operatorShiftMapper.findOperatorShiftOrderItemList( pager ,operatorSessionCode);
		pager.setList(itemList);
		return pager;
	}
	 
	@Override
	public OperatorShiftVO getOpeatorShiftDetail(String operatorSessionCode) {
		return operatorShiftMapper.findOperatorShift(operatorSessionCode);
	}
	@Override
	public POSOperationLog findOperatorSessionByDeviceNumber(String deviceNumber) {
		return posOperationLogMapper.findOperatorSessionByDeviceNumber(deviceNumber);
	}
}
