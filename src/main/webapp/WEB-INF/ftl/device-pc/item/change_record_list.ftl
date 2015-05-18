<#import "/macro.ftl" as m>
<@m.page_header title="出入库记录" subselected='record' css='seller_list_new|account|record_list' js='My97DatePicker/WdatePicker|validate|list_filter|record_list' selected='cashiering' />
<div class="rwrap">
		<div class="r_title"><span class="fl">出入库记录</span><div class="search_wrap">
		<form action="${base}/inventory/recordlist" class="search_form check_form fl">
			<input name="accountName" value="${(searcher.accountName)!}" type="text" class="search_input fi_1" placeholder="搜索操作员名字" data-maxlen="8" data-hint="操作员">
			<button class="search_btn"></button>
		</form>
		<a class="pop_a filter_a fl" href="javascript:" data-pop="pop_filter"></a>
		<#if request.queryString?has_content>
			<#assign exportParams = '?' + request.queryString />
		</#if>
		<#--<a class="pop_a export_a" href="${base}/inventory/record/export${exportParams}" ></a>--></div></div>
		<#if searcher != null && searcher.hasParameter >
			<div class="search_result">
				<p class="result_tit">
				<span class="rt_title">
					<span class="result_tit_text">搜索/筛选</span>
				</span>
					<#if searcher.accountName>
				<span class="b_tit b_tit1" data-fid="1">操作员：${searcher.accountName}<b>x</b></span></#if>
					<#if searcher.shopName><span class="b_tit b_tit1" data-fid="2">操作商户：${searcher.shopName}<b>x</b></span></#if>
					<#if searcher.startDateTime><span class="b_tit b_tit1 " data-fid="5">起始操作时间：${searcher.startDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					<#if searcher.endDateTime><span class="b_tit b_tit1 ml74" data-fid="6">截止操作时间：${searcher.endDateTime?string('yyyy-MM-dd HH:mm:ss')}<b>x</b></span></#if>
					</span>的结果</span>
				</p>
				<a href="${base}/inventory/recordlist" class="s_clear">清空</a>
			</div>
			</#if>
		<div class="opt_wrap" style="color:#666667;">共<span style="color:#009ce5;">${(records.totalCount)!'0'}</span>笔出入库操作 </div>
		<table class="tb_main tb_record">
			<tr class="th">
				<td class="date" id="handle_date">操作时间</td>
				<td class="trans_num">出/入库商品种类</td>
				<td class="m_num" id="record_num">出库数量</td>
				<td class="pay_way" id="record_num1">入库数量</td>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<td class="telephone">操作商户/操作员</td>
				</@shiro.hasAnyRoles>
				<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
					<td class="telephone">操作员</td>
				</@shiro.hasAnyRoles>
			</tr>
			<#list records.list as record>
				<tr class="tr <#if record_index%2==0>tr_bg</#if>" data-url="${base}/inventory/detaillist/${record.id}">
					<td class="date">${(record.createdDate?string("yyyy-MM-dd HH:mm:ss"))!}</td>
					<td class="trans_num">${record.exportItemQuantity}/${record.importItemQuantity}</td>
					<td class="m_num">${record.exportTotalQuantity}</td>
					<td class="trade_type">${record.importTotalQuantity}</td>
					<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
						<td class="telephone">${record.shop.name}/${record.account.username}</td>
					</@shiro.hasAnyRoles>
					<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
						<td class="telephone">${record.account.username}</td>
					</@shiro.hasAnyRoles>
				</tr>
			</#list>
		</table>
		<div class="page_wrap">
			<@m.p page=records.pageNumber totalpage=records.pageCount />  
		</div>
		<div class="pb pop_filter">
			<form action="${base}/inventory/recordlist" class="f_form">
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<div class="pb_item">
						<p class="pb_item_title">操作商户</p>
						<input type="text" class="pb_item_input f_input fi_2" name="shopName" value="<#if (searcher.shopName)?? >${searcher.shopName}</#if>"/>
					</div>
				</@shiro.hasAnyRoles>
				<div class="pb_item">
					<p class="pb_item_title">起始操作时间</p>
					<input type="text" class="tcal_time pb_item_input f_input fi_5" name="startDateTime" id="set_time1" value="<#if (searcher.startDateTime)?? >${searcher.startDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">截止操作时间</p>
					<input type="text" autocomplete="off" class="tcal_time pb_item_input f_input fi_6" name="endDateTime" id="set_time2" value="<#if (searcher.endDateTime)?? >${searcher.endDateTime?string('yyyy-MM-dd HH:mm:ss')}</#if>"/>
				</div>
			</div>
			<button class="pb_btn record_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
	</div>
<@m.page_footer />