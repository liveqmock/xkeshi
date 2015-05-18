package com.xkeshi.pojo.meta;


import com.xkeshi.pojo.po.Base;

import javax.persistence.Column;

/**
 * 字典基础类
 *
 * 用于作为字典类型的模板
 *
 * @author David
 */
public abstract class Meta extends Base {

	@Column(name="name")
	private String name;
	@Column(name="code")
	private String code;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	 @Override  
	 public String toString() {  
		 return super.toString();
	 }

}
