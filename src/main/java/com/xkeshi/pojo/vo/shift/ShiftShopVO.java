package com.xkeshi.pojo.vo.shift;


/**
 * Created xk
 */
public class ShiftShopVO {

	private Long id;

	private String name;

    private String contact;

    private Long merchantId;

	/** 是否需要交接班 */
	private Boolean enableShift;

    /** 是否需要支持多比支付 */
    private Boolean enableMultiplePayment;

	/** 交接班时是否需要显示应收现金 */
	private Boolean visibleShiftReceivableData;

	private Boolean deleted;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getEnableShift() {
		return enableShift;
	}

	public void setEnableShift(Boolean enableShift) {
		this.enableShift = enableShift;
	}

    public Boolean getEnableMultiplePayment() {
        return enableMultiplePayment;
    }

    public void setEnableMultiplePayment(Boolean enableMultiplePayment) {
        this.enableMultiplePayment = enableMultiplePayment;
    }

    public Boolean getVisibleShiftReceivableData() {
		return visibleShiftReceivableData;
	}

	public void setVisibleShiftReceivableData(Boolean visibleShiftReceivableData) {
		this.visibleShiftReceivableData = visibleShiftReceivableData;
	}

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
    
}
