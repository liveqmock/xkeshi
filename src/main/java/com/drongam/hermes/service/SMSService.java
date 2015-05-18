package com.drongam.hermes.service;
import com.drongam.hermes.entity.SMS;


public interface SMSService{
	
	/**
	 * 实现发短信
	 */
	void sendSMS(SMS sms);
	

}
