package com.xkeshi.pojo.vo.shift;

import java.math.BigDecimal;
/**
 * 
 * @author xk
 * 交接班商品清单
 */
public class ShiftItemVO {
	/*商品名称*/
	private String name;
	
	/*商品价格*/
	private BigDecimal price;
 
	/*销售数量*/
	private Integer quantity;
	
	/*商品总金额*/
	private BigDecimal totalAmount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotalAmount() {
		if (totalAmount != null) {
			return totalAmount.setScale(2,BigDecimal.ROUND_DOWN);
		}
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "ShiftItemResultVO [name=" + name + ", price=" + price
				+ ", quantity=" + quantity + ", totalAmount=" + totalAmount
				+ "]";
	}
	
	
	
}
