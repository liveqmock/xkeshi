<#import "/macro.ftl" as m>
<@m.page_header selected='pagersetting' css='add_seller_new|seller_info' js= "pager_input" />
<script>
	var IMG_BUCKET = '${img_bucket}',
		CONTEXT = '${base}',
		BASE = '${base}',
		IMG_PATH = '${image_base}',
		SHOP_ID = '',
		UP_IMG_TYPE = 5;
</script>
<script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="/static/ueditor/ueditor.all.js"></script>
<script type="text/javascript" src="/static/ueditor/lang/zh-cn/zh-cn.js"></script>
<!-- 实例化编辑器 -->
<script type="text/javascript">
    UE.getEditor('uecontent');
</script>
<script>
	var pageCategory = {
		<#list parentPageCategories as parentPageCategory>
			"${parentPageCategory.id}":[
				<#list parentPageCategory.children as childrenPageCategory>
					{"id":${childrenPageCategory.id},"name":"${childrenPageCategory.name}"},
				</#list>
			],
		</#list>
	};
	$(function(){
		pageCategory = eval(pageCategory);
		<#if (page.pageCategory.id)!>
			change("${(page.pageCategory.parent.id)!}");
			$('#parent').val("${(page.pageCategory.parent.id)!}");
			$("#child option[value='${(page.pageCategory.id)!}']").attr("selected",true);
		</#if>
		$('#parent').change(function(){
			change(this.value);
		});
		function change(id){
			$('#child').html('');
			$.each(pageCategory[id], function(i,e){
				$('#child').append($('<option>', {
				    value: e.id,
				    text: e.name
				}));
			});
		}
	});
</script>
<div class="rwrap">
	<p class="r_title"><a class="back_a" href="javascript:" title="返回"></a>文章</p>
		<div class="tb_wrap">
			<div class="tb_title">文章编辑</div>
			<#if (page)!>
				<form action="${base}/pager/pager/update" method="post">
					<input type="hidden" name="id" value="${(page.id)!}" />
					<input type="hidden" name="_method" value="PUT" />
				<#else>
				<form action="${base}/pager/pager/add" method="post">
				<input type="hidden" name="submissionToken" value="${submissionToken}"/>
			</#if>
			<table class="tb_main">
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">标题</span>
						<span class="td_val">
							<input type="text" name="title" value="${(page.title)!}" class="ip1" />
						</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">url</span>
						<span class="td_val">
							<input type="text" name="name" value="${(page.name)!}" class="ip1"  onblur="checkName(this)" />
						</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">关键词</span>
						<span class="td_val">
							<input type="text" name="metaKeywords" value="${(page.metaKeywords)!}" class="ip1" />
						</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">文章类型</span>
						<span class="td_val">
							 <select id="parent">
							 <option>请选择...</option>
							<#list parentPageCategories as pageCategory>
								<option value="${pageCategory.id}">${pageCategory.name}</option>
							</#list>
						</select>
						<select class="ml13" id="child" name="pageCategory.id"><option>请选择...</option></select>
						</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">页面模板</span>
						<span class="td_val">
							 <select id="parent"  name="pageTemplate.id">
							   <option >请选择模板</option>
							   <#list pageTemplate as template>
								<option value="${template.id}"<#if (template.id == page.pageTemplate.id)!>selected = "selected"</#if>>${template.title}</option>
							   </#list>
							</select>
						</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">内容</span>
						<span class="td_val">
							<script id="uecontent" name="content" type="text/plain" style="width:855px;height:600px;">
								${(page.content)!}
							</script>
						</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">是否发布</span>
						<span class="td_val">
							 <select name="published" class="ip1">
								<option value="true">发布</option>
								<option value="false">未发布</option>
							 </select>
						</span>
					</td>
				</tr>
				<#if (page.author)!>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">创建人</span>
						<span class="td_val">
							${(page.author)!}
						</span>
					</td>
				</tr>
				</#if>
			</table>
			<div class="btn_wrap"><button class="add_seller_btn">保存</button></div>
			</form>
		</div>
</div>
<@m.page_footer />