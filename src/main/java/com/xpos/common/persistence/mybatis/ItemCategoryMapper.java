package com.xpos.common.persistence.mybatis;

import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.xpos.common.entity.ItemCategory;
import com.xpos.common.persistence.BaseMapper;

public interface ItemCategoryMapper extends BaseMapper<ItemCategory>{

	/** 查询指定商户范围内的所有商品类目名称 */
	Set<String> selectCategoryNamesByShopIds(@Param("shopIds") Long[] shopIds);

	/**查询商品分类名称是否重复*/
	int findItemCategorysByNameAndBusiness(@Param("name") String name,@Param("id") Long id, @Param("businessId") Long businessId, @Param("businessType") String businessType);


}
