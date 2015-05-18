package com.xpos.common.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xpos.common.entity.face.EncryptId;
import com.xpos.common.utils.IDUtil;

public  class NewBaseEntity implements Serializable {

	private static final long serialVersionUID = -480097581231599147L;

	@Column
	private Long id;// ID
	
	@JsonIgnore
	@Column
	private Date createdTime;// 创建时间
	
	@JsonIgnore
	@Column
	private Date updatedTime;// 修改时间
	
	@Column
	@JsonIgnore
	private String comment; //备注
	
	@JsonIgnore
	@Column
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getEid(){
		if(this instanceof EncryptId){
			return getId() != null ? IDUtil.encode(getId()) : null;
		}else{
			return String.valueOf(getId());
		}
	}

}
