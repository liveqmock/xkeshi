package com.xpos.common.searcher;

import org.apache.commons.lang.StringUtils;

import com.xpos.common.entity.Activity;
import com.xpos.common.entity.example.ActivityExample;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.utils.IDUtil;

public class ActivitySearcher extends AbstractSearcher<Activity>{

	private String key;
	private String serial; //活动编号
	private String published;
	
	public boolean getHasParameter(){
		return StringUtils.isNotBlank(key) || StringUtils.isNotBlank(serial) || published != null;
	}
	
	
	public String getParameterString(String skipKey){
		if(!getHasParameter())
			return "";
		
		StringBuilder builder = new StringBuilder("?");
		if(StringUtils.isNotBlank(key) && !"key".equals(skipKey))
			builder.append("key=").append(key).append("&");
		if(StringUtils.isNotBlank(serial) && !"serial".equals(skipKey))
			builder.append("serial=").append(serial).append("&");
		if(StringUtils.isNotBlank(published) && !"published".equals(skipKey))
			builder.append("published=").append(published).append("&");
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

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}

	@Override
	public Example<?> getExample() {
		example = new ActivityExample();
		Criteria criteria = example.createCriteria();
		if(StringUtils.isNotBlank(key)){
			criteria.addCriterion("(name like '%"+key+"%')");
		}
		if(StringUtils.isNotBlank(serial)){
			Long id = IDUtil.decode(serial);
			criteria.addCriterion("id = ", id);
		}
		if(StringUtils.isNotBlank(published)){
			String orsql = "(";
			int orCount = 0;
			for(String str : StringUtils.split(published, ',')){
				if(orCount!=0)
					orsql+=" OR ";
				orCount++;
				orsql+= ("published = " + str);
			}
			orsql+=")";
			criteria.addCriterion(orsql);
		}
		example.setOrderByClause(" createDate DESC");
		return (ActivityExample)example;
	}

}
