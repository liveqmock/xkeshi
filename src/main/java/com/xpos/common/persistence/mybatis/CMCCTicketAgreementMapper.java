package com.xpos.common.persistence.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.persistence.BaseMapper;

public interface CMCCTicketAgreementMapper extends BaseMapper<BaseEntity> {
	
	@ResultType(value = Integer.class)
	@Insert("INSERT INTO CmccTicketAgreement(mobile, uid, remark) values(#{mobile}, #{uid}, '新增个人支付协议')")
	int save(@Param(value="mobile")String mobile, @Param(value="uid")String uid);

	@ResultType(value=String.class)
	@Select("select uid from CmccTicketAgreement where mobile = #{mobile} and deleted = false")
	String queryUidByMobile(String mobile);

	@ResultType(value=String.class)
	@Select("select uid from CmccTicketAgreement where id = #{id} and deleted = false")
	String queryUidById(int id);

}
