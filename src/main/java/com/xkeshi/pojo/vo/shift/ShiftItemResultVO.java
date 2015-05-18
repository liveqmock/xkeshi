package com.xkeshi.pojo.vo.shift;

import java.util.List;

/**
 * 
 * @author xk
 * 交接班商品清单
 */
public class ShiftItemResultVO { 
	
	/**交接班操作员信息*/
	private ShiftInfoVO  shiftInfo ;
	
	/** 交接班销售商品详细*/
	private List<ShiftItemVO>  itemList;

	public ShiftItemResultVO() {
		super();
	}

	public ShiftItemResultVO(ShiftInfoVO shiftInfo, List<ShiftItemVO> itemList) {
		super();
		this.shiftInfo = shiftInfo;
		this.itemList = itemList;
	}

	public ShiftInfoVO getShiftInfo() {
		return shiftInfo;
	}

	public void setShiftInfo(ShiftInfoVO shiftInfo) {
		this.shiftInfo = shiftInfo;
	}

	public List<ShiftItemVO> getItemList() {
		return itemList;
	}

	public void setItemList(List<ShiftItemVO> itemList) {
		this.itemList = itemList;
	}

 
	
	
}
