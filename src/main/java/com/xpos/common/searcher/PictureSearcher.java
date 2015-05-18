package com.xpos.common.searcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Category;
import com.xpos.common.entity.Picture;
import com.xpos.common.entity.example.PictureExample;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;

public class PictureSearcher extends AbstractSearcher<Picture>{

	private String key;
	private String district;
	private String cityCode;
	private List<Category> categories;
	private String tag;
	
	public boolean hasParameter(){
		return StringUtils.isNotBlank(key) || StringUtils.isNotBlank(district) || StringUtils.isNotBlank(cityCode)  || StringUtils.isNotBlank(tag);
	}
	
	
	public String getParameterString(String skipKey){
		if(!hasParameter())
			return "";
		
		StringBuilder builder = new StringBuilder("?");
		if(StringUtils.isNotBlank(tag) && !"tag".equals(skipKey))
			builder.append("tag=").append(tag).append("&");
		if(StringUtils.isNotBlank(key) && !"key".equals(skipKey))
			builder.append("key=").append(key).append("&");
		if(StringUtils.isNotBlank(district)  && !"district".equals(skipKey))
			builder.append("district=").append(district).append("&");
		if(StringUtils.isNotBlank(cityCode)  && !"cityCode".equals(skipKey))
			builder.append("cityCode=").append(cityCode).append("&");
		String string = builder.toString();
		return string.substring(0, string.length()-1);
	}
	
	
	/**拼接链接参数**/
	public String getParameterString(){
		return getParameterString(null);
		
			
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void addCategory(Category category) {
		if(categories == null)
			categories = new ArrayList<Category>();
		categories.add(category);
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}


	@Override
	public Example<?> getExample() {
		example = new PictureExample();
		Criteria criteria = example.createCriteria();
		
		if(StringUtils.isNotBlank(key))
			criteria.addCriterion("(description like'%"+key+"%')");
		if(StringUtils.isNotBlank(district))
			criteria.addCriterion("region_id=", district);
		if(categories!=null && categories.size() > 0){
			String orsql = "(";
			int orCount = 0;
			for(Category category:categories){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("category_id=" + category.getId());
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		if(StringUtils.isNotBlank(tag)) {
			String orsql = "(";
			int orCount = 0;
			for(String tag2:tag.split(",")){
				if (tag2.trim().equalsIgnoreCase("全部")) {
					return (PictureExample)example;
				}
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("tag='"+tag2.trim()+"'" );
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
			
		return (PictureExample)example;
	}

}
