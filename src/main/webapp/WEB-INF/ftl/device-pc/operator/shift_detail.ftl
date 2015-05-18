<#import "/macro.ftl" as m >
<@m.page_header selected='order' title='交接班'  subselected='shift'  css="seller_list_new|merchant_detail"  js ="My97DatePicker/WdatePicker|list_filter" />
<style type="text/css">.pb_item{border-top:none;}.pb_main{padding: 2px 0 0 0;}</style>
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;"><a href="/shift/list" class="back_a" title="返回"></a>交接班记录详情
	</div>	
	<div class="tb_wrap">
		<div class="tb_title">商铺：${(operatorShiftVO.shopName)!}</div>
		<table class="tb_main">
			<tbody>
				<tr class="tr_1">
					<td class="border">
						<span class="td_key">收银员</span>
						<span class="td_val">${(sumInfoVO.shiftInfo.operatorName)!''}</span>
					</td>
					<td class="border">
						<span class="td_key">时间</span>
						<span class="td_val"> 
						${(sumInfoVO.shiftInfo.startTime)!} -
						${(sumInfoVO.shiftInfo.endTime)!}
						</span>
					</td>
				</tr>
				<tr class="tr_bg tr_1">
					<td colspan="2" class="border">
					    <span class="td_key">总单数</span>
					    <span class="td_val">
					    	<a href="/order/list?operatorSessionCode=${(operatorShiftVO.operatorSessionCode)!}&startDateTime=${(sumInfoVO.shiftInfo.startTime)!}&endDateTime=${(sumInfoVO.shiftInfo.endTime)!}&status=SUCCESS" class="b_a">${(sumInfoVO.totalOrderCount)!'0'}</a>
					    </span> 
				    </td>
				</tr>
				<tr class="tr_1">
					<td class="border">
						<span class="td_key">商品销售<br/>总量</span>
						<span class="td_val">${(operatorShiftVO.totalOrderItemCount)!'0'}</span>
					</td>
					<td class="border">
						<span class="td_key">订单总金额</span>
						<span class="td_val">￥${(sumInfoVO.totalOrderAmount?string('0.00'))!'0.00'}</span>
					</td>
				</tr>

                <tr class="tr_bg tr_1">
                    <td colspan="2" class="border">
                        <span class="td_key">充值次数</span>
					    <span class="td_val">
					    	<a href="/prepaid/charge/list?beginTime=${(sumInfoVO.shiftInfo.startTime)!}&endTime=${(sumInfoVO.shiftInfo.endTime)!}" class="b_a">${(operatorShiftVO.prepaidcardRechargeAmountCount)!'0'}</a>
					    </span>
                    </td>
                </tr>
                <tr class="tr_1">
                    <td class="border">
                        <span class="td_key">实充总额</span>
                        <span class="td_val">${(operatorShiftVO.prepaidcardtotalRealityRechargeAmount)!'0'}</span>
                    </td>
                    <td class="border">
                        <span class="td_key">赠送金额</span>
                        <span class="td_val">￥${(operatorShiftVO.prepaidcardTotalPresentedAmount?string('0.00'))!'0.00'}</span>
                    </td>
                </tr>

				<#assign count = sumInfoVO.orderPreferential.orderPhysicalCouponList?size > 
				<#assign index = 1 > 
				<#list sumInfoVO.orderPreferential.orderPhysicalCouponList as orderPhysicalCoupon>
					<#if orderPhysicalCoupon_index%2==0>
						<#assign index = index+1 > 
						<tr class="<#if index%2==0>tr_bg</#if>  tr_1" >
							<td class="border">
								<span class="td_key">${(orderPhysicalCoupon.name)!''}<br/>总金额</span>
								<span class="td_val">￥${(orderPhysicalCoupon.totalAmount?string('0.00'))!'0.00'}</span>
							</td>
							<#if (orderPhysicalCoupon_index+1) < count >
							<td class="border">
								<span class="td_key">${(sumInfoVO.orderPreferential.orderPhysicalCouponList[orderPhysicalCoupon_index+1].name)!''}<br/>总金额</span>
								<span class="td_val">￥${(sumInfoVO.orderPreferential.orderPhysicalCouponList[orderPhysicalCoupon_index+1].totalAmount?string('0.00'))!'0.00'}</span>
							</td>
							</#if>
						</tr>
					</#if>
				</#list>
				
				<tr class="tr_1">
					<td class="border">
						<span class="td_key">应收现金</span>
						<span class="td_val">￥${(sumInfoVO.totalReceivableCashAmount?string('0.00'))!'0.00'}</span>
					</td>
					<td class="border">
						<span class="td_key">实收现金</span>
						<span class="td_val">￥${(operatorShiftVO.totalActuallyAmount?string('0.00'))!'0.00'}</span>
					</td>
				</tr>
				<tr class="tr_bg tr_1">
					<td colspan="2" class="border">
						<span class="td_key">现金差异额</span>
						<span class="td_val">￥${(operatorShiftVO.totalDifferenceCashAmount?string('0.00'))!'0.00'}</span>
					</td>
				</tr>
			</tbody>
		</table>
		<div class="tb_title">销售商品汇总</div>
		<table class="tb_main">
			<tr class="th">
			    <td class="pro_num">序号</td>
			    <td class="pro">商品</td>
			    <td class="pri">单价</td>
			    <td class="sell_num">销量</td>
			    <td class="sell_mon">金额</td>
		    </tr>
		 <#list pager.list as  shiftItemVO>
		    <tr class="<#if shiftItemVO_index%2==0>tr_bg</#if> ">
			    <td class="pro_num">${(shiftItemVO_index+1)!'0'}</td>
			    <td class="pro">${(shiftItemVO.name)!''}</td>
			    <td class="pri">￥${(shiftItemVO.price?string('0.00'))!'0.00'}</td>
			    <td class="sell_num">${(shiftItemVO.quantity)!'0'}</td>
			    <td class="sell_mon">￥${(shiftItemVO.totalAmount?string('0.00'))!'0.00'}</td>
		    </tr>
		    </#list>  
		</table>
		<#if !pager.list?? || pager.list?size==0>
			<div class="new_hint_wrap">暂时未找到销售商品数据</div>
		<#else>
			<div class="page_wrap">
				<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
			</div>
		</#if>
	</div>
</div>
<@m.page_footer />




