<#import "/macro.ftl" as m>
<@m.page_header selected='physical_coupon' subselected='list' title='商户实体券管理' js='member_type_info|list_filter' css='seller_list_new|set_mime|voucher_manage'/>
<div class="rwrap">
	<div class="r_title"><span class="fl">商户实体券管理</span>
	<div class="search_wrap">
	<form class="search_form f_form fl" action="${base}/physical_coupon/shop/list">
			<input type="text" name="name" <#if (searcher.name)?? >value="${searcher.name}"</#if> placeholder="实体券名称" class="search_input" id="fi_1">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:" class="pop_a filter_a fl" data-pop="pop_filter"></a>		
		<a href="${base}/physical_coupon/add" class="new_a fl ml12" ></a>
	</div>
		<#if searcher != null && searcher.hasShopParameter >
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if searcher.name><span class="b_tit" data-fid="1">实体券名称：${(searcher.name)!}<b>x</b></span></#if>
					<#if searcher.businessType><span class="b_tit" data-fid="3">来源商户：
						<#if (searcher.businessType)!  == 'MERCHANT' >集团</#if> 
						<#if (searcher.businessType)!  == 'SHOP'  && (searcher.businessId)! == shop.id >${shop.name}</#if>
						<b>x</b></span>
					</#if> 
					<#if searcher.enables><span class="b_tit" data-fid="2">状态：
					<#if searcher.enables?seq_contains(1) >开启</#if>
					<#if searcher.enables?seq_contains(0) >暂停</#if>
					 <b>x</b></span></#if>
					</span>的结果</span>
				</p>
				<a href="${base}/physical_coupon/shop/list" class="s_clear">清空</a>
			</div>
		</#if>
	</div>	
	<table class="tb_main">
		<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter) >
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>您还没有创建实体券，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">按钮创建新的实体券吧</div>
		<#else>
			<tr class="th">
				<td class="id">编号</td>
				<td class="value">面额（元）</td>
				<td class="act_name">名称</td>
				<td class="status">状态</td>
				<td class="from">实体券来源</td>
				<td class="operate">操作</td>			
			</tr>
			<#list pager.list as physicalCoupon>
				<tr <#if physicalCoupon_index%2==0>class="tr_bg"</#if>>
					<td class="">${physicalCoupon_index+1}</td>
					<td class="value">￥${(physicalCoupon.amount)!'0.00'}</td>
					<td class="act_name">${(physicalCoupon.name)!}</td>
					<td class="status"><#if (physicalCoupon.enable)! == true>启动<#else>暂停</#if></td>
					<td class="from">${(physicalCoupon.shopName)!}</td>
					<td class="operate">
					<#if physicalCoupon.business_type == 'SHOP'>
						<a href="/physical_coupon/${physicalCoupon.id}/update" class="b_a">修改</a>
						<#if (physicalCoupon.enable)! == true>
						<a href="/physical_coupon/${physicalCoupon.id}/open?enable=false" class="b_a">暂停</a>
						<#else>
						<a href="/physical_coupon/${physicalCoupon.id}/open?enable=true" class="b_a">启动</a>
						</#if>
						<a href="javascript:" class="del_attribute b_a pop_a" data-pop="pop_del" data-para="/physical_coupon/${physicalCoupon.id}/delete">删除</a></td>	
					</#if>
				</tr>
			</#list>
		</#if>
	</table>
	<div class="pb pop_filter">
			<form action="${base}/physical_coupon/shop/list"  method="get" class="f_form">
			<input type="hidden" name="name" value="<#if (searcher.name)?? >${searcher.name}</#if>" class="fi_1"/>
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title" name="from">来源商户</p>
					<select class="select-1 pb_sel fi_3" name="businessId">
							<option value="">请选择...</option>
							<option value="-1"  <#if (searcher.businessType)!  == 'MERCHANT' >selected="selected"</#if>   >集团</option>
							<option value="${(shop.id)!}" <#if (searcher.businessType)!  == 'SHOP'  && (searcher.businessId)! == shop.id >selected="selected"</#if>  >${shop.name}</option>
					</select>		
				</div>
				<div class="pb_item">
					<p class="pb_item_title" name="status">状态</p>
					<input type="checkbox" name="enables" id="start" value=1 <#if searcher.enables?? && searcher.enables?seq_contains(1) >checked=true</#if> class="check_lis check_li_c fi_2" ><label for="start">开启</label>					
					<input type="checkbox" name="enables" id="stop" value=0 <#if searcher.enables?? && searcher.enables?seq_contains(0) >checked=true</#if> class="check_lis check_li_c fi_2" ><label for="stop">暂停</label>	
				</div>			
			</div>
			<button type="submit" class="pb_btn pb_btn_s">确定</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>		
			</form>
		</div>
		<div class="pb pop_del">
			<form action="" method="post">
				<input type="hidden" name="_method" value="delete"/> 
				<div class="pb_title">删除</div>
				<div class="pb_main">确认删除实体券?</div>
				<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  	
		</div>
</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />