<#import "/macro.ftl" as m >
<@m.page_header  subselected='pager' selected='pagersetting' css="seller_list_new"   js= "page_template"/>
<div class="rwrap">
	<div class="r_title">页面列表
		<div class="search_wrap">
		<form action="${base}/pager/pager/list" class="search_form">
			<input type="text" name="key" <#if searcher&&searcher.key>value="${searcher.key}"</#if> 
			placeholder="标题/作者" class="search_input">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:" class="filter_a"></a>
		<a href="${base}/pager/pager/add" class="new_a"></a>
		</div>
	</div>
	<table class="tb_main">
			<tr class="th">
				<td class="id">ID</td>
				<td class="">标题</td>
				<td class="">作者</td>
				<td class="">关键词</td>
				<td class="">url名字</td>
				<td class="">是否发布</td>
			    <td class="">创建时间</td>
			    <td class="">操作</td>
			</tr>
			<#list pager.list as page>
			<tr <#if page_index%2==0>class="tr_bg"</#if>>
				<td class="id">${(page.id)!}</td>
				<td class=""><a class="b_a" href="${base}/pager/pager/${(page.id)!}">${(page.title)!}</a></td>
				<td class="">${(page.author)!}</td>
				<td class="">${(page.metaKeywords)!}</td>
				<td class="">${(page.name)!}</td>
				<td class="">
                    <#if (page.published)!> 
                    	已发布		   
				    <#else>
				              未发布
				    </#if>
				</td>
				<td class="">${(page.createDate?string('yyyy-MM-dd HH:mm'))!'-'}</td>
				<td class="">
					<a href="javascript:void()"  class="del_a" data-id="${page.id}" data-title="${page.title}" >删除</a>
				</td>
			</tr>
			</#list>
		</table>
		
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>	 
	<div class="pb pop_del">
		<form   action="${base}/pager/pager/delete/"  id="pageTemplateForm" name="pageTemplateForm" method="post">
		    <input type="hidden" name="_method" value="DELETE">
			<div class="pb_title">删除提示</div>
			<div class="pb_main">
				确认删除:<span class="pb_cate_name"></span>
			</div>
			<button class="pb_btn pb_btn_s"  type="submit">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>		
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<@m.page_footer />




