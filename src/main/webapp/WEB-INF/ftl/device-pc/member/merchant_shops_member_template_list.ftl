<#import "/macro.ftl" as m>
<@m.page_header title="会员属性模板" selected='member' subselected='template' css='seller_list_new' js='list_filter' />
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;">会员属性模板
		<div class="search_wrap">
			<form action="${base}/member/merchant/list" class="search_form f_form">
				<input type="text" name="nickName" value="${nickName!}" placeholder="搜索商户简称" class="search_input" id="fi_1">
				<button class="search_btn"></button>
			</form>
		</div>
		
	</div>	
	<table class="tb_main">
		<tr class="th">
			<td class="sequence">商户ID</td>
			<td class="name" style="width:200px;">商户简称</td>
			<td class="price">会员属性数量</td>
			<td class="opt">操作</td>
		</tr>
		<#list subShops as subShop>
		<tr  <#if subShop_index%2==0>class="tr_bg"</#if>>
			<td class="sequence">${(subShop.eid)!}</td>
			<td class="name">${(subShop.name)!}</td>
			<td class="price">${(shopMemberAttributeCountMap[subShop.id+""])!}</td>
			<td class="opt"><a href="${base}/member/merchant/${(subShop.eid)!0}/template/list" class="b_a">查看详情</a></td>
		</tr>
		</#list>
	</table>
</div>
<@m.page_footer />




