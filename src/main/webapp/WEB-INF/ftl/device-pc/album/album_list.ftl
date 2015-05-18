<#import "/macro.ftl" as m >

<@m.page_header title="相册" selected='shop' subselected='album' css="admin_frame|photo_list"  js="list_filter|photo_list"/>
<div class="rwrap">
		<div class="r_title"><span class="fl">相册</span>
			<div class="search_wrap">
				<form action="${base}/album/list" class="search_form fl tab_tips">
					<input type="text" class="search_input" name="key" <#if searcher?? && searcher.key?? >value="${searcher.key}"</#if> placeholder="图片名称">
					<button class="search_btn fr"></button>
				</form>
				<a class="filter_a fl smr0" href="javascript:"></a><a class="new_a fl ml12" href="/album/add"></a>
			</div>
			<#if searcher?? && searcher.hasParameter()>
			<div class="search_result ofw">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<div>
					<#if searcher.key><span class="b_tit" data-fid="2">图片名称：${searcher.key}<b>x</b></span></#if>
					<#if searcher.tag><span class="b_tit" data-fid="2">状态：${searcher.tag}<b>x</b></span></#if>
					</div>
					<span class="w_tit">的结果</span>
				</p>
				<a href="${(base)!}/album/list" class="s_clear">清空</a>
			</div>
			</#if>
		</div>
		<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter() )>
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>您还没有上传图片，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">按钮上传图片吧</div>
		</#if>
		<div class="pic_wrap">
				<#list pager.list as album>
				<#if album_index%4==0>
				<div class="pic_list<#if (((album_index)/4)?int)%2==1> pic_list_bg </#if>">
				</#if>
					<div class="pic_item">
						<a class="l_a img_a" href="javascript:" data-src="${image_base}${album}" >
							<img src="${image_base}${album}!xpos.merchant.home.avatar">
						</a>
						<div class="pic_opt">
							<a href="/album/editPic/${album.id}" class="pic_edit">修改</a>
							<a href="javascript:" class="pic_del" data-albumid="${album.id}" data-shopid="${shopId}">删除</a>
						</div>
						<p class="pic_name">${album.description}</p>
					</div>
				<#if album_index%4==3 || (album_index+1)==pager.list?size>
				</div>	
				</#if>		
				</#list>
		</div>
		
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>	
		<div class="pb pop_del">
			<form name="pop_del_form" id="pop_del_form" method="post">
			<input type="hidden" name="_method" value="delete">
			<div class="pb_title">删除</div>
			<div class="pb_main">
				确认删除图片<span class="pb_cate_name"></span>
			</div>
			<input type="hidden" name="id">
			<button type="button" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
		<div class="pb pop_filter">
			<form action="/album/list" method="POST" class="f_form">
				<!--<input type="hidden" class="search_input fi_1" name="key" <#if searcher?? && searcher.key?? >value="${searcher.key}"</#if> placeholder="图片名称">-->
				<div class="pb_title">筛选</div>
				<div class="pb_main">
					<div class="pf_title">分类</div>
					<div class="ck_list">
						<input type="checkbox" name="tag" id="cb1" value="全部" class="pfck fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("全部")!=-1> checked </#if>>
						<label for="cb1">全部</label>
						<input type="checkbox" name="tag" id="cb2" value="菜" class="pfck fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("菜")!=-1> checked </#if>>
						<label for="cb2">菜</label>
					</div>
					<div class="ck_list">
						<input type="checkbox" name="tag" id="cb3" value="环境" class="pfck fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("环境")!=-1> checked </#if>>
						<label for="cb3">环境</label>
						<input type="checkbox" name="tag" id="cb4" value="其他" class="pfck fi_2" <#if searcher?? && searcher.tag?? && searcher.tag?index_of("其他")!=-1> checked </#if>>
						<label for="cb4">其他</label>
					</div>
				</div>
				<button type="submit" class="pb_btn pb_btn_s">确定</button>
			    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
	</div>
<@m.page_footer />




