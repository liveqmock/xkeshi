package com.xpos.common.entity.pos;

import javax.persistence.Column;

import com.xpos.common.entity.BaseEntity;

public class POSOperationLog extends BaseEntity {
	private static final long serialVersionUID = -26558484855132457L;
	
	public static final int LOGIN = 0;
	public static final int COUPON_CONSUME = 1;
	public static final int PAYMENT = 2;
	
	@Column
	private String deviceNumber;
	@Column
	private int action;
	@Column
	private String type;
	@Column
	private String version;
	
	/*操作员的id*/
	@Column(name = "operator_id")
	private Long operatorId;
	
	/*当班回话session*/
	@Column(name="operator_session_code")
	private String operatorSessionCode;
	
	/*是否登录(0:注销,1:登录，2:已登录)*/
	@Column(name="logined")
	private Integer  logined ;
	
	/*是否交接(0未交接,1交接)*/
	@Column(name= "shift" )
	private Integer shift  ;
	
	public String getDeviceNumber() {
		return deviceNumber;
	}
	public void setDeviceNumber(String deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorSessionCode() {
		return operatorSessionCode;
	}
	public void setOperatorSessionCode(String operatorSessionCode) {
		this.operatorSessionCode = operatorSessionCode;
	}
	public Integer getLogined() {
		return logined;
	}
	public void setLogined(Integer logined) {
		this.logined = logined;
	}
	public Integer getShift() {
		return shift;
	}
	public void setShift(Integer shift) {
		this.shift = shift;
	}

}
