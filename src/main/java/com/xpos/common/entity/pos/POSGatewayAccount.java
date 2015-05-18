package com.xpos.common.entity.pos;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.xpos.common.entity.BaseEntity;
import com.xpos.common.entity.face.Business;
import com.xpos.common.entity.face.Business.BusinessModel;
import com.xpos.common.entity.face.Business.BusinessType;

/**
 * 
 * POS交易网关(通道)
 * @author Johnny
 *
 */
public class POSGatewayAccount extends BaseEntity{
	private static final long serialVersionUID = -7826953070321376595L;
	
	@Column
	private Long businessId;
	
	@Column
	private BusinessType businessType;
	
	@Column
	@NotBlank(message="商户号必填")
	private String account;
	
	@Column
	@NotNull(message="支付类型")
	private POSGatewayAccountType type;
	
	@Column
	@NotBlank(message="终端号必填")
	private String terminal;
	
	@Column
	private String remark;
	
	@Column
	private String signKey;

    @Column
    private Integer enable;

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public enum POSGatewayAccountType{
		UMPAY(1, "联动优势"),
		BOC(2, "中国银行"),
		SHENGPAY(3, "盛付通"),
		ALIPAY(4, "支付宝"),
        WECHAT(5,"微信");
		
		private int state;
		private String desc;
		POSGatewayAccountType(int state, String desc){
			this.state = state;
			this.desc = desc;
		}
		
		public int getState() {
			return state;
		}
		
		public String getDesc(){
			return desc;
		}
		
		public static final POSGatewayAccountType queryByState(int state){
			for(POSGatewayAccountType status : POSGatewayAccountType.values()){
				if(status.getState() == state){
					return status;
				}
			}
			return null;
		}
		
		public static final POSGatewayAccountType queryByStatus(String status){
			for(POSGatewayAccountType statue : POSGatewayAccountType.values()){
				if(StringUtils.equals(status, statue.toString())){
					return statue;
				}
			}
			return null;
		}
		
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}
	
	public void setBusiness(Business business){
		this.businessId = business.getAccessBusinessId(BusinessModel.POS);
		this.businessType = business.getAccessBusinessType(BusinessModel.POS);
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		if(StringUtils.isNotBlank(account)) {
			this.account = account.trim();
		}

	}

	public POSGatewayAccountType getType() {
		return type;
	}

	public void setType(POSGatewayAccountType type) {
		this.type = type;
	}
	
	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		if(StringUtils.isNotBlank(terminal)) {
			this.terminal = terminal.trim();
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		if(StringUtils.isNotBlank(signKey)) {
			this.signKey = signKey.trim();
		}
	}
	
}
