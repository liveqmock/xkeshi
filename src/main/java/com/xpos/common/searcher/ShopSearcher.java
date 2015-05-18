package com.xpos.common.searcher;

import com.xpos.common.entity.Category;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.example.Example;
import com.xpos.common.entity.example.Example.Criteria;
import com.xpos.common.entity.example.ShopExample;
import com.xpos.common.utils.BusinessSQLBuilder;
import com.xpos.common.utils.MapSQLBuilder;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopSearcher extends AbstractSearcher<Shop>{

	private String key;
	private String district;
	private String cityCode;
	private List<Category> categories;
	private Integer order;
	private Long landmarkId;
	private Long merchantId  ;
	private Long categoryId ;
	private Date createDate;// 创建日期
	private Date modifyDate;// 修改日期
	private String scType ;
    private Long categoryParentId;
	
	private String[] orderByClause = {"id ASC","id DESC",
			                          "name ASC","name DESC",
			                          "stras ASC","stars DESC",
			                          "creatDate ASC","createDate DESC"
			                          };
	public boolean getHasParameter(){
		return StringUtils.isNotBlank(key) || StringUtils.isNotBlank(district) || merchantId != null ||
			   createDate != null ||  modifyDate != null || StringUtils.isNotBlank(cityCode) || categoryId != null || categoryParentId !=null ||
			   StringUtils.isNotBlank(cityCode)||  landmarkId != null;
	}
	 
	public String getParameterString(String skipKey){
		if(!getHasParameter())
			return "";
		
		StringBuilder builder = new StringBuilder("?");
		if(StringUtils.isNotBlank(key) && !"key".equals(skipKey))
			builder.append("key=").append(key).append("&");
		if(StringUtils.isNotBlank(district)  && !"district".equals(skipKey))
			builder.append("district=").append(district).append("&");
		if(StringUtils.isNotBlank(cityCode)  && !"cityCode".equals(skipKey))
			builder.append("cityCode=").append(cityCode).append("&");
		if( landmarkId != null  && !"landmark".equals(skipKey))
			builder.append("landmarkId=").append(landmarkId).append("&");
		if(StringUtils.isNotBlank(scType) && !"scType".equals(skipKey))
			builder.append("scType=").append(scType).append("&");
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
	
	public Long getCategoryId() {
		return categoryId;
	}


	public Long getLandmarkId() {
		return landmarkId;
	}

	public void setLandmarkId(Long landmarkId) {
		this.landmarkId = landmarkId;
	}

	public String getScType() {
		return scType;
	}

	public void setScType(String scType) {
		this.scType = scType;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}


	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	 

	public void addCategory(Category category) {
		if(categories == null)
			categories = new ArrayList<Category>();
		categories.add(category);
	}

	public Long getMerchantId() {
		return merchantId;
	}


	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Date getModifyDate() {
		return modifyDate;
	}


	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}


	public String[] getOrderByClause() {
		return orderByClause;
	}


	public void setOrderByClause(String[] orderByClause) {
		this.orderByClause = orderByClause;
	}

    public Long getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(Long categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    @Override
	public Example<?> getExample() {
		example = new ShopExample();
		Criteria criteria = example.createCriteria();
		if(StringUtils.isNotBlank(key)) {
			//此处判断一下是否有单引号，如果有的话将其转化为双引号。
			//因为但因好不能用在sql语句中查询，会报错
			if(StringUtils.contains(key, "\'")) {
				String keyOrigin = key;
				key = "\"";
				criteria.addCriterion("(name like '%"+key+"%' or id like '%"+key+"%')");
				key = keyOrigin;
			} else {
				criteria.addCriterion("(name like '%"+key+"%' or id like '%"+key+"%')");
			}
		}
			
		if(StringUtils.isNotBlank(district))
			criteria.addCriterion("region_id=", district);
		if(StringUtils.isNotBlank(cityCode))
			criteria.addCriterion("cityCode=", cityCode);
		if(merchantId != null)
			criteria.addCriterion("merchant_id=", merchantId);
		if( categoryId != null)
            criteria.addCriterion("category_id=" + categoryId);
		if( categoryParentId != null)
            criteria.addCriterion("category_id in (select id from category where parent_id = "+categoryParentId+")");
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
		if ( createDate !=null ) 
			criteria.addCriterion(" createDate   >= ",new DateTime(createDate).toString("yyyy-MM-dd hh:mm:ss"));
		if ( modifyDate !=  null) 
			criteria.addCriterion(" modifyDate   >=  ",new DateTime(modifyDate).toString("yyyy-MM-dd hh:mm:ss"));
		 /*查看含有优惠券或活动的商户列表*/
		if ( "CIA".equalsIgnoreCase(scType) ) {
			criteria.addCriterion(BusinessSQLBuilder.getBusinessByShopIdsSQL());
		}
		if (landmarkId !=null) {//查询包含该商圈的商家
			criteria.addCriterion( MapSQLBuilder.landmarkInnerShopSQL(landmarkId));
		}
		if(order != null && order<orderByClause.length)
			example.setOrderByClause(orderByClause[order]);
		else
			example.setOrderByClause(" id DESC");
		return (ShopExample)example;
	}

}
