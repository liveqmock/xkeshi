package com.xpos.common.entity.member;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.json.JSONArray;

import org.apache.commons.lang3.StringUtils;

import com.xpos.common.entity.BaseEntity;

public class MemberAttribute extends BaseEntity {
	
	private static final long serialVersionUID = -1994733904673322383L;

	private MemberAttributeTemplate memberAttributeTemplate;
	
	@Column
	@NotNull
	private String name;
	
	@Column
	@NotNull
	private AttributeType attributeType;
	
	@Column
	@NotNull
	private boolean isRequired;
	
	@Column
	@NotNull
	private boolean isEnabled;
	
	@Column
	@Min(value=0)
	@Max(value=100)
	private Integer sequence;
	
	@Column
	private String optionalValues; //备选项（JSON格式存储）
	
	@Transient
	private String storedValue; //具体会员的属性值
	
	public enum AttributeType{
		text, number,
		select, checkbox, date;
	}

	public MemberAttributeTemplate getMemberAttributeTemplate() {
		return memberAttributeTemplate;
	}

	public void setMemberAttributeTemplate(MemberAttributeTemplate memberAttributeTemplate) {
		this.memberAttributeTemplate = memberAttributeTemplate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getOptionalValues() {
		return optionalValues;
	}

	public void setOptionalValues(String optionalValues) {
		this.optionalValues = optionalValues;
	}

	public String getStoredValue() {
		return storedValue;
	}

	public void setStoredValue(String storedValue) {
		this.storedValue = storedValue;
	}
	
	public List<String> getAttributeOptionalValues() {
		if (StringUtils.isEmpty(optionalValues)) {
			return null;
		}
		JSONArray jsonArray = new JSONArray(optionalValues);
		List<String> list = new ArrayList<>();
		for(int i = 0; i < jsonArray.length(); i++){
			list.add(jsonArray.getString(i));
		}
		return list;
	}
	
}
