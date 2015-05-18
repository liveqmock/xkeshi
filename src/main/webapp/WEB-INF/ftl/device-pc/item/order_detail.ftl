<#import "/macro.ftl" as m>
<@m.page_header selected='order' subselected="order" css="seller_info|coupon_detail|order_detail" title="点单详情"/>
<div class="rwrap">
	<p class="r_title"><a class="back_a" href="${base}/order/list"></a>点单详情</p>
	<div class="tb_wrap" style="border-top:1px solid #EBEBEB;margin-top:14px;">
		<div class="tb_title">基本信息</div>
		<table class="tb_main" style="margin-top:0px;">
			<tbody>
				<tr>
					<td style="width:50%;"><span class="td_key font_Blank">消费商户</span><span class="td_val">${(shop.name)!''}</span></td>
					<td><span class="td_key font_Blank">收银员</span><span class="td_val coupon_id"><#if (order.operator)??>${order.operator.username} / ${order.operator.realName}</#if></span></td>
				</tr>
				<tr class="tr_bg">
					<td style="width:50%;"><span class="td_key font_Blank">创建时间</span><span class="td_val">${(order.createDate?string('yyyy/MM/dd HH:mm:ss'))!}</span></td>
					<td><span class="td_key font_Blank">订单号</span><span class="td_val ios-tel">${(order.orderNumber)!}</span></td>
				</tr>
				<tr class="">
					<td style="width:50%;"><span class="td_key font_Blank">订单状态</span><span class="td_val">
						<#if order.status =="SUCCESS"> <font color="green">支付成功</font>
						<#elseif order.status =="FAILED"> 支付失败
						<#elseif order.status =="CANCEL"> 撤销订单
						<#elseif order.status =="PARTIAL_PAYMENT"> 部分支付
						<#elseif order.status =="TIMEOUT"> 支付超时
						<#elseif order.status =="REFUND"> 交易退货
						<#elseif order.status =="UNPAID"> 未支付
						</#if>
					</span>
					</td>
					<td><span class="td_key font_Blank">会员手机</span><span class="td_val"><#if (order.member.mobile)?? && order.member.mobile?length == 11>${order.member.mobile?substring(0,3)+'****'+order.member.mobile?substring(7)}<#else>-</#if></span></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="tb_wrap" style="border-top:1px solid #EBEBEB;margin-top:14px;">
		
		<div class="tb_title" style="text-align:center;font-size:16px;padding:20px 0px;">订单金额 
			<#if order.discount != null || order.discount != "">
				￥${((order.totalAmount*order.discount)?string("#0.00"))!'0.00'} &nbsp;
			<#else>
				￥${(order.totalAmount?string("#0.00"))!'0.00'} &nbsp;
			</#if>
			<#if order.discount != null || order.discount !="">
				<#if order.discount == 1>
					(会员折扣10折)
				<#else>
					(会员折扣${order.discount*10}折)
				</#if>
				
			</#if>
		</div>
		<div class="tb_title" style="border-top:1px solid #EBEBEB;color:#516372;">
			<span class="td_key font_Blank" style="margin-right:11px;">实收金额</span>
			<span class="td_val coupon_id">
				<#if order.status =="UNPAID" || order.status=="FAILED" || order.status=="TIMEOUT">
					￥0.00 
				<#else>
					￥${actualTotalPaid}
				</#if>
			</span>
		</div>
		<table class="tb_main" style="margin-top:0px;">
			<tbody>
				<#list paidList as paidTransaction>
					<#if paidList?size==1>
						<td style="width:50%;">
							<span class="td_key font_Blank wd90">${paidTransaction.transactionTypeDesc}收银</span>
							<span class="td_val">￥${(paidTransaction.amount)!'0.00'}</span>
						</td>
						<td style="width:50%;"></td>
					<#else>
						<#if paidTransaction_index % 2 == 0>
							<tr <#if  (((paidTransaction_index)/2)?int)%2==1>class="tr_bg"</#if>>
						</#if>
								<td style="width:50%;">
									<span class="td_key font_Blank wd90">${paidTransaction.transactionTypeDesc}收银</span>
									<span class="td_val">￥${(paidTransaction.amount)!'0.00'}</span>
								</td>
						<#if paidTransaction_index % 2 == 1>
							</tr>
						</#if>
					</#if>
				</#list>
			</tbody>
		</table>
		<div class="tb_title" style="color:#516372;">
			<span class="td_key font_Blank" style="margin-right:11px;">优惠金额</span>
			<span class="td_val coupon_id">￥${phyAmount}</span>
		</div>
		<table class="tb_main" style="margin-top:0px;">
			<tbody>
				<#list phyCouponOrderList as pcoList>
					<#if pcoList_index % 2 == 0 >
						<tr>
							<td style="width:50%;">
								<#if pcoList_index==0>
									<span class="td_key font_Blank">实体券</span>
								<#else>
									<span class="td_key font_Blank">&nbsp;&nbsp;&nbsp;</span>
								</#if>
								<span class="td_val">${pcoList.physicalCouponName}*${pcoList.counts}</span>
							</td>
							<td>
							<#if pcoList_index==0>	
								<span class="td_key font_Blank">电子券</td>
							<#else>
								<span class="td_key font_Blank">&nbsp;&nbsp;&nbsp;</span>
							</#if>
							<span class="td_val"></span>
						</tr>
					<#else>
						<tr class="tr_bg">
							<td style="width:50%;">
								<#if pcoList_index==0>
									<span class="td_key font_Blank">实体券</span>
								<#else>
									<span class="td_key font_Blank">&nbsp;&nbsp;&nbsp;</span>
								</#if>
								<span class="td_val">${pcoList.physicalCouponName}*${pcoList.counts}</span>
							</td>
							<td><span class="td_key font_Blank"></td>
						</tr>
					</#if>
				</#list>
			</tbody>
		</table>
	</div>
	<div class="tb_wrap tb_other" style="border-top:none;">
		<table class="tb_main">
			<tbody><tr class="th">
				<td class="name">商品名称</td>
				<td>数量</td>
				<td>单价</td>
				<td>优惠(折扣)</td>
			</tr>
			<#list order.items as orderItem>
			<tr class="tr_bg">
				<td class="name names"><img src="${image_base}${(orderItem.item.cover)!}!100" ><span class="pro_name">${(orderItem.itemName)!}</span></td>
				<td>${(orderItem.quantity)!}</td>
				<td>￥${(orderItem.price)!'0.00'!}</td>
				<td class="state">
					<#if (order.discount)?? && (order.discount) != 10>${order.discount*10}折<#else>-</#if>
				</td>
			</tr>
			</#list>
		</tbody></table>
	</div>
</div>
<@m.page_footer />