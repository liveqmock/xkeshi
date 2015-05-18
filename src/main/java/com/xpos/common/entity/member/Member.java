package com.xpos.common.entity.member;

import java.util.Date;

import javax.persistence.Column;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.Operator;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;
import com.xpos.common.entity.face.EncryptId;

public class Member extends BaseEntity implements EncryptId{
	private static final long serialVersionUID = 3501670543002341350L;

	@Column
	@NotBlank(message="姓名不能为空")
	private String name;
	
	@Column
	private String password;

	@Column
	private String salt;
	
	@Column
	private String nickName;
	
	@Column
	@NotBlank(message="性别不能为空")
	private String gender;
	
	@Column
	private Date birthday;
	
	@Column
	@Email(message="电子邮箱格式错误")
	private String email;
	
	@Column
	private Long businessId; //所属集团/商户的ID
	
	@Column
	private BusinessType businessType; //所属集团/商户的类型
	
	@Column
	private MemberType memberType; //会员类型（等级）
	
	@Column
	@NotBlank(message="手机号不能为空")
	private String mobile;
	
	@Column
	private Shop shop; //注册来源商户（当会员由集团统一管理时）
	
	@Column
	private Operator operator;
	
	@Column
	private String operatorSessionCode;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		if(birthday != null){
			birthday = new DateTime(birthday).withTimeAtStartOfDay().toDate(); //精确到日期、忽略时分秒
		}
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public void setBusiness(Business business){
		this.businessId = business.getSelfBusinessId();
		this.businessType = business.getSelfBusinessType();
	}
	
	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}

	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}

}
