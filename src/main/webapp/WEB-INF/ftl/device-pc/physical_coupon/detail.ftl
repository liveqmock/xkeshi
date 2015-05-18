<#import "/macro.ftl" as m>
<@m.page_header selected='physical_coupon' subselected='list' title='查看实体券' js='member_type_info|list_filter|validate' css='seller_list_new|set_mime|merchant_detail|voucher_add'/>
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;">
	<a href="<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">${base}/physical_coupon/merchant/list</@shiro.hasAnyRoles>
				<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">${base}/physical_coupon/shop/list</@shiro.hasAnyRoles>
			</@shiro.hasAnyRoles>" class="back_a"></a>查看实体券
	</div>	
	<div class="tb_wrap">
		<div class="tb_title">实体券信息</div>
		<table class="tb_main">
			<tbody>
				<tr class="tr_1">
					<td class="border">
						<span class="td_key isneed" data-hint="名称"><em>&nbsp;</em>名称</span>
						<span class="td_val">${(physicalCoupon.name)}</span>
					</td>
					<td class="border">
						<span class="td_key"><em>&nbsp;</em>面额</span>
						<span class="td_val">￥${(physicalCoupon.amount)!'0.00'}</span>
					</td>
				</tr>
				<tr class="tr_bg tr_1">
					<td class="border">
						<span class="td_key"><em>&nbsp;</em>排序</span>
						<span class="td_val">${(physicalCoupon.weight)!}</span>
					</td>
					<td class="border">
						<span class="td_key"><em>&nbsp;</em>状态</span>
						<span class="td_val"><#if physicalCoupon.enable == true >开启<#else>暂停</#if></span>
					</td>
				</tr>
				<tr class="tr_1">
					<td class="border" colspan="2">
						<span class="td_key"><em>&nbsp;</em>适用商户</span>
						<span class="td_val">
							<#list pcshopList as pcshop>
								${pcshop.shop.name}&nbsp;&nbsp;
							</#list>
						</span>
					</td>
				</tr>				
			</tbody>
		</table>

</div>
<@m.page_footer />