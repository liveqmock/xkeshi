<#import "/macro.ftl" as m>
<@m.page_header selected='pagersetting' css='add_seller_new|seller_info|pager_template'  />

<link rel="stylesheet" href="${static_base}/codemirror/4.2/lib/codemirror.css">
<#-- 编辑器样式 -->
<link rel="stylesheet" href="${static_base}/codemirror/4.2/theme/eclipse.css">
<link rel="stylesheet" href="${static_base}/codemirror/4.2/theme/the-matrix.css">
<link rel="stylesheet" href="${static_base}/codemirror/4.2/theme/neat.css">
<link rel="stylesheet" href="${static_base}/codemirror/4.2/theme/monokai.css">
<#-- 提示插件样式 -->
<link rel="stylesheet" href="${static_base}/codemirror/4.2/addon/hint/show-hint.css">
<#-- 查找插件 -->
<link rel="stylesheet" href="${static_base}/codemirror/4.2/addon/dialog/dialog.css">
<#-- F11全屏模式 -->
<link rel="stylesheet" href="${static_base}/codemirror/4.2/addon/display/fullscreen.css">
<link rel="stylesheet" href="${static_base}/codemirror/4.2/theme/night.css">
<#-- fold启动折叠模式 -->
<link rel="stylesheet" href="${static_base}/codemirror/4.2/addon/fold/foldgutter.css" />

<#-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
<script src="${static_base}/codemirror/4.2/lib/codemirror.js"></script>
<#-- 自动提示编辑插件 -->
<script src="${static_base}/codemirror/4.2/addon/hint/show-hint.js"></script>
<script src="${static_base}/codemirror/4.2/addon/hint/xml-hint.js"></script>
<#-- 插件公用 -->
<script src="${static_base}/codemirror/4.2/mode/xml/xml.js"></script>
<script src="${static_base}/codemirror/4.2/mode/javascript/javascript.js"></script>
<script src="${static_base}/codemirror/4.2/mode/css/css.js"></script>
<script src="${static_base}/codemirror/4.2/mode/htmlmixed/htmlmixed.js"></script>
<script src="${static_base}/codemirror/4.2/mode/markdown/markdown.js"></script>
<#-- 编辑器样式 -->
<script src="${static_base}/codemirror/4.2/addon/selection/active-line.js"></script>
<script src="${static_base}/codemirror/4.2/addon/edit/matchbrackets.js"></script>
<#-- 查找插件 -->
<script src="${static_base}/codemirror/4.2/addon/dialog/dialog.js"></script>
<script src="${static_base}/codemirror/4.2/addon/search/searchcursor.js"></script>
<script src="${static_base}/codemirror/4.2/addon/search/search.js"></script>
<#-- 显示高亮预览 -->
<script src="${static_base}/codemirror/4.2/addon/runmode/runmode.js"></script>
<#-- 自动补齐</..>	 -->
<script src="${static_base}/codemirror/4.2/addon/edit/closetag.js"></script>
<script src="${static_base}/codemirror/4.2/addon/fold/xml-fold.js"></script>
<#-- F11全屏模式 -->
<script src="${static_base}/codemirror/4.2/addon/display/fullscreen.js"></script>
<#-- fold折叠 -->
<script src="${static_base}/codemirror/4.2/addon/fold/foldcode.js"></script>
<script src="${static_base}/codemirror/4.2/addon/fold/foldgutter.js"></script>
<script src="${static_base}/codemirror/4.2/addon/fold/brace-fold.js"></script>
<script src="${static_base}/codemirror/4.2/addon/fold/xml-fold.js"></script>
<script src="${static_base}/codemirror/4.2/addon/fold/markdown-fold.js"></script>
<script src="${static_base}/codemirror/4.2/addon/fold/comment-fold.js"></script>
<script>
	var IMG_PATH = '${image_base}'
</script>

<div class="rwrap">
	<p class="r_title"><a class="back_a" href="javascript:" title="返回"></a>文章模板</p>
		<div class="tb_wrap">
			<div class="tb_title">文章模板编辑</div>
			<#if (pageTemplate)!>
				<form action="${base}/pager/template/update" method="post">
					<input type="hidden" name="id" value="${(pageTemplate.id)!}" />
					<input type="hidden" name="_method" value="PUT" />
				<#else>
				<form action="${base}/pager/template/add" method="post">
				<input type="hidden" name="submissionToken" value="${submissionToken}"/>
			</#if>
			<table class="tb_main">
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">标题</span><span class="td_val"><input type="text" name="title" value="${(pageTemplate.title)!}" class="ip1" /></span></td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">编辑器样式</span>
					<span class="td_val">
						<select onchange="selectTheme()" id="select">
						    <option >default</option>
						    <option selected>eclipse</option>
						    <option>the-matrix</option>
						    <option>neat</option>
						    <option>monokai</option>
						</select>
					</span>
					</td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">详情</span>
					 <form>
					 	<textarea id="content" name="content" placeholder="请输入页面模板">
							${(pageTemplate.content)!}
						</textarea>
					 </form>
					 <span class="td_val">
					   <em>*</em>操作提示：<br>
					  &nbsp;&nbsp; a. ctrl+f 查找  ; b. F11启动全屏,Esc退出全屏
					 </span>
					</td>
				</tr>
				<tr>
					<td class="ltd">
						<span class="td_key">
					 		<button type="button" onclick="doHighlight();"   class="doHighlight">预览</button>
					 		<button type="button" onclick="closeHighlight();"class ="closeHighlight">关闭预览</button>
						</span>
					    <pre id="outputcontext" class="cm-s-default"></pre> 
				    </td>
				</tr>
			</table>
			<div class="btn_wrap"><button  type="submit" class="add_seller_btn" >保存</button></div>
			</form>
		</div>
</div>
	<script type="text/javascript" src="${static_base}/js/pager_template_codemirror.js"></script>
<@m.page_footer />