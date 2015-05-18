package com.xpos.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 
 * 商户类别
 * @author Johnny
 *
 */
public class Category extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8636690733281395125L;
	
	@Column
	private String name;
	@Column
	private Category parent;
	@Column
	private String description;
	@Column
	private Picture banner;
	@Column
	private Boolean visible;
	@Column
	@Max(100)
	@Min(0)
	private Integer sequence;
	@Column
	private Integer count ;       //统计大类或小类下面有多少家商户
	
	private List<Category> categorys = new ArrayList<>();
	
	
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public List<Category> getCategorys() {
		return categorys;
	}
	public void setCategorys(List<Category> categorys) {
		this.categorys = categorys;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Picture getBanner() {
		return banner;
	}
	public void setBanner(Picture banner) {
		this.banner = banner;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Category))
			return false;
		Category category = (Category)obj;

		if(this.getId() == null || category.getId() == null)
			return this == category;
		
		return this.getId().equals(category.getId());
	}
}
