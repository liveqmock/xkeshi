<#import "/macro.ftl" as m>
<@m.page_header title="电子券&套票退款" selected='coupon' subselected="refundList" css="seller_info|coupon_back" js="page_template|msg_reset"/>
<script>
	var CONTEXT="${base}";
</script>
<div class="rwrap">
	<div class="r_title">电子券&套票退款
		<#--
		<div class="search_wrap">
			<form class="search_form" action="${base}/refund/list">
				<input type="text" name="key" <#if (searcher.key)?? >value="${searcher.key}"</#if> placeholder="电子券名称" class="search_input"/>
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="filter_a"></a>
		</div>
		<#if searcher != null && searcher.hasParameter >
		<div class="search_result">
			<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
				<#if searcher.key><span class="b_tit" data-fid="1">电子券名称：${searcher.key}<b>x</b></span></#if>
				<#if searcher.serial><span class="b_tit" data-fid="2">电子券编号：${searcher.serial}<b>x</b></span></#if>
				<#if searcher.status??><span class="b_tit" data-fid="3">状态：<#if searcher.status?contains("PUBLISHED_VISIBLE")>已发布(公开)</#if>  <#if searcher.status?contains("PUBLISHED_UNVISIBLE")>已发布(隐藏)</#if>  <#if searcher.status?contains("UNPUBLISHED")>未发布</#if><b>x</b></span></#if>
				</span>的结果</span>
			</p>
			<a href="${(base)!}/refund/list" class="s_clear">清空</a>
		</div>
		</#if>
		-->
	</div>
	<div class="tb_wrap tb_other">

		<div class="tb_title hiline">
			<div class="div_top">
				<div class="check_li_title">显示：</div>
				<div class="check_li_wrap">
					<input type="checkbox" id="state_cb1" class="check_li" name="status" value="APPLY" <#if searcher.status?? && searcher.status?contains("APPLY")>checked=true</#if>/><label for="state_cb1">审核中</label>
					<input type="checkbox" id="state_cb2" class="check_li" name="status" value="AUTO_EXECUTE" <#if searcher.status?? && searcher.status?contains("AUTO_EXECUTE")>checked=true</#if>/><label for="state_cb2">自动退款中</label>
					<input type="checkbox" id="state_cb3" class="check_li" name="status" value="MANUAL_EXECUTE" <#if searcher.status?? && searcher.status?contains("MANUAL_EXECUTE")>checked=true</#if>/><label for="state_cb3">人工退款中</label>
					<input type="checkbox" id="state_cb4" class="check_li" name="status" value="AUTO_SUCCESS" <#if searcher.status?? && searcher.status?contains("AUTO_SUCCESS")>checked=true</#if>/><label for="state_cb4">自动退款成功</label>
					<input type="checkbox" id="state_cb5" class="check_li" name="status" value="AUTO_FAILED" <#if searcher.status?? && searcher.status?contains("AUTO_FAILED")>checked=true</#if>/><label for="state_cb5">自动退款失败</label>
					<input type="checkbox" id="state_cb6" class="check_li" name="status" value="MANUAL_SUCCESS" <#if searcher.status?? && searcher.status?contains("MANUAL_SUCCESS")>checked=true</#if>/><label for="state_cb6">人工退款成功</label>
					<input type="checkbox" id="state_cb7" class="check_li" name="status" value="MANUAL_FAILED" <#if searcher.status?? && searcher.status?contains("MANUAL_FAILED")>checked=true</#if>/><label for="state_cb7">人工退款失败</label>
					<input type="checkbox" id="state_cb8" class="check_li" name="status" value="REJECTED" <#if searcher.status?? && searcher.status?contains("REJECTED")>checked=true</#if>/><label for="state_cb8">拒绝</label>				
				</div>
				<a href="javascript:" class="b_a reset_check_a">清空</a>
			</div>
		</div>
		
		<table class="tb_main tb_list">
			<thead>
			<tr class="th">
				<th style="width:100px;">退款提交时间</th>
				<th style="width:90px;">用户手机号码</th>
				<th style="width:220px;">退款电子券&套票</th>
				<th>退款金额</th>
				<th style="width:260px;">支付方式</th>
				<th>操作</th>
				</tr>
			</thead>
			<#list pager.list as refund>
			<tr class="tr<#if refund_index%2==0> tr_bg</#if>">
				<td>${refund.createDate?string('yyyy-MM-dd HH:mm:ss')}</td>
				<td>${(refund.coupon.mobile)!'-'}</td>
				<#if refund.coupon.type == 'NORMAL'>
					<td>
						<a style="float:none;margin-right:0;" href="/refund/refundlog?code=${refund.code}" title="${(refund.coupon.couponInfo.name)!}退款详情">
							${refund.coupon.couponInfo.name}
						</a>
					</td>
				<#elseif refund.coupon.type == 'CHILD'>
					<td>
						<a style="float:none;margin-right:0;" href="/refund/refundlog?code=${refund.code}"  title="${refund.coupon.parent.name}退款详情">
							【套票】${refund.coupon.parent.name}
						</a>
					</td>
				</#if>
				<td>${refund.sum}元</td>
				<td>
					<#if refund.payment.type = 'WEI_XIN'>
						微信支付</br>
					<#elseif refund.payment.type = 'ALIPAY_WAP'>
						支付宝Wap支付</br>
					<#elseif refund.payment.type = 'UMPAY_WAP'>
						联动优势支付</br>
					</#if>
						订单号码：${refund.payment.serial}
				</td>
				<td>
					<#if refund.status == 'AUTO_SUCCESS'>
						自动退款成功
					<#elseif refund.status == 'AUTO_FAILED'>
						自动退款失败 <br>
						<a href="/refund/update/retryRefund?code=${refund.code!}" style="float:none;margin-right:16px;">重新自动退款</a><br>
						<a href="/refund/update/toManual?code=${refund.code!}" style="float:none;margin-right:16px;">转人工退款</a>
					<#elseif refund.status == 'REJECTED'>
						审核拒绝
					<#elseif refund.status == 'APPLY'>
						审核中<br/>
						<a href="/refund/update/accept?code=${refund.code!}&status=ACCEPTED" class="pop_a" data-pop="pop_ask" data-status="ACCEPTED" style="float:none;margin-right:16px;">通过</a>
						<a href="/refund/update/reject?code=${refund.code!}&status=REJECTED" class="pop_a" data-pop="pop_ask" data-status="REJECTED" style="float:none;">拒绝</a>
					<#elseif refund.status == 'ACCEPTED'>
						退款中<br><a href="/refund/update/retryRefund?code=${refund.code!}">重新自动退款</a>
					<#elseif refund.status == 'AUTO_EXECUTE'>
						自动退款中<br/>
						<a href="/refund/update/retryRefund?code=${refund.code!}" style="float:none;margin-right:16px;">重新自动退款</a><br>
					<#elseif refund.status == 'MANUAL_SUCCESS'>
						人工退款成功
					<#elseif refund.status == 'MANUAL_FAILED'>
						人工退款失败
					<#elseif refund.status == 'MANUAL_EXECUTE'>
						人工退款中<br>
						<a href="/refund/update/manualSuccess?code=${refund.code!}" class="pop_a" data-pop="pop_ask" data-status="SUCCESS" style="float:none;margin-right:16px;">成功</a>
						<a href="/refund/update/manualFail?code=${refund.code!}" class="pop_a" data-pop="pop_ask" data-status="FAILED" style="float:none;margin-right:16px;">失败</a>
					</#if>
				</td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />
		</div>
	</div>
</div>
<div class="pb pop_ask">
	<form action="${base}/refund/updateStatus" method="post" id="refundForm" name="refundForm">
		<span class="pb_main"> </span>
		<div class="pb_main_desc">
			<span class="remark_title">备注说明:</span>
			<input type="text" id="description" name="remark" class="pb_item_input">
		</div>
		<button type="button" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
	</form>
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
<#if (refundForm)!>${(refundForm)!}</#if>
<@m.page_footer />

