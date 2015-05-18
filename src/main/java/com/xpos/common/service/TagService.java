package com.xpos.common.service;

import java.util.List;
import java.util.Map;

import com.xpos.common.entity.Tag;
import com.xpos.common.entity.example.TagExample;
import com.xpos.common.entity.face.Business;


public interface TagService { 
	
	boolean saveTag(Tag tag);
	
	boolean updateTag(Tag tag);
	
	//查询全部
	Map<String ,List<Tag>> findAllTags(Business business,TagExample example);
	
	Tag       findTag (Long tagId);
	
}
