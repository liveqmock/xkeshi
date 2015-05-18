package com.xkeshi.dao;

import com.xkeshi.pojo.po.Operator;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by dell on 2015/4/10.
 */
public interface OperatorDAO {
    /**
     * 根据shopId 获取操作员列表
     * @param shopId
     * @return
     */
    List<Operator> loadOperatorsByShopId(@Param("shopId")Long shopId);
}
