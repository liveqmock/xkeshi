package com.xkeshi.service;

import com.xkeshi.dao.OperatorDAO;
import com.xkeshi.pojo.po.Operator;
import com.xkeshi.pojo.vo.Result;
import com.xkeshi.pojo.vo.SystemParam;
import com.xkeshi.pojo.vo.offline.OffLineOperatorListVO;
import com.xkeshi.pojo.vo.offline.OffLineOperatorVO;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2015/4/10.
 */

@Service("newOperatorService")
public class OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(OperatorService.class);

    @Autowired( required = false)
    private OperatorDAO operatorDAO;

    @Autowired(required = false)
    private Mapper mapper;

    //************************************************************************************
    private static final String FAIL_INFO = "查询操作员列表失败";
    private static final String FAIL_CODE = "-1";

    private static final String SUCCESS_INFO = "查询操作员列表成功";
    private static final String SUCCESS_CODE = "0";

    private static final String PARAM_LEGAL_INFO = "参数不合要求";
    private static final String PARAM_LEGAL_CODE = "-2";
    /**
     * 获取SHOP 下的操作员列表
     * @param
     * @return
     */
    public Result loadOperatorsByShopIdForAPI(final SystemParam systemParam){

      /*  if(!checkParamLegal(systemParam)){
            return new Result(PARAM_LEGAL_CODE,PARAM_LEGAL_INFO);
        }*/
        List<OffLineOperatorVO> operatorList = null;
        OffLineOperatorListVO list = new OffLineOperatorListVO();
        try{
            List<Operator> operators = operatorDAO.loadOperatorsByShopId(systemParam.getMid());
            if(operators != null && operators.size() > 0){
                operatorList = new ArrayList<>();
                for(Operator o :operators){
                    OffLineOperatorVO vo = new OffLineOperatorVO();
                    mapper.map(o, vo);
                    operatorList.add(vo);
                }
            }
            list.setOperatorList(operatorList);
        }catch (Exception e){
            LOG.error("加载操作员列表异常", e);
            return new Result(FAIL_CODE,FAIL_INFO);
        }

        return new Result(SUCCESS_CODE,SUCCESS_INFO,list);
    }

  /*  public boolean checkParamLegal(SystemParam systemParam){
        if(StringUtil.isEmpty(systemParam.getDeviceNumber()) ||
                systemParam.getMid() == null ||
                systemParam.getOperatorId() == null ||
                systemParam.getMid() == 0L ||
                systemParam.getOperatorId() == 0L)
            return false;
        return true;
    }*/
    //************************************************************************************


}
