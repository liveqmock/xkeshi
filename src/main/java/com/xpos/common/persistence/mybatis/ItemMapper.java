package com.xpos.common.persistence.mybatis;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.Item;
import com.xpos.common.persistence.BaseMapper;

public interface ItemMapper extends BaseMapper<Item>{

	/** 在指定商户范围内，查询没有交易记录的商品 */
	List<String> selectUnOrderedItemNamesByShopIds(@Param("startDate")Date startDate, @Param("endDate")Date endDate,@Param("shopIds") Long[] shopIds);

}
