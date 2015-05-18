package com.xkeshi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xkeshi.dao.MerchantDAO;
import com.xkeshi.pojo.po.Merchant;
import com.xkeshi.pojo.vo.shift.ShiftShopVO;

/**
 * Created by david-y on 2015/1/7.
 */
@Service
public class XMerchantService {

    @Autowired
    private MerchantDAO merchantDAO;

    public boolean checkMemberCentralManagementByMerchantId(Long merchantId) {
        return merchantDAO.checkMemberCentralManagementByMerchantId(merchantId);
    }

    /**
     * 检查商户是否是集团统一管理且预付卡适用规则
     *
     * Check member central management available by shop id.
     *
     * @param shopId the shop id
     * @return the boolean
     */
    public boolean checkMemberCentralManagementPrepaidCardAvailableByShopId(Long shopId) {
        return merchantDAO.checkMemberCentralManagementPrepaidCardAvailableByShopId(shopId);
    }


    public boolean checkMemberCentralManagementByShopId(Long shopId) {
        return merchantDAO.checkMemberCentralManagementByShopId(shopId);
    }

    public boolean checkDiscountCentralManagementByShopId(Long shopId) {
        return merchantDAO.checkDiscountCentralManagementByShopId(shopId);
    }
    /**检查账户资金是否统一管理*/
	public boolean checkBalanceCentralManagementByShopId(Long shopId) {
		return merchantDAO.checkBalanceCentralManagementByShopId(shopId);
	}
    
	/**
	 * 获取Merchant
	 * @param shopId
	 * @return business = Merchant
	 */
	public Merchant findMerchantByShopId(Long shopId) {
		return merchantDAO.findMerchantByShopId(shopId);
	}

	public Merchant getMerchantByMerchantId(Long merchantId) {
		return  merchantDAO.findMerchantByMerchantId(merchantId);
	}

	public Merchant selectMerchantByMerchantId(Long merchantId) {
		return merchantDAO.selectMerchantByMerchantId(merchantId);
	}

	/**
	 * 判断子商铺是否需要交接班(存在)
	 * @param merchantId
	 */
	public boolean presenceEnableShiftShopByMerchantId(Long businessId) {
		return merchantDAO.presenceEnableShiftShopCountByMerchantId(businessId) >0;
	}

	/**
	 * 集团获取ShiftShopVO
	 * */
	public List<ShiftShopVO> getShiftShopVOByMerchantId(Long merchantId) {
		 
		return merchantDAO.getShiftShopVOByMerchantId(merchantId);
	}
}
