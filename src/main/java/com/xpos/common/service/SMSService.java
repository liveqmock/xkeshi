package com.xpos.common.service;

import com.drongam.hermes.entity.SMS;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.SMSMessage;
import com.xpos.common.entity.SMSMessage.SMSMessageStatus;
import com.xpos.common.entity.SMSTask;
import com.xpos.common.entity.example.SMSTaskExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.security.Account;
import com.xpos.common.utils.Pager;

public interface SMSService{
	/* ==================== sms message related ==================== */
	public void send(SMSMessage message); //默认使用“XPOS”channel

	/**直接发送短信，不做日志*/
	public boolean send(SMS sms); //默认使用“XPOS”channel
	
	public void send(SMSMessage smsMessage, String channel);
	
	/**直接发送短信，不做日志
	 * @return */
	public boolean send(SMS sms, String channel);
	
	/**保存发送短信记录*/
	public boolean insertSMSMessage(SMSMessage smsMessage);
	
	/** 发送短信验证码 */
	public void sendSMSCode(Business business, String mobile);
	
	public String sendByMobileList(Business business, Account account, SMSTask task, String[] mobileArray, CouponInfo couponInfo);
	
	public String sendByMemberList(Business business, Account account, SMSTask task, CouponInfo couponInfo);

	public String sendByMobileList(Business business, Account account, SMSTask task, String[] mobileArray);
	
	public String sendByMemberList(Business business, Account account, SMSTask task);
	
	public boolean updateSMSMessageStatus(String messageKey, SMSMessageStatus status);
	
	public Pager<SMSMessage> findSMSMessagesBySMSTask(Pager<SMSMessage> pager, SMSTask task);

	/* ==================== sms task related ==================== */
	public SMSTask findSMSTaskById(Long id);
	
	public Pager<SMSTask> findSMSTasks(Business business, SMSTaskExample example, Pager<SMSTask> pager);

	public int[] statusStatisticBySMSTask(SMSTask smsTask);
    
	/**保存、发送、扣费*/
	public boolean sendSMSAndDeductions(Long businessId ,BusinessType businessType,   SMS sms,String hiddenContent  , String balanceTransactionDescription);

	public boolean sendSMSAndDeductions(Long businessId, Long businessTypeId, SMS sms,	String hiddenContent, String balanceTransactionDescription);

		
}
