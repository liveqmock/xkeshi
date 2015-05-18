<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected='pos_transaction' title='刷卡流水' css="seller_list_new|coupon_list" js="coupon_list|list_filter|My97DatePicker/WdatePicker" />
<style>
.dtime{
width:140px;
}
</style>
	<div class="rwrap">
		<div class="r_title"><span class="fl">刷卡统计&明细</span>
		<div class="search_wrap">
			<form action="${(base)!}/pos_transaction/bank_card/list" class="search_form fl">
				<input type="text" name="key" <#if (searcher.key)??>value="${(searcher.key)!}"</#if> placeholder="会员名称/手机号码/交易卡号" class="search_input">
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="filter_a fl"></a>
		</div>
		<#if searcher != null && searcher.hasParameter >
			<div class="search_result ofw">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if searcher.key><span class="b_tit" data-fid="1">关键字：${searcher.key}<b>x</b></span></#if>
					<#if searcher.statusSet><span class="b_tit" data-fid="2">交易状态：<#if searcher.statusSet?seq_contains("PAID_SUCCESS")>支付成功</#if>  <#if searcher.statusSet?seq_contains("PAID_FAIL")>支付失败</#if>  <#if searcher.statusSet?seq_contains("UNPAID")>未支付</#if>  <#if searcher.statusSet?seq_contains("PAID_REVOCATION")>撤销交易</#if><b>x</b></span></#if>
					<#if searcher.startDateTime><span class="b_tit ml74" data-fid="3">起始交易时间：${searcher.startDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					<#if searcher.endDateTime><span class="b_tit" data-fid="4">截止交易时间：${searcher.endDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					<#if searcher.nickName><span class="b_tit ml74" data-fid="5">商户简称：${searcher.nickName}<b>x</b></span></#if>
					</span><span class="ml12">的结果</span></span>
				</p>
				<a href="${(base)!}/pos_transaction/bank_card/list" class="s_clear">清空</a>
			</div>
		</#if>
	</div>
		<table class="tb_main">
			<tr class="th">
				<td class="sdtime" >交易时间</td>
				<td class="" >交易金额</td>
				<td class="" >交易卡号</td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="" >商户简称</td>
				</@shiro.hasAnyRoles>
				<td class="" >手机号码</td>
				<td class="" >交易状态</td>
				<td class="">操作</td>
			</tr>
			<#list pager.list as posTransaction>
			<tr <#if posTransaction_index%2==0>class="tr_bg"</#if>>
				<td class="sdtime">${(posTransaction.tradeDate?string('yyyy-MM-dd HH:mm:ss'))!(posTransaction.createDate?string('yyyy-MM-dd HH:mm:ss'))}</td>
				<td class="">${(posTransaction.sum)!}</td>
				<td class="">${(posTransaction.cardNumber)!}</td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="">${applicableShops[''+posTransaction.businessId].name}</td>
				</@shiro.hasAnyRoles>
				<td class=""><#if (posTransaction.mobile)?? && posTransaction.mobile?length == 11 >${posTransaction.mobile?substring(0,3)+'****'+posTransaction.mobile?substring(7)}</#if>
				</td>
				<td class="">
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
				</td>
				<td>
					<a href="${base}/pos_transaction/detail/${(posTransaction.eid)!}" class="b_a">查看详情</a>
				</td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base}/pos_transaction/bank_card/list" method="get" class="f_form">
			<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main pb_main_b">
				<div class="pb_item">
					<p class="pb_item_title">交易状态</p>
					<input type="checkbox" id="cb1" class="pfck fi_2" name="statusSet" value="PAID_SUCCESS" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("PAID_SUCCESS")>checked=true</#if>><label class="pflb" for="cb1">支付成功</label>
					<input type="checkbox" id="cb2" class="pfck fi_2" name="statusSet" value="PAID_FAIL" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("PAID_FAIL")>checked=true</#if>><label class="pflb" for="cb2">支付失败</label>
					<input type="checkbox" id="cb3" class="pfck fi_2" name="statusSet" value="UNPAID" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("UNPAID")>checked=true</#if>><label class="pflb" for="cb3">未支付</label>
					<input type="checkbox" id="cb4" class="pfck fi_2" name="statusSet" value="PAID_REVOCATION" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("PAID_REVOCATION")>checked=true</#if>><label class="pflb" for="cb4">撤销交易</label>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">交易日期</p>
					<input type="text" class="tcal_time pb_item_input fi_3" name="startDateTime" value="<#if (searcher.startDateTime)?? >${searcher.startDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
					<input type="text" class="tcal_time pb_item_input fi_4" name="endDateTime" value="<#if (searcher.endDateTime)?? >${searcher.endDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<div class="pb_item pb_item_b">
						<p class="pb_item_title">商户简称</p>
						<input type="text" class="pb_item_input fi_5" name="nickName" value="<#if (searcher.nickName)?? >${searcher.nickName}</#if>"/>
					</div>
				</@shiro.hasAnyRoles>
			</div>
			<button class="pb_btn pb_btn_s">确定</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
	</div>
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />




