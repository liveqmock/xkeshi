<#import "/macro.ftl" as m >
<@m.page_header selected='shop' subselected = "shop" css="seller_list_new"  js ="shop_list|My97DatePicker/WdatePicker|list_filter" />

<script>
	var categories = {
		<#if (categories)!>
		<#list categories?keys as key>
			"${key}":[
				<#list categories?values[key_index] as category>
					{"id":${category.id},"name":"${category.name}"},
				</#list>
			],
		</#list>
		</#if> 
	}; 
	$(function(){
		categories = eval(categories);
    <#if (searcher.categoryParentId != null  && searcher.categoryId == null)!>
        $('#parent').val('${searcher.categoryParentId}');
        change('${searcher.categoryParentId}');
    </#if>
		<#if (searcher.categoryId)!>
			var categoryId = '${searcher.categoryId}';
			 $.each(categories, function(i,v){
			    $.each(v, function(j,category){
				    if (categoryId == category.id ){
				    	change(i);
				    	$('#parent').val(i);
				    	$("#child option[value= "+category.id+"]").attr("selected",true);
					} 
				});
			});
		</#if>
		$('#parent').change(function(){
			change(this.value);
		});
		function change(id){
			$('#child').html('');
			$('#child').append($('<option>', {
				    value: '',
				    text: '请选择'
				}));
			$.each(categories[id], function(i,e){
				$('#child').append($('<option>', {
				    value: e.id,
				    text: e.name
				}));
			});
		}
		
	});
	
</script>

<div class="rwrap">
	<div class="r_title"><span class="fl">商户列表</span>
	<div class="search_wrap">
		<form action="${base}/shop/list" class="search_form fl">
		<input type="text" name="key" <#if searcher&&searcher.key>value="${(searcher.key)!}"</#if> placeholder="商户ID/商户简称" class="search_input fi_1" maxlength="40">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:" class="filter_a fl"></a>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
			<a href="${base}/shop/add" class="new_a fl ml12"></a>
		</@shiro.hasAnyRoles>
	</div>
	  <#if (searcher != null && searcher.hasParameter)! >
			<div class="search_result ofw">
				<p class="result_tit clbh"><span class="result_tit_text">搜索/筛选</span>
					<div>
					<#if searcher.key>
						<span class="b_tit" data-fid="1">商户ID/商户简称:${(searcher.key)!}<b>x</b></span>
					</#if>
                    <#if searcher.categoryParentId>
                        <span class="b_tit" data-fid="7">分类:
                            <#if (parentCategories)!>
                                <#list parentCategories as parentCategorie>
                                   <#if (searcher.categoryParentId == parentCategorie.id)! > ${parentCategorie.name}</#if>
                                </#list>
                            </#if>
                        <b class="f_x">x</b></span>
                    </#if>
					<#if searcher.categoryId><span class="b_tit" data-fid="2">
					<#if (categories)!>
						<#list categories?keys as key>
							<#list categories?values[key_index] as category>
								<#if (searcher.categoryId == category.id)! > ${category.name}</#if>
							</#list>
						</#list>
					</#if>
					<b>x</b></span></#if>
					<#if searcher.merchantId>
					<span class="b_tit" data-fid="3">集团:
				  	  <#list merchants as merchant>
				       	   	<#if (searcher.merchantId == merchant.id)! >${(merchant.fullName)!}  </#if> 
					  </#list>
					 <b>x</b></span>
					</#if>
					<#if searcher.createDate><span class="b_tit" data-fid="5">入驻时间起:${(searcher.createDate?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					<#if searcher.modifyDate><span class="b_tit" data-fid="6">修改时间起：${(searcher.modifyDate?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					</div>
					<span class="w_tit">的结果</span></span>
				</p>
				<a href="${base}/shop/list" class="s_clear">清空</a>
			</div>
	</#if>
	</div>
		<div class="opt_wrap">
			<div class="opt_main2">
				<#--
				<@shiro.hasAnyRoles name="ROLE_ADMIN">
					<a href="javascript:" class="opt1">商户置顶</a>
					<a href="javascript:" class="opt2">取消置顶</a>
				</@shiro.hasAnyRoles>
				-->
					<a href="javascript:" class="opt3">商户公开</a>
					<a href="javascript:" class="opt4">商户隐藏</a>
				<@shiro.hasAnyRoles name="ROLE_ADMIN">
					<a href="javascript:" class="opt5 rel_merchant">关联集团</a>
				</@shiro.hasAnyRoles>
			</div>
		</div>
		<form id="shops" action = "${base}/shop/publish"  method="POST">
		<input type="hidden" name="visible"  />
		<input type="hidden" name="_method"  value="PUT" />
		<table class="tb_main">
			<tr class="th">

				<td class="opt"><input type="checkbox" id="checkAll" /><label for="checkAll" style="padding-left:15px;">&nbsp;</label></td>
				<td class="id" style="padding-left:33px;">商户ID</td>
				<td class="" style="width:100px;">商户全称</td>
				<td class="addr">商户地址</td>
				<td class="" style="width:50px;">分类</td>
				<td class="" style="width:70px;">入驻时间</td>
				<td class="" style="width:75px;">修改时间</td>
				<td class="id">集团</td>
				<td class="state">状态</td>
			</tr>
			<#list pager.list as shop>
			<tr <#if shop_index%2==0>class="tr_bg"</#if>>

				<td class="opt"><input type="checkbox" name="shopIds" class="checkbox_shop" value="${shop.id}" id="cb${shop.id}"  data-name="${(shop.name)!}"><label for="cb${shop.id}" style="padding-left:15px;">&nbsp;</label></td>
				<td class=""><div class="id">${(shop.id)!}<a target="_blank" href="http://v2.xkeshi.com/shop/pc${shop.id}" class="house_icon house_icon2"></a></div></td>
				<td class=""><div class="h_name">
				<a href="${base}/shop/${shop.id}" class="b_a">
					<#if (shop.fullName =='' || shop.fullName == null) >(空)<#else>${(shop.fullName)!'(空)'}</#if>
				</a></div>
				</td>
				<td class="addr">${(shop.address)!}</td>
				<td class="cate">${(shop.category.parent.name)!} - ${(shop.category.name)!}</td>
				<td class="">${(shop.createDate?string('yyyy-MM-dd'))!'-'}</td>
				<td class="">${(shop.modifyDate?string('MM-dd HH:mm'))!'-'}</td>
				<td class=""><div class="l_id">
						<#assign merchantName  = '无集团'  />
						<#list merchants as merchant>
						   <#if (merchant.id == shop.merchant.id)! >
								<#assign merchantName  = '${(merchant.fullName)!}'  />
						   </#if>
						</#list>
						${(merchantName)!'无集团'}</div>
				</td>
				<td class="state">
				<#if shop.visible>
					<span class="state1">公开</span>
				<#else>
					<span class="state2">隐藏</span>
				</#if>
				</td>
			</tr>
			</#list>
		</table>
		</form>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>
	<div class="pb pb_rel_merchant">
		<form action="${base}/shop/relmerchant" method="post" id="relMerchantForm">
			<div class="pb_title">关联集团账号</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title ">关联集团:</p>
					<@shiro.hasAnyRoles name="ROLE_ADMIN">
						<select name="id" class="f_input pb_item_input select_style sel_rel_merchant" style="width: 180px;" >
								<option value="">---无集团---</option>
						<#list merchants as merchant>
								<option value="${(merchant.id)!}">${(merchant.fullName)!}</option>
						</#list>
						</select>
					</@shiro.hasAnyRoles>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">已选商户:</p>
					 <div class="shopIds"> </div>
				</div>
			</div>
			<button class="pb_btn pb_btn_shop pb_btn_s" type="submit">保存</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	
	 <div class="pb pop_filter">
			<form action="${base}/shop/list"  method="get" class="f_form">
			<div class="pb_title">筛选</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">分类:</p>
				     <select id="parent" class="f_input pb_item_input select_style fi_7" name="categoryParentId" >
					 	<option value="">请选择...</option>
						<#list parentCategories as category>
							<option value="${category.id}">${category.name}</option>
						</#list>
					 </select>
					 <select id="child" class="f_input pb_item_input select_style fi_2" name="categoryId"><option value="">请选择...</option></select>
				</div>
				<@shiro.hasAnyRoles name="ROLE_ADMIN">
				<div class="pb_item">
					<p class="pb_item_title">集团：</p>
					<select name="merchantId" style="width: 213px;color:#000000;margin-left:0px;" class="f_input pb_item_input select_style fi_3">
				  	  <option  value=" " >--集团--</option>
				  	  <#list merchants as merchant>
				       	   <option  value="${(merchant.id)!}" 
				       	   	<#if (searcher.merchantId == merchant.id)! > selected = selected </#if> >
				       	   	${(merchant.fullName)!} 
				       	   </option>
					  </#list>
					</select>
				</div>
				</@shiro.hasAnyRoles>
				<#-- 
				<div class="pb_item">
					 <p class="pb_item_title">商户行政区：</p>
					 <select name="cityCode" class="po_city_code" >
			 	   		 <option value="">请选择</option>
			 	     </select>
					 <select name="district" class="po_district_code fi_4" disabled="disabled">
			 	   		 <option value="">请选择</option>
			 	     </select>
				</div>
				 -->
				<div class="pb_item">
					<p class="pb_item_title">入驻时间起:</p>
					<input type="text" class="pb_item_input f_input fi_5" name="createDate"  
										value="${(searcher.createDate?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" readonly="readonly">
				</div>
				<div class="pb_item pb_item_b">
					<p class="pb_item_title">修改时间起:</p>
					<input type="text" class="pb_item_input f_input fi_6" name="modifyDate"
					                   value="${(searcher.modifyDate?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" readonly="readonly">
				</div>
				 
			</div>
			<button class="pb_btn pb_btn_s">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>	
	
	
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<div class="pb pb_warn">
				<div class="pb_title">警告<a href="javascript:" class="pb_close"></a></div>
				<div class="pb_main pb_main2">请选择需要操作的商户!</div>
				<button type="button" class="pb_btn pb_close" >确定</button>
</div>
<script type="text/javascript">

$('#checkAll').click(function(){
	$.each($('input[name="shopIds"]'),function(i,e){
		$(e).attr('checked', $('#checkAll').attr("checked") == 'checked');
	});
});


//公开
	 $('.opt3').click(function(){
		 if( $("input[name='shopIds']:checked").length == 0){
			 var self = $('.opt3'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
     				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left+self.width()}).show();
			 return false;
		  }
		$('input[name="visible"]').val("1");
		$('#shops').submit(); 
	 });
	//隐藏
	 $('.opt4').click(function(){
		 if( $("input[name='shopIds']:checked").length == 0){
			 var self = $('.opt4'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
				pop;
				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left+self.width()}).show();
			 return false;
		  }
		$('input[name="visible"]').val("0");
		$('#shops').submit(); 
	 });
</script>
<@m.page_footer />




