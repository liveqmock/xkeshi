package com.xkeshi.common.convert;


import com.xkeshi.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 百分比对象与字符串之间的转换 <br>
 * @author David 
 */
public class PercentConvert implements CustomConverter {
    private static final Logger logger = LoggerFactory.getLogger(PercentConvert.class);

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof BigDecimal) {
					sourceFieldValue = ((BigDecimal)sourceFieldValue).multiply(new BigDecimal(100));
					String rawStr = Tools.trimZero((BigDecimal) sourceFieldValue);
					obj = rawStr;
				} else if (sourceFieldValue instanceof String) {
					String value = (String)sourceFieldValue;
					obj = new BigDecimal(StringUtils.trim(value)).divide(new BigDecimal(100));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}
}
