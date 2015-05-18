<#import "/macro.ftl" as m >
<@m.page_header subselected='pagertemplate' selected='pagersetting' css="seller_list_new" />
<div class="rwrap">
	<div class="r_title">页面模板列表
		<div class="search_wrap">
		<form action="${base}/pager/template/list" class="search_form">
			<input type="text" name="key" <#if searcher&&searcher.key>value="${searcher.key}"</#if> 
			placeholder="名称" class="search_input">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:" class="filter_a"></a>
		<a href="${base}/pager/template/add" class="new_a"></a>
		</div>
	</div>
	<table class="tb_main">
			<tr class="th">
				<td class="id">ID</td>
				<td class="">名称</td>
			    <td class="">创建时间</td>
			    <!--
			    <td class="">操作</td>
			    -->
			</tr>
			<#list pager.list as page>
			<tr <#if page_index%2==0>class="tr_bg"</#if>>
				<td class="id">${(page.id)!}</td>
				<td class=""><a class="b_a" href="${base}/pager/template/${(page.id)!}">${(page.title)!}</a></td>
				<td class="">${(page.createDate?string('yyyy-MM-dd HH:mm'))!'-'}</td>
				<!--
				<td class="">删除</td>
				-->
			</tr>
			</#list>
		</table>
		
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>	 
	
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<@m.page_footer />




