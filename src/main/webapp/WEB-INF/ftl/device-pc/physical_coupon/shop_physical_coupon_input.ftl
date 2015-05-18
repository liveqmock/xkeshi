<#import "/macro.ftl" as m>
<@m.page_header selected='physical_coupon' subselected='list' js='member_type_info|list_filter|validate' title='商户实体券管理' css='set_mime|merchant_detail|voucher_add'/>
<script>
	$(function(){
		check_all_none($('#all'), $('.check_li_c'))
	})
</script>
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;">
	  <a href="<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">${base}/physical_coupon/merchant/list</@shiro.hasAnyRoles> <@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">${base}/physical_coupon/shop/list</@shiro.hasAnyRoles> " class="back_a"></a>
	  <#if physicalCoupon>修改实体券<#else>添加实体券</#if>

	</div>	
	<div class="tb_wrap">
		<div class="tb_title">实体券信息</div>
		<#if physicalCoupon>
			<form class="check_form" action="${base}/physical_coupon/shop/update" method="POST">
		<#else>
			<form class="check_form" action="${base}/physical_coupon/shop/add" method="POST">
			<input type="hidden" name="submissionToken" value="${submissionToken}" />
		</#if>
		<input type="hidden" value="${(physicalCoupon.id)!}" name = "id">
		<table class="tb_main">
			<tbody>
				<tr class="tr_1">
					<td class="border">
						<span class="td_key" data-hint="名称"><em>*</em>名称</span>
						<span class="td_val"><input name="name"  value="${(physicalCoupon.name)!}"  type="text" class="text_tic isneed" data-hint="名称"></span>
					</td>
					<td class="border">
						<span class="td_key"><em>*</em>面额</span>
						<span class="td_val"><input name="amount" value="${(physicalCoupon.amount)!}" type="text" class="text_value isneed isplusfloat isint" data-hint="面额"></span>
					</td>
				</tr>
				<tr class="tr_bg tr_1">
					<td class="border">
						<span class="td_key"><em style="margin-left:6px;"></em>顺序</span>
						<span class="td_val"><input name="weight" value="${(physicalCoupon.weight)!50}" type="text" class="pro_num isplusfloat isint" data-hint="顺序" data-maxlen="5" data-minlen="0"></span>
					</td>
					<td class="border"></td>
				</tr>				
			</tbody>
		</table>
	</div>
	<div class="btn_bottom">
	<button type="submit" class="pb_btn">确定</button><span class="pb_btn_split">或</span><a href="${base}/physical_coupon/shop/list" class="pb_cancel_a">取消</a>
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