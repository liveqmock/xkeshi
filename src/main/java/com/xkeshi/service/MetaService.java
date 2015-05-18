package com.xkeshi.service;

import com.xkeshi.dao.meta.*;
import com.xkeshi.pojo.meta.*;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class MetaService {


    @Autowired(required = false)
    private MetaPrepaidCardChargeChannelDAO metaPrepaidCardChargeChannelDAO;

    @Autowired(required = false)
    private MetaPrepaidCardGiftTypeDAO metaPrepaidCardGiftTypeDAO;
    @Autowired(required = false)
    private MetaBusinessTypeDAO metaBusinessTypeDAO;

    @Autowired(required = false)
    private MetaOrderPaymentStatusDAO metaOrderPaymentStatusDAO;
    @Autowired(required = false)
    private MetaPhysicalCouponTypeDAO metaPhysicalCouponTypeDAO;
    @Autowired(required = false)
    private MetaTransactionPaymentStatusDAO metaTransactionPaymentStatusDAO;

    @Autowired(required = false)
    private MetaDiscountWayDAO metaDiscountWayDAO;

    @Autowired(required = false)
    private Mapper dozerMapper;


    public List<MetaPrepaidCardChargeChannel> metaPrepaidCardChargeChannelList() {
        return metaPrepaidCardChargeChannelDAO.getListAll(MetaPrepaidCardChargeChannel.class);
    }


    public List<MetaPrepaidCardGiftType> metaPrepaidCardGiftTypeList() {
        return metaPrepaidCardGiftTypeDAO.getListAll(MetaPrepaidCardGiftType.class);
    }

    public List<MetaBusinessType> metaBusinessTypeList() {
        return metaBusinessTypeDAO.getListAll(MetaBusinessType.class);
    }

    public List<MetaOrderPaymentStatus> metaOrderPaymentStatusList() {
        return metaOrderPaymentStatusDAO.getListAll(MetaOrderPaymentStatus.class);
    }

    public List<MetaPhysicalCouponType> metaPhysicalCouponTypeList() {
        return metaPhysicalCouponTypeDAO.getListAll(MetaPhysicalCouponType.class);
    }


    public List<MetaTransactionPaymentStatus> metaTransactionPaymentStatusList() {
        return metaTransactionPaymentStatusDAO.getListAll(MetaTransactionPaymentStatus.class);
    }


    public List<MetaDiscountWayName> metaDiscountWayNameList() {
        return metaDiscountWayDAO.getListAll(MetaDiscountWayName.class);
    }
}
