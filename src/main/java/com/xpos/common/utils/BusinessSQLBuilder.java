package com.xpos.common.utils;

import com.xpos.common.entity.Merchant;
import com.xpos.common.entity.Shop;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessType;

public class BusinessSQLBuilder {
	
	public static String getSQL(Business business){
		StringBuilder sql = new StringBuilder("(businessType='SHOP' AND businessId ");
		if(business instanceof Shop){
			sql.append("=").append(business.getSelfBusinessId()); 
		}else if(business instanceof Merchant){
			sql.append(" in (select s.id from Shop s where s.deleted=false and s.merchant_id = "+business.getSelfBusinessId()+" )");
			
		}
		sql.append(")");
		return sql.toString();
	}
	
	public static  String  getBusinessSQL (BusinessType businessType , Long businessId ){
		StringBuilder builder = new StringBuilder("(");
		 if (BusinessType.MERCHANT.equals(businessType)) {
			//集团下 集团模式的查询   --关联查询出集团下,所有的商户
			 builder.append("  ( businessId = " + businessId);
			 builder.append(" and businessType = '" + BusinessType.MERCHANT.toString() +"' )");
			 //关联集团下的商户
			 builder.append(" or  ( businessType = '" + BusinessType.SHOP.toString()+"' ");
			 builder.append(" and  businessId in ( select s.id from Shop s where s.deleted=false and s.merchant_id = "+businessId+" ))");
		 }else{
			 //商户下 商户模式查询    --关联出所有，该商户的所在的集团
			 builder.append("  ( businessId = " + businessId);
			 builder.append(" and businessType = '" + BusinessType.SHOP.toString() +"' )");
			 //关联集团
			 builder.append(" or  ( businessType = '" + BusinessType.MERCHANT.toString()+"' ");
			 builder.append(" and  businessId in ( select s.merchant_id from Shop s where s.deleted=false and s.id = "+businessId+" ))");
		 }
		 builder.append(")");
		 return  builder.toString();
	}
	
	public static  String  getBusinessSQLByShopNickName (BusinessType businessType , Long businessId, String nickName ){
		StringBuilder builder = new StringBuilder("(");
		 if (BusinessType.MERCHANT.equals(businessType)) {
			//集团下 集团模式的查询   --关联查询出集团下,所有的商户
			 builder.append("  ( businessId = " + businessId);
			 builder.append(" and businessType = '" + BusinessType.MERCHANT.toString() +"' )");
			 //关联集团下的商户
			 builder.append(" or  ( businessType = '" + BusinessType.SHOP.toString()+"' ");
			 builder.append(" and  businessId in ( select s.id from Shop s where s.deleted=false and s.merchant_id = "+businessId+" and name = '"+nickName+"'))");
		 }else{
			 //商户下 商户模式查询    --关联出所有，该商户的所在的集团
			 builder.append("  ( businessId = " + businessId);
			 builder.append(" and businessType = '" + BusinessType.SHOP.toString() +"' and name = '"+nickName+"')");
			 //关联集团
			 builder.append(" or  ( businessType = '" + BusinessType.MERCHANT.toString()+"' ");
			 builder.append(" and  businessId in ( select s.merchant_id from Shop s where s.deleted=false and s.id = "+businessId+" ))");
		 }
		 builder.append(")");
		 return  builder.toString();
	}
	
	/**
	 * 集团下商户独立的查询,
	 */
	public static  String  getBusinessByShopInCouponInfoSQL (BusinessType businessType , Long businessId ){
		StringBuilder builder = new StringBuilder("(");
		 if (BusinessType.MERCHANT.equals(businessType)) {
			//集团下 集团模式的查询   --关联查询出集团下,所有的商户
			 builder.append("  ( businessId = " + businessId);
			 builder.append(" and businessType = '" + BusinessType.MERCHANT.toString() +"' )");
			 //关联集团下的商户
			 builder.append(" or  ( businessType = '" + BusinessType.SHOP.toString()+"' ");
			 builder.append(" and  businessId in ( select s.id from Shop s where s.deleted=false and s.merchant_id = "+businessId+" ))");
		 }else{
			 //商户下 商户模式查询    --关联出所有，该商户的所在的集团
			 builder.append("  ( id in ( select couponInfo_id from CouponInfo_Scope where deleted=false and businessId= "+businessId+" and businessType='SHOP' ))");
		 }
		 builder.append(")");
		 return  builder.toString();
	}
	/**
	 * 集团下商户独立的查询,
	 */
	public static  String  getBusinessByShopSQL (BusinessType businessType , Long businessId ){
		StringBuilder builder = new StringBuilder("(");
		if (BusinessType.MERCHANT.equals(businessType)) {
			//集团下 集团模式的查询   --关联查询出集团下,所有的商户
			builder.append("  ( businessId = " + businessId);
			builder.append(" and businessType = '" + BusinessType.MERCHANT.toString() +"' )");
			//关联集团下的商户
			builder.append(" or  ( businessType = '" + BusinessType.SHOP.toString()+"' ");
			builder.append(" and  businessId in ( select s.id from Shop s where s.deleted=false and s.merchant_id = "+businessId+" ))");
		}else{
			//商户下 商户模式查询    --关联出所有，该商户的所在的集团
			builder.append("  ( businessType='SHOP' and businessId= "+businessId+")");
		}
		builder.append(")");
		return  builder.toString();
	}
	 /**
	  * 关联查询含有优惠券和活动的商铺
	  */
	public static String  getBusinessByShopIdsSQL(  ){
		    StringBuilder builder = new StringBuilder("id  in (");
			builder.append(" SELECT DISTINCT(shops.businessId) FROM ( SELECT cs.businessId FROM CouponInfo_Scope cs ");
			builder.append(" LEFT OUTER JOIN Activity_CouponInfo ac ON ac.couponInfo_id = cs.couponInfo_id ");
			builder.append(" LEFT OUTER JOIN CouponInfo ci ON ci.id = cs.couponInfo_id WHERE cs.deleted = FALSE ");
			builder.append(" AND ci.published = TRUE AND ci.deleted = FALSE AND ac.deleted = FALSE ") ;
			builder.append(" UNION SELECT a.businessId FROM Activity a WHERE a.businessType = 'SHOP' AND a.deleted = FALSE ") ;
			builder.append(" AND a.published = TRUE UNION SELECT cs.businessId FROM CouponInfo_Scope cs LEFT OUTER JOIN ") ;
			builder.append(" CouponInfo ci ON cs.couponInfo_id = ci.id WHERE cs.deleted = FALSE AND ci.published = TRUE AND ci.deleted = FALSE ) shops)");
		return  builder.toString();
	}
	
}
