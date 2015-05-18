package com.xpos.common.utils;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import com.alibaba.fastjson.JSON;


public class JsonMessageConverter  extends AbstractMessageConverter {
	private static Logger log = LoggerFactory.getLogger(JsonMessageConverter.class);

	public static final String DEFAULT_CHARSET = "UTF-8";

	private volatile String defaultCharset = DEFAULT_CHARSET;
	
	public JsonMessageConverter() {
		super();
	}
	
	public void setDefaultCharset(String defaultCharset) {
		this.defaultCharset = (defaultCharset != null) ? defaultCharset : DEFAULT_CHARSET;
	}
	
	public Object fromMessage(Message message)
			throws MessageConversionException {
		return null;
	}
	
	public static <T> T fromMessage(Message message,Class<T> clz) {
		String json = "";
		try {
			json = new String(message.getBody(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return (T)JSON.parseObject(json, clz);
	}	
	

	protected Message createMessage(Object objectToConvert, MessageProperties messageProperties)
			throws MessageConversionException {
		byte[] bytes = null;
		try {
			String jsonString = JSON.toJSONString(objectToConvert);
			bytes = jsonString.getBytes(this.defaultCharset);
		} catch (UnsupportedEncodingException e) {
			log.info("Failed to convert Message content", e);
			throw new MessageConversionException(
					"Failed to convert Message content", e);
		} 
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		messageProperties.setContentEncoding(this.defaultCharset);
		if (bytes != null) {
			messageProperties.setContentLength(bytes.length);
		}
		return new Message(bytes, messageProperties);

	}
}