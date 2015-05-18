<#import "/macro.ftl" as m>
<@m.page_header title="出入库记录明细" subselected='record' css='seller_list_new|account|record_list|record_list_detail' js='My97DatePicker/WdatePicker|back_base|validate' selected='cashiering' />
	<div class="rwrap">
		<div class="r_title"><span class="fl"><a class="back_a" href="javascript:history.go(-1);"></a>出入库记录明细</span>
		<div class="search_wrap ">
		<form action="${base}/inventory/detaillist/${record.id}" class="search_form check_form fl">
		<input type="text" name="key" <#if key??>value="${key}"</#if> class="search_input" placeholder="搜索商品名称" data-maxlen="8" data-hint="商品名称">
		<button class="search_btn">
		</button></form>
		<a class="export_a fl" href="${base}/inventory/detail/export/${record.id}?key=${key}" ></a></div></div>
		<table class="tb_main tb_mains">
			<tr class="">
				<td class="date_h">操作时间<span class="s_span">${(record.createdDate?string("yyyy-MM-dd HH:mm:ss"))!}</span></td>
				<td class="trans_num">操作员<span class="s_span">${record.account.username}</span></td>
			</tr>
			<tr class="tr_bg">
				<td class="date_h">操作商户<span class="s_span">${record.shop.name}</span></td>
				<td class="trans_num"></td>
			</tr>	
		</table>
		<table class="tb_main">
			<tr class="tr_bg"><td class="td_tit" colspan="8">出入库操作清单</td></tr>
			<tr class="th ">
				<td class="id">排列序号</td>
				<td class="td_img"></td>
				<td class="trans_num">商品</td>
				<td class="m_num">单价</td>
				<td class="pay_way">分类</td>
				<td class="telephone">操作前库存</td>
				<td class="telephone">入库/出库</td>
				<td class="telephone">操作后库存</td>
			</tr>
			<#list detailList as detail>
				<tr class="tr <#if detail_index%2==0>tr_bg</#if>">
					<td class="id">${(detail.item.sequence)!}</td>
					<td class="td_img"><img src="${image_base}${(detail.item.cover)!}!100" class="item_img"></td>
					<td class="trans_num">${(detail.item.name)!}</td>
					<td class="m_num">${(detail.item.price)!'0.00'}元</td>
					<td class="trade_type">${(detail.item.category.name)!}</td>
					<td class="telephone">${(detail.beforeChangeQuantity)!}</td>
					<td class="trade_type"><#if detail.inventoryType>+<#else>-</#if>${(detail.quantity)!}</td>
					<td class="telephone">${(detail.afterChangeQuantity)!}</td>
				</tr>
				
			</#list>
		</table>
	</div>
</div>
<script>
	$(function(){
		$('.export_a').click(function(){
			if(confirm("是否要导出数据？")){
				return true;
			}
			return false;
		});
	});
</script>
<@m.page_footer />