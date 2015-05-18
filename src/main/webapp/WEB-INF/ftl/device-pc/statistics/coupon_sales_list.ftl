<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected='couponSales' title='优惠销售统计&明细' css="seller_list_new|coupon_list|coupon_sales" js="coupon_list|list_filter|My97DatePicker/WdatePicker" />
	<div class="rwrap">
		<div class="r_title"><span class="fl">电子券销售统计&明细</span>
			<div class="search_wrap">
				<form action="${base!}/statistics/coupon/sales" class="search_form fl">
					<input type="text" name="key" <#if (searcher.key)??>value="${(searcher.key)!}"</#if> placeholder="电子券名称" class="search_input">
					<button class="search_btn"></button>
				</form>
				<a href="javascript:" class="filter_a fl"></a>
				<#if request.queryString?has_content>
					<#assign exportParams = '?' + request.queryString />
				</#if>
				<a href="${base!}/statistics/coupon/sales/export${exportParams!}"  class="report_a ml12"></a>
			</div>
			<#if searcher != null && searcher.hasParameter >
				<div class="search_result ofw">
					<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
						<div>
					
						<#if searcher.key><span class="b_tit" data-fid="1">电子券名称：${searcher.key}<b>x</b></span></#if>
						<#if searcher.startDate><span class="b_tit" data-fid="2">起始销售时间：${searcher.startDate?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
						<#if searcher.endDate><span class="b_tit" data-fid="3">截止销售时间：${searcher.endDate?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
						<#if searcher.mobile><span class="b_tit" data-fid="4">手机号码：${searcher.mobile}<b>x</b></span></#if>
						<#if searcher.type><span class="b_tit" data-fid="5">支付方式：<#list searcher.type as type> ${type.desc} </#list><b>x</b></span></#if>
						<#if searcher.source><span class="b_tit" data-fid="6">销售渠道：${searcher.source}<b>x</b></span></#if>
						</div>
						<span class="w_tit">的结果</span></span>
					</p>
					<a href="${base!}/statistics/coupon/sales" class="s_clear">清空</a>
				</div>
			</#if>
		</div>
		<div class="data_wrap">
			<div class="data_l">
				<span>售价合计<em>${total[0]}元</em></span><span class="data_split">/</span><span>实际支付合计<em>${total[1]}元</em></span>
			</div>
			<div class="data_r data_cte">
			<#--
				<#if request.queryString?has_content>
					<#assign exportParams = '?' + request.queryString />
				</#if>
				<span><a href="${base!}/statistics/coupon/sales/export${exportParams!}" target="_blank" class="b_a">导出Excel</a></span>-->
				<span>共<em>${pager.totalCount!0}笔</em>交易</span>
			</div>
		</div>
		<table class="tb_main" id="tb_main">
			<tr class="th">
				<td class="xtime" id="" >销售时间</td>
				<td class="" id="" >电子券名称</td>
				<td class="xmobile" id="" >手机号码</td>
				<td class="" id="" style="width:50px;">单价</td>
				<td class="" id="" style="width:60px;">数量</td>
				<td class="" id="" style="width:80px;">实际支付</td>
				<td class="" id="" style="width:80px;">支付方式</td>
				<td class="" id="" style="width:100px;">支付账号</td>
				<td class="" id="" style="width:100px;">交易流水号</td>
			</tr>
			<#list pager.list as payment>
			<tr <#if payment_index%2==0>class="tr_bg"</#if>>
				<td class="xtime">${(payment.tradeDate?string('yyyy-MM-dd HH:mm:ss'))!}</td>
				<td class=""><#if (payment.couponInfo.type)!'' == "PACKAGE">【套票】</#if>${(payment.couponInfo.name)!}</td>
				<td class="xmobile"><#if (payment.mobile)?? >${payment.mobile?substring(0,3)+'****'+payment.mobile?substring(7)}<#else>-</#if>
				<td class="">${(payment.couponInfo.price)!}</td>
				<td class="">${(payment.quantity)!}</td>
				<td class="">${(payment.sum)!}</td>
				<td class="">${(payment.type.desc)!}</td>
				<#if (payment.type)?? && payment.type == 'ALIPAY_WAP'>
					<td class="">${(payment.buyerAccount)!''}</td>
				<#elseif (payment.type)?? && payment.type == 'UMPAY_WAP'>
					<td class="">${(payment.cardNumber)!}</td>
				<#else>
					<td class=""></td>
				</#if>
				<td class="">${(payment.serial)!}</td>
			</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base!}/statistics/coupon/sales" method="get" class="f_form">
			<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">销售时间</p>
					<input type="text" class="tcal_time pb_item_input fi_2" name="startDate" value="<#if (searcher.startDate)?? >${searcher.startDate?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
					<input type="text" class="tcal_time pb_item_input fi_3" name="endDate" value="<#if (searcher.endDate)?? >${searcher.endDate?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">手机号码</p>
					<input type="text" class="pb_item_input fi_4" name="mobile" value="<#if (searcher.mobile)?? >${searcher.mobile}</#if>"/>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">支付方式</p>
					<input type="checkbox" id="cb1" class="pfck fi_5" name="type" value="WEI_XIN" <#if searcher.type?? && searcher.type?seq_contains("WEI_XIN")>checked=true</#if>><label class="pflb" for="cb1">微信</label>
					<input type="checkbox" id="cb2" class="pfck fi_5" name="type" value="ALIPAY_WAP" <#if searcher.type?? && searcher.type?seq_contains("ALIPAY_WAP")>checked=true</#if>><label class="pflb" for="cb2">支付宝手机网站</label>
					<input type="checkbox" id="cb3" class="pfck fi_5" name="type" value="ALIPAY_SHORTCUT" <#if searcher.type?? && searcher.type?seq_contains("ALIPAY_SHORTCUT")>checked=true</#if>><label class="pflb" for="cb3">支付宝快捷</label>
					<input type="checkbox" id="cb4" class="pfck fi_5" name="type" value="UMPAY_WAP" <#if searcher.type?? && searcher.type?seq_contains("UMPAY_WAP")>checked=true</#if>><label class="pflb" for="cb4">联动优势手机网站</label>
					<input type="checkbox" id="cb5" class="pfck fi_5" name="type" value="SMS_PUSH" <#if searcher.type?? && searcher.type?seq_contains("SMS_PUSH")>checked=true</#if>><label class="pflb" for="cb5">短信赠送</label>
				</div>
				<#--
				<div class="pb_item pb_item_b">
					<p class="pb_item_title">销售渠道</p>
					<input type="text" class="pb_item_input fi_6" name="source" value="<#if (searcher.source)?? >${searcher.source!}</#if>"/>
				</div>
				-->
			</div>
			<button class="pb_btn pb_btn_s">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
	</div>
</div>
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




