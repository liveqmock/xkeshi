<#import "/macro.ftl" as m>
<@m.page_header selected='coupon' subselected="couponInfoPackageList" css="seller_list_new|ticket_list" js="list_filter" title="电子券套票列表"/>
<div class="rwrap">
	<div class="r_title"><span class="fl">电子券套票列表</span>
		<div class="search_wrap">
			<form class="search_form fl" action="${base}/coupon/couponInfoPackage/list">
				<input type="text" name="key" <#if (searcher.key)?? >value="${searcher.key}"</#if> placeholder="套票名称/电子券名称" class="search_input"/>
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="pop_a filter_a fl" data-pop="pop_filter "></a>
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
				<a href="${base}/coupon/package/add" class="new_a fl ml12"></a>
			</@shiro.hasAnyRoles>
		</div>
		<#if searcher != null && searcher.hasParameter >
		<div class="search_result">
			<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
				<#if searcher.key><span class="b_tit b_tit1" data-fid="1">套票名称/电子券名称：${searcher.key}<b>x</b></span></#if>
				<#if searcher.serial><span class="b_tit b_tit1" data-fid="2">套票编号：${searcher.serial}<b>x</b></span></#if>
				<#if searcher.status??><span class="b_tit b_tit1" data-fid="3">状态：<#if searcher.status?contains("PUBLISHED_VISIBLE")>已发布(公开)</#if>  <#if searcher.status?contains("PUBLISHED_UNVISIBLE")>已发布(隐藏)</#if>  <#if searcher.status?contains("UNPUBLISHED")>未发布</#if><b>x</b></span></#if>
				</span>的结果</span>
			</p>
			<a href="${(base)!}/coupon/couponInfoPackage/list" class="s_clear">清空</a>
		</div>
		</#if>
	</div>
	<table class="tb_main tb_main1 tb_cte"> 
		<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter)>
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>您还没有创建电子券套票，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">按钮创建新的电子券套票</div>
		<#else>
		<tr class="th th1">
			<td class="id">编号</td>
			<td class="name" style="padding-left:16px;">套票名称</td>
			<td class="state">状态</td>
			<td class="date">有效期</td>
			<td class="" style="width:30px;">总数</td>
		</tr>
		</#if>
		<#list pager.list as couponInfo>
		<tr <#if couponInfo_index%2==0>class="tr_bg th1"<#else>class="tr "</#if>>
			<td class="">${(couponInfo.eid)!}</td>
			<td class="name" style="padding-left:16px;">
				<p class="tk_title"><a class="b_a" href="/coupon/detail/package/${(couponInfo.eid)!}"><#if (couponInfo.name)?length == 0 >(空)<#else>${(couponInfo.name)!'-'}</#if></a></p>
				<#list couponInfo.items as item>
					<p class="tk_item"><em>- ${item.name} / ${item.quantity}份</em>（适用<#list item.scope as shopId> ${applicableShopMap[''+shopId].name} </#list>）</p>
				</#list>
			</td>
			<td class="state"><#if !couponInfo.published><span class="state2">未发布</span><#else><span class="state1">已发布(<#if couponInfo.visible>公开<#else>隐藏</#if>)</span></#if></td>
			<td class="date"><#if (couponInfo.startDate)??>${couponInfo.startDate?string('yyyy/MM/dd')}</#if>-<#if (couponInfo.endDate)??>${couponInfo.endDate?string('yyyy/MM/dd')}</#if></td>
			<td class=""><#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount}</#if></td>
		</tr>
		</#list>
	</table>
		
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount />
	</div>
	<div class="pb pop_filter">
		<form action="${base}/coupon/couponInfoPackage/list" method="get" class="f_form">
		<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
		<div class="pb_title">筛选</div>
		<div class="pb_main pb_main_b">
			<div class="pb_item">
				<p class="pb_title2">套票编号</p>
				<input type="text" class="pb_input fi_2" name="serial" value="<#if (searcher.serial)?? >${searcher.serial}</#if>"/>
			</div>
			<div class="pb_item pb_item_b">
				<p class="pb_title2">按状态</p>
					<div class="pb_cb_item"><input type="checkbox" id="cb1" class="pb_cb fi_3" name="status" value="PUBLISHED_VISIBLE" <#if searcher.status?? && searcher.status?contains("PUBLISHED_VISIBLE")>checked=true</#if>><label class="pb_lb" for="cb1">已发布(公开)</label></div>
					<div class="pb_cb_item"><input type="checkbox" id="cb2" class="pb_cb fi_3" name="status" value="PUBLISHED_UNVISIBLE" <#if searcher.status?? && searcher.status?contains("PUBLISHED_UNVISIBLE")>checked=true</#if>><label class="pb_lb" for="cb2">已发布(隐藏)</label></div>
					<div class="pb_cb_item"><input type="checkbox" id="cb3" class="pb_cb fi_3" name="status" value="UNPUBLISHED" <#if searcher.status?? && searcher.status?contains("UNPUBLISHED")>checked=true</#if>><label class="pb_lb" for="cb3">未发布</label></div>
			</div>
		</div>
		<button type="button" class="pb_btn pb_btn_s pb_btn_filter">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>		
	</div>
	

	
</div>
<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
</#if>
<#if (status == "success")!>
	<div class="pop_hint pop_hint2">${(msg)!}</div>
</#if>
<@m.page_footer />

