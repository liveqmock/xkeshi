package com.xkeshi.common.convert;

import com.xkeshi.utils.Tools;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;

/**
 *
 *
 * 百分比字符串和百分比数组之间转换，分隔符为半角逗号
 * （例如："7,10" ==> [0.07,0.1]）
 *
 */
public class StringArrayToPercentageConverter implements CustomConverter {
	private static final Logger logger = LoggerFactory.getLogger(StringArrayToPercentageConverter.class);
	
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
                        percentageArr[i] = Tools.trimZero(Tools.getDecimalProportion(percentageArr[i].toString())
								.setScale(4, RoundingMode.HALF_UP));
                    }
                    String rawStr = StringUtils.join(percentageArr, ',');
                    obj = rawStr;
                } else if (sourceFieldValue instanceof String) {
					String value = (String)sourceFieldValue;

                    String[] percentageArr =  StringUtils.split(value, ',');
                    for (int i = 0; i < percentageArr.length; i++) {
                        percentageArr[i] = Tools.trimZero(Tools.getDecimalProportion(percentageArr[i].toString())
                                .setScale(4, RoundingMode.HALF_UP));
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
