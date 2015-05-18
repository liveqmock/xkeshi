package com.xkeshi.common.convert;

import com.xkeshi.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class BigDecimalTenThousandConverter implements CustomConverter {
	private static final Logger logger = LoggerFactory
			.getLogger(BigDecimalTenThousandConverter.class);

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof BigDecimal) {
					// 四舍五入处理小数后第三位（厘）
//					((BigDecimal) sourceFieldValue).setScale(2,
//							BigDecimal.ROUND_HALF_UP);
//					((BigDecimal) sourceFieldValue)
//							.divide(new BigDecimal(10000));
					String rawStr = ((BigDecimal) sourceFieldValue).divide(
							new BigDecimal(10000)).toPlainString();
					rawStr = Tools.trimZero(rawStr);
					obj = rawStr;
				} else if (sourceFieldValue instanceof String) {
					String value = (String) sourceFieldValue;
					BigDecimal decimal = new BigDecimal(StringUtils.trim(value));
					obj = decimal.multiply(new BigDecimal(10000));
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}

	

}
