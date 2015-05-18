package com.xpos.common.searcher.member;

import org.apache.commons.lang3.StringUtils;

public class MemberAttributeSearcher {
	
	private String key; //属性名称
	
	private Long memberAttributeTemplateId;
	
	public boolean getHasParameter() {
		return StringUtils.isNotBlank(key);
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getMemberAttributeTemplateId() {
		return memberAttributeTemplateId;
	}

	public void setMemberAttributeTemplateId(Long memberAttributeTemplateId) {
		this.memberAttributeTemplateId = memberAttributeTemplateId;
	}
	
}
