<#import "/macro.ftl" as m>

<@m.page_header title="会员类型" selected='member' subselected='type' css='seller_list_new|member_type_info'  js="merchant_subshop_member_tyep_info"  />
<script>
 var shopEid = "${(shop.eid)!}";
 var IMAGE_BASE = "${image_base}";
</script>
<div class="rwrap">
	<div class="r_title">会员类型管理
		<div class="search_wrap">
			<form action="${base}/member/merchant/${(shop.eid)!}/type/list" class="search_form f_form">
				<input type="text" name="key" value="${(searcher.key)!}" placeholder="会员类型" class="search_input" id="fi_1">
				<button class="search_btn"></button>
			</form>
			<a href="javascript:" class="new_a"></a>
		</div>
		<#if searcher != null && searcher.hasParameter>
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if searcher.key><span class="b_tit" data-fid="1">${(searcher.key)!}<a href="${(base)!}/member/merchant/${(shop.eid)!}/type/list"><b>x</b></a></span></#if>
					</span>的结果</span>
				</p>
				<a href="${(base)!}/member/merchant/${(shop.eid)!}/type/list" class="s_clear">清空</a>
			</div>
		</#if>
	</div>
	<table class="tb_main">
		<#if (!pager.list?? || pager.list?size==0) && ( searcher == null || !searcher.hasParameter)>
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>该子商户还没有创建会员模板，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">按钮创建新的会员模板吧</div>
		<#else>
			<tr class="th">
				<td class="id">编号</td>
				<td class="name">名称</td>
				<td class="discount">折扣</td>
				<td class="name">默认会员类型</td>
				<td class="createDate">创建时间</td>
				<td class="modifyDate">修改时间</td>
				<td class="opt">操作</td>
			</tr>
		</#if>
		<#list pager.list as memberType>
			<tr <#if memberType_index%2==0>class="tr_bg"</#if>>
				<td class="id">${memberType_index+1}</td>
				<td class="name">${(memberType.name)!}</td>
				<td class="discount">
					<#if memberType.discount == null || memberType.discount <= 0>
						未设置
					<#elseif (memberType.discount >= 1)>
						10折(无优惠)
				 	<#else>
						${memberType.discount * 10}折
					</#if>
				</td>
				<td class="name"><#if (memberType.default)!>是<#else>否</#if></td>
				<td class="createDate">${(memberType.createdTime?string('YY-MM-dd HH:mm'))!'-'}</td>
				<td class="modifyDate">${(memberType.updatedTime?string('YY-MM-dd HH:mm'))!'-'}</td>
				<td class="opt" style="white-space: nowrap">
					<a href="javascript:void(0)" class="updateType b_a" data-id="${(memberType.id)!}"
						data-name="${(memberType.name)!}" data-discount="${(memberType.discount * 10)!}"
						data-memberTemplateId="${(memberType.memberAttributeTemplate.id)!}"  data-cover_picture = "${(memberType.coverPicture)!}"
						data-defaulted="${(memberType.default?string('true','false'))!}"
						>修改</a>
					<#if !memberType.default>
							<a href="javascript:void(0);" class="b_a pop_a" data-pop="pop_del" data-para="/member/merchant/${(shop.eid)!}/type/delete/${(memberType.id)!}" data-name="${(memberType.name)!}"
							data-defaulted = "${(memberType.default?string('true','false'))!}"
							title="只有没有用户使用的会员类型才能被删除">删除</a>
					</#if>
				</td>
			</tr>
		</#list>
	</table>
	<div class="pb add_opt">
		<form action="${base}/member/merchant/${(shop.eid)!}/type/add" method="POST" id="typeForm" name="typeForm" enctype="multipart/form-data">
			<div class="pb_title"><span class = "pb_title_sp"></span></div>
			<input type="hidden" name="submissionToken" value="${submissionToken}"/>
			<input type="hidden"  name="memberAttributeTemplate.id"  value="${(template.id)!}" >
			<input type="hidden"  name="shop.id" value="${(shop.id)!}" >
			<input type="hidden"  name="id" id="typeId">
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">名称</p>
					<input type="text" class="pb_item_input" name="name" id="nameId" placeholder="会员类型名称">
				</div>
				<div class="pb_item  pb_discount" >
					<p class="pb_item_discount_title">折扣</p>
					<input type="text" class="pb_item_input" name="discount" id="discountId" placeholder="0.00~10.0折">
				</div>
				<div class="pb_item pb_defaulted">
					<p class="pb_title2">设置</p>
					<div class="pb_rd_item">
						<input type="checkbox" id="set_cb1" class="pb_rd" name="default" value="true"><label for="set_cb1"  class="lab_defaulted">默认会员类型</label>
					</div>
				</div>
				<div class="pb_item pb_card">
					<p class="pb_item_card_title">会员卡</p>
					<input type="file" name="coverPictureFile" id="cover_picture" class="pb_item_input " value="" data-msg ="会员卡">
					<a href="${image_base}" target="_blank"  class="pb_input show_cover_picture">点击查看会员卡</a>建议尺寸:555×330
				</div>
			</div>
			<button class="pb_btn pb_btn_s" type="button" onclick="return saveOp();">保存</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_del">
		<form action="" method="POST">
			<div class="pb_title"><span class="pb_title_sp">删除会员类型提示</span></div>
			<div class="pb_main">
				<p class="pb_item_title">确认删除:<span class="pb_name"></span></p>
			</div>
			<button class="pb_btn pb_btn_s">确认删除</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount/>
	</div>
	
</div>

	 <#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
	
<@m.page_footer />
