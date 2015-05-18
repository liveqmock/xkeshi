package com.xkeshi.service;

import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.dao.ShopDiscountSettingDAO;
import com.xkeshi.pojo.meta.MetaDiscountWayName;
import com.xkeshi.pojo.po.ShopDiscountSetting;
import com.xkeshi.pojo.vo.ShopDiscountSettingVO;
import com.xkeshi.pojo.vo.param.ShopDiscountSettingParam;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nt on 2015-04-01.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShopDiscountSettingService {

    @Autowired
    private ShopDiscountSettingDAO discountWayDAO;

    @Autowired
    private Mapper dozerMapper;

    /**
     * 根据商户id查询优惠方式
     *
     * @return
     */
    private List<ShopDiscountSettingVO> getDiscountWayById(Long id) {
        List<ShopDiscountSetting> poList = discountWayDAO.getDiscountWayById(id);
        List<ShopDiscountSettingVO> voList = new ArrayList<>();
        for(ShopDiscountSetting po : poList){
            voList.add(dozerMapper.map(po,ShopDiscountSettingVO.class));
        }
        return voList;
    }

    /**
     * 根据商户id查询优惠方式
     *
     * @return
     */
    public List<ShopDiscountSettingVO> getShopDiscountWayById(Long id) {
        Integer count = getDiscountWayCountById(id);
        if(count == 0){
            saveShopDiscountWay(id);
        }
        return getDiscountWayById(id);
    }

    /**
     * 创建商户的几种优惠方式
     *
     * @param id
     */
    private void saveShopDiscountWay(Long id) {
        List<MetaDiscountWayName> nameList = GlobalSource.metaDiscountWayNameList;
        for(MetaDiscountWayName po : nameList){
            ShopDiscountSetting discountWay = new ShopDiscountSetting();
            discountWay.setShopId(id);
            discountWay.setDiscountWayNameId(po.getId());
            //会员卡默认开通预付卡
            if(po.getName().equals("会员卡")){
                discountWay.setEnablePrepaidCard(1);
            }
            discountWayDAO.insert(discountWay);
        }
    }

    /**
     * 查看当前商户有几种优惠方式
     *
     * @param id
     * @return
     */
    private Integer getDiscountWayCountById(Long id) {
        return discountWayDAO.getDiscountWayCountById(id);
    }

    /**
     * 更新优惠方式
     *
     * @param param
     * @return
     */
    public boolean discountUpdate(ShopDiscountSettingParam param) {
        if(param.getEnableDZQ() != null){
            param.setEnable(param.getEnableDZQ());
        }
        if(param.getEnableHYK() != null){
            param.setEnable(param.getEnableHYK());
            if(param.getEnableHYK()==0){
                param.setEnablePrepaidCard(0);
            }
        }
        if(param.getEnableSTQ() != null){
            param.setEnable(param.getEnableSTQ());
        }
        return discountWayDAO.discountUpdate(param) > 0;
    }
}
