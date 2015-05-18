package com.xpos.common.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.drongam.hermes.entity.SMS;
import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.pojo.po.BalanceTransaction;
import com.xkeshi.pojo.po.ShopInfo;
import com.xkeshi.service.XBalanceTransactionService;
import com.xkeshi.service.XMerchantService;
import com.xkeshi.service.XShopInfoService;
import com.xkeshi.service.XShopService;
import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.CouponInfo.CouponInfoType;
import com.xpos.common.entity.CouponPayment;
import com.xpos.common.entity.CouponPayment.CouponPaymentSource;
import com.xpos.common.entity.CouponPayment.CouponPaymentStatus;
import com.xpos.common.entity.CouponPayment.CouponPaymentType;
import com.xpos.common.entity.SMSMessage;
import com.xpos.common.entity.SMSMessage.SMSMessageStatus;
import com.xpos.common.entity.SMSTask;
import com.xpos.common.entity.SMSTask.SMSTaskStatus;
import com.xpos.common.entity.example.SMSMessageExample;
import com.xpos.common.entity.example.SMSTaskExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.member.Member;
import com.xpos.common.entity.security.Account;
import com.xpos.common.entity.security.User;
import com.xpos.common.exception.CouponInfoException;
import com.xpos.common.exception.GenericException;
import com.xpos.common.persistence.mybatis.SMSMessageMapper;
import com.xpos.common.persistence.mybatis.SMSTaskMapper;
import com.xpos.common.service.member.MemberService;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.Pager;
import com.xpos.common.utils.UUIDUtil;

@Service
public class SMSServiceImpl implements SMSService{
	private final static Logger logger = LoggerFactory.getLogger(SMSServiceImpl.class);
	
//	private final static String SMS_QUEUE_OUT_KEY = "sms_queue_out_key";
	private final static String PLACE_HOLDER_NAME = "$客户姓名$";
	//private final static String PLACE_HOLDER_SEX = "$客户性别$";
	private final static String PLACE_HOLDER_BIRTHDAY = "$客户生日$";
	private final static String PLACE_HOLDER_RECEIVE = "$优惠获取链接$";
	private final static String PLACE_HOLDER_PACKAGE_DETAIL = "$优惠查看链接$";
	private final static String PLACE_HOLDER_COUPON_CODE = "$优惠码$";	

	@Resource
	private MemberService memberService;
	
	@Resource
	private CouponService couponService;
	
	@Resource
	private BalanceTransactionService balanceTransactionService;
	
//	@Resource
//	private AmqpTemplate amqpTemplate;
	@Resource
	private com.drongam.hermes.service.SMSService service;

	@Resource
	private ShopService shopService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private CouponPaymentService couponPaymentService;
	
	@Resource
	private SMSMessageMapper smsMessageMapper;

	@Resource
	private MobileBindService mobileBindService;
	
	@Resource
	private XMerchantService  xMerchantService  ;
	
	@Resource
	private XShopService   xShopService   ;
	
	@Resource
	private XShopInfoService  xShopInfoService   ;
	
	@Resource
	private XBalanceTransactionService xbalanceTransactionService;
	
	@Resource
	private SMSTaskMapper smsTaskMapper;
	
	@Value("#{settings['sms.unitPrice']}")
	private double smsUnitPrice;
	
	@Override
	public void send(SMSMessage smsMessage) {
		send(smsMessage, "xpos");
	}
	
	@Override
	public void send(SMSMessage smsMessage, String channel) {
		
		smsMessage.setStatus(SMSMessageStatus.SENDING);
		smsMessageMapper.insert(smsMessage);
		
		SMS sms = new SMS();
		sms.setMobile(smsMessage.getMobile());
		sms.setMessage(smsMessage.getContent());
		sms.setChannel(channel);
		sms.setCreateDate(new Date());
		try {
			service.sendSMS(sms);
		} catch (Exception e) {
			logger.error("SendSMS出错了:"+e.getMessage());
		}
	}


	@Override
	public boolean send(SMS sms) {
		 //默认发送xpos通道
		 return send(sms, "xpos");
	}

	@Override
	public boolean send(SMS sms, String channel) {
		sms.setChannel(channel);
		sms.setCreateDate(new Date());
		try {
			service.sendSMS(sms);
		} catch (Exception e) {
			logger.error("send出错了:"+e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean insertSMSMessage(SMSMessage smsMessage) {
		smsMessage.setStatus(SMSMessageStatus.SENDING);
		return smsMessageMapper.insert(smsMessage) >0;
	}
	
	@Override
	@Transactional
	public String sendByMemberList(Business business, Account account, SMSTask smsTask, CouponInfo couponInfo) {
		List<Member> members = memberService.findMembersByBusiness(business, null, null).getList();
		int totalCount = members.size();
		int successCount = 0;
		
		//从Balance扣除相应金额
		BigDecimal amount = new BigDecimal(totalCount).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP);
		balanceTransactionService.deductBalance(account, business, amount, "根据会员列表批量发送优惠券短信");
		
		
		//创建SMSTask
		smsTask.setBusiness(business);
		smsTask.setCount(totalCount);
		smsTask.setStatus(SMSTaskStatus.SENDING); //TODO 暂时只有立即发送。后期有定时发送功能再改成PENDING
		smsTaskMapper.insert(smsTask);
		
		String smsSuffix = shopService.findSmsSuffixByBusinessTypeAndBusinessId(business.getSelfBusinessType(), business.getSelfBusinessId());
		if(StringUtils.isBlank(smsSuffix)){
			smsSuffix = "爱客仕xpos";
		}
		
		//遍历会员列表，发送短信
		for(Member member : members){
			try{
				List<Coupon> couponList = new ArrayList<>();
				String packageSerial = UUIDUtil.getRandomString(32);
				if(couponInfo != null){
					//1.校验couponInfo是否可领取(库存、销售时间等)
					if(!couponInfo.canBuy()||couponInfo.getPublished()==false){
						throw new GenericException("优惠券失效或未发布导致无法领取");
					}
					
					//2.生成couponPayment购买记录
					CouponPayment payment = new CouponPayment();
					payment.setCouponInfo(couponInfo);
					payment.setSum(new BigDecimal(0));
					payment.setCode(UUIDUtil.getRandomString(32));
					payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
					payment.setType(CouponPaymentType.SMS_PUSH);
					payment.setQuantity(1);
					payment.setTradeDate(new Date());
					payment.setSource(CouponPaymentSource.XKESHI_WEB);
					couponPaymentService.saveCouponPayment(payment);
					
					//3.领取优惠券，coupon表插入数据
					if(couponInfo.getType().equals(CouponInfoType.NORMAL)){
						Coupon coupon = new Coupon();
						coupon.setBusiness(business);
						coupon.setType(CouponInfoType.NORMAL);
						coupon.setCouponInfo(couponInfo);
						coupon.setPayment(payment);
						// 添加关联用户会员信息 writed by snoopy @2014-11-21
						coupon.setMember(member);
						couponService.saveCoupon(coupon, true);//coupon表添加记录
						couponList.add(coupon);
					}else if(couponInfo.getType().equals(CouponInfoType.PACKAGE) && !CollectionUtils.isEmpty(couponInfo.getItems())){
						for(CouponInfo item : couponInfo.getItems()){
							for(int i = 0; i < item.getQuantity(); i++){
								item.setStartDate(couponInfo.getStartDate());
								item.setEndDate(couponInfo.getEndDate());
								Coupon coupon = new Coupon();
								coupon.setBusiness(business);
								coupon.setType(CouponInfoType.CHILD);
								coupon.setParent(couponInfo);
								coupon.setPackageSerial(packageSerial);
								coupon.setCouponInfo(item);
								coupon.setPayment(payment);
								// 添加关联用户会员信息 writed by snoopy @2014-11-21
								coupon.setMember(member);
								couponService.saveCoupon(coupon, true);//coupon表添加记录
								couponList.add(coupon);
							}
						}
					}
				}
				
				for(Coupon coupon : couponList){
					//3.创建短信
					SMSMessage message = new SMSMessage();
					String replacedContent = StringUtils.replace(smsTask.getTemplate(), PLACE_HOLDER_NAME, member.getName());
					if(member.getBirthday() != null){
						replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_BIRTHDAY, new DateTime(member.getBirthday()).toString("MM月dd日"));
					}else{
						replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_BIRTHDAY, "");
					}
					if(couponInfo != null){
						replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_RECEIVE, "http://v2.xkeshi.com/coupon/" + couponInfo.getId() + "/detail");
						replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_PACKAGE_DETAIL, "http://v2.xkeshi.com/coupon/c/p/" + couponInfo.getEid() + "?sn="+packageSerial);
						if(StringUtils.contains(replacedContent, PLACE_HOLDER_COUPON_CODE)){
							 //v2版uuid coupon code
							replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_COUPON_CODE, coupon.getCouponCode() + " 查看详情：http://coupon.xka.me/"+coupon.getUniqueCode());
						}
					}
					message.setContent("【"+smsSuffix+"】"+replacedContent);
					message.setTask(smsTask);
					message.setMessageKey(UUIDUtil.getRandomString(32));
					message.setMobile(member.getMobile());
					//4.发送到MQ
					try{
						String channel = shopService.findSmsChannelByBusiness(business);
						if(StringUtils.isBlank(channel)){
							send(message);
						}else{
							send(message, channel);
						}
						//5.统计
						successCount++;//刷新发送成功计数
					}catch(Exception e){
						logger.error("短信发送任务失败", e);
					}
				}
				
			}catch(Exception e){
				logger.error("批量短信发送任务失败，会员姓名【"+member.getName()+"】，手机号【"+member.getMobile()+"】", e);
				throw new RuntimeException("短信发送失败");
			}
		}
		
		return "短信发送任务完成，共 "+totalCount+" 条， 发送成功 "+successCount+" 条";
	}

	/*
	private Message createMessage(Object objectToConvert, MessageProperties messageProperties) throws MessageConversionException {
		byte[] bytes = null;
		try {
			String jsonString = JSONObject.fromObject(objectToConvert).toString();
			bytes = jsonString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new MessageConversionException(
					"Failed to convert Message content", e);
		} 
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		messageProperties.setContentEncoding("UTF-8");
		if (bytes != null) {
			messageProperties.setContentLength(bytes.length);
		}
		return new Message(bytes, messageProperties);

	}
	*/

	@Override
	@Transactional
	public String sendByMobileList(Business business, Account account, SMSTask smsTask, String[] mobileArray, CouponInfo couponInfo) {
		int totalCount = mobileArray.length;
		int successCount = 0;
		
		//从Balance扣除相应金额
		BigDecimal amount = new BigDecimal(totalCount).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP);
		balanceTransactionService.deductBalance(account, business, amount, "根据手机列表批量发送优惠短信");
		
		
		//创建SMSTask
		smsTask.setBusiness(business);
		smsTask.setCount(totalCount);
		smsTask.setStatus(SMSTaskStatus.SENDING); //TODO 暂时只有立即发送。后期有定时发送功能再改成PENDING
		smsTaskMapper.insert(smsTask);
		
		String smsSuffix = shopService.findSmsSuffixByBusinessTypeAndBusinessId(business.getSelfBusinessType(), business.getSelfBusinessId());
		if(StringUtils.isBlank(smsSuffix)){
			smsSuffix = "爱客仕xpos";
		}
		
		//遍历手机号列表，发送短信
		for(String mobile : mobileArray){
			mobile = mobile.trim();
			try{
				List<Coupon> couponList = new ArrayList<>();
				String packageSerial = UUIDUtil.getRandomString(32);
				if(couponInfo != null){
					//1.校验couponInfo是否可领取(库存、销售时间等)
					if(!couponInfo.canBuy()||couponInfo.getPublished()==false){
						throw new CouponInfoException("优惠券失效或未发布导致无法领取");
					}
					
					//2.生成couponPayment购买记录
					User user = userService.findUserByMobile(mobile);
					CouponPayment payment = new CouponPayment();
					payment.setMobile(mobile);
					payment.setCouponInfo(couponInfo);
					payment.setSum(new BigDecimal(0));
					payment.setCode(UUIDUtil.getRandomString(32));
					payment.setStatus(CouponPaymentStatus.PAID_SUCCESS);
					payment.setType(CouponPaymentType.SMS_PUSH);
					payment.setQuantity(1);
					payment.setTradeDate(new Date());
					payment.setSource(CouponPaymentSource.XKESHI_WEB);
					payment.setUser(user);
					couponPaymentService.saveCouponPayment(payment);
					
					//3.领取优惠券，coupon表插入数据
					if(couponInfo.getType().equals(CouponInfoType.NORMAL)){
						Coupon coupon = new Coupon();
						coupon.setBusiness(business);
						coupon.setType(CouponInfoType.NORMAL);
						coupon.setCouponInfo(couponInfo);
						coupon.setMobile(mobile);
						coupon.setPayment(payment);
						if(user != null){
							coupon.setUser(user);
						}
						/* 添加关联用户会员信息 writed by snoopy @2014-11-21
						 * FIXME 集团创建优惠券，如果非会员统一管理，如何set member信息
						if(business !=null) {
							Member member = memberService.findMemberByMobile(business, mobile);
							if(member!=null) {
								coupon.setMember(member);
							}
						}
						 */
						couponService.saveCoupon(coupon, true);//coupon表添加记录
						couponList.add(coupon);
					}else if(couponInfo.getType().equals(CouponInfoType.PACKAGE) && !CollectionUtils.isEmpty(couponInfo.getItems())){
						for(CouponInfo item : couponInfo.getItems()){
							for(int i = 0; i < item.getQuantity(); i++){
								item.setStartDate(couponInfo.getStartDate());
								item.setEndDate(couponInfo.getEndDate());
								Coupon coupon = new Coupon();
								coupon.setBusiness(business);
								coupon.setType(CouponInfoType.CHILD);
								coupon.setParent(couponInfo);
								coupon.setPackageSerial(packageSerial);
								coupon.setCouponInfo(item);
								coupon.setMobile(mobile);
								coupon.setPayment(payment);
								if(user != null){
									coupon.setUser(user);
								}
								/* 添加关联用户会员信息 writed by snoopy @2014-11-21
								 * FIXME 集团创建优惠券，如果非会员统一管理，如何set member信息
								if(business !=null) {
									Member member = memberService.findMemberByMobile(business, mobile);
									if(member!=null) {
										coupon.setMember(member);
									}
								}
								 */
								couponService.saveCoupon(coupon, true);//coupon表添加记录
								couponList.add(coupon);
							}
						}
					}
				}
				
				for(Coupon coupon : couponList){
					//3.创建短信
					SMSMessage message = new SMSMessage();
					String replacedContent = smsTask.getTemplate();
					if(couponInfo != null){
						replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_RECEIVE, "http://v2.xkeshi.com/coupon/" + couponInfo.getId() + "/detail");
						replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_PACKAGE_DETAIL, "http://v2.xkeshi.com/coupon/c/p/" + couponInfo.getEid() + "?sn="+packageSerial);
						if(StringUtils.contains(replacedContent, PLACE_HOLDER_COUPON_CODE)){
							replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_COUPON_CODE, coupon.getCouponCode() + " 查看详情：http://coupon.xka.me/"+coupon.getUniqueCode());
						}
					}
					message.setContent("【"+smsSuffix+"】"+replacedContent);
					message.setTask(smsTask);
					message.setMessageKey(UUIDUtil.getRandomString(32));
					message.setMobile(mobile);
					
					//4.发送到MQ
					try{
						String channel = shopService.findSmsChannelByBusiness(business);
						if(StringUtils.isBlank(channel)){
							send(message);
						}else{
							send(message, channel);
						}
						//5.统计
						successCount++;//刷新发送成功计数
					}catch(Exception e){
						logger.error("批量短信发送任务失败", e);
					}
					
				}
			}catch(RuntimeException e){
				logger.error("批量短信发送任务失败，手机号【" + mobile + "】", e);
				throw e;
			}catch(Exception e){
				logger.error("批量短信发送任务失败，手机号【" + mobile + "】", e);
				throw new RuntimeException("短信发送失败");
			}
		}
		
		return "短信发送任务完成，共 "+totalCount+" 条， 发送成功 "+successCount+" 条";
	}


	@Override
	public Pager<SMSTask> findSMSTasks(Business business, SMSTaskExample example, Pager<SMSTask> pager) {
		if(example == null){
			example = new SMSTaskExample();
			example.createCriteria();
		}
		example.appendCriterion("deleted=", false);
		example.appendCriterion(BusinessSQLBuilder.getBusinessByShopSQL(business.getSelfBusinessType(), business.getSelfBusinessId()));
		example.setOrderByClause(" createDate DESC");
		
		List<SMSTask> list = smsTaskMapper.selectByExample(example, pager);
		int totalCount = smsTaskMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	@Transactional
	public boolean updateSMSMessageStatus(String messageKey, SMSMessageStatus status) {
		boolean result = true;
		
		SMSMessageExample smsMessageExample = new SMSMessageExample();
		smsMessageExample.createCriteria()
						.addCriterion("messageKey=", messageKey)
						.addCriterion("deleted=", false);
		SMSMessage smsMessage = smsMessageMapper.selectOneByExample(smsMessageExample);
		if(smsMessage == null){
			return false;
		}
		
		//update SMSMessage
		smsMessage.setStatus(status);
		result = smsMessageMapper.updateByPrimaryKey(smsMessage) == 1;
		
		//update SMSTask count
		SMSTask task = smsMessage.getTask();
		if(SMSMessageStatus.SUCCESS.equals(status)){
			task.setSucceeded(task.getSucceeded() + 1);
		}else if(SMSMessageStatus.FAILED.equals(status)){
			task.setFailed(task.getFailed() + 1);
		}
		result = result & smsTaskMapper.updateByPrimaryKey(task) == 1;
		
		//update SMSTask status
		if(task.getCount() == task.getSucceeded() + task.getFailed()){
			task.setStatus(SMSTaskStatus.DONE);
			result = result & smsTaskMapper.updateByPrimaryKey(task) == 1;
			//TODO 结算账户余额
		}
		
		if(!result){
			throw new RuntimeException();
		}else{
			return true;
		}
	}

	@Override
	public SMSTask findSMSTaskById(Long id) {
		SMSTask smsTask = smsTaskMapper.selectByPrimaryKey(id);
		if(smsTask == null || smsTask.getDeleted()==true){
			return null;
		}
		return smsTask;
	}


	@Override
	public Pager<SMSMessage> findSMSMessagesBySMSTask(Pager<SMSMessage> pager, SMSTask task) {
		SMSMessageExample example = new SMSMessageExample();
		example.createCriteria()
				.addCriterion("task_id=", task.getId())
				.addCriterion("deleted=", false);
		
		List<SMSMessage> list = smsMessageMapper.selectByExample(example, pager);
		int totalCount = smsMessageMapper.countByExample(example);
		pager.setTotalCount(totalCount);
		pager.setList(list);
		
		return pager;
	}

	@Override
	public int[] statusStatisticBySMSTask(SMSTask smsTask) {
		int[] statistic = new int[4];
		
		//succeeded
		SMSMessageExample example = new SMSMessageExample();
		example.createCriteria().addCriterion("task_id=", smsTask.getId()).addCriterion("deleted=", false);
		example.appendCriterion("status=", SMSMessageStatus.SUCCESS.toString());
		statistic[0] = smsMessageMapper.countByExample(example);
		
		//failed
		example = new SMSMessageExample();
		example.createCriteria().addCriterion("task_id=", smsTask.getId()).addCriterion("deleted=", false);
		example.appendCriterion("status=", SMSMessageStatus.FAILED.toString());
		statistic[1] = smsMessageMapper.countByExample(example);
		
		//sending
		example = new SMSMessageExample();
		example.createCriteria().addCriterion("task_id=", smsTask.getId()).addCriterion("deleted=", false);
		example.appendCriterion("status=", SMSMessageStatus.SENDING.toString());
		statistic[2] = smsMessageMapper.countByExample(example);
		
		//pending
		example = new SMSMessageExample();
		example.createCriteria().addCriterion("task_id=", smsTask.getId()).addCriterion("deleted=", false);
		example.appendCriterion("status=", SMSMessageStatus.PENDING.toString());
		statistic[3] = smsMessageMapper.countByExample(example);
		
		return statistic;
	}

	@Override
	public void sendSMSCode(Business business, String mobile) {
		String   smsSuffix  = "";
		if (business != null) {
			smsSuffix = shopService.findSmsSuffixByBusinessTypeAndBusinessId(business.getSelfBusinessType(),business.getSelfBusinessId());
		}
	 	String   code      = mobileBindService.generateValidationCodewithOutopenId("xkeshi", mobile);
		String   channel   = shopService.findSmsChannelByBusiness(business);
		smsSuffix  =  StringUtils.isBlank(smsSuffix)   ? "爱客仕xpos" : smsSuffix ;
		channel    =  StringUtils.isBlank(channel)     ? "xpos"       : channel;
		SMSMessage message = new SMSMessage();
		message.setMobile(mobile);
		message.setContent("【"+smsSuffix+"】验证码："+code);
		this.send(message, channel);
	}
	
	@Override
	@Transactional
	public String sendByMemberList(Business business, Account account, SMSTask smsTask) {
		List<Member> members = memberService.findMembersByBusiness(business, null, null).getList();
		int totalCount = members.size();
		int successCount = 0;
		
		//从Balance扣除相应金额
		BigDecimal amount = new BigDecimal(totalCount).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP);
		balanceTransactionService.deductBalance(account, business, amount, "根据会员列表批量发送推广短信");
		
		
		//创建SMSTask
		smsTask.setBusiness(business);
		smsTask.setCount(totalCount);
		smsTask.setStatus(SMSTaskStatus.SENDING); //TODO 暂时只有立即发送。后期有定时发送功能再改成PENDING
		smsTaskMapper.insert(smsTask);
		
		String smsSuffix = shopService.findSmsSuffixByBusinessTypeAndBusinessId(business.getSelfBusinessType(), business.getSelfBusinessId());
		if(StringUtils.isBlank(smsSuffix)){
			smsSuffix = "爱客仕xpos";
		}
		
		//遍历会员列表，发送短信
		for(Member member : members){
			try{
				//创建短信
				SMSMessage message = new SMSMessage();
				String replacedContent = StringUtils.replace(smsTask.getTemplate(), PLACE_HOLDER_NAME, member.getName());
				if(member.getBirthday() != null){
					replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_BIRTHDAY, new DateTime(member.getBirthday()).toString("MM月dd日"));
				}else{
					replacedContent = StringUtils.replace(replacedContent, PLACE_HOLDER_BIRTHDAY, "");
				}
				message.setContent("【"+smsSuffix+"】"+replacedContent);
				message.setTask(smsTask);
				message.setMessageKey(UUIDUtil.getRandomString(32));
				//4.发送到MQ
				try{
					String channel = shopService.findSmsChannelByBusiness(business);
					if(StringUtils.isBlank(channel)){
						send(message);
					}else{
						send(message, channel);
					}
					//5.统计
					successCount++;//刷新发送成功计数
				}catch(Exception e){
					logger.error("短信发送任务失败", e);
				}
				
			}catch(Exception e){
				logger.error("批量短信发送任务失败，会员姓名【"+member.getName()+"】，手机号【"+member.getMobile()+"】", e);
				throw new RuntimeException("短信发送失败");
			}
		}
		
		return "短信发送任务完成，共 "+totalCount+" 条， 发送成功 "+successCount+" 条";
	}

	@Override
	@Transactional
	public String sendByMobileList(Business business, Account account, SMSTask smsTask, String[] mobileArray) {
		int totalCount = mobileArray.length;
		int successCount = 0;
		
		//从Balance扣除相应金额
		BigDecimal amount = new BigDecimal(totalCount).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP);
		balanceTransactionService.deductBalance(account, business, amount, "根据手机列表批量发送推广短信");
		
		
		//创建SMSTask
		smsTask.setBusiness(business);
		smsTask.setCount(totalCount);
		smsTask.setStatus(SMSTaskStatus.SENDING); //TODO 暂时只有立即发送。后期有定时发送功能再改成PENDING
		smsTaskMapper.insert(smsTask);
		
		String smsSuffix = shopService.findSmsSuffixByBusinessTypeAndBusinessId(business.getSelfBusinessType(), business.getSelfBusinessId());
		if(StringUtils.isBlank(smsSuffix)){
			smsSuffix = "爱客仕xpos";
		}
		
		//遍历手机号列表，发送短信
		for(String mobile : mobileArray){
			mobile = mobile.trim();
			try{
				//创建短信
				SMSMessage message = new SMSMessage();
				String replacedContent = smsTask.getTemplate();
				message.setContent("【"+smsSuffix+"】"+replacedContent);
				message.setTask(smsTask);
				message.setMessageKey(UUIDUtil.getRandomString(32));
				message.setMobile(mobile);
				
				//4.发送到MQ
				try{
					String channel = shopService.findSmsChannelByBusiness(business);
					if(StringUtils.isBlank(channel)){
						send(message);
					}else{
						send(message, channel);
					}
					//5.统计
					successCount++;//刷新发送成功计数
				}catch(Exception e){
					logger.error("批量短信发送任务失败", e);
				}
					
			}catch(Exception e){
				logger.error("批量短信发送任务失败，手机号【" + mobile + "】", e);
				throw new RuntimeException("短信发送失败");
			}
		}
		
		return "短信发送任务完成，共 "+totalCount+" 条， 发送成功 "+successCount+" 条";
	}
	
	
	/**
	 * 保存短信并扣费
	 * @author xk
	 * @param businessId
	 * @param businessType
	 * @param sms            短信内容
	 * @param hiddenContent  系统保存短信信息（短信内容隐藏）
	 * @Param balanceTransactionDescription 描述
	 */
	public  boolean sendSMSAndDeductions(Long businessId ,BusinessType businessType, SMS sms,String hiddenContent,String balanceTransactionDescription) {

		//a.扣除相应金额
		BigDecimal amount = new BigDecimal(-1).multiply(new BigDecimal(smsUnitPrice)).setScale(2, RoundingMode.HALF_UP);
	    //b.资金是否统一管理
		String channel    = "xpos";
		String smsSuffix  = "爱客仕xpos";
		if (BusinessType.SHOP.equals(businessType)) {
	        boolean central = xMerchantService.checkMemberCentralManagementByShopId(businessId);
	        Long merchantId = xShopService.getMerchantId(businessId);
	        if (central) { //统一管理
	        	businessType =  BusinessType.MERCHANT ;
				businessId   =  merchantId;
	        }else {
				  ShopInfo shopInfo = xShopInfoService.getShopInfoByShopId(businessId);
				  if (shopInfo != null) {
				    //TODO  商户自定义通道暂时不使用  
					//smsSuffix  =  shopInfo.getSmsSuffix();
					//channel    =  shopInfo.getSmsChannel();
      			  }
			}
		} else if (BusinessType.MERCHANT.equals(businessType)) {
				boolean central = xMerchantService.checkMemberCentralManagementByMerchantId(businessId);
			    if (!central) 
					 return false;
			    //TODO  商户自定义通道暂时不使用  
			    //smsSuffix = merchant.getSmsSuffix();
			    //  channel = merchant.getSmsChannel();
		} else {
			return false;
		}
		sms.setMessage(StringUtils.join("【"+smsSuffix+"】",sms.getMessage()));
		//c.获取剩余资金
		BigDecimal  balance = xbalanceTransactionService.findBalanceByBusiness(businessId, businessType.toString());
		balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
		/*if (balance.compareTo(new BigDecimal(0)) >= 0) {
		    TODO 扣费未启用,保留balance资金余额为负的情况
		}*/
		//d.保留短信记录
		SMSMessage smsMessage = new SMSMessage();
		smsMessage.setMobile(sms.getMobile());
		smsMessage.setContent(StringUtils.isNotBlank(hiddenContent) ? StringUtils.join("【"+smsSuffix+"】",hiddenContent) :  sms.getMessage());
		BalanceTransaction balanceTransaction = new BalanceTransaction();
		balanceTransaction.setDescription(balanceTransactionDescription);
		balanceTransaction.setAmount(amount);
		balanceTransaction.setBusinessId(businessId);
		balanceTransaction.setBusinessType(businessType.toString());
		balanceTransaction.setType("DEDUCT");
		//e.发送短信
		return xbalanceTransactionService.insertBalanceTransaction(balanceTransaction ,smsMessage ) ? send(sms , channel) : false ;
	}
	
	@Override
	public  boolean sendSMSAndDeductions(Long businessId , Long businessTypeId, SMS sms,String hiddenContent,String balanceTransactionDescription) {
		if(GlobalSource.getIDByName(GlobalSource.metaBusinessTypeList, "集团").equals(businessTypeId)){
			return sendSMSAndDeductions(businessId, BusinessType.MERCHANT, sms, hiddenContent, balanceTransactionDescription);
		}else if(GlobalSource.getIDByName(GlobalSource.metaBusinessTypeList, "商户").equals(businessTypeId)){
			return sendSMSAndDeductions(businessId, BusinessType.SHOP, sms, hiddenContent, balanceTransactionDescription);
		}
		return false;
	}
}
