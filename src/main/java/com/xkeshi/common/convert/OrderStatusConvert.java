package com.xkeshi.common.convert;


import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xkeshi.common.em.OrderPaymentStatus;

/**
 * Order状态从Integer==>OrderPaymentStatus
 */
public class OrderStatusConvert implements CustomConverter {
    private static final Logger logger = LoggerFactory.getLogger(OrderStatusConvert.class);

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof Integer) {
					switch ((Integer) sourceFieldValue) {
					case 1:
						obj = OrderPaymentStatus.SUCCESS.name();
						break;
					case 2:
						obj = OrderPaymentStatus.UNPAID.name();
						break;
					case 3:
						obj = OrderPaymentStatus.FAILED.name();
						break;
					case 4:
						obj = OrderPaymentStatus.TIMEOUT.name();
						break;
					case 5:
						obj = OrderPaymentStatus.CANCEL.name();
						break;
					case 6:
						obj = OrderPaymentStatus.PARTIAL_PAYMENT.name();
						break;
					case 7:
						obj = OrderPaymentStatus.REFUND.name();
						break;
					default:
						break;
					}
                }
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return obj;
	}
}
