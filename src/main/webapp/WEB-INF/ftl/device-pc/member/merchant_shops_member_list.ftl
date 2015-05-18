<#import "/macro.ftl" as m>

<@m.page_header title="商户会员列表" selected='member' subselected='list' css='seller_list_new' js='list_filter' />
<div class="rwrap">
	<div class="r_title" style="overflow:hidden;">商户会员列表
		<div class="search_wrap">
			<form action="${base}/member/merchant/list" class="search_form f_form">
				<input type="text" name="nickName" value="${nickName!}" placeholder="搜索商户简称" class="search_input" id="fi_1">
				<button class="search_btn"></button>
			</form>
		</div>
	   <#if (nickName != null)! >
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if nickName??><span class="b_tit" data-fid="1">商户简称：${(nickName)!}<b>x</b></span></#if>
					</span>的结果</span>
				</p>
				<a href="${(base)!}/member/merchant/list" class="s_clear">清空</a>
			</div>
		</#if>
	</div>	
	<table class="tb_main">
		<tr class="th">
			<td class="sequence">商户ID</td>
			<td class="name" style="width:200px;">商户简称</td>
			<td class="price">会员数量</td>
			<td class="opt">操作</td>
		</tr>
		<#list subShops as subShop>
		<tr  <#if subShop_index%2==0>class="tr_bg"</#if>>
			<td class="sequence">${(subShop.eid)!}</td>
			<td class="name">${(subShop.name)!}</td>
			<td class="price">${(shopMemberCountMap[subShop.id+""])!}</td>
			<td class="opt"><a href="${base}/member/merchant/${(subShop.eid)!0}/list" class="b_a">查看详情</a></td>
		</tr>
		</#list>
	</table>
</div>
<#if (status == "faild")!>
	<div class="pop_hint pop_hint3">${(msg)!}</div>
</#if>
<#if (status == "success")!>
	<div class="pop_hint pop_hint2">${(msg)!}</div>
</#if>
<@m.page_footer />




