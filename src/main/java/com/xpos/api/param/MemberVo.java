package com.xpos.api.param;

import com.xpos.common.entity.member.MemberAttribute;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

public class MemberVo {
	
	@NotBlank(message="商户ID不能为空")
	private String mid;
	
	@NotBlank(message="设备号不能为空")
	private String deviceNumber;
	
	private Long operatorId;
	
	private String mbid;
	
	@NotBlank(message="用户名不能为空")
	private String name;
	
	private String sex;
	
	@NotBlank(message="手机号码不能为空")
	@Pattern(regexp="^(1(([357][0-9])|(47)|[8][0-9]))\\d{8}$",message="手机号码不合规则")
	private String mobile;
	
	private String birthday;
	
	private String email;
	
	private Long memberTypeId;
	
	private List<MemberAttribute> memberAttributes;
	
	public String getMid() {
		return mid;
	}
	
	public Long getMidLong() {
		return Long.valueOf(mid);
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getMbid() {
		return mbid;
	}
	
	public Long getMbidLong() {
		return Long.valueOf(mbid);
	}

	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}
	
	public void setSex(String sex) {
		if(StringUtils.equalsIgnoreCase(sex, "f")){
			this.sex = "female";
		}else if(StringUtils.equalsIgnoreCase(sex, "m")){
			this.sex = "male";
		}
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBirthday() {
		return birthday;
	}
	
	public Date getBirthdayDate(){
		try {
			Date date = DateUtils.parseDateStrictly(birthday, "yyyy-MM-dd");
			return date;
		} catch (Exception e) {
			return null;
		}
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMemberTypeId() {
		return memberTypeId;
	}

	public void setMemberTypeId(Long memberTypeId) {
		this.memberTypeId = memberTypeId;
	}

	public List<MemberAttribute> getMemberAttributes() {
		return memberAttributes;
	}

	public void setMemberAttributes(List<MemberAttribute> memberAttributes) {
		this.memberAttributes = memberAttributes;
	}
}
