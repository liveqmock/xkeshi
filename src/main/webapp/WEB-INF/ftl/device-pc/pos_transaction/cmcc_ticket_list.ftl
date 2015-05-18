<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected='cmcc_ticket' title='电子券流水' css="seller_list_new|coupon_list|cmcc_data" js="coupon_list|list_filter|My97DatePicker/WdatePicker" />
	<div class="rwrap">
		<div class="r_title">电子券统计&明细
			<div class="search_wrap">
				<form action="${(base)!}/pos_transaction/cmcc_ticket/list" class="search_form">
					<input type="text" name="key" <#if (searcher.key)??>value="${(searcher.key)!}"</#if> placeholder="订单编号" class="search_input">
					<button class="search_btn"></button>
				</form>
				<a href="javascript:" class="filter_a"></a>
			</div>
			<#if searcher != null && searcher.hasParameter >
				<div class="search_result">
					<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
						<#if searcher.key><span class="b_tit" data-fid="1">订单编号：${searcher.key}<b>x</b></span></#if>
						<#if searcher.startDate><span class="b_tit" data-fid="2">起始交易时间：${searcher.startDate?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
						<#if searcher.endDate><span class="b_tit" data-fid="3">截止交易时间：${searcher.endDate?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
						<#if searcher.statusSet><span class="b_tit" data-fid="4">交易状态：<#if searcher.statusSet?seq_contains("PAID_SUCCESS")>支付成功</#if>  <#if searcher.statusSet?seq_contains("PAID_FAIL")>支付失败</#if>  <#if searcher.statusSet?seq_contains("UNPAID")>未支付</#if>  <#if searcher.statusSet?seq_contains("PAID_REVOCATION")>撤销交易</#if><b>x</b></span></#if>
						<#if searcher.mobile><span class="b_tit" data-fid="5">手机尾号：${searcher.mobile}<b>x</b></span></#if>
						<#if searcher.minSum><span class="b_tit" data-fid="6">最小金额：${searcher.minSum}<b>x</b></span></#if>
						<#if searcher.maxSum><span class="b_tit" data-fid="7">最大金额：${searcher.maxSum}<b>x</b></span></#if>
						</span>的结果</span>
					</p>
					<a href="${(base)!}/pos_transaction/cmcc_ticket/list" class="s_clear">清空</a>
				</div>
			</#if>
		</div>
		<div class="top_data">共<em>${pager.totalCount!0}</em>笔交易，支付成功<em>${success_count!0}</em>笔/金额<em>${success_amount!0}</em>元，支付失败<em>${fail_count!0}</em>笔/金额<em>${fail_amount!0}</em>元，未支付<em>${unpaid_count!0}</em>笔/金额<em>${unpaid_amount!0}</em>元，撤销交易<em>${revocation_count!0}</em>笔/金额<em>${revocation_amount!0}</em>元</div>
		<div class="data_wrap">
			<div class="data_l">
				<span>成功支付<em>${success_count!0}笔</em></span><span class="data_split">/</span><span>金额<em>${success_amount!0}元</em></span>
			</div>
			<div class="data_l">
				<span>失败支付<em>${fail_count!0}笔</em></span><span class="data_split">/</span><span>金额<em>${fail_amount!0}元</em></span>
			</div>
			<div class="data_l">
				<span>未支付<em>${unpaid_count!0}笔</em></span><span class="data_split">/</span><span>金额<em>${unpaid_amount!0}元</em></span>
			</div>
			<div class="data_l">
				<span>撤销交易<em>${revocation_count!0}笔</em></span><span class="data_split">/</span><span>金额<em>${revocation_amount!0}元</em></span>
			</div>
			<div class="data_r">
				<span>共<em>${pager.totalCount!0}笔</em>交易</span>
			</div>			
		</div>
		<table class="tb_main">
			<tr class="th">
				<td class="time">交易时间</td>
				<td class="addr">订单编号</td>
				<td class="status">订单状态</td>
				<td class="mobile" style="width:100px;">客户手机号码</td>
				<td class="name">支付金额</td>
			</tr>
			<#list pager.list as posTransaction>
			<tr <#if posTransaction_index%2==0>class="tr_bg"</#if>>
				<td class="time">${(posTransaction.tradeDate?string('yyyy-MM-dd HH:mm:ss'))!}</td>
				<td class="addr">${(posTransaction.code)!}</td>
				<td class="status">
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
				<td class="mobile"><#if (posTransaction.mobile)?? >${posTransaction.mobile?substring(0,3)+'****'+posTransaction.mobile?substring(7)}</#if>
				</td>
				<td class="name">${(posTransaction.sum)!}</td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base}/pos_transaction/cmcc_ticket/list" method="get" class="f_form">
			<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main pb_main_b">
				<div class="pb_item">
					<p class="pb_item_title">交易日期</p>
					<input type="text" class="tcal_time pb_item_input fi_2" name="startDate" value="<#if (searcher.startDate)?? >${searcher.startDate?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
					<input type="text" class="tcal_time pb_item_input fi_3" name="endDate" value="<#if (searcher.endDate)?? >${searcher.endDate?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">订单状态</p>
					<input type="checkbox" id="cb1" class="pfck fi_4" name="statusSet" value="PAID_SUCCESS" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("PAID_SUCCESS")>checked=true</#if>><label class="pflb" for="cb1">支付成功</label>
					<input type="checkbox" id="cb2" class="pfck fi_4" name="statusSet" value="PAID_FAIL" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("PAID_FAIL")>checked=true</#if>><label class="pflb" for="cb2">支付失败</label>
					<input type="checkbox" id="cb3" class="pfck fi_4" name="statusSet" value="UNPAID" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("UNPAID")>checked=true</#if>><label class="pflb" for="cb3">未支付</label>
					<input type="checkbox" id="cb4" class="pfck fi_4" name="statusSet" value="PAID_REVOCATION" <#if searcher.statusSet?? && searcher.statusSet?seq_contains("PAID_REVOCATION")>checked=true</#if>><label class="pflb" for="cb4">撤销交易</label>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">客户手机尾号</p>
					<input type="text" class="pb_item_input fi_5" name="mobile" value="<#if (searcher.mobile)?? >${searcher.mobile}</#if>"/>
				</div>
				<div class="pb_item pb_item_b">
					<p class="pb_item_title">支付金额范围</p>
					<input type="text" class="pb_item_input yuan_input fi_6" name="minSum" value="<#if (searcher.minSum)?? >${searcher.minSum}</#if>"/>元
					<input type="text" class="pb_item_input yuan_input fi_7" name="maxSum" value="<#if (searcher.maxSum)?? >${searcher.maxSum}</#if>"/>元
				</div>
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




