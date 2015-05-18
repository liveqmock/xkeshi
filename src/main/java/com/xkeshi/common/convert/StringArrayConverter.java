package com.xkeshi.common.convert;

import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 字符串和数组之间转换，分隔符为半角逗号
 *
 */
public class StringArrayConverter implements CustomConverter {
	private static final Logger logger = LoggerFactory.getLogger(StringArrayConverter.class);
	
	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof Object[]) {

                    String rawStr = StringUtils.join((Object[]) sourceFieldValue, ',');
                    obj = rawStr;
                } else if (sourceFieldValue instanceof String) {
					String value = (String)sourceFieldValue;
					obj = StringUtils.split(value, ',');
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}

}
