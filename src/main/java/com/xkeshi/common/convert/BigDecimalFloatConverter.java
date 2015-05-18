package com.xkeshi.common.convert;

import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class BigDecimalFloatConverter implements CustomConverter {
	private static final Logger logger = LoggerFactory.getLogger(BigDecimalFloatConverter.class);
	
	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof BigDecimal) {
					
					Float floatValue = ((BigDecimal) sourceFieldValue).setScale(2,
							BigDecimal.ROUND_HALF_UP).floatValue();
					obj = floatValue;
				} else if (sourceFieldValue instanceof Float) {
                    Float value = (Float)sourceFieldValue;
					obj = BigDecimal.valueOf(value);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}

}
