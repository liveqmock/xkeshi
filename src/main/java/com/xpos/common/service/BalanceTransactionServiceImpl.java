package com.xpos.common.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.config.FactoryConfig;
import com.alipay.config.PaySourceConfig;
import com.alipay.config.PaySourceConfig.PaySource;
import com.alipay.service.AlipayDirectService;
import com.xpos.common.entity.BalanceTransaction;
import com.xpos.common.entity.BalanceTransaction.BalanceChangeType;
import com.xpos.common.entity.Payment;
import com.xpos.common.entity.Payment.PaymentStatus;
import com.xpos.common.entity.example.BalanceTransactionExample;
import com.xpos.common.entity.example.PaymentExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.Account;
import com.xpos.common.persistence.mybatis.BalanceTransactionMapper;
import com.xpos.common.persistence.mybatis.PaymentMapper;
import com.xpos.common.utils.Pager;
import com.xpos.common.utils.UUIDUtil;

@Service
public class BalanceTransactionServiceImpl implements BalanceTransactionService{

	@Resource
	private BalanceTransactionMapper balanceTransactionMapper;
	
	@Resource
	private PaymentMapper paymentMapper;
	
	@Resource
	private AlipayDirectService  alipayDirectService ;
	
	@Autowired
	private MerchantService merchantService;
	
	@Autowired
	private ShopService shopService;
		
	@Override
	public Pager<BalanceTransaction> findBalanceTransaction(
			Pager<BalanceTransaction> pager, BalanceTransactionExample example) {

		List<BalanceTransaction> list = balanceTransactionMapper.selectByExample(example, pager);
		pager.setList(list);
		
		int totalCount = balanceTransactionMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		
		return pager;
	}
	
	@Transactional
	public String generateAlipayUrl(Account account, Business business, BigDecimal amount, String bank ,PaySource paySource) throws Exception {
		
		Payment payment = new Payment();
		payment.setSerialNo(UUIDUtil.getRandomString(32));
		payment.setBusinessId(business.getSelfBusinessId());
		payment.setBusinessType(business.getSelfBusinessType());
		payment.setAmount(amount);
		payment.setStatus(PaymentStatus.WAITFORPAY);
		payment.setDescription("充值"+payment.getAmount()+"元");
		payment.setAccount(account);
		
		paymentMapper.insert(payment);
		
		PaySourceConfig payConfig = FactoryConfig.getPayConfig(paySource);
		String input_charset = payConfig.getCharSet();
		String sign_type = payConfig.getSignType();
		String seller_email = payConfig.getSellerEmail();
		String partner = payConfig.getPartnerID();
		String key = payConfig.getKey();

		String show_url = payConfig.getShowUrl();
		String notify_url = payConfig.getNotifyUrl();
		String return_url = payConfig.getCallBackUrl();

		String subject = payment.getDescription(); // 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
		String body = ""; // 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
		String total_fee = String.valueOf(payment.getAmount()); // 订单总金额，显示在支付宝收银台里的“应付总额”里
		String orderNum = payment.getSerialNo();

		String paymethod = "bankPay";

		if ("ALIPAY".equals(bank)){
			paymethod = "directPay";
			bank = "";
		}
		return alipayDirectService.CreateUrl(PaySource.XKESHI_ALIPAY_DIRECT, partner, seller_email, return_url,
				notify_url, show_url, orderNum, subject, body, total_fee,
				paymethod, bank, "", "", "", "", "", "", "",
				input_charset, key, sign_type);
	}

	@Override
	@Transactional
	public boolean increaseBalance(Account account, Business business, BigDecimal amount, String description) {
		BalanceTransaction transaction = new BalanceTransaction();
		transaction.setType(BalanceChangeType.CHARGE);
		transaction.setBusinessId(business.getSelfBusinessId());
		transaction.setBusinessType(business.getSelfBusinessType());
		transaction.setAmount(amount);
		transaction.setAccount(account);
		transaction.setDescription(description);
		return balanceTransactionMapper.insert(transaction) > 0;
	}

	@Override
	public Payment findPaymentByNum(String num) {
		PaymentExample example = new PaymentExample();
		example.createCriteria().addCriterion("serialNo=", num)
								.addCriterion("deleted=", false);
		
		return paymentMapper.selectOneByExample(example);
	}

	@Override
	@Transactional
	public boolean processPayment(Payment payment) {
		payment.setStatus(PaymentStatus.PAID);
		paymentMapper.updateByPrimaryKey(payment);
		Business business = null;
		if(BusinessType.MERCHANT.equals(payment.getBusinessType())){
			business = merchantService.findMerchant(payment.getBusinessId());
		}else if(BusinessType.SHOP.equals(payment.getBusinessType())){
			business = shopService.findShopById(payment.getBusinessId());
		}
		return increaseBalance(payment.getAccount(), business, payment.getAmount(), payment.getDescription());
	}

	@Override
	@Transactional
	public boolean deductBalance(Account account, Business business, BigDecimal amount, String description) {
		BalanceTransaction transaction = new BalanceTransaction();
		transaction.setType(BalanceChangeType.DEDUCT);
		transaction.setBusinessId(business.getSelfBusinessId());
		transaction.setBusinessType(business.getSelfBusinessType());
		transaction.setAmount(amount.multiply(new BigDecimal(-1)));
		transaction.setAccount(account);
		transaction.setDescription(description);
		return balanceTransactionMapper.insert(transaction) > 0;
	}
}
