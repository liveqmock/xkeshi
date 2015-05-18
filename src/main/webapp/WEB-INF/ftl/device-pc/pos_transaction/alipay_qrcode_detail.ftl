<#import "/macro.ftl" as m>
<@m.page_header selected='order' subselected='alipay_qrcode' title='扫码付详情' css='seller_info'/>
<style>
	.td_key{
		font-size: 14px;
		font-weight: bold;
		white-space: nowrap ;
	}
	.td_val{
		padding: 0px 0 0px 40px;
		 
	}
</style>
<div class="rwrap">
	<p class="r_title"> POS流水详情</p>
		<div class="tb_wrap">
		   <#if (alipayTransactionDetail)!>
			<table class="tb_main">
				<tr>
					<td><span class="td_key">用户:</span><span class="td_val">${(alipayTransactionDetail.memberName)!}</span></td>
					<td><span class="td_key">订单ID:</span><span class="td_val">${(alipayTransactionDetail.eid)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">订单金额:</span><span class="td_val">${(alipayTransactionDetail.amount)!}</span></td>
					<td><span class="td_key">手机号:</span><span class="td_val">
						<#if (alipayTransactionDetail.memberMobile)?? && alipayTransactionDetail.memberMobile?length == 11 >
							${alipayTransactionDetail.memberMobile?substring(0,3)+'****'+alipayTransactionDetail.memberMobile?substring(7)}
						<#else>
							-
						</#if>
					</span></td>
				</tr>
				<tr>
					<td><span class="td_key">内部订单:</span><span class="td_val">${(alipayTransactionDetail.serial)!}</span></td>
					<td><span class="td_key">外部订单:</span><span class="td_val">${(alipayTransactionDetail.alipaySerial)!}</span></td>
				</tr>
				 <tr>
					<td><span class="td_key">状态:</span>
					<span class="td_val">
					<#if (alipayTransactionDetail.statusCode =="FAILED")! >
						付款失败
					<#elseif (alipayTransactionDetail.statusCode =="UNPAID")!>
						等待付款
					<#elseif (alipayTransactionDetail.statusCode =="SUCCESS")!>
						付款成功
					<#elseif (alipayTransactionDetail.statusCode =="PAID_REFUND")!>
						退款成功
					<#elseif (alipayTransactionDetail.statusCode =="TIMEOUT")!>
						交易超时
					<#elseif (alipayTransactionDetail.statusCode =="CANCEL")!>
						撤销成功
                    <#elseif (alipayTransactionDetail.statusCode =="PARTIAL_PAYMENT")!>
                        部分支付成功
                    <#else>
                        其他
					</#if>
					</span></td>
					<td><span class="td_key">操作员:</span><span class="td_val">${(alipayTransactionDetail.operatorUserName)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">付款账号:</span><span class="td_val ios-tel">${(alipayTransactionDetail.buyerId)!}</span></td>
					<td><span class="td_key">收款账号:</span><span class="td_val ios-tel">${(alipayTransactionDetail.sellerAccount)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">刷卡终端号:</span><span class="td_val">${(alipayTransactionDetail.deviceNumber)!}</span></td>
					<td><span class="td_key">交易返回码:</span><span class="td_val">${(alipayTransactionDetail.responseCode)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">创建时间:</span><span class="td_val">${(alipayTransactionDetail.createdTime?string('MM-dd HH:mm'))!}</span></td>
					<td><span class="td_key">交易时间:</span><span class="td_val">${(alipayTransactionDetail.tradeTime?string('yyyy-MM-dd'))!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">修改时间:</span><span class="td_val">${(alipayTransactionDetail.updatedTime?string('MM-dd HH:mm'))!}</span></td>
				</tr>
			</table>
			</#if>
		</div>
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />