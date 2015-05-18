<#import "/macro.ftl" as m>
<@m.page_header selected='shop' css="add_seller_new|seller_info" js='pos_info|shop_validate' />
<script type="text/javascript">
	function position(){
	 	var iHeight = 700 ;
	 	var iWidth  = 950 ;
    	var iTop = (window.screen.availHeight-30-iHeight)/2;        
    	var iLeft = (window.screen.availWidth-10-iWidth)/2;        
		window.open("/position/input?address="
					+$('input[name="address"]').val()
					+"&&lat="+$('#latitude').val()
					+"&&lng="+$('#longitude').val()
					+"&&adrsEmpty=<#if (shop.address)!>false<#else>true</#if>"
					+"&&isEdit=true", "top",
					"height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no"
					); 
	}
	
	function setPosition(p, lng, lat ,address){
		$('#region').val(p);
		$('#latitude').val(lat);
		$('#longitude').val(lng);
		$('#position').val("维度:"+lng+" 经度:"+lat);
		<#if !(shop.address)!>
		$('#address').val(address);
		</#if>
		$('.position').hide().text("");
	}
	
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
		<#if (shop.category.parent.id)!>
			change("${(shop.category.parent.id)!}");
			$('#parent').val("${(shop.category.parent.id)!}");
			$("#child option[value='${(shop.category.id)!}']").attr("selected",true);
		</#if>
		$('#parent').change(function(){
			change(this.value);
		});
		function change(id){
			$('#child').html('');
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
		<p class="r_title"><a onclick="history.back(-1);" class="back_a"></a><#if shop>修改商户<#else>添加商户</#if></p>
		<form action="${base}/shop/<#if shop>edit<#else>add</#if>" method="POST" enctype="multipart/form-data"  id="shopForm" name="shopForm">
		<#if !shop??><input type="hidden" name="submissionToken" value="${submissionToken}"/></#if>
		<div class="tb_wrap">
			<div class="tb_title">商户基本资料</div>
			<table class="tb_main">
				<tr>
					<td class="ltd"><em>*</em>
					<span class="td_key">商户全称</span>
					<span class="td_val">
						<input name="fullName" class="ip1 lh14 required" type="text" data-msg = "商户全称" data-maxlen="100" data-minlen="2" value="${(shop.fullName)!}">
						<div  class="required_error required_errors"></div>
					</span>
					</td>
				</tr>
			</table>
		</div>
		<div class="tb_wrap">
			<div class="tb_title">主页用信息</div>
			<input type="hidden" name="id" value="${(shop.id)!}" />
			<input type="hidden" id="latitude" name="position.latitude" value="${(shop.position.latitude)!}" />
			<input type="hidden" id="longitude" name="position.longitude" value="${(shop.position.longitude)!}" />
			<input type="hidden" id="region" name="region.id" value="${(shop.region.id)!}" />
			<table class="tb_main">
				<tr>
					<td>
						<em>*</em>
						<span class="td_key">商户简称</span>
						<span class="td_val">
							<input type="text" name="name" value="${(shop.name)!}"  class="required "  data-msg="商户简称" data-maxlen="100" data-minlen="2" >
			  				<div  class="required_error required_errors"></div>
			  			</span>
					</td>
					<td>
						<em class="td_keys">*</em>
						<span class="td_key td_keys pr9">商户类型</span>
						<span class="td_val">
						<div class="boxs" style="width:140px;"">
							<select id="parent" class="required "  data-msg="商户父类型"  style="width:140px;" >
							 	<option value="">请选择...</option>
								<#list parentCategories as category>
									<option value="${category.id}">${category.name}</option>
								</#list>
							</select>
							<div  class="required_error required_errors"></div>
						</div>
						<div class="boxs" style="width:140px;">
							<select class="ml13 required" id="child" style="width:140px;" name="category.id" data-msg="商户子类型">
							   <option value="">请选择...</option>
							</select>
							<div class="required_error"></div>
						</div>
						</span>
					</td>
				</tr>
				<tr class="tr_bg ">
					<td>
						<em>*</em>
						<span class="td_key">商户地址</span>
						<span class="td_val td_map">
							<a href="javascript:position(); " class="map_a">地图设置</a>
							<input type="text" name="address" id="address" value="${(shop.address)!}" class="map_input lh14 required" data-msg="商户地址"/>
							
						</span>
						<div  class="required_error position"></div>
						<#--<em>*</em><span class="td_key">地图位置</span><span class="td_val">
						<input type="text" disabled ="disabled"  id="position" class="map_input" value="纬度：${(shop.position.longitude)!} 经度：${(shop.position.latitude)!}" ></span>-->
					</td>
					<td><em style="margin-left:9px;"></em><span class="td_key pr6">营业时间</span><span class="td_val"><input type="text" name="shopHours" value="${(shop.shopHours)!}" /></span></td>
				</tr>
				<tr>
					<td><em >*</em>
						<span class="td_key">联系电话</span>
						<span class="td_val">
							<input type="text" name="contact" value="${(shop.contact)!}"  class="required" data-msg="联系电话" />
							<div  class="required_error required_errors"></div>
						</span>
						</td>
					<td><em style="margin-left:9px;"></em><span class="td_key pr6">网站地址</span><span class="td_val"><input  type="text" name="domain" value="${(shop.domain)!}" /></span></td>
				</tr>
				<tr class="tr_bg">
					<td><em style="margin-left:12px;"></em>
					<span class="td_key">微信账号</span>
					<span class="td_val"><input type="text" name="wechat" value="${(shop.wechat)!}" /></span></td>
					<td></td>
				</tr>
				<tr>
					<td class="std"><em style="margin-left:12px;"></em>
					<span class="td_key">新浪微博账号</span>
					<span class="td_val"><input type="text" name="weibo" class="v9" value="${(shop.weibo)!}" /></span></td>
					<td class="std"></td>
				</tr>
				<tr class="tr_bg">
					<td class="std" style="vertical-align:top;">
					<em>*</em>
					<span class="td_key">Logo图片</span>
					<span class="td_val">
						<input type="file" name="avatarFile" class="file_ip <#if !(shop.avatar)!>required</#if>" data-msg="商户Logo" />
						<div  class="required_error required_errors"></div>
					</span>
					<#if (shop.avatar)!><img src="${image_base}${shop.avatar}"  style="width:400px;margin-top:20px;" /> </#if>
					</td>
					<td class="std" style="vertical-align:top;">
					<em style="margin-left:9px;"></em>
					<span class="td_key">Banner大图</span>
					<span class="td_val">
						<input type="file" name="bannerFile" class="file_ip v9 file_b" data-msg="商户Banner大图" />
						<div  class="required_error"></div>
					</span>
					<#if (shop.banner)!> <img src="${image_base}${shop.banner}"  style="width:400px;margin-top:20px;" /> </#if>
					</td>
				</tr>
			</table>
		</div>
		<@shiro.hasAnyRoles name="ROLE_ADMIN">
		<div class="tb_wrap">
			<div class="tb_title">商户联系人资料</div>
			<input type="hidden" name="contacts[0].id" value="${(contacts[0].id)!}" />
			<table class="tb_main">
				<tr>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key pr6">联系人姓名</span><span class="td_val v9"><input type="text" name="contacts[0].name" value="${(contacts[0].name)!}" /></span></td>
					<td class="std"><em style="margin-left:9px;"></em><span class="td_key pr6">联系人手机号码</span><span class="td_val v9"><input type="text" name="contacts[0].mobile" value="${(contacts[0].mobile)!}" /></span></td>
				</tr>
				<tr class="tr_bg">
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key pr6">联系人电话号码</span><span class="td_val v9"><input type="text" name="contacts[0].telephone" value="${(contacts[0].telephone)!}" /></span></td>
					<td class="std"><em style="margin-left:9px;"></em><span class="td_key pr6">联系人邮箱</span><span class="td_val v9"><input type="text" name="contacts[0].email" value="${(contacts[0].email)!}" /></span></td>
				</tr>
			</table>
		</div>
		</@shiro.hasAnyRoles>
	  <#if (_BUSINESS_.id == 2 )! || (_BUSINESS_.merchant.id == 2)! || (shop.merchant.id == 2)!>
		<div class="tb_wrap">
			<div class="tb_title">商户标签 </div>
				<table class="tb_main">
					<tr>
						<td class="ltd">
						<em>&nbsp;</em><span class="td_key">商户标签</span>
						<span class="td_val"><input type="text" class="pb_item_input ip1" name="tag" id="tag"  value="${(shop.tag)!}" placeholder="多个便签用','隔开。如：美食,娱乐" ></span>
						</td>
					</tr>
				</table>
		</div>
		</#if>
		<div class="btn_wrap">
			<button type="button" class="add_seller_btn">
			<#if shop>保存商户资料<#else>添加商户资料</#if>
			</button>
			<span>或</span>
			<a <#if shop>href="${base}/shop/${(shop.id)!}"<#else>href="${base}/shop/list"</#if> class="cancel_a">取消</a>
			<#--<#if shop><a target="_blank" href="http://v2.xkeshi.com/shop/pc${shop.id}" class="pc_a">网络版预览</a></#if>-->
		</div>
		</form>
	</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
<@m.page_footer />