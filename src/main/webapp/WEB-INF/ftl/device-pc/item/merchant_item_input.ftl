<#import "/macro.ftl" as m>
<@m.page_header title="商品添加&修改" subselected='merchant' css="seller_info|add_seller_new|add_ticket|add_product" js="validate|product_add" selected='cashiering' />
<div class="rwrap">
	<div class="r_title"><a href="javascript:history.go(-1)" class="back_a"></a><#if (item.id)??>商品修改<#else>商品添加</#if></div>
	<#if (item.id)??>
	<form class="check_form" action="/item/merchant/edit" method="POST" enctype="multipart/form-data">
		<input type="hidden" name="id" value="${(item.id)!}" />
	<#else>
	<form class="check_form" action="/item/merchant/${shop.eid!}/add" method="POST" enctype="multipart/form-data">
	</#if>
		<input type="hidden" name="shopEid" value="${shop.eid!}"/>
		<input type="hidden" name="submissionToken" value="${(submissionToken)!''}"/>
	<div class="tb_wrap">
	<div class="tb_title">基本信息</div>
	<table class="tb_main">
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">类别</span><span class="td_val" >
					<select class="selects" name="category.id">
						<#list categories as category>
							<option value="${category.id}" <#if item&&(item.category.id==category.id)>selected="selected"</#if>>${category.name}</option>
						</#list>
					</select></span></td>
					<td class="ltd"><em></em><span class="td_key">顺序</span><span class="td_val">
					<input class="ip1 isint" data-hint="顺序" type="text" name="sequence"   value="${(item.sequence)!'50'}"></span></td>			
				</tr>
				<tr class="tr_bg">
					<td class="ltd"><em>*</em><span class="td_key">名称</span><span class="td_val">
					<input class="ip1 isneed" data-hint="名称" data-type="text" data-maxlen="16" name="name" value="${(item.name)!}"></span></td>
					<td class="ltd"><em></em><span class="td_key">单位</span><span class="td_val">
					<input class="ip1" type ="text" name="unit" data-hint="单位" data-maxlen="3" value="${(item.unit)!}"></span></td>
				</tr>
				<tr>
					<td class="ltd"><em>*</em><span class="td_key">单价</span><span class="td_val">
					<input class="ip1 isneed isplusfloat" data-hint="单价" type="text" name="price" value="${(item.price)!'0.00'}"></span></td>
					<td class="ltd"><em></em><span class="td_key">是否上架</span><span class="td_val">
					<input type="checkbox"  id="cb1" name="marketable" value="true" <#if !item??>checked="checked"</#if> <#if item && item.marketable>checked="checked"</#if> /><label for="cb1">是</label></span></td>
				</tr>
				<tr class="tr_bg">
					<td class="std"><em style="margin-left:7px;"></em><span class="td_key">图片</span><span class="td_val">
					<input type="file" class="file_ip file_ip1" data-hint="图片" name="coverFile" ></span></td>
					<td class="std"><em></em><span class="td_key">打印档口</span><span class="td_val">
						<select class="selects" name="printerId">
						<#if !printers?? || printers?size==0>
							<option value="">没有档口</option>
						<#else>
							<#list printers as printer>
								<option value="${printer.id}" <#if item&&(item.printerId==printer.id)>selected="selected"</#if>>${printer.name}</option>
							</#list>
						</#if>
						</select></span></td>
				</tr>
				<#if (item.cover)!>
					<tr>
						<td class="img_td" colspan="2"><em>&nbsp;</em><span class="td_key">文件</span><span class="td_val">
						<img src="${image_base}${item.cover}!100" class="item_img" onerror="javascript:this.src='${base}/static/css/img/error_miss.png'"></span></td>
					</tr>
				</#if>
			</table>
		<div class="btn_wrap"><button class="add_seller_btn">保存商品</button><span>或</span><a href="javascript:history.go(-1);" class="cancel_a">取消</a></div>
	</form>
	
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />