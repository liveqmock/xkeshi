<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected='alipay_qrcode' title='扫码付流水' css="seller_list_new|coupon_list" js="coupon_list|list_filter|My97DatePicker/WdatePicker" />
<style>
.btime{
width:140px;
}
</style>
		<div class="rwrap">
		<div class="r_title"><span class="fl">扫码付统计&明细</span>
		<div class="search_wrap">
			<form action="${(base)!}/alipay_transaction/alipay_qrcode/list" class="search_form fl">
				<input type="text" name="key" <#if (key)??>value="${(key)!}"</#if> placeholder="会员名称/手机号码/支付账号" class="search_input">
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="filter_a fl"></a>
		</div>
            <#if key != null || alipayTransaction.filterParameter>
			<div class="search_result ofw">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<div>
					<#if key><span class="b_tit" data-fid="1">关键字：${key}<b>x</b></span></#if>
					<#if alipayTransaction.filterStatus><span class="b_tit" data-fid="2">交易状态：
                        <#if alipayTransaction.filterStatus?contains("SUCCESS")>支付成功</#if>  
                        <#if alipayTransaction.filterStatus?contains("FAILED")>支付失败</#if>
                        <#if alipayTransaction.filterStatus?contains("UNPAID")>未支付</#if>
                        <#if alipayTransaction.filterStatus?contains("CANCEL")>撤销交易</#if>
                        <#if alipayTransaction.filterStatus?contains("TIMEOUT")>超时</#if>
                        <#if alipayTransaction.filterStatus?contains("PARTIAL_PAYMENT")>部分支付成功</#if>
                        <#if alipayTransaction.filterStatus?contains("REFUND")>退货</#if><b>x</b></span>
                    </#if>
					<#if alipayTransaction.startDateTime><span class="b_tit" data-fid="3">起始交易时间：${alipayTransaction.startDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					<#if alipayTransaction.endDateTime><span class="b_tit" data-fid="4">截止交易时间：${alipayTransaction.endDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					<#if alipayTransaction.shopName><span class="b_tit" data-fid="5">商户简称：${alipayTransaction.shopName}<b>x</b></span></#if>
					</div>
					<span class="w_tit">的结果</span></span>
				</p>
				<a href="${(base)!}/alipay_transaction/alipay_qrcode/list" class="s_clear">清空</a>
			</div>
		</#if>
	</div>
		<table class="tb_main">
			<tr class="th">
				<td class="sbtime" >交易时间</td>
				<td class="">交易金额</td>
				<td class="">支付账号</td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="">商户简称</td>
				</@shiro.hasAnyRoles>
				<td >交易状态</td>
				<td >操作</td>
			</tr>
			<#list pager.list as alipayTransaction>
			<tr <#if alipayTransaction_index%2==0>class="tr_bg"</#if>>
				<td class="sbtime">${(alipayTransaction.tradeTime?string('yyyy-MM-dd HH:mm:ss'))!(alipayTransaction.createdTime?string('yyyy-MM-dd HH:mm:ss'))}</td>
				<td class="">${(alipayTransaction.amount)!}</td>
				<td class="">${(alipayTransaction.buyerId)!}</td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="">${(alipayTransaction.shopName)}</td>
				</@shiro.hasAnyRoles>
				<td class="">${alipayTransaction.statusName}</td>
				<td>
					<a href="${base}/alipay_transaction/detail/${(alipayTransaction.eid)!}/alipay_qrcode" class="b_a">查看详情</a>
				</td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base}/alipay_transaction/alipay_qrcode/list" method="get" class="f_form">
			<input type="hidden" name="key" value="<#if (key)?? >${key}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main pb_main_b">
				<div class="pb_item">
					<p class="pb_item_title">交易状态</p>
					<input type="checkbox" id="cb1" class="pfck fi_2" name="status" value="SUCCESS" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("SUCCESS")>checked=true</#if>><label class="pflb" for="cb1">支付成功</label>
					<input type="checkbox" id="cb2" class="pfck fi_2" name="status" value="FAILED" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("FAILED")>checked=true</#if>><label class="pflb" for="cb2">支付失败</label>
					<input type="checkbox" id="cb3" class="pfck fi_2" name="status" value="UNPAID" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("UNPAID")>checked=true</#if>><label class="pflb" for="cb3">未付款</label>
					<input type="checkbox" id="cb4" class="pfck fi_2" name="status" value="CANCEL" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("CANCEL")>checked=true</#if>><label class="pflb" for="cb4">撤销订单</label>
                    <input type="checkbox" id="cb5" class="pfck fi_2" name="status" value="TIMEOUT" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("TIMEOUT")>checked=true</#if>><label class="pflb" for="cb5">超时</label>
                    <input type="checkbox" id="cb6" class="pfck fi_2" name="status" value="PARTIAL_PAYMENT" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("PARTIAL_PAYMENT")>checked=true</#if>><label class="pflb" for="cb6">部分支付成功</label>
                    <input type="checkbox" id="cb7" class="pfck fi_2" name="status" value="REFUND" <#if alipayTransaction.filterStatus?? && alipayTransaction.filterStatus?contains("REFUND")>checked=true</#if>><label class="pflb" for="cb7">退货</label>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">交易日期</p>
					<input type="text" class="tcal_time pb_item_input fi_3" name="startDateTime" value="<#if (alipayTransaction.startDateTime)?? >${alipayTransaction.startDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
					<input type="text" class="tcal_time pb_item_input fi_4" name="endDateTime" value="<#if (alipayTransaction.endDateTime)?? >${alipayTransaction.endDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<div class="pb_item pb_item_b">
						<p class="pb_item_title">商户简称</p>
						<input type="text" class="pb_item_input fi_5" name="shopName" value="<#if (alipayTransaction.shopName)?? >${alipayTransaction.shopName}</#if>"/>
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




