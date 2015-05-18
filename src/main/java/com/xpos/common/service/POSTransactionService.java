package com.xpos.common.service;

import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.param.POSPaymentRequestParam;
import com.xkeshi.pojo.vo.param.POSPaymentResultParam;
import com.xkeshi.pojo.vo.param.POSRefundResultParam;
import com.xkeshi.pojo.vo.param.POSSignatureUploadParam;
import com.xkeshi.pojo.vo.param.POSUpdateInfoParam;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSTransaction;
import com.xpos.common.searcher.POSTransactionSearcher;
import com.xpos.common.utils.Pager;

public interface POSTransactionService {
	public Pager<POSTransaction> findTransactions(Business business, POSTransactionSearcher searcher, Pager<POSTransaction> pager);
	
	public POSTransaction findTransactionByCode(String code);

	public POSTransaction findTransactionById(Long id);

	public boolean savePOSTransaction(POSTransaction posTransaction);
	
	public boolean updatePOSTransaction(POSTransaction posTransaction);

	public boolean updatePOSTransactionByCode(POSTransaction posTransaction);

	/** 创建移动电子券支付订单 */
	public String createCMCCTicketOrder(POSTransaction transaction, String deviceNumber);

	/** 移动电子券冲正（撤销） */
	public String revocationCMCCTicketOrder(POSTransaction transaction);

	/** 电子现金消费统计 */
	public String[] getElectronicCashStatistic(Business business, POSTransactionSearcher searcher);
	
	/** POS刷卡消费统计
	 * @return string[0]:刷卡笔数，string[1]:刷卡金额
	 */
	public String[] getBankCardStatistic(Business business, POSTransactionSearcher searcher);

	/** 支付宝当面付消费统计
	 * @return string[0]:当面付笔数，string[1]:当面付金额
	 */
	public String[] getAlipayQRCodeStatistic(Business business, POSTransactionSearcher searcher);

	/** API 2.0创建支付流水 */
	public Result paymentForPOS(SystemParam systemParam, POSPaymentRequestParam posPaymentParam, String orderNumber, POSGatewayAccount account);

	/** 上传客户签字图片 */
	public Result uploadSignature(SystemParam systemParam, POSSignatureUploadParam signatureParam,
			String orderNumber, POSGatewayAccount account);

	/** 处理POS刷卡支付结果的同步方式回调 */
	public Result processPaymentResultCallback(SystemParam systemParam,
			POSPaymentResultParam paymentResultParam, String orderNumber, String serial, POSGatewayAccount account);

	/** 处理POS退款操作的同步回调 */
	public Result processRefundResultCallback(SystemParam systemParam,
			POSRefundResultParam refundResultParam, String orderNumber, String serial, POSGatewayAccount account);

	/** 更新支付流水的手机号，并发送电子账单 */
	public Result updateConsumerMobile(SystemParam systemParam,
						POSUpdateInfoParam updateInfoParam, String orderNumber, String serial);

}
