package com.xpos.common.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.xpos.common.entity.OrderItem;
import com.xpos.common.persistence.BaseMapper;
import com.xpos.common.searcher.OrderSearcher;
import com.xpos.common.utils.Pager;

public interface OrderItemMapper extends BaseMapper<OrderItem>{

	@ResultMap("DetailMap")
	@Select("select oi.*, (oi.price * oi.quantity) as amount from OrderItem oi where oi.order_id = #{id}")
	public List<OrderItem> selectByOrderId(Long id);

	@ResultMap("ListMap")
	@Select("SELECT oi.itemName, sum(oi.quantity) quantity FROM `Orders` o LEFT JOIN `OrderItem` oi on o.id = oi.order_id where ${searcher.businessSQL} and o.status='SUCCESS' and oi.createDate >= #{searcher.startDate} and oi.createDate <= #{searcher.endDate} group by oi.item_id order by quantity desc, oi.createDate desc limit #{pager.startNumber} ,#{pager.pageSize}")
	public List<OrderItem> selectOrderItemStatisticList(@Param("searcher")OrderSearcher searcher, @Param("pager")Pager<OrderItem> pager);

	@ResultType(Integer.class)
	@Select("SELECT count(distinct oi.item_id) FROM `Orders` o LEFT JOIN `OrderItem` oi on o.id = oi.order_id where ${searcher.businessSQL} and o.status='SUCCESS' and oi.createDate >= #{searcher.startDate} and oi.createDate <= #{searcher.endDate}")
	public int countOrderItemStatisticList(@Param("searcher")OrderSearcher searcher);
	
	public int deleteById(@Param("id")Long id);
	
}
