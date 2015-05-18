<#import "/macro.ftl" as m>
<@m.page_header selected='shop' css='seller_info|pos_info' subselected = "detail"/>
<script type="text/javascript">
	function position(){
	 	var iHeight = 700 ;
	 	var iWidth  = 950 ;
    	var iTop = (window.screen.availHeight-30-iHeight)/2;        
    	var iLeft = (window.screen.availWidth-10-iWidth)/2;        
		window.open("/position/input?address=${(shop.address)!}"
					+"&&lat=${(shop.position.latitude)!}"
					+"&&lng=${(shop.position.longitude)!}"
					+"&&adrsEmpty=<#if (shop.address)!>true<#else>false</#if>"
					+"&&isEdit=false", "_blank",
					"height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no"
					); 
	}
</script>
<style>
.tb_main tr td{
border: 1px solid #e5e8e6;
}
</style>
<div class="rwrap">
	<div class="r_title"><@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN"><a href="${base}/shop/list" " class="back_a"></a></@shiro.hasAnyRoles>商户资料</div>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
		<div class="nav_wrap">
			<a href="${base}/shop/${(shop.id)!}" class="nav_a nav_now">商户基本信息</a>
			<a href="${base}/shop/${(shop.id)!}/about" class="nav_a ">关于商户</a>
			<a href="${base}/shop/${(shop.id)!}/album" class="nav_a ">相册列表</a>
			<a href="${base}/shop/${(shop.id)!}/account" class="nav_a ">商户账号</a>
			<a href="${base}/shop/${(shop.id)!}/pos" class="nav_a">POS相关</a>
		</div>
		</@shiro.hasAnyRoles>
		<div class="tb_wrap">
			<div class="tb_title lh20"><span class="fl">商户基本资料</span>
				<a href="${base}/shop/edit/${(shop.id)!}" class="pop" >修改商户资料</a>
			</div>
			<table class="tb_main">
				<tr>
					<td class="ltd">
					<em>*</em>
					<span class="td_key">商户全称</span><span class="td_val">${(shop.fullName)!}</span></td>
				</tr>
			</table>
		</div>
		<div class="tb_wrap">
			<div class="tb_title">主页用信息</div>
			<table class="tb_main">
				<tr>
					<td>
					<em>*</em>
					<span class="td_key">商户简称</span><span class="td_val">${(shop.name)!}</span></td>
					<td>
					<em>*</em>
					<span class="td_key">商户类型</span><span class="td_val">${(shop.category.parent.name)!}-${(shop.category.name)!}</span></td>
				</tr>
				<tr class="tr_bg">
					<td>
					<em>*</em>
					<span class="td_key">商户地址</span><span class="td_val td_vals">${(shop.address)!}</span><a href="javascript:position();" class="a_map">查看地图</a></td>
					<td><em style="margin-left:12px;"></em><span class="td_key">营业时间</span><span class="td_val">${(shop.shopHours)!'-'}</span></td>
				</tr>
				<tr>
					<td>
					<em>*</em>
					<span class="td_key">联系电话</span><span class="td_val ios-tel">${(shop.contact)!}</span></td>
					<td><em style="margin-left:12px;"></em><span class="td_key">网站地址</span><span class="td_val">${(shop.domain)!}</span></td>
				</tr>
				<tr class="tr_bg">
					<td><em style="margin-left:12px;"></em><span class="td_key">微信账号</span><span class="td_val">${(shop.wechat)!}</span></td>
					<#--<td><em style="margin-left:12px;"></em><span class="td_key">微信昵称</span><span class="td_val">-</span></td>-->
                    <td></td>
				</tr>
				<tr>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key">新浪微博账号</span><span class="td_val">${(shop.weibo)!}</span></td>
					<#--<td class="std"><em style="margin-left:12px;"></em><span class="td_key">新浪微博链接</span><span class="td_val">-</span></td>-->
                    <td></td>
				</tr>
				<tr class="tr_bg">
					<td class="std">
					<em>*</em>
					<span class="td_key">Logo图片</span><#if (shop.avatar)!><span class="td_val"><a class="l_a img_a" href="javascript:" data-src="${image_base}${shop.avatar}" >查看图片</a></span></#if></td>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key">Banner大图</span><#if (shop.banner)!><span class="td_val"><a class="l_a img_a" href="javascript:" data-src="${image_base}${shop.banner}" >查看图片</a></span></#if></td>
				</tr>
			</table>
		</div>
		<@shiro.hasAnyRoles name="ROLE_ADMIN">
		<#if contacts && contacts?size gt 0>
		<div class="tb_wrap">
			<div class="tb_title">商户联系人资料</div>
			<table class="tb_main">
				<#list contacts as contact>
				<tr <#if contact_index%2==1>class="tr_bg"</#if>>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key">联系人姓名</span><span class="td_val v9">${(contact.name)!}</span></td>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key">联系人手机号码</span><span class="td_val v9">${(contact.mobile)!}</span></td>
				</tr>
				<tr <#if contact_index%2==1>class="tr_bg"</#if>>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key">联系人电邮</span><span class="td_val v9">${(contact.email)!}</span></td>
					<td class="std"><em style="margin-left:12px;"></em><span class="td_key">联系人固话</span><span class="td_val v9">${(contact.telephone)!}</span></td>
				</tr>
				</#list>
			</table>
		</div>
		</#if>
		</@shiro.hasAnyRoles>
		<#if (_BUSINESS_.id == 2 )! || (_BUSINESS_.merchant.id == 2)! || (shop.merchant.id == 2)!>
		<div class="tb_wrap">
			<div class="tb_title">商户标签 </div>
			<table class="tb_main">
				<tr>
					<td class="ltd">
					<em>&nbsp;</em><span class="td_key">商户标签</span>
					<span class="td_val ip1">${(shop.tag)!}</span>
					</td>
				</tr>
			</table>
		</div>
		</#if>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<@m.page_footer />