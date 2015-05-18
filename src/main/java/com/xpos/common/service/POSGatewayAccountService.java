package com.xpos.common.service;

import com.xkeshi.pojo.vo.param.POSGatewayAccountParam;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.pos.POSGatewayAccount;
import com.xpos.common.entity.pos.POSGatewayAccount.POSGatewayAccountType;

public interface POSGatewayAccountService {
	
	/** 根据account & gatewayType查找POSGatewayAccount */
	public POSGatewayAccount findByAccountAndType(String gatewayAccount, POSGatewayAccountType gatewayType);
	
	/** 根据business & gatewayType查找POSGatewayAccount */
	public POSGatewayAccount findByBusinessAndType(Business business, POSGatewayAccountType gatewayType);
	
	/** 商户添加支付终端、账号 */
	public boolean save(POSGatewayAccount account);

	/** 商户编辑支付终端、账号 */
	public boolean update(POSGatewayAccount account);

	/** 商户删除支付终端、账号 */
	public boolean deleteByShopId(Long shopId, Long accountId);

    POSGatewayAccount findBankCardPosGatewayAccount(Long id);

    POSGatewayAccount findByShopIdAndType(Long id, POSGatewayAccountType type);

    boolean saveOrUpdate(POSGatewayAccountParam param);

    boolean updateBandCard(POSGatewayAccountParam param);
}
