package com.xpos.common.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.RefundLog;
import com.xpos.common.entity.example.RefundLogExample;
import com.xpos.common.persistence.mybatis.RefundLogMapper;

@Service
public class RefundLogServiceImpl implements RefundLogService{

	@Resource
	private RefundLogMapper   refundLogMapper  ;
	
	@Override
	@Transactional
	public int insert(RefundLog refundLog) {
		return  refundLogMapper.insert(refundLog);
	}

	@Override
	public List<RefundLog> refundLogs(String refundCode) {
		 RefundLogExample logExample = new RefundLogExample();
		 logExample.appendCriterion("refund_id in (select id from Refund  where code = '"+refundCode+"')")
		           .addCriterion("deleted=", false);
		 logExample.setOrderByClause("createDate desc");
		return refundLogMapper.selectByExample(logExample, null);
	} 
	
}
