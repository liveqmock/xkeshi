<#import "/macro.ftl" as m>
<@m.page_header selected='shop' subselected= 'about' css='add_seller_new|seller_info|pos_info' />
<script>
	var IMG_BUCKET = '${img_bucket}',
		CONTEXT = '${base}',
		IMG_PATH = '${image_base}',
		UP_IMG_TYPE = 4,
		SHOP_ID = ${(shopId)!}
</script>
<script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="/static/ueditor/ueditor.all.js"></script>
<script type="text/javascript" src="/static/ueditor/lang/zh-cn/zh-cn.js"></script>
<!-- 实例化编辑器 -->
<script type="text/javascript">
	UE.getEditor('memo');
    UE.getEditor('content');
    $(function(){
	    $(".pop").click(function(){
			 $('.show_edit').hide();
			 $('.hide_edit').show();
	    })
	    $(".cancel_a").click(function(){
	    	 $('.show_edit').show();
			 $('.hide_edit').hide();
	    })
    })
</script>
<div class="rwrap">
	<div class="r_title">商户资料</div>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
		<div class="nav_wrap">
			<a href="${base}/shop/${shopId}" class="nav_a ">商户基本信息</a>
			<a href="${base}/shop/${shopId}/about" class="nav_a nav_now">关于商户</a>
			<a href="${base}/shop/${shopId}/album" class="nav_a ">相册列表</a>
			<a href="${base}/shop/${shopId}/account" class="nav_a ">商户账号</a>
			<a href="${base}/shop/${shopId}/pos" class="nav_a">POS相关</a>
		</div>
		</@shiro.hasAnyRoles>
	
		<div class="tb_wrap">
			<div class="tb_title lh20"><span class="fl">标题</span><a href="javascript:void(0);" class="pop" >修改商户资料</a></div>
			<form action="${base}/shop/${shopId}/article" method="post">
			<input type="hidden" name="id" value="${(articles[0].id)!}" />
			<table class="tb_main ">
				<tr>
					<td class="ltd">
					<span class="td_val">
						<div class="show_edit">${(articles[0].title)!}</div>
						<input type="text" name="title" value="${(articles[0].title)!}" class="ip1 hide_edit ip_tab" />
					</span>
					</td>
				</tr>
			</table>
			<div class="tb_title tb_br">简介 </div>
			<table class="tb_main">
				<tr>
					<td class="ltd">
					<div class="show_edit">${(articles[0].memo)!}</div>
					<script id="memo" name="memo" type="text/plain" class="hide_edit" style="width:855px">${(articles[0].memo)!}</script>
					</td>
				</tr>
			</table>
			<div class="tb_title tb_br">详情 </div>
			<table class="tb_main">
				<tr>
					<td class="ltd">
					<div class="show_edit">${(articles[0].content)!}</div>
					<script id="content" name="content" type="text/plain" class="hide_edit" style="width:855px;">${(articles[0].content)!}</script>
					</td>
				</tr>
			</table>
			<div class="btn_wrap hide_edit"><button class="add_seller_btn">保存</button><span>或</span><a href="javascript:" class="cancel_a">取消</a></div>
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