<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected='couponConsume' title='优惠销售统计&明细' css="seller_list_new|coupon_list" js="coupon_list|list_filter|My97DatePicker/WdatePicker" />
	<div class="rwrap">
		<div class="r_title"><span class="fl">电子券核销统计&明细</span>
		<div class="search_wrap">
				<form action="${base!}/statistics/coupon/consume" class="search_form fl">
					<input type="text" name="key" <#if (searcher.key)??>value="${(searcher.key)!}"</#if> placeholder="电子券名称" class="search_input">
					<button class="search_btn"></button>
				</form>
				<a href="javascript:" class="filter_a fl"></a>
				<#if request.queryString?has_content>
				<#assign exportParams = '?' + request.queryString />
			</#if>
			<a href="${base!}/statistics/coupon/consume/export${exportParams!}" class="report_a ml12"></a>
			</div>
			<#if searcher != null && searcher.hasParameter >
				<div class="search_result ofw">
					<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<span class="p_wrap">
						<div>
						<#if searcher.key><span class="b_tit" data-fid="1">电子券名称：${searcher.key}<b>x</b></span></#if>
						<#if searcher.startDate><span class="b_tit" data-fid="2">起始核销时间：${searcher.startDate?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
						<#if searcher.endDate><span class="b_tit" data-fid="3">截止核销时间：${searcher.endDate?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
						<#if searcher.mobile><span class="b_tit" data-fid="4">手机号码：${searcher.mobile}<b>x</b></span></#if>
						<#if searcher.type><span class="b_tit" data-fid="5">支付方式：<#list searcher.type as type> ${type.desc} </#list>	<b>x</b></span></#if>
						<#if searcher.source><span class="b_tit" data-fid="6">销售渠道：${searcher.source}<b>x</b></span></#if>
						<#if queryStr><span class="b_tit" data-fid="7"><@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">核销商户/</@shiro.hasAnyRoles>操作员：${queryStr!}<b>x</b></span></#if>
						</div>
						<span class="w_tit">的结果</span></span>
					</p>
					<a href="${base!}/statistics/coupon/consume" class="s_clear">清空</a>
				</div>
			</#if>
		</div>
		<div class="top_data">共<em>${pager.totalCount!0}</em>条记录，实际支付合计<em>${total!'0.00'}</em>元
		<#--
			<#if request.queryString?has_content>
				<#assign exportParams = '?' + request.queryString />
			</#if>
			<span><a href="${base!}/statistics/coupon/consume/export${exportParams!}" target="_blank" class="b_a">导出Excel</a></span>-->
		</div>
		<table class="tb_main" id="tb_main2">
			<tr class="th">
				<td class="mtime">核销时间</td>
				<td >电子券名称</td>
				<td class="xmobile">手机号码</td>
				<td style="width:60px;">实际支付</td>
				<td style="width:60px;">支付方式</td>
				<td >支付账号</td>
				<td >交易流水号</td>
				<td class="name" style="width:100px;"><@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">核销商户/</@shiro.hasAnyRoles>操作员</td>
			</tr>
			<#list pager.list as coupon>
			<tr <#if coupon_index%2==0>class="tr_bg"</#if>>
				<td class="mtime" >${(coupon.consumeDate?string('yyyy-MM-dd HH:mm:ss'))!}</td>
				<td class="name"><#if (coupon.type)! == "CHILD">【套票】${(coupon.parent.name)!}-</#if>${(coupon.couponInfo.name)!}</td>
				<td class="xmobile"><#if (coupon.mobile)?? && ((coupon.mobile)?length>10)>${coupon.mobile?substring(0,3)+'****'+coupon.mobile?substring(7)}<#else>-</#if>
				<td class="name"><#if (coupon.type)! == 'CHILD'>未知<#else>${(coupon.payment.avaragePrice)!}</#if></td>
				<td class="name">${(coupon.payment.type.desc)!}</td>
				<#if (coupon.payment.type)?? && coupon.payment.type == 'ALIPAY_WAP'>
					<td class="name">${(coupon.payment.buyerAccount)!''}</td>
				<#elseif (coupon.payment.type)?? && coupon.payment.type == 'UMPAY_WAP'>
					<td class="name">${(coupon.payment.cardNumber)!}</td>
				<#else>
					<td class="name"></td>
				</#if>
				<td class="name">${(coupon.payment.serial)!}</td>
				<td class="name">
					<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">${(applicableShopMap[''+coupon.businessId].name)!}/</@shiro.hasAnyRoles>
					${(coupon.operator.username)!}
				</td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base!}/statistics/coupon/consume" method="get" class="f_form">
			<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">核销时间</p>
					<input type="text" class="tcal_time pb_item_input fi_2" name="startDate" value="<#if (searcher.startDate)?? >${searcher.startDate?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
					<input type="text" class="tcal_time pb_item_input fi_3" name="endDate" value="<#if (searcher.endDate)?? >${searcher.endDate?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">手机号码</p>
					<input type="text" class="pb_item_input fi_4" name="mobile" value="<#if (searcher.mobile)?? >${searcher.mobile}</#if>"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">支付方式</p>
					<input type="checkbox" id="cb1" class="pfck fi_5" name="type" value="WEI_XIN" <#if (searcher.type)?? && searcher.type?seq_contains("WEI_XIN")>checked=true</#if>><label class="pflb" for="cb1">微信</label>
					<input type="checkbox" id="cb2" class="pfck fi_5" name="type" value="ALIPAY_WAP" <#if (searcher.type)?? && searcher.type?seq_contains("ALIPAY_WAP")>checked=true</#if>><label class="pflb" for="cb2">支付宝手机网站</label>
					<input type="checkbox" id="cb3" class="pfck fi_5" name="type" value="ALIPAY_SHORTCUT" <#if (searcher.type)?? && searcher.type?seq_contains("ALIPAY_SHORTCUT")>checked=true</#if>><label class="pflb" for="cb3">支付宝快捷</label>
					<input type="checkbox" id="cb4" class="pfck fi_5" name="type" value="UMPAY_WAP" <#if (searcher.type)?? && searcher.type?seq_contains("UMPAY_WAP")>checked=true</#if>><label class="pflb" for="cb4">联动优势手机网站</label>
					<input type="checkbox" id="cb5" class="pfck fi_5" name="type" value="SMS_PUSH" <#if (searcher.type)?? && searcher.type?seq_contains("SMS_PUSH")>checked=true</#if>><label class="pflb" for="cb5">短信赠送</label>
				</div>
				<#--
				<div class="pb_item pb_item_b">
					<p class="pb_item_title">销售渠道</p>
					<input type="text" class="pb_item_input fi_6" name="source" value="<#if (searcher.source)?? >${searcher.source!}</#if>"/>
				</div>
				-->
				<div class="pb_item pb_item_b">
					<p class="pb_item_title"><@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">核销商户/</@shiro.hasAnyRoles>操作员</p>
					<input type="text" class="pb_item_input fi_7" name="queryStr" value="<#if (queryStr)?? >${queryStr!}</#if>"/>
				</div>
			</div>
			<button class="pb_btn pb_btn_s">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
	</div>
</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${(errMsg)!}</div>
	</#if>
<script>
	$(function(){
		$('.report_a').click(function(){
			if(confirm("是否要导出数据？")){
				return true;
			}
			return false;
		});
	});
</script>
<@m.page_footer />



