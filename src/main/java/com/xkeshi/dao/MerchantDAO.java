package com.xkeshi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xkeshi.pojo.po.Merchant;
import com.xkeshi.pojo.po.PrepaidCard;
import com.xkeshi.pojo.vo.shift.ShiftShopVO;

/**
 * Created by david-y on 2015/1/7.
 */
public interface MerchantDAO  extends BaseDAO<PrepaidCard> {


    boolean checkMemberCentralManagementByMerchantId(@Param("merchantId") Long merchantId);
   
    boolean checkMemberCentralManagementPrepaidCardAvailableByShopId(@Param("shopId") Long shopId);

    boolean checkMemberCentralManagementByShopId(@Param("shopId") Long shopId);

    boolean checkDiscountCentralManagementByShopId(@Param("shopId") Long shopId);
    
	boolean checkBalanceCentralManagementByShopId(@Param("shopId")Long shopId);
	
	Merchant findMerchantByShopId(@Param("shopId")Long shopId);
	
	Merchant findMerchantByMerchantId(@Param("merchantId")Long merchantId);

    String getNameById(@Param("merchantId") Long merchantId);

    Merchant selectMerchantByMerchantId(@Param("merchantId")Long merchantId);
    
    /**获取需要交接班的商户数量*/
	int presenceEnableShiftShopCountByMerchantId(@Param("merchantId")Long businessId);
	
	List<ShiftShopVO> getShiftShopVOByMerchantId(@Param("merchantId")Long merchantId);

}
