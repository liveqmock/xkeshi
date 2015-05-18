<#import "/macro.ftl" as m>
<@m.page_header selected='activity' css="seller_list_new|act_list_new" js="activity_list|list_filter" title="活动列表"/>
<div class="rwrap">
	<div class="r_title">活动列表
		<div class="search_wrap">
			<form class="search_form" action="${base}/activity/list">
				<input type="text" name="key" <#if (searcher.key)?? >value="${searcher.key}"</#if> placeholder="活动名称" class="search_input"/>
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="filter_a"></a>
			<a href="${base}/activity/create" class="new_a"></a>
		</div>
		<#if searcher != null && searcher.hasParameter >
		<div class="search_result">
			<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
				<#if searcher.key><span class="b_tit" data-fid="1">活动名称：${searcher.key}<b>x</b></span></#if>
				<#if searcher.serial><span class="b_tit" data-fid="2">活动编号：${searcher.serial}<b>x</b></span></#if>
				<#if searcher.published??><span class="b_tit" data-fid="3">状态：<#if searcher.published?contains("true")>发布</#if>  <#if searcher.published?contains("false")>未发布</#if><b>x</b></span></#if>
				</span>的结果</span>
			</p>
			<a href="${(base)!}/activity/list" class="s_clear">清空</a>
		</div>
		</#if>
	</div>
	<table class="tb_main">
		<tr class="th">
			<td class="id">活动编号</td>
			<td class="name">活动名称</td>
			<td class="state">状态</td>
			<td class="time">有效期</td>
			<td class="cate">适用商户简称</td>
			<td class="addr">电子券</td>
		</tr>
		<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter)>
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>您还没有创建活动，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">按钮创建新的活动吧</div>
		</#if>
		<#list pager.list as activity>
		<tr <#if activity_index%2==0>class="tr_bg"</#if>>
			<td class="id">${(activity.eid)!}</td>
			<td class="name"><a class="b_a" href="/activity/detail/${(activity.eid)!}">${(activity.name)!}</a></td>
			<td class="state"><#if (activity.published)!><span class="state1">已发布</span><#else><span class="state2">未发布</span></#if></td>
			<td class="time"><#if (activity.startDate)??>${activity.startDate?string('yyyy/MM/dd')}</#if>-<#if (activity.endDate)??>${activity.endDate?string('yyyy/MM/dd')}</#if></td>
			<td class="cate">-</td>
			<td class="addr"><#if (activity.couponInfos)??>${activity.couponInfos?size}<#else>0</#if>组</td>
		</tr>
		</#list>
	</table>
		
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount />
	</div>
	<div class="pb pop_filter">
		<form action="${base}/activity/list" method="get" class="f_form">
		<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
		<div class="pb_title">筛选</div>
		<div class="pb_main">
			<div class="pb_item">
				<p class="pb_item_title">活动编号</p>
				<input type="text" class="pb_item_input fi_2" name="serial" value="<#if (searcher.serial)?? >${searcher.serial}</#if>">
			</div>
			<div class="pb_item">
				<p class="pb_item_title">状态</p>
				<input type="checkbox" class="pfck fi_3" id="state_cb1" name="published" value="false" <#if searcher.published?? && searcher.published?contains("false")>checked=true</#if>><label class="pflb" for="state_cb1">未发布</label>
				<input type="checkbox" class="pfck fi_3" id="state_cb2" name="published" value="true" <#if searcher.published?? && searcher.published?contains("true")>checked=true</#if>><label class="pflb" for="state_cb2">发布</label>
			</div>
			<div class="pb_item">
				<p class="pb_item_title">适用商户简称</p>
				<input type="text" class="pb_item_input" id="fi_4">
			</div>
		</div>
		<button class="pb_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	
</div>
<@m.page_footer />



