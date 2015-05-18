package com.xkeshi.service;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.ShopDAO;
import com.xkeshi.pojo.po.Shop;
import com.xkeshi.pojo.vo.ShopLiteVO;

/**
 * Created by david-y on 2015/1/8.
 */
@Service
public class XShopService {

    @Autowired
    private ShopDAO shopDAO;

    @Autowired(required = false)
    private Mapper dozerMapper;

    /**
     * 检查商户是否有关联集团
     *
     * @param shopId the business id
     * @return the boolean
     */
    public boolean hasMerchant(Long shopId) {
        return shopDAO.hasMerchant(shopId);
    }

    public Long getMerchantId(Long shopId) {
        return shopDAO.getMerchantId(shopId);
    }

    public List<ShopLiteVO> getShopsByMerchantId(Long merchantId) {
        return shopDAO.getShopsByMerchantId(merchantId);

    }
    
	public Shop findShopByShopId(Long shopId) {
		return shopDAO.getShopByShopId(shopId);
	}
	
	@Transactional
	public Boolean updateShopByShift(Shop shop) {
		return shopDAO.updateShopByShift(shop) >0;
	}

    public Boolean updateShopByMultiplePayment(Shop shop) {
        return  shopDAO.updateShopByMultiplePayment(shop) > 0;
    }


    public ShopLiteVO getShopLiteById(Long shopId) {
        Shop po = shopDAO.getShopLiteById(shopId);
        if (po == null) {
            return null;
        }
        return dozerMapper.map(po, ShopLiteVO.class);
    }

	public Shop selectShopByShopId(Long businessId) {
		return shopDAO.selectShopByShopId(businessId);
	}
}
