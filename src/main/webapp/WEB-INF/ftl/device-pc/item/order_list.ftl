<#import "/macro.ftl" as m>
<@m.page_header css='seller_list_new|account|revenue_detail' selected='order' subselected='order' title='点单明细' js="My97DatePicker/WdatePicker|list_filter" />
<script>
$(function(){
	var re_phone=/^1\d{10}$/;
	var telephone=$('#telephone'),set_time1=$('#set_time1'),set_time2=$('#set_time2');
	$('.pb_btn').click(function(){
	   var set_tv1 = set_time1.val();
	   var set_tv2 = set_time2.val();
	   var date1 = set_tv1.substr(0, 10);
	   var date2 = set_tv2.substr(0, 10); 
	   var today = get_today_date();
	   if(telephone[0]){
			if (telephone.val()!='' && !re_phone.test(telephone.val())) {
				alert('您输入的号码有误，请修改。');
				return false;
			};
	   }
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
		<div class="r_title"><span class="fl">点单明细表</span>
			<div class="search_wrap">
				<form action="${(base)!}/order/list" class="search_form fl">
					<input type="text" name="orderNumber" <#if (searcher.orderNumber)??>value="${(searcher.orderNumber)!}"</#if> placeholder="交易号" class="search_input f_input">
					<button class="search_btn"></button>
					<#if (searcher.operatorSessionCode)?? ><input type="hidden" name="operatorSessionCode" value="${searcher.operatorSessionCode}" /></#if>
				</form>
				<a href="javascript:" class="pop_a filter_a fl" data-pop="pop_filter"></a>
			</div>
			<#if searcher != null && searcher.hasParameter >
			<div class="search_result ofw">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<div>
					<#if searcher.orderNumber><span class="b_tit" data-fid="1">交易号：${searcher.orderNumber}<b>x</b></span></#if>
					<#if searcher.status><span class="b_tit" data-fid="2">
						交易状态：
						<#if searcher.status =="SUCCESS">
							<font color="green">支付成功</font>
						<#elseif searcher.status =="FAILED">
							支付失败
						<#elseif searcher.status =="CANCEL">
							撤销订单
						<#elseif searcher.status =="PARTIAL_PAYMENT">
							部分支付
						<#elseif searcher.status =="TIMEOUT">
							支付超时
						<#elseif searcher.status =="REFUND">
							退款成功
						<#elseif searcher.status =="UNPAID">
							未支付
						</#if>
					<b>x</b></span></#if>
					<#if searcher.mobileNumber><span class="b_tit" data-fid="4">会员手机号：${searcher.mobileNumber}<b>x</b></span></#if>
					<#if searcher.typeSet><span class="b_tit" data-fid="7">支付方式：<#if searcher.typeSet?seq_contains("CASH")>现金</#if>  <#if searcher.typeSet?seq_contains("BANKCARD")>刷卡</#if>  <#if searcher.typeSet?seq_contains("PREPAID")>预付卡</#if>
					<#if searcher.typeSet?seq_contains("ALIPAY_QRCODE")>支付宝扫码</#if>  <#if searcher.typeSet?seq_contains("WXPAY_QRCODE")>微信扫码</#if><b>x</b></span></#if>
					<#if searcher.nickName><span class="b_tit" data-fid="8">消费商户：${searcher.nickName}<b>x</b></span></#if>
					<#if searcher.startDateTime><span class="b_tit" data-fid="5">起始交易时间：${searcher.startDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					<#if searcher.endDateTime><span class="b_tit" data-fid="6">截止交易时间：${searcher.endDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					</div>
					<span class="w_tit">的结果</span></span>
				</p>
				<a href="${(base)!}/order/list" class="s_clear">清空</a>
			</div>
			</#if>
		</div>
		<div class="opt_wrap">共${pager.totalCount!0}笔交易, 交易金额合计： <em class="g_num">${totalAmount!0}元</em> </div>
		<table class="tb_main ">
			<tr class="th th_cs">
				<td class="sdtime">时间</td>
				<td class="" >交易号</td>
				<td class="" >交易金额</td>
				<td class="" >支付方式</td>
				<td class="" >会员手机</td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="" >消费商户</td>
				</@shiro.hasAnyRoles>
				<td class="">交易状态</td>
				<td class="detail" style="width:60px;">操作</td>
			</tr>
			<#list pager.list as order>
			<tr <#if order_index%2==0>class="tr_bg"</#if>>
				<td class="sdtime td_cs">${(order.createDate?string('yyyy-MM-dd HH:mm:ss'))!}</td>
				<td class="td_cstd_cs">${order.orderNumber}</td>
				<td class="td_cs">
					<#if order.discount != null || order.discount != "">
						￥${((order.totalAmount*order.discount)?string("#0.00"))!'0.00'}
					<#else>
						￥${(order.totalAmount?string("#0.00"))!'0.00'}
					</#if>
				</td>
				<td class="td_cs">
				<#if order.status != "TIMEOUT" && order.status != "UNPAID" && order.status != "FAILED" && order.status != "" && order.status != null>
					<#if order.type =="CASH">现金 
						<#elseif order.type =="BANKCARD">刷卡
						<#elseif order.type =="PREPAID">预付卡
						<#elseif order.type =="ALIPAY_QRCODE">支付宝扫码
						<#elseif order.type =="WXPAY_QRCODE">微信扫码
						<#elseif order.type == "BANK_NFC_CARD">刷卡
						<#else>-
					</#if>
				<#elseif searcher.status != "UNPAID" && searcher.status != "TIMEOUT" && searcher.status != "FAILED" && searcher.status != "" && searcher.status != null>
					<#if order.type =="CASH">现金 
						<#elseif order.type =="BANKCARD">刷卡
						<#elseif order.type =="PREPAID">预付卡
						<#elseif order.type =="ALIPAY_QRCODE">支付宝扫码
						<#elseif order.type =="WXPAY_QRCODE">微信扫码
						<#elseif order.type =="BANK_NFC_CARD">刷卡
						<#else>-
					</#if>
				<#else>
					-
				</#if>
				</td>
				<td class=""><#if (order.member.mobile)?? && order.member.mobile?length == 11>${order.member.mobile?substring(0,3)+'****'+order.member.mobile?substring(7)}<#else>-</#if></td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="td_cs">${applicableShops[''+order.businessId].name}</td>
				</@shiro.hasAnyRoles>
				<td class="td_cs">
					<#if order.status =="SUCCESS"><font color="green">支付成功</font>
					<#elseif order.status =="FAILED">支付失败
					<#elseif order.status =="CANCEL">交易撤销
					<#elseif order.status =="PARTIAL_PAYMENT">部分支付
					<#elseif order.status =="TIMEOUT">交易超时
					<#elseif order.status =="REFUND">退款成功
					<#elseif order.status =="UNPAID">未支付
					<#else>-
					</#if>
				</td>
				
				<td class="detail"><a class="b_a" href="${(base)!}/order/${order.businessId}/detail/${order.orderNumber}">详情</a></td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base}/order/list" method="get" class="f_form">
			<#if (searcher.orderNumber)?? ><input type="hidden" name="orderNumber" value="${searcher.orderNumber}" class="fi_1"/></#if>
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<div class="pb_item">
					<span class="pb_item_title">交易状态</span>
					<select name="status" class="f_input pb_item_input select_style fi_2">
						<option value="" <#if ! searcher.status??>selected="selected"</#if>>全部</option>
						<option value="SUCCESS" <#if searcher.status?? && searcher.status == "SUCCESS">selected="selected"</#if>><font color="green">支付成功</font></option>
						<option value="UNPAID" <#if searcher.status?? && searcher.status == "UNPAID">selected="selected"</#if>>未支付</option>
						<option value="PARTIAL_PAYMENT" <#if searcher.status?? && searcher.status == "PARTIAL_PAYMENT">selected="selected"</#if>>部分支付</option>
						<option value="FAILED" <#if searcher.status?? && searcher.status == "FAILED">selected="FAILED"</#if>>支付失败</option>
						<option value="CANCEL" <#if searcher.status?? && searcher.status == "CANCEL">selected="selected"</#if>>撤销订单</option>
						<option value="TIMEOUT" <#if searcher.status?? && searcher.status == "TIMEOUT">selected="selected"</#if>>支付超时</option>
						<option value="REFUND" <#if searcher.status?? && searcher.status == "REFUND">selected="selected"</#if>>退款成功</option>
					</select>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">会员手机号</p>
					<input type="text" class="pb_item_input f_input fi_4" id="telephone" name="mobileNumber" value="<#if (searcher.mobileNumber)?? >${searcher.mobileNumber}</#if>"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">支付方式</p>
					<div class="item_box">
						<input type="checkbox" class="pfck f_input fi_7" id="cb1" name="typeSet" value="CASH" <#if searcher.typeSet?? && searcher.typeSet?seq_contains("CASH")>checked=true</#if>><label class="pflb" for="cb1">现金</label>
						<input type="checkbox" class="pfck f_input fi_7" id="cb2" name="typeSet" value="BANKCARD" <#if searcher.typeSet?? && searcher.typeSet?seq_contains("BANKCARD")>checked=true</#if>><label class="pflb" for="cb2">刷卡</label>
						<input type="checkbox" class="pfck f_input fi_7" id="cb3" name="typeSet" value="ALIPAY_QRCODE" <#if searcher.typeSet?? && searcher.typeSet?seq_contains("ALIPAY_QRCODE")>checked=true</#if>><label class="pflb" for="cb3">支付宝扫码</label>
						<input type="checkbox" class="pfck f_input fi_7" id="cb4" name="typeSet" value="WXPAY_QRCODE" <#if searcher.typeSet?? && searcher.typeSet?seq_contains("WXPAY_QRCODE")>checked=true</#if>><label class="pflb" for="cb4">微信扫码</label>
						<input type="checkbox" class="pfck f_input fi_7" id="cb5" name="typeSet" value="PREPAID" <#if searcher.typeSet?? && searcher.typeSet?seq_contains("PREPAID")>checked=true</#if>><label class="pflb" for="cb5">预付卡</label> 
					</div> 
				</div>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<div class="pb_item">
						<p class="pb_item_title">消费商户</p>
						<input type="text" class="pb_item_input f_input fi_8" name="nickName" value="<#if (searcher.nickName)?? >${searcher.nickName}</#if>" id="fi_8"/>
					</div>
				</@shiro.hasAnyRoles>
				<div class="pb_item">
					<p class="pb_item_title">交易时间</p>
					<input type="text" autocomplete="off" class="tcal_time pb_item_input f_input fi_5" id="set_time1" name="startDateTime" value="<#if (searcher.startDateTime)?? >${searcher.startDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/><em class="pb_item_title">至</em>
					<input type="text" autocomplete="off" class="tcal_time pb_item_input f_input fi_6" id="set_time2" name="endDateTime" value="<#if (searcher.endDateTime)?? >${searcher.endDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
				<#if (searcher.operatorSessionCode)?? ><input type="hidden" name="operatorSessionCode" value="${searcher.operatorSessionCode}" class="fi_7"/></#if>
			</div>
			<button class="pb_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
	</div>
</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />