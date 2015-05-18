package com.xpos.common.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xpos.common.entity.Coupon;
import com.xpos.common.entity.CouponInfo;
import com.xpos.common.entity.Evaluation;
import com.xpos.common.entity.Evaluation.EvaluationType;
import com.xpos.common.entity.Page;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.CouponExample;
import com.xpos.common.entity.example.EvaluationExample;
import com.xpos.common.persistence.mybatis.CouponInfoMapper;
import com.xpos.common.persistence.mybatis.CouponMapper;
import com.xpos.common.persistence.mybatis.EvaluationMapper;
import com.xpos.common.persistence.mybatis.ShopMapper;
import com.xpos.common.utils.Pager;

@Service
public class EvaluationServiceImpl implements EvaluationService{

	@Resource
	private EvaluationMapper evaluationMapper;
	
	@Resource
	private CouponInfoMapper couponInfoMapper;
	
	@Resource
	private CouponMapper couponMapper;
	
	@Resource
	private ShopMapper shopMapper;

	@Override
	public List<Evaluation> findListByUserId(Pager<Evaluation> pager,
			EvaluationExample evaluationExample, Long id) {
			evaluationExample.createCriteria().addCriterion("user_id='"+id+"'")
												.addCriterion("deleted=", false);
		return evaluationMapper.selectByExample(evaluationExample, pager);
	}

	@Override
	public boolean saveByUser(Evaluation evaluation,String code) {
		EvaluationExample evaluationExample = new EvaluationExample ();
		evaluationExample.createCriteria().addCriterion("user_id=", evaluation.getUser().getId())
											.addCriterion("type='"+evaluation.getType()+"'")
											.addCriterion("businessId=", evaluation.getBusinessId())
											.addCriterion("deleted=", false);
		if(evaluationMapper.selectOneByExample(evaluationExample)!=null) {
			return evaluationMapper.updateByPrimaryKey(evaluation)==1
					&& updateStars(evaluation,code);
		}else {
			return evaluationMapper.insert(evaluation)==1
					&& updateStars(evaluation,code);
		}
	}
	
	private boolean updateStars (Evaluation evaluation,String couponCode) {
		try {
			EvaluationExample evaluationExample = new EvaluationExample ();
			evaluationExample.createCriteria().addCriterion("type='"+evaluation.getType()+"'")
											  .addCriterion("businessId=", evaluation.getBusinessId())
											  .addCriterion("deleted=", false);
			//平均星级
			String type = evaluation.getType().toString();
			Long businessId = evaluation.getBusinessId();
			int rstarts = evaluationMapper.selectRstars(type,businessId);
			
			
			CouponExample example = new CouponExample();
			example.createCriteria().addCriterion("couponCode = ", couponCode).addCriterion("deleted = ", false);
			Coupon coupon = couponMapper.selectOneByExample(example);
			
			//更新该优惠券星级
			CouponInfo couponInfo = coupon.getCouponInfo();
			couponInfo.setStars((double)rstarts);
			couponInfoMapper.updateByPrimaryKey(couponInfo);
			
			//若存在父优惠券，则更新父优惠券星级
			CouponInfo gcouponInfo = coupon.getParent();
			int rgstarts = -1;
			if (gcouponInfo!=null) {
				Long gbusinessId = gcouponInfo.getId();
				rgstarts = evaluationMapper.selectRgstars(type,gbusinessId);
				gcouponInfo.setStars((double)rgstarts);
				couponInfoMapper.updateByPrimaryKey(gcouponInfo);
			}
			//更新商户星级
			Shop shop = shopMapper.selectByPrimaryKey(coupon.getBusinessId());
			if (shop != null) {
				shop.setStars((double)(rgstarts==-1?rstarts:rgstarts));
				shopMapper.updateByPrimaryKey(shop);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean deleteByUser(Evaluation evaluation) {
		evaluation.setDeleted(true);
		return evaluationMapper.updateByPrimaryKey(evaluation)==1;
	}

	@Override
	public Evaluation findOneEvaluation(EvaluationExample example) {
		return evaluationMapper.selectOneByExample(example);
	}

	@Override
	public Pager<Evaluation> findListByCouponInfoId(Long id,EvaluationType type ,Pager<Evaluation>pager) {
		if(pager==null) {
			pager = new Pager<Evaluation>();
		}
		EvaluationExample example = new EvaluationExample ();
		example.createCriteria().addCriterion("type='"+type+"'")
							.addCriterion("businessId=", id)
							.addCriterion("deleted=", false);
		pager.setList(evaluationMapper.selectByExample(example, pager));
		pager.setTotalCount(evaluationMapper.countByExample(example));
		return pager;
	}

	@Override
	public Pager<Evaluation> findListByShopId(Long id, EvaluationType type, Pager<Evaluation> pager) {
		if(pager==null) {
			pager = new Pager<Evaluation>();
		}
		EvaluationExample example = new EvaluationExample ();
		example.createCriteria().addCriterion("type='"+type+"'")
							.addCriterion("businessId in (select id from CouponInfo where businessId='"+id+"' and businessType='SHOP' and deleted=false )")
							.addCriterion("deleted=", false);
		pager.setList(evaluationMapper.selectByExample(example, pager));
		pager.setTotalCount(evaluationMapper.countByExample(example));
		return pager;
	}
	
	
}
