package com.xkeshi.pojo.po;

import javax.persistence.Column;

/**
 * Created by david-y on 2015/1/8.
 */
public class Shop {

	public static String SHOP_ENABLE_SHIFT = "_SHOP_ENABLE_SHIFT";

    public static String SHOP_ENABLE_MULTIPLE_PAYMENT = "_SHOP_ENABLE_MULTIPLE_PAYMENT";

	@Column
	private Long id;

	@Column
	private String name;

    private String contact;

    private Long merchantId;

	/** 是否需要交接班 */
	@Column(name = "enable_shift")
	private Boolean enableShift;

    /** 是否需要支持多比支付 */
    @Column(name = "enable_multiple_payment")
    private Boolean enableMultiplePayment;

	/** 交接班时是否需要显示应收现金 */
	@Column(name = "visible_shift_receivable_Data")
	private Boolean visibleShiftReceivableData;

	@Column(name="deleted")
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
