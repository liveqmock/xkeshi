package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.xpos.common.entity.Evaluation;
import com.xpos.common.entity.Evaluation.EvaluationType;
import com.xpos.common.persistence.BaseMapper;

public interface EvaluationMapper extends BaseMapper<Evaluation> {

	@Select("select IFNULL(ROUND(avg(a.stars)),3) as rstars  from Evaluation a where type=#{type} and businessId=#{businessId}")
	int selectRstars(@Param(value="type")String type, @Param(value="businessId")Long businessId);
	
	@Select("select IFNULL(ROUND(avg(a.stars)),3) as rstars  from Evaluation a where type=#{type} "
			+ "and a.businessId in (select b.item_id from CouponInfo_Package b where  b.parent_id=#{gbusinessId})")
	int selectRgstars(@Param(value="type")String type, @Param(value="gbusinessId")Long gbusinessId);
	
}
