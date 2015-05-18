package com.xpos.common.service;

import java.util.List;

import com.xpos.common.entity.Evaluation;
import com.xpos.common.entity.Evaluation.EvaluationType;
import com.xpos.common.entity.example.EvaluationExample;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.utils.Pager;



public interface EvaluationService {

	public List<Evaluation> findListByUserId(Pager<Evaluation> pager, EvaluationExample evaluationExample, Long id);

	public boolean saveByUser(Evaluation evaluation, String code);

	public boolean deleteByUser(Evaluation evaluation);

	public Evaluation findOneEvaluation(EvaluationExample example);

	public Pager<Evaluation> findListByCouponInfoId(Long id, EvaluationType coupon, Pager<Evaluation> pager);

	public Pager<Evaluation> findListByShopId(Long id, EvaluationType coupon, Pager<Evaluation> pager);
	
}
