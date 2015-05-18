package com.xkeshi.service;

import com.xkeshi.common.em.TransactionPaymentStatus;
import com.xkeshi.pojo.po.alipay.AlipayTransaction;
import com.xkeshi.pojo.po.AlipayTransactionDetail;
import com.xkeshi.pojo.po.AlipayTransactionList;
import com.xpos.common.persistence.mybatis.AlipayTransactionMapper;
import com.xpos.common.utils.Pager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * 支付宝付款流水
 * @author Administrator
 *
 */
@Service
public class AlipayTransactionService {
	private static Logger logger = Logger.getLogger(AlipayTransactionService.class);
	
	@Autowired
	private AlipayTransactionMapper alipayTransactionMapper;
	
	/**
	 * 根据ID值更新现有记录。
	 * 更新前会进行付款状态的判断，只有符合条件才会执行更新
	 * @param alipayTransaction
	 * @return
	 */
	
	public boolean updateById(AlipayTransaction alipayTransaction) {
		try{
			AlipayTransaction originalTransaction = alipayTransactionMapper.selectById(alipayTransaction.getId());
			TransactionPaymentStatus originalStatus = TransactionPaymentStatus.findByValue(originalTransaction.getAlipayPaymentStatus());
			TransactionPaymentStatus curStatus = TransactionPaymentStatus.findByValue(alipayTransaction.getAlipayPaymentStatus());
			
			if(originalStatus.equals(curStatus)){
				//状态未发生改变
				return true;
			}else if(TransactionPaymentStatus.UNPAID.equals(originalStatus)){
				//当前等待付款、付款中状态，可以直接更新
				return alipayTransactionMapper.updateById(alipayTransaction) > 0;
			}else if(originalStatus.equals(TransactionPaymentStatus.SUCCESS)){
				//当前付款成功的状态
				if(TransactionPaymentStatus.REVOCATION.equals(curStatus) || TransactionPaymentStatus.REFUND.equals(curStatus)) {
					//新状态为撤销或退款，否则忽略新的通知
					return alipayTransactionMapper.updateById(alipayTransaction) > 0;
				}
			}else if(TransactionPaymentStatus.FAILED.equals(originalStatus)){
				if(TransactionPaymentStatus.SUCCESS.equals(curStatus) || TransactionPaymentStatus.REVOCATION.equals(curStatus) || TransactionPaymentStatus.REFUND.equals(curStatus)){
					//当前付款失败的状态，只有通知付款成功、撤销、退款才更新状态，否则忽略新的通知
					return alipayTransactionMapper.updateById(alipayTransaction) > 0;
				}
			}
		}catch(DataAccessException dae){
			logger.error("更新AlipayTransaction支付流水失败", dae);
			throw dae;
		}
		return false;
	}

    public Pager<AlipayTransactionList> AlipayQRCodeList(String key,String businessType,AlipayTransactionList alipayTransactionList,Pager<AlipayTransactionList> pager) {
        if(alipayTransactionList.getBusinessId() == null) {
            return null;
        }else if(pager == null) {
            pager = new Pager<AlipayTransactionList>();
            pager.setPageSize(Integer.MAX_VALUE);
        }
        pager.setList(alipayTransactionMapper.AlipayQRCodeList(key,businessType, alipayTransactionList,pager));
        pager.setTotalCount(alipayTransactionMapper.AlipayQRCodeList(key,businessType, alipayTransactionList,null).size());
        return pager;
    }

    public AlipayTransactionDetail findAlipayTransactionById(Long id) {
        return alipayTransactionMapper.findAlipayTransactionById(id);
    }

}
