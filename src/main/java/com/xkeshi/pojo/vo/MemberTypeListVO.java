package com.xkeshi.pojo.vo;

import java.util.ArrayList;
import java.util.List;

public class MemberTypeListVO{
	
	private List<MemberTypeVO> memberTypes = new ArrayList<MemberTypeVO>();
	
	public MemberTypeListVO(){
		
	}
	
	public MemberTypeListVO(List<MemberTypeVO> memberTypes){
		this.memberTypes = memberTypes;
	}

	public List<MemberTypeVO> getMemberTypes() {
		return memberTypes;
	}

	public void setMemberTypes(List<MemberTypeVO> memberTypes) {
		this.memberTypes = memberTypes;
	}
	
}