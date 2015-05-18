<#import "/macro.ftl" as m>

<@m.page_header title="商品管理" subselected='merchant' css='seller_list_new' js='list_filter' selected='cashiering' />
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;"><span class="fl">商品管理</span>
	<div class="search_wrap">
		<form action="${base}/item/merchant/list" class="search_form f_form">
			<input type="text" name="nickName" value="${nickName!}" placeholder="搜索商户简称" class="search_input" id="fi_1">
			<button class="search_btn"></button>
		</form>
	</div>
	<#if nickName != null>
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<span class="b_tit" data-fid="1">商户简称：${nickName}<a href="${(base)!}/item/merchant/list"><b>x</b></a></span>
					<span>的结果<#if items>，共为您找到<span style="color: #428bca;padding:0 5px;font-weight:bold;">${items.list?size}</span>件符合的商品</#if></span>
				</p>
				<a href="${(base)!}/item/merchant/list" class="s_clear">清空</a>
			</div>
	</#if>
	</div>	
	<table class="tb_main">
		<tr class="th">
			<td class="sequence">商户ID</td>
			<td class="name" style="width:200px;">商户简称</td>
			<td class="price">商品数量</td>
			<td class="opt">操作</td>
		</tr>
		<#list subShops as subShop>
		<tr  <#if subShop_index%2==0>class="tr_bg"</#if>>
			<td class="sequence">${(subShop.eid)!}</td>
			<td class="name">${(subShop.name)!}</td>
			<td class="price">${(shopByItemCount[subShop.id+""])!}</td>
			<td class="opt"><a href="${base}/item/merchant/${(subShop.eid)!0}/list" class="b_a">查看详情</a></td>
		</tr>
		</#list>
	</table>
	<#--
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount />
	</div>-->
</div>
<@m.page_footer />




