package com.xkeshi.common.convert;


import org.dozer.CustomConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xpos.common.entity.pos.POSTransaction.POSTransactionStatus;

/**
 *	POS状态Long==>POSTransactionStatus
 */
public class POSTransactionStatusConvert implements CustomConverter {
    private static final Logger logger = LoggerFactory.getLogger(POSTransactionStatusConvert.class);

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		Object obj = null;
		if (null != sourceFieldValue) {
			try {
				if (sourceFieldValue instanceof Integer) {
					switch ((int)sourceFieldValue) {
					case 1:
						obj = POSTransactionStatus.PAID_SUCCESS;
						break;
					case 2:
						obj = POSTransactionStatus.UNPAID;
						break;
					case 3:
						obj = POSTransactionStatus.PAID_FAIL;
						break;
					case 4:
						obj = POSTransactionStatus.PAID_TIMEOUT;
						break;
					case 5:
						obj = POSTransactionStatus.PAID_FAIL;
						break;
					case 6:
						obj = POSTransactionStatus.PAID_REVOCATION;
						break;
					case 7:
						obj = POSTransactionStatus.PAID_REFUND;
						break;
					case 8:
						obj = POSTransactionStatus.REVERSAL;
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
