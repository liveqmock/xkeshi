<#import "/macro.ftl" as m>
<@m.page_header title="优惠活动列表" selected='coupon' subselected="couponInfoList" css="seller_list_new|coupon_list" js="coupon_list|list_filter"/>
<div class="rwrap">
	<div class="r_title"><span class="fl">电子券列表</span>
		<div class="search_wrap">
			<form class="search_form fl" action="${base}/coupon/couponInfo/list">
				<input type="text" name="key" <#if (searcher.key)?? >value="${searcher.key}"</#if> placeholder="电子券名称" class="search_input"/>
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="filter_a fl"></a>
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
				<a href="${base}/coupon/add" class="new_a  fl ml12"></a>
			</@shiro.hasAnyRoles>
		</div>
		<#if searcher != null && searcher.hasParameter >
		<div class="search_result">
			<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
				<#if searcher.key><span class="b_tit b_tit1" data-fid="1">电子券名称：${searcher.key}<b>x</b></span></#if>
				<#if searcher.serial><span class="b_tit b_tit1" data-fid="2">电子券编号：${searcher.serial}<b>x</b></span></#if>
				<#if searcher.status??><span class="b_tit b_tit1" data-fid="3">状态：<#if searcher.status?contains("PUBLISHED_VISIBLE")>已发布(公开)</#if>  <#if searcher.status?contains("PUBLISHED_UNVISIBLE")>已发布(隐藏)</#if>  <#if searcher.status?contains("UNPUBLISHED")>未发布</#if><b>x</b></span></#if>
				</span>的结果</span>
			</p>
			<a href="${(base)!}/coupon/couponInfo/list" class="s_clear">清空</a>
		</div>
		</#if>
	</div>
	<table class="tb_main tb_main1 tb_cte"> 
	<tbody>
		<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter)>
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>您还没有创建电子券，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">创建新的电子券吧</div>
		<#else>
		<tr class="th">
			<td class="" style="width:70px;">电子券编号</td>
			<td class="name" style="width:100px;">电子券名称</td>
			<td class="state" style="width:60px;">状态</td>
			<td class="time widen">有效期</td>
			<td class="addr" style="width:72px;">总数/已使用</td>
			<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
			<td class="cate">适用商户简称</td>
			</@shiro.hasAnyRoles>
		</tr>
		</#if>
		<#list pager.list as couponInfo>
		<tr <#if couponInfo_index%2==0>class="tr_bg th1"</#if>>
			<td class="">${(couponInfo.eid)!}</td>
			<td class="name"><a class="b_a" href="/coupon/detail/${(couponInfo.eid)!}"><#if (couponInfo.name)?length == 0 >(空)<#else>${(couponInfo.name)!'-'}</#if></a></td>
			<td class="state"><#if !couponInfo.published ><span class="state2">未发布</span><#else><span class="state1">已发布(<#if couponInfo.visible>公开<#else>隐藏</#if>)</span></#if></td>
			<td class="time"><#if (couponInfo.startDate)??>${couponInfo.startDate?string('yyyy/MM/dd')}</#if>-<#if (couponInfo.endDate)??>${couponInfo.endDate?string('yyyy/MM/dd')}</#if></td>
			<td class="addr"><#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount}</#if>/${couponInfo.received}</td>
			<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
			<td class="cate">
			    <#assign applicable="true">
				<#list applicableShops as shop>
					  <#if (couponInfo.scope)?? && couponInfo.scope?seq_contains(shop.id)>
					  	${shop.name}  <#assign applicable="false">
					  </#if>
				</#list>
				<#if applicable == "true" >
				    没有适用商户
				 </#if>
			</td>
			</@shiro.hasAnyRoles>
		</tr>
		</#list>
	</tbody>
	</table>
	<style>
	.state span{
	width:94px;
	}
	</style>
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount />
	</div>
	<div class="pb pop_filter">
		<form action="${base}/coupon/couponInfo/list" method="get" class="f_form">
		<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
		<div class="pb_title">筛选</div>
		<div class="pb_main pb_main_b">
			<div class="pb_item">
				<p class="pb_item_title">电子券编号</p>
				<input type="text" class="pb_item_input fi_2" name="serial" value="<#if (searcher.serial)?? >${searcher.serial}</#if>" />
			</div>
			<div class="pb_item pb_item_b">
				<p class="pb_item_title">按状态</p>
				<input type="checkbox" id="cb1" class="pfck fi_3" name="status" value="PUBLISHED_VISIBLE" <#if searcher.status?? && searcher.status?contains("PUBLISHED_VISIBLE")>checked=true</#if>><label class="pflb" for="cb1">已发布(公开)</label>
				<input type="checkbox" id="cb2" class="pfck fi_3" name="status" value="PUBLISHED_UNVISIBLE" <#if searcher.status?? && searcher.status?contains("PUBLISHED_UNVISIBLE")>checked=true</#if>><label class="pflb" for="cb2">已发布(隐藏)</label>
				<input type="checkbox" id="cb3" class="pfck fi_3" name="status" value="UNPUBLISHED" <#if searcher.status?? && searcher.status?contains("UNPUBLISHED")>checked=true</#if>><label class="pflb" for="cb3">未发布</label>
			</div>
			<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
				<div class="pb_item">
					<p class="pb_item_title">适用商户简称</p>
					<input type="text" class="pb_item_input">
				</div>
			</@shiro.hasAnyRoles>
		</div>
		<button type="button" class="pb_btn pb_btn_s pb_btn_filter">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>		
	</div>
	

	
</div>
<@m.page_footer />

