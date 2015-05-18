<#import "/macro.ftl" as m>
<@m.page_header selected='order' subselected='physical_coupon_order' title='实体券核销明细' js='member_type_info|list_filter|My97DatePicker/WdatePicker' css='seller_list_new|set_mime|voucher_manage|voucher_detail'/>
<script>
$(function(){
	var set_time1=$('#set_time1'),set_time2=$('#set_time2');
	$('.pb_btn').click(function(){
	   var set_tv1 = set_time1.val();
	   var set_tv2 = set_time2.val();
	   var date1 = set_tv1.substr(0, 10);
	   var date2 = set_tv2.substr(0, 10);
	   var today = get_today_date();
		if (date1 && date2 && date1>date2) {
			alert('起始时间不能超出截止时间');
			return false;
		};
		if(date2>today){
			alert('所选日期不能超出当天');
			return false;
		}
		$('form').submit();
	})
})
</script>
<div class="rwrap">
	<div class="r_title"><span class="fl">实体券核销明细</span>
	<div class="search_wrap">
	<form class="search_form f_form fl" action="${base}/physical_coupon/used/list">
			<input type="text" name="orderNumber" <#if (searcher.orderNumber)?? >value="${searcher.orderNumber}"</#if> placeholder="订单号" class="search_input" id="fi_1">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:" class="pop_a filter_a" data-pop="pop_filter"></a>
	</div>
		<#if searcher != null && searcher.hasParameter>
			<div class="search_result ofw">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<div>
					<#if searcher.orderNumber><span class="b_tit" data-fid="1">订单号：${searcher.orderNumber}<b>x</b></span></#if>
					<#if searcher.shopIds>
						<span class="b_tit" data-fid="2">商户:
							<#list shopResourceList as shop>
								<#if  (searcher.shopIds[0])! == shop.id  >${shop.name}</#if>
							</#list>
						<b>x</b></span>
					</#if>
					<#if searcher.status><span class="b_tit" data-fid="3">订单状态：
					<#if searcher.status=="SUCCESS">支付成功</#if>
					<#if searcher.status=="FAILED">支付失败</#if>
					<#if searcher.status=="CANCEL">交易撤销</#if>
					<#if searcher.status=="UNPAID">未支付</#if>
					<#if searcher.status=="TIMEOUT">交易超时</#if>
					<#if searcher.status=="PARTIAL_PAYMENT">部分支付</#if>
					<#if searcher.status=="PARTIAL_REFUND">部分退款</#if>
					<#if searcher.status=="REFUND">交易退款</#if><b>x</b></span></#if>
					<#if searcher.startTime><span class="b_tit" data-fid="6">起始核销时间：${(searcher.startTime)!}<b>x</b></span></#if>
					<#if searcher.endTime><span class="b_tit" data-fid="7">截止核销时间：${(searcher.endTime)!}<b>x</b></span></#if>
					<#if searcher.operatorName><span class="b_tit" data-fid="4">收银员：${(searcher.operatorName)!}<b>x</b></span></#if>
					<#if searcher.name><span class="b_tit" data-fid="5">实体券名称：${(searcher.name)!}<b>x</b></span></#if>
					</div>
					<span class="w_tit">的结果</span></span>
				</p>
				<a href="${base}/physical_coupon/used/list" class="s_clear">清空</a>
			</div>
		</#if>
	</div>
	<div class="opt_wrap">共核销 <em class="g_num">${(orderCount)!}</em> 笔，核销张数 <em class="g_num">${(usedCount)!}</em>，核销金额合计 <em class="g_num">${(totalAmount)!}元</em></div>
	<table class="tb_main">
		<tr class="th">
			<td class="sdtime">核销时间</td>
			<td class="act_name">实体券名称</td>
			<td class="value">面额（元）</td>
			<td class="order_num">订单号</td>
			<td class="order_sta">订单状态</td>
			<td class="cashier">收银员</td>
			<td class="sell_merchant">核销商户</td>
		</tr>
			<#list pager.list as physicalCouponWriteOffVO>
			<tr <#if physicalCouponWriteOffVO_index%2==0>class="tr_bg"</#if>>
				<td class="sdtime">${(physicalCouponWriteOffVO.writeOffTime?string('yyyy-MM-dd HH:mm:ss'))!}</td>
				<td class="act_name">${(physicalCouponWriteOffVO.physicalCouponName)!}</td>
				<td class="value">${(physicalCouponWriteOffVO.amount)!}</td>
				<td class="order_num">${(physicalCouponWriteOffVO.orderNumber)!}</td>
				<td class="order_sta">
					<#if physicalCouponWriteOffVO.status =="SUCCESS">支付成功
						<#elseif physicalCouponWriteOffVO.status =="FAILED">支付失败
						<#elseif physicalCouponWriteOffVO.status =="CANCEL">交易撤销
						<#elseif physicalCouponWriteOffVO.status =="PARTIAL_PAYMENT">部分支付
						<#elseif physicalCouponWriteOffVO.status =="TIMEOUT">交易超时
						<#elseif physicalCouponWriteOffVO.status =="REFUND">交易退款
						<#elseif physicalCouponWriteOffVO.status =="UNPAID">未支付
						<#elseif physicalCouponWriteOffVO.status =="PARTIAL_REFUND">部分退款
					</#if>
				</td>
				<td class="cashier">${(physicalCouponWriteOffVO.operatorName)!}</td>
				<td class="sell_merchant blue">${(physicalCouponWriteOffVO.shopName)!}</td>
			</tr>
		</#list>
	</table>
	<div class="pb pop_filter">
			<form action="${base}/physical_coupon/used/list"  method="get" class="f_form">
			<input type="hidden" name="orderNumber" value="<#if (searcher.orderNumber)?? >${searcher.orderNumber}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main">
			<#if shopResourceList>
				<div class="pb_item">
					<p class="pb_item_title">商户</p>
					<select class="select-1 pb_sel fi_2" name="shopIds">
						<option value="">请选择...</option>
							<#list shopResourceList as shop>
								<option value="${shop.id}"  <#if (searcher.shopIds[0])! == shop.id >selected="selected"</#if>  >${shop.name}</option>
							</#list>
					</select>
				</div>
			</#if>
				<div class="pb_item">
					<p class="pb_item_title">订单状态</p>
					<select class="pb_item_i pb_sel fi_3" name="status">
						<option value="">请选择...</option>
						<option value="SUCCESS"  <#if (searcher.status)! == 'SUCCESS' >selected="selected"</#if>  >支付成功</option>
						<option value="FAILED"  <#if (searcher.status)! == 'FAILED' >selected="selected"</#if>  >支付失败</option>
						<option value="CANCEL"  <#if (searcher.status)! == 'CANCEL' >selected="selected"</#if>  >交易撤销</option>
						<option value="REFUND"  <#if (searcher.status)! == 'REFUND' >selected="selected"</#if>  >交易退款</option>
						<option value="UNPAID"  <#if (searcher.status)! == 'UNPAID' >selected="selected"</#if>  >未支付</option>
						<option value="TIMEOUT"  <#if (searcher.status)! == 'TIMEOUT' >selected="selected"</#if>  >交易超时</option>
						<option value="PARTIAL_REFUND"  <#if (searcher.status)! == 'PARTIAL_REFUND' >selected="selected"</#if>  >部分退款</option>
						<option value="PARTIAL_PAYMENT"  <#if (searcher.status)! == 'PARTIAL_PAYMENT' >selected="selected"</#if>  >部分支付</option>
					</select>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">收银员</p>
					<input type="text" name="operatorName" class="pb_item_input f_input fi_4" value="${(searcher.operatorName)!}"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">实体券名称</p>
					<input type="text" name="name" class="pb_item_input f_input fi_5" value="${(searcher.name)!}"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">核销时间</p>
					<input type="text" name="startTime" value="${(searcher.startTime)!}" class="tcal pb_input fi_6" id="set_time1" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
					<p class="pb_con">至</p>
					<input type="text" name="endTime" value="${(searcher.endTime)!}" class="tcal pb_input fi_7" id="set_time2" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
			</div>
			<button type="submit" class="pb_btn pb_btn_s">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />
		</div>
</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />