package com.xkeshi.common.convert;

import com.xkeshi.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 *
 * 百分比数组和字符串之间转换，分隔符为半角逗号
 * （例如："0.07,0.10" ==> [7,10]）
 *
 */
public class PercentageArrayToStringConverter implements CustomConverter {
	private static final Logger logger = LoggerFactory.getLogger(PercentageArrayToStringConverter.class);
	
	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof Object[]) {
                    Object[] percentageArr = (Object[]) sourceFieldValue;
                    for (int i = 0; i < percentageArr.length; i++) {
                        percentageArr[i] = Tools.trimZero(new BigDecimal(percentageArr[i].toString()).multiply(BigDecimal.valueOf(100))
								.setScale(2, RoundingMode.HALF_UP));
                    }
                    String rawStr = StringUtils.join(percentageArr, ',');
                    obj = rawStr;
                } else if (sourceFieldValue instanceof String) {
					String value = (String)sourceFieldValue;

                    String[] percentageArr =  StringUtils.split(value, ',');
                    for (int i = 0; i < percentageArr.length; i++) {
                        percentageArr[i] = Tools.trimZero(new BigDecimal(percentageArr[i].toString()).multiply(BigDecimal.valueOf(100))
                                .setScale(2, RoundingMode.HALF_UP));
                    }
                    obj = percentageArr;
                }
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}

}
