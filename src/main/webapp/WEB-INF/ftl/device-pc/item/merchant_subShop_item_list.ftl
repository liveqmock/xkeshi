<#import "/macro.ftl" as m>
<@m.page_header title="商品管理" subselected='merchant' css='seller_list_new|product_list' js='validate|product_list|list_filter' selected='cashiering' />
<div class="rwrap">
	<div class="r_title"><span class="fl">商品管理</span>
	<div class="search_wrap">
		<form action="${base}/item/merchant/${shop.eid!0}/list" class="search_form check_form fl" >
			<input type="text" name="key" value="${(searcher.key)!}" placeholder="搜索商品名称" class="search_input fi_1" data-maxlen="8" data-hint="商品名称">
			<button class="search_btn"></button>
		</form>
		<a class="pop_a filter_a fl" href="javascript:" data-pop="pop_filter"></a>
		<a href="${base}/item/merchant/${shop.eid!0}/add" class="new_a fl ml12"></a>
   </div>
		<#if searcher != null && searcher.hasParameter >
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if searcher.key><span class="b_tit" data-fid="1">商品关键字：${(searcher.key)!}<b>x</b></span></#if>
					<#if searcher.categoryId><span class="b_tit" data-fid="2">分类：
					
					<#list categories as category>
							<#if (category.id == searcher.categoryId)!>
								${category.name}
							</#if>
					</#list>
					<b>x</b></span></#if>
					<#if (searcher.marketable)??><span class="b_tit" data-fid="3">是否上架：
					<#if searcher.marketable>已上架<#else>未上架</#if>
					<b>x</b></span></#if>
					<span>的结果<#if items>，共为您找到<span style="color: #428bca;padding:0 5px;font-weight:bold;">${items.list?size}</span>件符合的商品</#if></span>
				</p>
				<a href="${(base)!}/item/merchant/${shop.eid!0}/list" class="s_clear">清空</a>
			</div>
		</#if>
   </div>	
	<table class="tb_main">
		<tr class="th">
			<td class="id">顺序</td>
			<td class="img">图片</td>
			<td class="name">商品名称</td>
			<td class="state">单价</td>
			<td class="state" style="width:60px;">打印档口</td>
			<td class="date">库存</td>
			<td class="sum">分类</td>
			<td class="sum sum1">是否上架</td>
			<td class="opt">操作</td>
		</tr>
		<#list items.list as item>
		<tr  <#if item_index%2==0>class="tr_bg"<#else> class="tr"</#if> data-id="${item.id}">
			<td class="">${(item.sequence)!}</td>
			<td class="img">
				<img src="${image_base}${(item.cover)!}!100" width="70" height="40" />
			</td>
			<td class="name">${(item.nameOmit)!}</td>
			<td class="price">${(item.price)!'-'}元</td>
			<td class="">
				<#if printers >
					<#list printers as printer>
						<#if item.printerId == printer.id>${(printer.name)!}</#if>
					</#list>
				</#if>
			</td>
			<td class="amount">${(item.itemInventory.inventory)!}</td>
			<td class="cat">${(item.category.name)!}</td>
			<td class="ison"><#if item.marketable>已上架<#else>未上架</#if></td>
			<td class="opt"><a href="${base}/item/merchant/${shop.eid!0}/edit/${(item.id)!}" class="b_a">修改</a> 
			<#--
			<a href="javascript:deleteItem(${(item.id)!},'${(item.name)!}');" class="b_a">删除</a></td>-->
			<a class="del_attribute b_a pop_a"  href="javascript:" data-name="${(item.name)!}" data-no="${(item.id)!}" data-pop="pop_del" data-para="${base}/item/merchant/${shop.eid!0}/${(item.id)!}">删除</a>
		</tr>
		</#list>
	</table>
	<div class="page_wrap">
		<@m.p page=items.pageNumber totalpage=items.pageCount />  
	</div>
	<div class="pb pop_filter">
		<form action="${base}/item/merchant/${shop.eid!0}/list" class="f_form">
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">分类</p>
					<select name="categoryId" class="f_input pb_item_input select_style fi_2">
						<option value="">全部</option>
						<#list categories as category>
							<option value="${category.id}" 
							<#if (category.id == searcher.categoryId)!>
								  selected =  "selected"
								</#if>
								>${category.name}</option>
						</#list>
					</select>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">是否上架</p>
					<select name="marketable" class="f_input pb_item_input select_style fi_3">
						<option value="" <#if !(searcher.marketable)??> selected = "selected"</#if>>全部</option>
						<option value="1" <#if (searcher.marketable)?? && searcher.marketable> selected = "selected"</#if>>已上架</option>
						<option value="0" <#if (searcher.marketable)?? && !searcher.marketable> selected = "selected"</#if>>未上架</option>
					</select>
				</div>
			</div>
			<button class="pb_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_del">
			<form action="" method="post">
				<input type="hidden" name="_method" value="delete"/> 
				<input type="text" class="id" name="id" style="display:none">
				<div class="pb_title">删除</div>
				<div class="pb_main2">确认删除商品<span class="pb_name name_key"></span></div>
				<button type="button" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
	</div>
</div>
<script type="text/javascript">
	function deleteItem(id,name){
		if(id!==''||id!==null){
			$.ajax({
				url:'${base}/item/merchant/'+ ${shop.eid!0} +'/'+id,
				type:'delete',
				dataType :'json',
				success:function(data){
					if(data.status == 'success') {
						$('.pop_suss').text(data.description).addClass('pop_hint2').show();
						location.reload();
					}else if(data.status == 'faild') {
						$('.pop_suss').text(data.description).addClass('pop_hint3').show();
						location.reload();
					}else {
						$('.pop_suss').text(data.description).addClass('pop_hint3').show();					
						location.reload();
					}
				}
			});
		}
	}
	$(function(){
		$('.pop_del .pb_btn_s').click(function(){
			deleteItem($('.id').val());
		})
	})
	//$('.img img').each(function() {
	//	var src = $(this).data('src')
	//	var dsrc = this.src
	//
	//	this.onerror = function (type) {
	//		this.src = dsrc
	//	}
	//	this.src = src 
	//})

</script>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
	<div class="pop_hint pop_suss" ></div>
<@m.page_footer />