<#import "/macro.ftl" as m>

<@m.page_header subselected='category' selected='setting' css='reset|admin_frame|seller_category' js="seller_category"  />
<script>
 var BASE  = "${base}";
</script>
<div class="rwrap">
	 <div class="r_title"><span class="fl">商户分类</span>
	 	<div class="search_wrap">
		   <a href="${base}/category/sequence/0" class="sort_a fr" title="大类排序"></a>
		   <a href="${base}/category/add" class="add_a fr" title="添加大类"></a>
	 	</div>
	 </div>
	 
	<#list categorys as category>
	<div class="cate_block">
	   <table class="cate_tb"> 
			<tr class="ltr">
				<td class="img">
				    <#if (category.banner != '')!>
						<a class="l_a img_a" href="javascript:" data-src="${image_base}${(category.banner)!}"> 
						<img src="${image_base}${(category.banner)!}!xpos.merchant.home.avatar" style="width: 150px;" ></a>
					<#else>
						<img src="${base}/static/css/img/error_miss.png" style="width: 150px;">
					</#if>
				</td>
				<td class="state">
					<#if (category.visible == true)! >
						<span class="state1">显示 </span>
					<#else>
						<span class="state2">隐藏</span>
					</#if>
				</td>				
				<td class="parent_name">大类: ${(category.name)!}（商户：${(category.count)!}）</td>
				<td class="edit"><a href="${base}/category/edit/${(category.id)!}"  title="编辑${(category.name)!}">编辑</a></td>
				<td class="del">
				<#if  (category.categorys?size==0)>
					  <a  href="javascript:void()"  class="del_a" data-category="${(category.id)!}" 
					              data-categorytype="big" data-categoryname="${(category.name)!}"  title="删除${(category.name)!}">删除</a>
			    </#if>
			    </td>
			</tr>
			<#list category.categorys as littleCategory>
			<tr class="str">
				<td class="img">
					<#if (littleCategory.banner != '')!>
						<a class="l_a img_a" href="javascript:" data-src="${image_base}/${(littleCategory.banner)!}">
							<image src="${image_base}/${(littleCategory.banner)!}!xpos.merchant.home.avatar" style="width: 110px;" >
						</a>
					<#else>
						<img src="${base}/static/css/img/error_miss.png" style="width: 110px;" >
					</#if>
				</td>
				<td class="state">
					<#if (littleCategory.visible == true)! >
						<span class="state1">显示 </span>
					<#else>
						<span class="state2">隐藏</span>
					</#if>
				</td>				
				<td class="name">
			         小类: ${(littleCategory.name)!}（商户：${(littleCategory.count)!}）
				</td>
				<td class="edit">
			         <a class="b_a" href="${base}/category/edit/${(littleCategory.id)!}"  title="编辑${(littleCategory.name)!}">编辑</a>
			    </td>
				<td class="del">
			       <#if (littleCategory.count)=0>
				     <a class="b_a" href="javascript:void()"  class="del_a" data-category="${(littleCategory.id)!}"
				     	 data-categorytype="little" data-categoryname=" ${(littleCategory.name)!}" title="删除${(littleCategory.name)!}">删除</a>
				   </#if>
				</td>
			</tr>
			</#list>
			<tr>
				<table class="botb">
					<tr>
						<td class="add_scate">
						  <a href="${base}/category/add/${category.id}" >添加小类   </a>
						</td>
						<td class="sort_scate">
						<#if  (category.categorys?size!=0)>
				  		  <a href="${base}/category/sequence/${category.id}" >小类排序</a>
				  		</#if>
					    </td>
					</tr>
				</table>
			</tr>
		</table>
	</div>
	</#list> 
	<div class="pb pop_del">
		<form   id="sellerCategoryForm" name="sellerCategoryForm" method="post">
		    <input type="hidden" name="_method" value="DELETE">
			<div class="pb_title">删除提示</div>
			<div class="pb_main">
				确认删除大类<span class="pb_cate_name">大类名称</span>
			</div>
			<button class="pb_btn"  type="submit">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>		
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />




