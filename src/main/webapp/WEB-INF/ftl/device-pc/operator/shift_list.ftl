<#import "/macro.ftl" as m >
<@m.page_header selected='order' title='交接班'  subselected='shift'  css="seller_list_new|merchant_list"  js ="My97DatePicker/WdatePicker|list_filter" />
<div class="rwrap">
    <div class="r_title" style="overflow:hidden;">
        <span class="fl">交接班记录</span>
        <div class="search_wrap">
            <form class="search_form f_form fl">
                <input type="text" name="key" value="${(searcher.key)!''}" placeholder="收银员" class="search_input" id="fi_1">
                <button class="search_btn"></button>
            </form>
            <a href="javascript:" class="pop_a filter_a fl" data-pop="pop_filter"></a>
        </div>
<#if (searcher.shopId||searcher.startTime||searcher.endTime||searcher.key)>
        <div class="search_result">
            <p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
            <#if searcher.key><span class="b_tit">收银员：${(searcher.key)!}
                <a href="${(base)!}/shift/list?shopId=${(searcher.shopId)!}&startTime=${(searcher.startTime)!}&endTime=${(searcher.endTime)!}">
                <b>x</b></a></span></#if>
            <#if searcher.shopId>
                <span class="b_tit">
                <#list shops as shop>
                    <#if shop.id == searcher.shopId>
                    商户：${(shop.name)!}
                        <#break>
                    </#if>
                </#list>
                    <a href="${(base)!}/shift/list?startTime=${(searcher.startTime)!}&endTime=${(searcher.endTime)!}&key=${(searcher.key)!}">
                        <b>x</b></a></span>
            </#if>
            <#if searcher.startTime><span class="b_tit">时间范围起：${(searcher.startTime)!} <a href="${(base)!}/shift/list?shopId=${(searcher.shopId)!}&endTime=${(searcher.endTime)!}&key=${(searcher.key)!}">
                <b>x</b></a></span></#if>
            <#if searcher.endTime><span class="b_tit">时间范围止：${(searcher.endTime!)} <a href="${(base)!}/shift/list?shopId=${(searcher.shopId)!}&startTime=${(searcher.startTime)!}&key=${(searcher.key)!}">
                <b>x</b></a></span></#if>
                </span>的结果</span>
            </p>
            <a href="${(base)!}/shift/list" class="s_clear">清空</a>
        </div>
    </#if>
	</div>	
	
	<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter)>
			<div class="new_hint_wrap">暂时未找到交接班数据</div>
	<#else>
	<table class="tb_main">
			<tr class="th">
				<td class="dbtime">时间</td>
				<td class="all_num" >总单数</td>
				<td class="sell_num">销售量</td>
				<td class="full_sum" >订单总金额</td>
				<td class="coupon">核销实体券</td>
				<td class="recive_amount">应收现金</td>
				<td class="paid_amount">实收现金</td>
				<td class="cash_def">现金差异额</td>
				<td class="cashier" >收银员</td>
				<td class="seller" >商户</td>
			</tr>
		<#list pager.list as operatorShiftVO>
			<tr <#if operatorShiftVO_index%2==0>class="tr_bg"</#if> >
				<td class="dbtime">
					<a href="/shift/detail/${(operatorShiftVO.operatorSessionCode)!}" class="b_a">
						${(operatorShiftVO.shiftedStartTime?string('yyyy-MM-dd HH:mm:ss'))!}-<br>
						${(operatorShiftVO.shiftedEndTime?string('yyyy-MM-dd HH:mm:ss'))!}
					</a>
				</td>
				<td class="all_num">${(operatorShiftVO.totalOrderCount)!'0'}</td>
				<td class="sell_num">${(operatorShiftVO.totalOrderItemCount)!'0'}</td>
				<td class="full_sum">￥${(operatorShiftVO.totalOrderAmount?string('0.00'))!'0.00'}</td>
				<#-- 核销实体券 -->
				<td class="recive_amount">￥${(operatorShiftVO.totalPhysicalCouponAmount?string('0.00'))!'0.00'}</td>
				<td class="paid_amount">￥${(operatorShiftVO.totalReceivableAmount?string('0.00'))!'0.00'}</td>
				<#-- 应收金额 -->
				<td class="cou">￥${(operatorShiftVO.totalActuallyAmount?string('0.00'))!'0.00'}</td>
				<#-- 差异金额  -->
				<td class="cash_def">￥${(operatorShiftVO.totalDifferenceCashAmount?string('0.00'))!'0.00'}</td>
				<td class="cashier">${(operatorShiftVO.operatorRealName)!'-'}</td>
				<td class="seller" >${(operatorShiftVO.shopName)!'-'}</td>
			</tr>
		</#list>
	</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</#if>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
	<div class="pb pop_filter">
			<form action="/shift/list" class="f_form">
			<div class="pb_title">筛选</div>
			<div class="pb_main">
			<#if shops??>
				<div class="pb_item">
						<p class="pb_item_title">商户</p>
						<select name="shopId" class="sel_rel_merchant pb_item_input f_input fi_2" style="width: 198px;margin-left:0;" >
							<option value="">---全部商户---</option>
							<#list shops as shop>
							       <#if shop.enableShift == true ><option class="" value="${(shop.id)!}">${(shop.name)!}</option> </#if>
							</#list>
						</select>
				</div>
			</#if>
				<div class="pb_item">
					<p class="pb_item_title">选择时间范围</p>
					<input type="text"  value="${(searcher.startTime)!}" class="tcal pb_input pb_input0" name= "startTime" id="set_time1">
					-
					<input type="text" value="${(searcher.endTime)!}" class="tcal pb_input pb_input0" name= "endTime" id="set_time2">
				</div>				
			</div>
			<button type="submit" class="pb_btn pb_btn_s">确定</button>
			<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
</div>

</div>
<@m.page_footer />




