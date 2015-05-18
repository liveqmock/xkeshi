<#import "/macro.ftl" as m >
<@m.page_header subselected='pagercategory' selected='pagersetting' css="seller_list_new|page_category"  js="page_category"  />
<script>
  var BASE  =  "${(base)!}";
</script>
<div class="rwrap">
	<div class="r_title">文章分类列表
		<div class="search_wrap">
		<form action="${base}/pager/category/list" class="search_form">
			<input type="text" name="key" <#if searcher&&searcher.key>value="${searcher.key}"</#if> 
			placeholder="分类" class="search_input">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:void(0)" class="filter_a" title="查询过滤"></a>
		<a href="javascrtpt:void(0)" class="new_a" title="新增大类"></a>
		</div>
	</div>
	<table class="tb_main">
			<tr class="th">
				<td class="id">ID</td>
				<td class="">排序号</td>
				<td class="">名称</td>
				<td class="">上级名称</td>
			    <td class="">创建时间</td>
			    <td class="">操作</td>
			</tr>
			<#list pager.list as page>
			<tr <#if page_index%2==0>class="tr_bg"</#if>>
				<td class="id">${(page.id)!}</td>
				<td class="">${(page.sequnce)!}</td>
				<td class="">
				      <a class="b_a" href="javascript:void()" title="编辑"  class="category_edit"  data-categorytype="big"
				     	 data-sequnce="${(page.sequnce)!}" data-categoryname="${(page.name)!}" 
				         data-category="${(page.id)!}" >${(page.name)!}</a>
				</td>
				<td class="">
                    <#if (page.parent == null)!> 
                    	--	   
				    <#else>
				        ${(page.parent.name)!}
				    </#if>
				</td>
				<td class="">${(page.createDate?string('yyyy-MM-dd HH:mm'))!'-'}</td>
				<td class="">
				 <#if (page.parent == null)!> 
					<a class="b_a" href="javascript:void(0)" class="new_category_a"  title ="新增小类"  
							 data-category="${(page.id)!}" data-categoryname="${(page.name)!}"  >新增小类</a>
				 </#if>
				 <!--
				 <#if  (page.children?size==0 && page.count == 0)>
					  <a  href="javascript:void()"  class="del_a" data-category="${(page.id)!}" 
					              data-categorytype="big" data-categoryname="${(page.name)!}"  title="删除${(page.name)!}">删除</a>
			     </#if>
			     -->
				</td>
			</tr>
			<#list page.children as children>
			<tr <#if children_index%2==0>class="tr_bg"</#if>>
				<td class="id">${(children.id)!}</td>
				<td class="">${(children.sequnce)!}</td>
				<td class="">
				      <a class="b_a" href="javascript:void()" title="编辑"  class="category_edit"  data-sequnce="${(children.sequnce)!}"
				         data-categoryparentid="${(page.id)!}" data-categoryparentname="${(page.name)!}"
				         data-category="${(children.id)!}" data-categoryname="${(children.name)!}"  >${(children.name)!}</a>
				</td>
				<td class="">
                    <#if (children.parent == null)!> 
                    	--  
				    <#else>
				        ${(children.parent.name)!}
				    </#if>
				</td>
				<td class="">${(children.createDate?string('yyyy-MM-dd HH:mm'))!'-'}</td>
				<td class="">
					<!--
					<#if  (children.count)=0>
					 <a href="javascript:void()"  class="del_a" data-category="${(children.id)!}"
				     	 data-categorytype="little" data-categoryname=" ${(children.name)!}" title="删除${(children.name)!}">删除</a>
			   		 </#if>
			   		 -->
				</td>
			</tr>
			</#list>
			</#list>
		</table>
		
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>	 
</div>
	<div class="pb add_opt">
		<form action="${base}/pager/category/add" method="POST" id="typeForm" name ="typeForm">
		    <input type="hidden" name="_method" value="put"  id="methodId">
			<div class="pb_title"><span class = "pb_title_sp"></span></div>
			<div class="pb_main">
				<div class="pb_item  pb_parent">
				  <em>*</em>
				 <span class="td_key">所属大类：</span>
				      	<input type="hidden" name="parent.id" value="" id="parentId" >
	       				<input type="hidden" name="id"  value=""  id="categoryId"  >
					 <span class="td_val"  id="parentName"></span>
				 </div>
				 <div class="pb_item">
				  	<em>*</em>
				  	<span class="td_key">名称：</span>
				  	<span class="td_val">
	       		   		<input type="text" name="name" class="input-l" value="" id="nameId">
				  	</span>	
				 </div>
				 <div class="pb_item">
				 	<em>*</em>
					<span class="td_key">排列序号：</span>
					<span class="td_val">
						<input type="text" class="input-l" id="sequnce" name="sequnce" value="" size="16">
	       			</sapn>
				 </div>
			</div>
			<button class="pb_btn pb_btn_s" type="submit" onclick="return saveOp();">保存</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<!--
	<div class="pbdel pop_del">
		<form   id="sellerCategoryForm" name="sellerCategoryForm" method="post">
		    <input type="hidden" name="_method" value="DELETE">
			<div class="pb_title">删除提示</div>
			<div class="pb_main">
				确认删除大类<span class="pb_cate_name">大类名称</span>
			</div>
			<button class="pb_btn"  type="submit">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>		
	-->
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
	
<@m.page_footer />




