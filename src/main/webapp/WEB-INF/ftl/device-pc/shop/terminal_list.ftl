<#import "/macro.ftl" as m >
<@m.page_header selected='terminal' css="seller_list_new"  js = "terminal_manage|My97DatePicker/WdatePicker|list_filter" />
<div class="rwrap">
		<div class="r_title"><span class="fl">
			POS机终端列表</span>
			<div class="search_wrap">
				<form action="${base}/shop/terminal/list" class="search_form f_form fl">
					<input type="text" placeholder="设备号/商户ID/商户简称" class="search_input" maxlength="40"  name="key"  id="fi_1"
					<#if searcher&&searcher.key>value="${searcher.key}"</#if>>
					<button class="search_btn"></button>
			    </form>
				<a href="javascript:" class="filter_a fl"></a>
			</div>
			<#if searcher != null && searcher.hasParameter >
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if searcher.key><span class="b_tit b_tit1" data-fid="1">设备号/商户ID/商户简称：${searcher.key}<b>x</b></span></#if>
					<#if searcher.lastStartLogin><span class="b_tit b_tit1" data-fid="2">最后登录时间起：${(searcher.lastStartLogin?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					<#if searcher.lastEndLogin><span class="b_tit b_tit1" data-fid="3">最后登录时间止：${(searcher.lastEndLogin?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					</span><span style="padding-left:10px;">的结果</span>
				</p>
				<a href="${(base)!}/shop/terminal/list" class="s_clear">清空</a>
			</div>
			</#if>
		</div>
		<table class="tb_main">
			<tr class="th">
				<td class="shop_id">商户ID</td>
				<td class="name">商户简称</td>
			    <td class="device_number">设备号</td>
				<td class="last_time">最后登录时间</td>
				<td class="create_time">创建时间</td>
			</tr>
			<#list pager.list as terminal>
            <#if terminal.shop??>
                <tr <#if terminal_index%2==0>class="tr_bg"</#if>>
                    <td class="shop_id">${(terminal.shop.id)!}</td>
                    <td class="name"><a href="${base}/shop/${terminal.shop.id}" class="b_a">${(terminal.shop.name)!}</a></td>
                    <td class="device_number"><a href="${base}/shop/${terminal.shop.id}/pos" class="b_a">${(terminal.deviceNumber)!}</a></td>
                    <td class="last_time">${(terminal.lastLogin?string('yyyy-MM-dd HH:mm:ss'))!'-'}</td>
                    <td class="create_time">${(terminal.createDate?string('yyyy-MM-dd HH:mm:ss'))!'-'}</td>
                </tr>
            </#if>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>
	 <div class="pb pop_filter">
			<form action="${base}/shop/terminal/list" method="get" class="f_form">
			<input type="hidden" name="key" value="<#if (searcher.key)?? >${searcher.key}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main pb_main_b">
				<div class="pb_item">
					<p class="pb_item_title">最后登录时间起:</p>
					<input type="text" class="pb_item_input  f_input fi_2" id="fi_2" name="lastStartLogin"  value="${(searcher.lastStartLogin?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
				<div class="pb_item pb_item_b">
					<p class="pb_item_title">最后登录时间止:</p>
					<input type="text" class="pb_item_input  f_input fi_3" id="fi_3"name="lastEndLogin" value="${(searcher.lastEndLogin?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
			</div>
			<button class="pb_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>		
	</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
	
	
</div>
<@m.page_footer />




