<#import "/macro.ftl" as m>
<@m.page_header title="商品批量出入库" subselected='shop' css='seller_list_new|product_list' js='back_base|storage_list' selected='cashiering' />
<div class="rwrap">
		<div class="r_title"><a class="back_a" href="${base}/item/list"></a>商品批量出入库</div>
		<table class="tb_main tb_all">
			<tr class="th">
				<td class="id">顺序</td>
				<td class="img"></td>
				<td class="name">商品名称</td>
				<td class="state">单价</td>
				<td class="date">库存</td>
				<td class="sum">分类</td>
				<td class="sum">是否上架</td>
				<td class="storage">出库/入库</td>
			</tr>
			<#list items.list as item>
				<tr <#if item_index%2==0>class="tr tr_bg"<#else> class="tr"</#if> data-id="${item.id}">
					<td class="id">${(item.sequence)!}</td>
					<td class="img"><img src="${image_base}${(item.cover)!}!100" /></td>
					<td class="name">${(item.name)!}</td>
					<td class="price">${(item.price)!'0.00'}元</td>
					<td class="amount">${(item.itemInventory.inventory)!}</td>
					<td class="cat">${(item.category.name)!}</td>
					<td class="ison"><#if item.marketable>已上架<#else>未上架</#if></td>
					<td class="storage"><input type="text" class="storage_input"></td>
				</tr>
			</#list>
		</table>
		<div class="btn_wrap">
			<button class="pb_btn sub_btn">提交库存修改</button>
		</div>
	</div>
	<div class="pb pop_change">
		<div class="pb_title">本次库存修改如下</div>
		<div class="pb_main">
			<table class="tb_main pb_tb">
			</table>
		</div>
		<div class="pb_btn_wrap">
			<button class="pb_btn change_btn">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</div>
		<form class="change_form" action="/inventory/batch/add" method="POST">
			<input type="hidden" name="submissionToken" id="submissionToken" value="${submissionToken}"/>
			<input type="hidden" name="iicdList" value="" class="hidden_data">
		</form>
	</div>
	<div class="pb pop_error">
		<div class="pb_title">出错信息如下</div>
		<div class="pb_main">
			<table class="tb_main pb_err_tb">
			</table>
		</div>
		<div class="pb_btn_wrap">
			<button class="pb_btn error_btn">确定</button>
		</div>
	</div>
<div class="black_cover"></div>
<@m.page_footer />