package com.xkeshi.service;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xkeshi.dao.CashTransactionDAO;
import com.xkeshi.dao.PhysicalCouponOrderDAO;
import com.xkeshi.dao.PrepaidCardChargeOrderDAO;
import com.xkeshi.dao.WXPayTransactionDAO;
import com.xkeshi.pojo.vo.DailyStatisticsSummaryVO;
import com.xkeshi.pojo.vo.SystemParam;
import com.xpos.common.entity.Order.Status;
import com.xpos.common.entity.Shop;
import com.xpos.common.persistence.mybatis.AlipayTransactionMapper;
import com.xpos.common.persistence.mybatis.BankNFCTransactionMapper;
import com.xpos.common.persistence.mybatis.CouponMapper;
import com.xpos.common.persistence.mybatis.POSTransactionMapper;
import com.xpos.common.persistence.mybatis.member.MemberMapper;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.searcher.member.MemberSearcher;
import com.xpos.common.service.OrderService;
import com.xpos.common.service.ShopService;

/**
 * 统计信息
 * @author chengj
 *
 */
@Service
public class StatisticsService {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private CashTransactionDAO cashTransactionDAO;
	@Autowired
	private AlipayTransactionMapper alipayTransactionMapper;
	@Autowired
	private POSTransactionMapper posTransactionMapper;
	@Autowired
	private BankNFCTransactionMapper bankNFCTransactionMapper;
	@Autowired
	private WXPayTransactionDAO wxpayTransactionDAO;
	@Autowired
	private PrepaidCardChargeOrderDAO prepaidCardChargeOrderDAO;
	@Autowired
	private PhysicalCouponOrderDAO physicalCouponOrderDAO;
	@Autowired
	private CouponMapper couponMapper;
	@Autowired
	private MemberMapper memberMapper;
	
	
	public DailyStatisticsSummaryVO generateDailySummary(SystemParam param, String orderType, Date day) {
		DailyStatisticsSummaryVO summaryVO = new DailyStatisticsSummaryVO();
		DateTime startTime = new DateTime(day).withTimeAtStartOfDay();
		Date startDate = startTime.toDate();
		Date endDate = startTime.plusDays(1).minusMillis(1).toDate();
		
		Shop shop = shopService.findShopByIdIgnoreVisible(param.getMid());
		
		//收银统计
		if(StringUtils.equalsIgnoreCase("XPOS_ORDER", orderType)){
			OrderSearcher orderSearcher = new OrderSearcher();
			orderSearcher.setStartDateTime(startTime.toDate());
			orderSearcher.setEndDateTime(startTime.plusDays(1).toDate());
			orderSearcher.setOperatorId(param.getOperatorId());
			orderSearcher.setStatus(Status.SUCCESS);
			String[] orderStatistic = orderService.getOrderStatistics(shop, orderSearcher);
			summaryVO.setOrderAmount(new BigDecimal(orderStatistic[1]));
			summaryVO.setOrderCount(Integer.valueOf(orderStatistic[2]));
		}else if(StringUtils.equalsIgnoreCase("XPOS_ORDER", orderType)){
			//TODO third order
		}

		//现金统计
		int cashTransactionCount = cashTransactionDAO.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal cashTransactionAmount = cashTransactionDAO.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setCashTransactionCount(cashTransactionCount);
		summaryVO.setCashTransactionAmount(cashTransactionAmount);
		//支付宝统计
		int alipayTransactionCount = alipayTransactionMapper.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal alipayTransactionAmount = alipayTransactionMapper.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setAlipayTransactionCount(alipayTransactionCount);
		summaryVO.setAlipayTransactionAmount(alipayTransactionAmount);
		//刷卡统计
		int posTransactionCount = posTransactionMapper.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal posTransactionAmount = posTransactionMapper.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setPOSTransactionCount(posTransactionCount);
		summaryVO.setPOSTransactionAmount(posTransactionAmount);
		//NFC统计
		int bankNFCTransactionCount = bankNFCTransactionMapper.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal bankNFCTransactionAmount = bankNFCTransactionMapper.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setBankNFCTransactionCount(bankNFCTransactionCount);
		summaryVO.setBankNFCTransactionAmount(bankNFCTransactionAmount);
		//微信支付统计
		int wxpayTransactionCount = wxpayTransactionDAO.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal wxpayTransactionAmount = wxpayTransactionDAO.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setWxpayTransactionCount(wxpayTransactionCount);
		summaryVO.setWxpayTransactionAmount(wxpayTransactionAmount);
		//预付卡统计
		int prepaidCardChargeOrderCount = prepaidCardChargeOrderDAO.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal prepaidCardChargeOrderAmount = prepaidCardChargeOrderDAO.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setPrepaidCardChargeOrderCount(prepaidCardChargeOrderCount);
		summaryVO.setPrepaidCardChargeOrderAmount(prepaidCardChargeOrderAmount);
		//实体券统计
		int physicalCouponCount = physicalCouponOrderDAO.countByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		BigDecimal physicalCouponAmount = physicalCouponOrderDAO.getAmountByOperatorAndType(param.getOperatorId(), orderType, startDate, endDate, Status.SUCCESS);
		summaryVO.setPhysicalCouponCount(physicalCouponCount);
		summaryVO.setPhysicalCouponAmount(physicalCouponAmount);
		
		/*CouponSearcher couponSearcher = new CouponSearcher();
		Set<CouponStatus> couponStatus = new HashSet<>();
		couponStatus.add(CouponStatus.USED);
		couponSearcher.setStatus(couponStatus);
		couponSearcher.setBusiness(shop);
		couponSearcher.setStartDate(startDate);
		couponSearcher.setEndDate(endDate);
		couponSearcher.setOperatorId(param.getOperatorId());
		int couponConsumedCount = couponMapper.countBySearcher(couponSearcher);*/
		summaryVO.setCouponConsumedCount(0);//电子券暂未设置
		
		MemberSearcher memberSearcher = new MemberSearcher();
		memberSearcher.setCreateStartDate(startDate);
		memberSearcher.setCreateEndDate(endDate);
		memberSearcher.setOperatorId(param.getOperatorId());
		int registeredMemberCount = memberMapper.countBySearcher(memberSearcher);
		summaryVO.setCouponConsumedCount(registeredMemberCount);
		
		return summaryVO;
	}
	
	
}
