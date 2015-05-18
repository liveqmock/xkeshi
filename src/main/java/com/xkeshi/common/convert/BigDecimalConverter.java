package com.xkeshi.common.convert;

import com.xkeshi.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class BigDecimalConverter implements CustomConverter {
	private static final Logger logger = LoggerFactory.getLogger(BigDecimalConverter.class);
	
	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof BigDecimal) {
					
					String rawStr = ((BigDecimal) sourceFieldValue).setScale(2,
							BigDecimal.ROUND_HALF_UP).toPlainString();
					rawStr = Tools.trimZero(rawStr);
					obj = rawStr;
				} else if (sourceFieldValue instanceof String) {
					String value = (String)sourceFieldValue;
					obj = new BigDecimal(StringUtils.trim(value));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}

}
