package com.xpos.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xpos.common.entity.Tag;
import com.xpos.common.entity.example.TagExample;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.persistence.mybatis.TagMapper;

@Service
public class TagServiceImpl implements TagService{

	@Resource
	private TagMapper   tagMapper  ;
	
	
	@Override
	@Transactional
	public boolean saveTag(Tag tag) {
		return tagMapper.insert(tag)>0;
	}

	@Override
	@Transactional
	public boolean updateTag(Tag tag) {
		 
		return tagMapper.updateByPrimaryKey(tag)>0;
	}

	@Override
	public Map<String, List<Tag>> findAllTags(Business business, TagExample example) {
		if(example == null){
			example = new TagExample();
			example.createCriteria();
		}
		example.appendCriterion("businessId=", business.getAccessBusinessId(BusinessModel.COUPON))
								.addCriterion("businessType=", business.getAccessBusinessType(BusinessModel.COUPON).toString())
								.addCriterion("published= ", true)
								.addCriterion("deleted= ", false);
		if (business.getAccessBusinessType(BusinessModel.COUPON) == BusinessType.SHOP) {
			//关联集团
			String sql = " ( SELECT s.merchant_id from   Shop s where s.id= "+business.getAccessBusinessId(BusinessModel.COUPON)+" )  ";
			example.or().addCriterion("businessType  = ",BusinessType.MERCHANT.toString())
						.addCriterion("businessId in "+ sql);
		}
		List<Tag> allTags = tagMapper.selectByExample(example,null);
		Map<String, List<Tag>>  tagMaps  =  new HashMap<>();
		for (Tag tag : allTags) {
			List<Tag> list = tagMaps.get(tag.getGroup());
			if (list == null) 
				list  = new ArrayList<>();
			list.add(tag);
			tagMaps.put(tag.getGroup(), list);
		}
		return tagMaps ;
	}

	@Override
	public Tag findTag(Long tagId) {
		return tagMapper.selectByPrimaryKey(tagId);
	}
	
}
