package com.xkeshi.common.convert;


import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;

/**
 *	GatewayType状态String==>c
 */
public class GatewayTypeConvert implements CustomConverter {
    private static final Logger logger = LoggerFactory.getLogger(GatewayTypeConvert.class);

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				obj = POSGatewayAccountType.queryByStatus((String)sourceFieldValue);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}
	
}
