<#import "/macro.ftl" as m>

<@m.page_header selected='setting' css='add_seller_new|category_input' js="category_input" />
<div class="rwrap">
	<p class="r_title"> 
		 <a class="back_a" href="${base}/category/list" title="返回"></a>
	  <#if  (categoryParent.id != 0)!>  
	         小类排序
	   <#else>
	        大类排序
	  </#if>
	</p>
       <form action="${base}/category/update/sequence"  method="POST" >
  	<table class="tb_main">		
       <input type="hidden" name="_method"  value="put">
       <input type="hidden" name="parentId"  value="  ${(categoryParent.id)!}">
      <#if (categoryParent.id != 0)!>
	       <tr class="tr_bg">
		     <td class="ltd">
	       		<span class="td_key">所属大类：</span>
				<span class="td_val">${(categoryParent.name)!}</span>
		     </td>
	    </tr>
       </#if>
       <tr class="tr_bg">
		     <td class="ltd">
		       	<span class="td_key">
		       		 序号
		       	</span>
			  	<span class="td_val">
			  		 类名
			  	</span>
		       	</td>
		       </tr>
	       <#list categoryList  as category >
		    <tr class="tr_bg">
		     <td class="ltd">
		       	<span class="td_key">
		       		<input type="hidden"  name="categoryId" value="${(category.id)!}">
		       		<input type="text" class="td_sequence" name="sequence" value="${(category.sequence)!}" size="5">
		       	</span>
			  	<span class="td_val">
			  		${(category.name)!}
			  	</span>
		       	</td>
		       </tr>
	       </#list>
     </table>
         <div class="btn_wrap"><button class="add_seller_btn" type="submit" >保存</button></div>
     </form>
</div>

	 
	
<@m.page_footer />




