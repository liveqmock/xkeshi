package com.xkeshi.common.globality;

import com.xkeshi.pojo.meta.*;
import com.xkeshi.service.MetaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局资源
 *
 * @author david
 */
@Component
public class GlobalSource implements Initializable {


    public static List<MetaPrepaidCardChargeChannel> metaPrepaidCardChargeChannelList;
    public static List<MetaPrepaidCardGiftType> metaPrepaidCardGiftTypeList;
    public static List<MetaBusinessType> metaBusinessTypeList;
    public static List<MetaOrderPaymentStatus> metaOrderPaymentStatusList;
    public static List<MetaPhysicalCouponType> metaPhysicalCouponTypeList;
    public static List<MetaTransactionPaymentStatus> metaTransactionPaymentStatusList;
    public static List<MetaDiscountWayName> metaDiscountWayNameList;

    @Autowired
    private MetaService metaService;


    private GlobalSource() {
    }


    /**
     * 启动时load缓存数据
     */
    public void init() {
        metaPrepaidCardChargeChannelList = metaService.metaPrepaidCardChargeChannelList();
        metaPrepaidCardGiftTypeList = metaService.metaPrepaidCardGiftTypeList();
        metaBusinessTypeList = metaService.metaBusinessTypeList();
        metaOrderPaymentStatusList = metaService.metaOrderPaymentStatusList();
        metaPhysicalCouponTypeList = metaService.metaPhysicalCouponTypeList();
        metaTransactionPaymentStatusList = metaService.metaTransactionPaymentStatusList();
        metaDiscountWayNameList = metaService.metaDiscountWayNameList();
    }


    public void reset() {
        init();
    }

    public static Long getIDByName(List<? extends Meta> metaList, String name) {
        for (Meta meta : metaList) {
            if (StringUtils.equalsIgnoreCase(meta.getName(), name)) {
                return meta.getId();
            }
        }
        return null;
    }

    public static Long getIDByCode(List<? extends Meta> metaList, String code) {
        for (Meta meta : metaList) {
            if (StringUtils.equalsIgnoreCase(meta.getCode(), code)) {
                return meta.getId();
            }
        }
        return null;
    }

    public static String getNameByID(List<? extends Meta> metaList, Long id) {
        if (id == null) {
            return null;
        }
        for (Meta meta : metaList) {
            if (meta.getId().equals(id)) {
                return meta.getName();
            }
        }
        return null;
    }

    public static String getCodeByID(List<? extends Meta> metaList, Long id) {
        if (id == null) {
            return null;
        }
        for (Meta meta : metaList) {
            if (meta.getId().equals(id)) {
                return meta.getCode();
            }
        }
        return null;
    }

    public static List<Meta> getListByComment(List<? extends Meta> metaList,String comment){
        if(metaList==null&& StringUtils.isEmpty(comment)){
            return null;
        }
        List list = new ArrayList();
        for(Meta m : metaList){
            if(comment.equals(m.getComment())){
                list.add(m);
            }
        }
        return list;
    }

}
