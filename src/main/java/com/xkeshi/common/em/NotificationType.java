package com.xkeshi.common.em;


/**
 *
 * 站内通知的枚举类型
 *
 */
public enum NotificationType{

		/**
		 * Deal Asset related Notification
		 */
		DEAL_ASSET_REDEMPTION_NEED_CONFIRM("解除委托请确认"),
		DEAL_ASSET_REDEMPTION_CONFIRMED("所有款项已确认到帐"),
		DEAL_ASSET_REDEMPTION_CANNOT_CONFIRM("打款无法确认，有疑问"),
		DEAL_ASSET_REDEMPTION_PAID("赎回订单财务已经打款"),
		DEAL_ASSET_REDEMPTION_PAYMENT_VOUCHER_UPLOADED("财务已上传打款凭证"),



		/**
		 * Asset related Notification
		 */
		ASSET_HAS_PASSIVE_REDEMPTION("资产包有被动赎回"),
		ASSET_SALE_RESTRICT_ASSIGNED("销售额度分配"),

        /*
        * pawn alarm Notification
        * */
        ASSET_PAWN_ALARM_UPDATED("资产包抵押物预警"),

		/**
		 * Client related Notification
		 */
		CLIENT_CONTINUE_BUY_REQUEST("资产包续购申请"),
		CLIENT_DISTRIBUTED("客户分配"),
		CLIENT_NEW_ADDED("客服新添加了客户");

		private String genericTitle;

		private NotificationType(String title){
			setGenericTitle(title);
		}

		public String getGenericTitle() {
			return genericTitle;
		}

		public void setGenericTitle(String genericTitle) {
			this.genericTitle = genericTitle;
		}

}
