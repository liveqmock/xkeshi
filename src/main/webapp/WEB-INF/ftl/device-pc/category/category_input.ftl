<#import "/macro.ftl" as m>

<@m.page_header selected='setting' css='add_seller_new|category_input' js="category_input" />
<div class="rwrap">
	<div class="tb_wrap">
	<div class="tb_title">
	  <a class="back_a" href="${base}/category/list" title="返回"></a>
	  <#if  (category!=null)!>编辑<#else>添加</#if>
	  <#if  (category.parent!=null)!>小类 <#else>大类 </#if>
	</div>
	<form action="${base}/category/add" method="post" enctype="multipart/form-data">
	<input type="hidden" name="submissionToken" value="${submissionToken}">
	<table class="tb_main">			
		  <#if (category.parent!=null)!>
		<tr>
			<td class="ltd">
				<em>*</em>
				<span class="td_key">所属大类：</span>
				<span class="td_val">
					${(category.parent.name)!}
			      <input type="hidden" name="parent.id" value="${(category.parent.id)!}"  >
				</span>
			</td>
		</tr>
       </#if>
		<tr class="tr_bg">
			<td class="ltd">
				<em>*</em>
				<span class="td_key">排列序号：</span>
				<span class="td_val">
					<input type="text" class="input-l" id="sequence" name="sequence" value="${(category.sequence)!}">
       				<input type="hidden" name="id"  value="${(category.id)!}"  >
       			</sapn>
			</td>
		</tr>
		<tr class="tr_bg">
			<td class="ltd">
			  	<em>*</em>
			  	<span class="td_key">名称：</span>
			  	<span class="td_val">
       		   		<input type="text" name="name" class="input-l lh14" value="${(category.name)!}">
			  	</span>
			 </td>
		</tr>
       	<tr class="tr_bg">
			<td class="ltd"><em>*</em><span class="td_key"  style="margin-left:7px">图片：</span>
				<span class="td_val">
		       		<input type="file"  name="bannerFile" class="input-l" >
		       		<#if (category.banner)!>
		       		   <a class="l_a img_a" href="javascript:" data-src="${image_base}${(category.banner)!}">查看图片</a>
		       		</#if>
				</span>
			</td>
		</tr>
       	<tr class="tr_bg">
			<td class="ltd"><em>*</em><span class="td_key" style="margin-left:7px">显示状态：</span>
				<span class="td_val">
			       	 <select name="visible" class="input-l">
			       	 	 <option  value="false" <#if ((category.visible)! == false)> selected = "selected"</#if>>隐藏</option>
			       	 	 <option  value="true"  <#if ((category.visible)! == true) > selected = "selected"</#if> >显示</option>
			       	 </select>
				</span>
			</td>
		</tr>
     </table>
     <div class="btn_wrap"><button class="add_seller_btn" type="submit" >保存</button></div>
	</form>
</div>

	
<@m.page_footer />




