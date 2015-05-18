package com.xkeshi.common.convert;


import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 折扣数据与字符串之间的转换 <br>
 *     如：0.75 ==> 7.5
 *
 * @author David 
 */
public class DiscountConvert implements CustomConverter {
    private static final Logger logger = LoggerFactory.getLogger(DiscountConvert.class);

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof BigDecimal) {
					sourceFieldValue = ((BigDecimal)sourceFieldValue).multiply(new BigDecimal(10));
                    obj = ((BigDecimal) sourceFieldValue).floatValue();
                } else if (sourceFieldValue instanceof Float) {
                    Float value = (Float)sourceFieldValue;
					obj = new BigDecimal(value.toString()).divide(new BigDecimal(10));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}
}
