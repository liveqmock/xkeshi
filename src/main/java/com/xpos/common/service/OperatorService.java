package com.xpos.common.service;

import com.xpos.common.entity.Operator;

public interface OperatorService {

	public boolean login(Operator operator);
	
	public Operator findById(Long id);
	
	public String save(Operator operator);

	public String update(Operator operator);
	
	public boolean deleteById(Long opid);

	public String verifyManagerByToken(String token,Long shopId,String orderNumber);
}
