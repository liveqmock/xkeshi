package com.xpos.common.utils;

import com.xpos.common.entity.Position;

public class MapSQLBuilder {
   
	/**
	 * 查询出地标内的所有商户
	 * @param landmarkId  //地标的id
	 * @return
	 */
	public static String landmarkInnerShopSQL(Long landmarkId ){
		StringBuilder  builder = new StringBuilder(" position_id in (");
		builder.append(" SELECT lmInS.position_id FROM ");
		builder.append("( SELECT ( lm.radius - Asin( sqrt( power( sin( ( lm.latitude * 0.01745329222 - p.latitude * 0.01745329222 ) / 2 ), 2 ) ");
		builder.append(" + cos(lm.latitude * 0.01745329222) * cos(p.latitude * 0.01745329222) * power( ");
		builder.append(" sin( ( lm.longitude * 0.01745329222 - p.longitude * 0.01745329222 ) / 2 ), 2 ) ) ) * 2 * 6370996.81 ) AS distance, s.id ,s.position_id ");
		builder.append(" FROM Shop s LEFT OUTER JOIN Position p ON s.position_id = p.id ,");
		builder.append(" ( SELECT pl.latitude, pl.longitude, ldm.radius FROM Landmark ldm ") ;
		builder.append(" LEFT OUTER JOIN Position pl ON ldm.id = pl.foreignId WHERE pl.foreignId = "+landmarkId );
		builder.append(" AND pl.type = '"+Position.PositionType.LANDMARK+"'  AND pl.deleted  = FALSE AND ldm.deleted  = FALSE ) lm ") ;
		builder.append(" WHERE p.type = '"+Position.PositionType.SHOP+"' AND p.latitude IS NOT NULL AND p.longitude IS NOT NULL ");
		builder.append(" AND p.deleted = FALSE AND s.deleted = FALSE AND s.visible = TRUE ORDER BY distance DESC )  lmInS ");
		builder.append(" where lmInS.distance >=0");
		builder.append(")");
		return builder.toString();
	}
}
