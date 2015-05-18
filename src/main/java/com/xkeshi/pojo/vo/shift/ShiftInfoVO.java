package com.xkeshi.pojo.vo.shift;


/**
 * 
 * @author xk
 * 交接班操作员的信息
 */
public class ShiftInfoVO {
	/*操作员的账号*/
	private String operatorAccount ;
	/*操作员的名称*/
	private String operatorName  ; 
	/*当前班次,操作员第一次登录的时间*/
	private String startTime  ; 
	/*当前时间*/
	private String endTime  ;
	
	public String getOperatorAccount() {
		return operatorAccount;
	}
	public void setOperatorAccount(String operatorAccount) {
		this.operatorAccount = operatorAccount;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
	
}














