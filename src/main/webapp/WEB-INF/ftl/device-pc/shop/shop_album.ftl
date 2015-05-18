<#import "/macro.ftl" as m>
<@m.page_header selected='shop' subselected = "album" css='seller_info|pos_info|photo_list|shop_list'js="list_filter|photo_list" />
<script>
	var IMG_BUCKET = '${img_bucket}',
		CONTEXT = '${base}',
		IMG_PATH = '${image_base}',
		UP_IMG_TYPE = 4,
		SHOP_ID = ${(shopId)!}
</script>
  
<div class="rwrap">
	<p class="r_title">商户资料</p>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
		<div class="nav_wrap">
			<a href="${base}/shop/${shopId}" class="nav_a ">商户基本信息</a>
			<a href="${base}/shop/${shopId}/about" class="nav_a ">关于商户</a>
			<a href="${base}/shop/${shopId}/album" class="nav_a nav_now">相册列表</a>
			<a href="${base}/shop/${shopId}/account" class="nav_a">商户账号</a>
			<a href="${base}/shop/${shopId}/pos" class="nav_a">POS相关</a>
		</div>
		</@shiro.hasAnyRoles>
	<div class="tb_wrap">
		<div class="tb_title ">
		<div class="div_top ">
		<b class="show_t">显示</b>
		<form action="/shop/${shopId}/album" method="GET" class="f_form" id="formid">
			 <input type="checkbox" name="tag" id="cb1" value="全部" class="check_lis check_li_c fi_2 " <#if searcher?? && searcher.tag?? && searcher.tag?index_of("全部")!=-1> checked </#if>><label for="cb1">全部</label>
			 <input type="checkbox" name="tag" id="cb2" value="菜" class="check_lis check_li_c fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("菜")!=-1> checked </#if>><label for="cb2">菜</label>
			 <input type="checkbox" name="tag" id="cb3" value="环境" class="check_lis check_li_c fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("环境")!=-1> checked </#if>><label for="cb3">环境</label>
			 <input type="checkbox" name="tag" id="cb4" value="其他" class="check_lis check_li_c fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("其他")!=-1> checked </#if>><label for="cb4">其他</label>
		 </form>
		 </div>
		 <a class="pop_a pop_account" href="/shop/album/add/${shopId}">添加图片</a>
		 </div>
			<div class="tb_main">
				<#list pager.list as album>
					<#if album_index%4==0>
					<div class="pic_list  <#if  (((album_index)/4)?int)%2==1>  pic_list_bg </#if> ">
					</#if>
						<div class="pic_item">
							<a href="javascript:" data-src="${image_base}${album}" style="margin-right:0;"  class="pic_item_a img_a">
								<img src="${image_base}${album}!xpos.merchant.home.avatar">
							</a>
							<div class="pic_opt">
								<a href="javascript:" class="pic_del" data-albumid="${album.id}"  data-shopid="${shopId}">删除</a>
								<a href="/shop/album/editPic/${album.id}?shopId=${shopId}" class="pic_edit">修改</a>
							</div>
							<p class="pic_name">${album.description}</p>
						</div>
					<#if album_index%4==3 || (album_index+1)==pager.list?size>
					</div>
					</#if>
				</#list>
			</div>
		</div> 
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>	
	<div class="pb pop_del">
		<form name="pop_del_form" id="pop_del_form" method  ="post">
		<input type="hidden" name="_method" value="delete">
		<div class="pb_title">删除</div>
		<div class="pb_main">
			确认删除图片<span class="pb_cate_name"></span>
		</div>
		<button type="button" class="pb_btn pb_btn_s" >确定</button>
		<span class="pb_btn_split">或</span>
		<a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount />
	</div>
</div>
<@m.page_footer />