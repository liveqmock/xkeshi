package com.xpos.common.searcher.member;

import org.apache.commons.lang.StringUtils;

public class MemberTypeSearcher{
	
	private String key; //会员类型名称
	
	public boolean getHasParameter() {
		return StringUtils.isNotBlank(key);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
