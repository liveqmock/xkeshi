<#import "/macro.ftl" as m>
<@m.page_header selected='physical_coupon' subselected='list' js='member_type_info|list_filter|validate' title='集团实体券管理' css='set_mime|merchant_detail|voucher_add'/>
<script>
	$(function(){
		check_all_none($('#all'), $('.check_li_c'))
	})
</script>
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;">
	<a href="<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">${base}/physical_coupon/merchant/list</@shiro.hasAnyRoles>
			 <@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">${base}/physical_coupon/shop/list</@shiro.hasAnyRoles>" class="back_a"></a><#if physicalCoupon>修改实体券<#else>添加实体券</#if>

	</div>	
	<div class="tb_wrap">
		<div class="tb_title">实体券信息</div>
		<#if physicalCoupon>
			<form class="check_form" action="${base}/physical_coupon/merchant/update" method="POST">
		<#else>
			<form class="check_form" action="${base}/physical_coupon/merchant/add" method="POST">
			<input type="hidden" name="submissionToken" value="${submissionToken}" />
		</#if>
		<input type="hidden" value="${(physicalCoupon.id)!}" name = "id">
		<table class="tb_main">
			<tbody>
				<tr class="tr_1">
					<td class="border">
						<span class="td_key"><em>*</em>名称</span>
						<span class="td_val"><input name="name" data-hint="名称" value="${(physicalCoupon.name)!}"  type="text" class="text_tic isneed"></span>
					</td>
					<td class="border">
						<span class="td_key"><em>*</em>面额</span>
						<span class="td_val"><input name="amount" data-hint="面额" value="${(physicalCoupon.amount)!}" type="text" class="text_value isneed isplusfloat isint" data-maxlen="5" data-minlen="0"></span>
					</td>
				</tr>
				<tr class="tr_bg tr_1">
					<td class="border">
						<span class="td_key"><em style="margin-left:6px;"></em>顺序</span>
						<span class="td_val"><input name="weight" value="${(physicalCoupon.weight)!50}" type="text" class="pro_num isplusfloat isint" data-hint="顺序" data-maxlen="5" data-minlen="0"></span>
					</td>
					<#if physicalCoupon>
						<#if physicalCoupon.business_type == 'SHOP'>
							<td class="border">
								<span class="td_key"><em style="margin-left:6px;"></em>适用商户</span>
								<span class="td_val">${shopList[0].name}</span>
								<input type="hidden" name="shopList" value="${shopList[0].id}" />
							</td>
						<#else>
							<td class="border"></td>
						</#if>
					</#if>
				</tr>				
			</tbody>
		</table>
		<#if !physicalCoupon?? || physicalCoupon.business_type == 'MERCHANT'>
			<div class="tb_title">适用商户</div>
			<table class="tb_main">
				<tbody>
					<tr class="tr_1">
						<td class="border" colspan="2"><input type="checkbox" name="suit" id="all" value="全部" class="check_lis check_li_c fi_2" ><label for="all">全部</label>	</td>
					</tr>
					<#list shopList as shop>
						<#if shop_index%2 == 0 >
							<tr>
						</#if>
						<td class="border"><input type="checkbox" data-hint="适用商户" <#if pcsList><#list pcsList as pcshop><#if pcshop.shop.id = shop.id>checked=true</#if></#list></#if> name="shopList" id="suit${shop_index+1}" value="${shop.id}" class="check_lis check_li_c fi_2 isneed" ><label for="suit${shop_index+1}">${shop.name}</label>	</td>
						<#if shop_index%2 != 0 >
							</tr>
						</#if>
					</#list>
				</tbody>	    
			</table>
		</#if>
	</div>
	<div class="btn_bottom">
	<button type="submit" class="pb_btn">确定</button><span class="pb_btn_split">或</span><a href="${base}/physical_coupon/merchant/list" class="pb_cancel_a">取消</a>
	</div>
	</form>

</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />