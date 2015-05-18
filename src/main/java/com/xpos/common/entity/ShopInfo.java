package com.xpos.common.entity;

import com.xpos.common.entity.pos.POSGatewayAccount;

import javax.persistence.Column;
import java.util.List;


/**
 * 
 * 店铺相关信息
 * @author snoopy
 *
 */
public class ShopInfo extends BaseEntity {

	private static final long serialVersionUID = -5420457670949387095L;

	@Column
	private Long  shopId;
	
	//短信后缀
	@Column
	private String smssuffix;
	
	//短信通道
	@Column
	private String smsChannel;
	
	//账号管理
	private List<POSGatewayAccount> posAccountList;
	
	//爱客仕二级域名
	@Column
	private String xposSld;
	
	@Column
	private ConsumeType consumeType;

    private Integer enableCash;

    public Integer getEnableCash() {
        return enableCash;
    }

    public void setEnableCash(Integer enableCash) {
        this.enableCash = enableCash;
    }

    public enum ConsumeType{
		PAY_FIRST_WITH_SEAT("先付款后消费(有座)"), //先付款后消费，有座
		PAY_FIRST_WITHOUT_SEAT("先付款后消费(无座)"), //先付款后消费，无座
		CONSUME_FIRST_WITH_SEAT("先消费后付款(有座)"), //先消费后付款，有座
		CONSUME_FIRST_WITHOUT_SEAT("先消费后付款(无座)"); //先消费后付款，无座
		
		private String description;
		
		public void setDescription(String description) {
			this.description = description;
		}
		private ConsumeType(String description){
			this.setDescription(description);
		}
		public String getDescription() {
			return description;
		}
		
	}
	
	public List<POSGatewayAccount> getPosAccountList() {
		return posAccountList;
	}
	public void setPosAccountList(List<POSGatewayAccount> posAccountList) {
		this.posAccountList = posAccountList;
	}
	public String getSmssuffix() {
		return smssuffix;
	}
	public void setSmssuffix(String smssuffix) {
		this.smssuffix = smssuffix;
	}
	public String getSmsChannel() {
		return smsChannel;
	}
	public void setSmsChannel(String smsChannel) {
		this.smsChannel = smsChannel;
	}
	public String getXposSld() {
		return xposSld;
	}
	public void setXposSld(String xposSld) {
		this.xposSld = xposSld;
	}
	public Long getShopId() {
		return shopId;
	}
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	public ConsumeType getConsumeType() {
		return consumeType;
	}
	public void setConsumeType(ConsumeType consumeType) {
		this.consumeType = consumeType;
	}
	
}
