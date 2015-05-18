<#import "/macro.ftl" as m>
<@m.page_header selected='order' subselected='pos_transaction' title='流水详情' css='seller_info'/>
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
		   <#if (posTransaction)!>
			<table class="tb_main">
				<tr>
					<td><span class="td_key">用户:</span><span class="td_val">${(posTransaction.use.name)!}</span></td>
					<td><span class="td_key">订单ID:</span><span class="td_val">${(posTransaction.eid)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">订单金额:</span><span class="td_val">${(posTransaction.sum)!'0.00'}</span></td>
					<td><span class="td_key">手机号:</span><span class="td_val">
						<#if (posTransaction.mobile)?? && posTransaction.mobile?length == 11 >
							${posTransaction.mobile?substring(0,3)+'****'+posTransaction.mobile?substring(7)}
						<#else>
							-
						</#if>
					</span></td>
				</tr>
				<tr>
					<td><span class="td_key">内部订单:</span><span class="td_val">${(posTransaction.code)!}</span></td>
					<td><span class="td_key">外部订单:</span><span class="td_val">${(posTransaction.serial)!}</span></td>
				</tr>
				<tr >
				<#-- 
					<td><span class="td_key">商户ID:</span><span class="td_val">${(posTransaction.businessId)!}</span></td>
				-->
					<td><span class="td_key">商户类型:</span>
						<span class="td_val">
						<#if (posTransaction.businessType =="SHOP")! >
							商户
						<#elseif (posTransaction.businessType =="MERCHANT")!>
							集团
					    <#else>
					              其他
						</#if>
						</span>
					</td>
					<td/>
				</tr>
				 <tr>
					<td><span class="td_key">状态:</span>
					<span class="td_val">
					<#if (posTransaction.status =="PAID_FAIL")! >
						付款失败
					<#elseif (posTransaction.status =="UNPAID")!>
						等待付款
					<#elseif (posTransaction.status =="PAID_SUCCESS")!>
						付款成功
					<#elseif (posTransaction.status =="PAID_REFUND")!>
						退款成功
					<#elseif (posTransaction.status =="PAID_TIMEOUT")!>
						交易超时
					<#elseif (posTransaction.status =="PAID_REVOCATION")!>
						撤销成功
				    <#else>
				              其他
					</#if>
					</span></td>
					<td><span class="td_key">操作员:</span><span class="td_val">${(posTransaction.operator)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">付款账号:</span><span class="td_val ios-tel">${(posTransaction.cardNumber)!}</span></td>
					<td><span class="td_key">交易返回码:</span><span class="td_val">${(posTransaction.responseCode)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">刷卡终端号:</span><span class="td_val">${(posTransaction.terminal)!}</span></td>
					<td><span class="td_key">位置:</span><span class="td_val">${(posTransaction.location)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">交易时间:</span><span class="td_val">${(posTransaction.tradeDate?string('yyyy-MM-dd'))!}</span></td>
					<td><span class="td_key">账号:</span><span class="td_val ios-tel">${(posTransaction.gatewayAccount)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">账号类型:</span><span class="td_val">
					<#if (posTransaction.gatewayType =="UMPAY")! >
					 	联动优势
					<#elseif (posTransaction.gatewayType =="BOC")!>
						中国银行
					<#elseif (posTransaction.gatewayType =="SHENGPAY")!>
						盛付通
				    <#else>
				              其他
					</#if>
					</span></td>
			      <td><span class="td_key">签名:</span>
			     	 <span class="td_val">
			     	 	<#if posTransaction.status == 'PAID_SUCCESS' || posTransaction.status == 'PAID_REFUND' || posTransaction.status == 'PAID_REVOCATION'>
				     		<#if posTransaction.gatewayType == 'UMPAY' && posTransaction.type == 'BANK_CARD'>
								<img src="${image_base}/order/signature/${posTransaction.gatewayAccount }/${posTransaction.code}.png" class="signature">
							<#elseif posTransaction.gatewayType == 'SHENGPAY'>
								<img src="${image_base}/order/shengPay_signature/${posTransaction.gatewayAccount }/${posTransaction.code}.png" class="signature">
							</#if>
						</#if>
			     	 </span>
			      </td>
				</tr>
				<tr>
					<td><span class="td_key">交易参考号:</span><span class="td_val ios-tel">${(posTransaction.refNo)!}</span></td>
					<td><span class="td_key">交易批次号:</span><span class="td_val">${(posTransaction.batchNo)!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">交易跟踪号:</span><span class="td_val">${(posTransaction.traceNo)!}</span></td>
					<td><span class="td_key">创建时间:</span><span class="td_val">${(posTransaction.createDate?string('MM-dd HH:mm'))!}</span></td>
				</tr>
				<tr>
					<td><span class="td_key">修改时间:</span><span class="td_val">${(posTransaction.modifyDate?string('MM-dd HH:mm'))!}</span></td>
					<td><span class="td_key"></span><span class="td_val"></span></td>
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