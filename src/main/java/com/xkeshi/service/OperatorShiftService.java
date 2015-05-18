package com.xkeshi.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xkeshi.dao.OperatorShiftConsumedPhysicalCouponDAO;
import com.xkeshi.dao.PhysicalCouponDAO;
import com.xkeshi.pojo.po.OperatorShiftConsumedPhysicalCoupon;
import com.xkeshi.pojo.po.PhysicalCoupon;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.offline.OffLineOperatorShiftVO;
import com.xkeshi.pojo.vo.offline.OffLinePhysicalCouponVO;
import com.xkeshi.pojo.vo.shift.ShiftInfoVO;
import com.xpos.common.entity.OperatorShift;
import com.xpos.common.entity.pos.POSOperationLog;
import com.xpos.common.persistence.mybatis.OperatorShiftMapper;
import com.xpos.common.persistence.mybatis.POSOperationLogMapper;
import com.xpos.common.utils.DateUtil;

/**
 * Created by dell on 2015/4/11.
 */

@Service("newOperatorShiftService")
public class OperatorShiftService{

    private static  final Logger LOG = LoggerFactory.getLogger(OperatorShiftService.class);
    @Autowired(required = false)
    private OperatorShiftMapper operatorShiftMapper;

    @Autowired(required = false)
    private POSOperationLogMapper posOperationLogMapper;

    @Autowired(required  = false)
    private PhysicalCouponDAO physicalCouponDAO   ;

    @Autowired(required = false)
    private OperatorShiftConsumedPhysicalCouponDAO operatorShiftConsumedPhysicalCouponDAO ;

    @Resource(name = "newOperatorShiftService")
    private OperatorShiftService thisInstance;
    /****************************************************************************************/

    private static final String SUCCESS_INFO = "上传成功";
    private static final String SUCCESS_CODE = "0";

    private static final String FAIL_INFO = "上传失败";
    private static final String FAIL_CODE = "-1";

    public Result uploadOffLineOperatorShiftInfo(final SystemParam systemParam,
                                                 final List<OffLineOperatorShiftVO> shiftVOs){

        List uploadFails = null;
        
        POSOperationLog operatorLog = posOperationLogMapper.findOperatorSessionByDeviceNumber(systemParam.getDeviceNumber());
        
        if(shiftVOs != null && shiftVOs.size() > 0){
            uploadFails = new ArrayList();
            for(OffLineOperatorShiftVO shiftVO : shiftVOs){
                try {
                    if(!thisInstance.saveOffLineOperatorShiftInfo(systemParam, shiftVO,operatorLog)){
                        uploadFails.add(shiftVO.getOperatorSessionCode());
                    }
                } catch (Exception e) {
                    uploadFails.add(shiftVO.getOperatorSessionCode());
                }
            }
        }

        if(uploadFails != null && uploadFails.size() != 0){
            return new Result(SUCCESS_CODE,FAIL_INFO,uploadFails);
        }

        return new Result(SUCCESS_INFO,SUCCESS_CODE);
    }

    /**
     * @description
     * @param shiftVO
     * @return
     */

    @Transactional
    public boolean saveOffLineOperatorShiftInfo(SystemParam systemParam,
    												OffLineOperatorShiftVO shiftVO,
    												POSOperationLog operatorLog) {
        if(shiftVO == null){
            return false;
        }
		boolean result = true;

		try {

			if (shiftVO.isShiftCompleted()) {
				if (operatorLog != null && shiftVO.getOperatorSessionCode().equals(
						operatorLog.getOperatorSessionCode())) {
					result = result && savePOSOperatorLog(shiftVO,
							systemParam.getDeviceNumber());
				}else {
					result = result && savePOSOperatorLog(shiftVO,systemParam.getDeviceNumber());
					shiftVO.setShiftCompleted(false);
					result = result && savePOSOperatorLog(shiftVO,systemParam.getDeviceNumber());
				}
				Long operatorShiftId = saveOperatorShift(shiftVO,
						systemParam.getMid());
				if (operatorShiftId != null) {
					// operator shift consume physical coupon
					List<OffLinePhysicalCouponVO> physicalCouponVOs = shiftVO.getPhysicalCoupons();
				    if(physicalCouponVOs != null && physicalCouponVOs.size() > 0){
				    	result = result && saveOperatorShiftConsumedPhysicalCoupon(shiftVO,operatorShiftId);
				    }
				} else {
					result = false;
				}
			} else {
				result = result && savePOSOperatorLog(shiftVO,
						systemParam.getDeviceNumber());
			}

		} catch (Exception e) {
			LOG.error("", e);
			result = false;
		}
        

        if(!result) {
            throw new RuntimeException("离线信息上传失败");
        }
        return result;
    }

    /**
     * @Description 保存 Operator Log
     * @param operatorId
     * @param deviceNumber
     * @param operatorSessionCode
     * @return
     */

    public boolean savePOSOperatorLog(OffLineOperatorShiftVO vo,String deviceNumber){
        //OperatorLog
        POSOperationLog operationLog = new POSOperationLog();
        operationLog.setOperatorId(vo.getOperatorId());
        operationLog.setDeviceNumber(deviceNumber);
        operationLog.setOperatorSessionCode(vo.getOperatorSessionCode());
        
        try {
        	
          if(!vo.isShiftCompleted()){
           	 operationLog.setLogined(1);//未交接
           	 operationLog.setCreateDate(DateUtil.getDateFormatter(vo.getShiftStartTime()));
           }else{
           	operationLog.setLogined(0); //已交接
           	operationLog.setCreateDate(DateUtil.getDateFormatter(vo.getShiftEndTime()));
          }
          
           operationLog.setShift(1);   //交接班
        	
		
		} catch (ParseException e1) {
			LOG.error("解析operatorLog 时间 异常!!!",e1);
		}
        
        try{
            return posOperationLogMapper.insertPOSOperationLog(operationLog) > 0 ? true : false;
        }catch(Exception e){
            LOG.error("保存operator log 异常!!!",e);
        }

        return false;
    }

    /**
     * @description save operator shift
     * @param shiftVO
     * @param shopId
     * @return
     */

    public Long saveOperatorShift(final OffLineOperatorShiftVO shiftVO,final Long shopId){
        try{
            OperatorShift operatorShift = new OperatorShift();
            operatorShift.setOperatorId(shiftVO.getOperatorId());
            operatorShift.setOperatorSessionCode(shiftVO.getOperatorSessionCode());

            ShiftInfoVO shiftInfoVO = posOperationLogMapper.findOperatorShiftInfo(shiftVO.getOperatorSessionCode());
            if (shiftInfoVO == null) {
                return null;
            }
            operatorShift.setOperatorRealName(shiftInfoVO.getOperatorName());

            try {
                operatorShift.setShiftedStartTime(DateUtil.getDateFormatter(shiftVO.getShiftStartTime()));
                operatorShift.setShiftedEndTime(DateUtil.getDateFormatter(shiftVO.getShiftEndTime()));
            } catch (ParseException e) {
                LOG.error("",e);
                e.printStackTrace();
            }
            operatorShift.setCreatedTime(new Date());

            operatorShift.setTotalConsumeCount(shiftVO.getTotalCouponConsumeCount());
            operatorShift.setTotalMemberCount(shiftVO.getTotalMemberCount());
            operatorShift.setTotalOrderCount(shiftVO.getTotalOrderCount());
            operatorShift.setTotalOrderItemCount(shiftVO.getTotalOrderItemCount());
            operatorShift.setTotalOrderAmount(shiftVO.getTotalOrderAmount());
            operatorShift.setTotalReceivableAmount(shiftVO.getTotalActuallyAmount());//临时修改
            operatorShift.setTotalPhysicalCouponAmount(shiftVO.getTotalPhysicalCouponAmount());
            operatorShift.setTotalActuallyAmount(shiftVO.getTotalReceivableAmount());
            operatorShift.setTotalDifferenceCashAmount(
                    shiftVO.getTotalActuallyAmount().subtract(shiftVO.getTotalReceivableAmount()
                    ));
            /*添加预付卡信息*/
            operatorShift.setPrepaidcardRechargeAmountCount(shiftVO.getPrepaidcardRechargeAmountCount());
            operatorShift.setPrepaidcardTotalPresentedAmount(shiftVO.getPrepaidcardTotalPresentedAmount());
            operatorShift.setPrepaidcardtotalRealityRechargeAmount(shiftVO.getPrepaidcardtotalRealityRechargeAmount());

            operatorShift.setShopId(shopId);
            operatorShiftMapper.insertOperatorShift(operatorShift);
            return operatorShift.getId()!=null ?operatorShift.getId() :null;
        }catch(Exception e){
            LOG.error("",e);
            throw new RuntimeException("");
        }

    }

    /**
     * @Description save operator shift consumed physical coupon
     * @param shiftVO
     * @param operatroShiftId
     * @return
     */

    public boolean saveOperatorShiftConsumedPhysicalCoupon(final OffLineOperatorShiftVO shiftVO,
                                                           final Long operatroShiftId){
        boolean result = false;
        List<OffLinePhysicalCouponVO> physicalCouponVOs = shiftVO.getPhysicalCoupons();
        for(OffLinePhysicalCouponVO p:physicalCouponVOs){
            PhysicalCoupon physicalCoupon = physicalCouponDAO.getByID(PhysicalCoupon.class, p.getId());
            OperatorShiftConsumedPhysicalCoupon shiftCoupon = new OperatorShiftConsumedPhysicalCoupon();
            shiftCoupon.setOperatorShiftId(operatroShiftId);
            shiftCoupon.setPhysicalCouponId(physicalCoupon.getId());
            shiftCoupon.setPhysicalCouponName(physicalCoupon.getName());
            shiftCoupon.setPhysicalCouponAmount(physicalCoupon.getAmount());
            shiftCoupon.setTotalConsumedCount(p.getCount());
            result = operatorShiftConsumedPhysicalCouponDAO.
                    insertOperatorShiftConsumedPhysicalCoupon(shiftCoupon) > 0 ? true : false;
            if(!result) break;
        }
        return result;
    }

    /****************************************************************************************/
}
