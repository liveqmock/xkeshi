<#macro page_header selected='' subselected='' css='' js='' title=''>
<!doctype html>

<head>
<meta charset="utf-8">
<meta version="tag 1.0.5">
<title>${title} - 爱客仕</title>
<meta http-equiv="X-UA-Compatible" content="IE=10" />
<link rel="stylesheet" href="${static_base}/css/reset.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/fancybox/jquery.fancybox.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/admin_frame.css" type="text/css">
<#if css?length gt 0>
	<#list css?split("|") as c>
<link rel="stylesheet" type="text/css" href="${static_base}/css/${c}.css">
	</#list>
</#if>
<script src="${static_base}/js/jquery-1.8.3.min.js"></script>
<script src="${static_base}/js/json2.js"></script>
<script src="${static_base}/js/fancybox/jquery.fancybox.pack.js"></script>
<script src="${static_base}/js/back_base.js"></script>
<#if js?length gt 0>
	<#list js?split("|") as j>
		<script src="${static_base}/js/${j}.js"></script>
	</#list>
</#if>
</head>

<body>
<div class="top">
	<div class="top_main">
		<span class="logo fl"></span>
		<a href="javascript:" class="msg fl">
			<span class="msg_text">站内信</span><span class="msg_num">0</span>
		</a>
		<div class="user"><span class="com_name">${_ACCOUNT_.username}</span><a href="${base}/logout" class="a_logout">退出</a></div>
	</div>
</div>
<div class="wrapper">
	<div class="lwrap">
		<div class="lnav">
			<div class="lnav_top"></div>

			<@shiro.hasAnyRoles name="ROLE_ADMIN">
			<a class="lnav_item <#if selected='terminal'>lnav_now</#if>" href="${base}/shop/terminal/list"><img src="${static_base}/css/img/admin_frame/<#if selected='terminal'>lnav_img1_w.png<#else>lnav_img1.png</#if>" class="lnav_img lnav_img1"><span class="lnav_text">POS机终端</span></a>
			</@shiro.hasAnyRoles>

			<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
			<a class="lnav_item <#if selected='shop'>lnav_now</#if>" href="${base}/shop/list"><img src="${static_base}/css/img/admin_frame/<#if selected='shop'>lnav_img2_w.png<#else>lnav_img2.png</#if>" class="lnav_img lnav_img2"><span class="lnav_text" style="margin-left:-1px;">商户</span></a>
				<@shiro.hasAnyRoles name="ROLE_ADMIN">
				<div class="lnav2_wrap   <#if selected='shop'>lnav2_wrap_show</#if>">
					<a class="lnav2_item <#if subselected='shop'>lnav2_now</#if>" href="${base}/shop/list">商户</a>
					<a class="lnav2_item  <#if subselected='merchant'>lnav2_now</#if>" href="${base}/merchant/list">集团</a>
				</div>
				</@shiro.hasAnyRoles>
			</@shiro.hasAnyRoles>

			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
			<a class="lnav_item <#if selected='shop'>lnav_now</#if>" href="${base}/shop/${_ACCOUNT_.businessId}"><img src="${static_base}/css/img/admin_frame/<#if selected='shop'>lnav_img11_w.png<#else>lnav_img11.png</#if>" class="lnav_img lnav_img11"><span class="lnav_text" style="margin-left:6px;">展示</span></a>
			<div class="lnav2_wrap   <#if selected='shop'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='detail'>lnav2_now</#if>" href="${base}/shop/${_ACCOUNT_.businessId}">商户资料</a>
				<a class="lnav2_item <#if subselected='about'>lnav2_now</#if>" href="${base}/shop/${_ACCOUNT_.businessId}/about">关于商户</a>
				<a class="lnav2_item  <#if subselected='album'>lnav2_now</#if>" href="${base}/album/list">相册</a>
			</div>
			</@shiro.hasAnyRoles>

			<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
			<a class="lnav_item <#if selected='member'>lnav_now</#if>" href="${base}/member/merchant/list" ><img src="${static_base}/css/img/admin_frame/<#if selected='member'>lnav_img3_w.png<#else>lnav_img3.png</#if>" class="lnav_img lnav_img3"><span class="lnav_text">会员</span></a>
			<div class="lnav2_wrap <#if selected='member'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='list'>lnav2_now</#if>" href="${base}/member/merchant/list">会员列表</a>
				<a class="lnav2_item <#if subselected='type'>lnav2_now</#if>" href="${base}/member/merchant/type/list">会员类型</a>
				<a class="lnav2_item <#if subselected='template'>lnav2_now</#if>" href="${base}/member/merchant/template/list">会员模板</a>
			</div>
			</@shiro.hasAnyRoles>
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
			<a class="lnav_item <#if selected='member'>lnav_now</#if>" href="${base}/member/shop/list" ><img src="${static_base}/css/img/admin_frame/<#if selected='member'>lnav_img3_w.png<#else>lnav_img3.png</#if>" class="lnav_img lnav_img3"><span class="lnav_text">会员</span></a>
			<div class="lnav2_wrap <#if selected='member'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='list'>lnav2_now</#if>" href="${base}/member/shop/list">会员列表</a>
				<a class="lnav2_item <#if subselected='type'>lnav2_now</#if>" href="${base}/member/shop/type/list">会员类型</a>
				<a class="lnav2_item <#if subselected='template'>lnav2_now</#if>" href="${base}/member/shop/template/list">会员模板</a>
			</div>
			</@shiro.hasAnyRoles>
            <#if !(_BUSINESS_TYPE_ =="SHOP" && _BUSINESS_CENTRAL_ && !_BUSINESS_CENTRAL_AVAILABLE_)>
                <@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
                    <a class="lnav_item <#if selected='prepaid'>lnav_now</#if>" <#if _BUSINESS_TYPE_!="MERCHANT" && (_BUSINESS_CENTRAL_)>href="${base}/prepaid/rule"<#else >href="${base}/prepaid/card/list"</#if>
                            ><img src="${static_base}/css/img/admin_frame/<#if selected='prepaid'>lnav_img16_w.png<#else>lnav_img16.png</#if>" class="lnav_img lnav_img16"><span class="lnav_text">预付卡</span></a>
                    <div class="lnav2_wrap <#if selected='prepaid'>lnav2_wrap_show</#if>">
                        <#if _BUSINESS_TYPE_=="MERCHANT" && (!_BUSINESS_CENTRAL_)>
                            <a class="lnav2_item <#if subselected='shop_list'>lnav2_now</#if>" href="${base}/merchant/${_ACCOUNT_.businessId}/prepaid/shop/list">商户列表</a>
                        <#elseif _BUSINESS_TYPE_!="MERCHANT" && (_BUSINESS_CENTRAL_)>
                            <a class="lnav2_item <#if subselected='rule_management'>lnav2_now</#if>" href="${base}/prepaid/rule">充值规则管理</a>
                        <#else >
                            <a class="lnav2_item <#if subselected='prepaid'>lnav2_now</#if>" href="${base}/prepaid/card/list">预付卡管理</a>
                            <a class="lnav2_item <#if subselected='rule_management'>lnav2_now</#if>" href="${base}/prepaid/rule">充值规则管理</a>
                        </#if>
                    </div>
                </@shiro.hasAnyRoles>
            </#if>
			<#--
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
				<a class="lnav_item <#if selected='activity'>lnav_now</#if>" href="/activity/list"><img src="${static_base}/css/img/admin_frame/<#if selected='activity'>lnav_img4_w.png<#else>lnav_img4.png</#if>" class="lnav_img lnav_img4"><span class="lnav_text">活动</span></a>
			</@shiro.hasAnyRoles>
			-->
			<a class="lnav_item <#if selected='coupon'>lnav_now</#if>" href="/coupon/couponInfo/list"><img src="${static_base}/css/img/admin_frame/<#if selected='coupon'>lnav_img5_w.png<#else>lnav_img5.png</#if>" class="lnav_img lnav_img5"><span class="lnav_text" style="margin-left:3px;">电子券&套票</span></a>
			<div class="lnav2_wrap <#if selected='coupon'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='couponInfoList'>lnav2_now</#if>" href="${base}/coupon/couponInfo/list">电子券列表</a>
				<a class="lnav2_item <#if subselected='couponInfoPackageList'>lnav2_now</#if>" href="${base}/coupon/couponInfoPackage/list">电子券套票列表</a>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<a class="lnav2_item <#if subselected='refundList'>lnav2_now</#if>" href="${base}/refund/list">电子券&套票退款</a>
				</@shiro.hasAnyRoles>
			</div>
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
			<a class="lnav_item <#if selected='marketing'>lnav_now</#if>" href="${base}/sms/task/list"><img src="${static_base}/css/img/admin_frame/<#if selected='marketing'>lnav_img13_w.png<#else>lnav_img13.png</#if>" class="lnav_img lnav_img13"><span class="lnav_text" style="margin-left:4px;">推广</span></a>
			<div class="lnav2_wrap <#if selected='marketing'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='list'>lnav2_now</#if>" href="${base}/sms/task/list">短信任务</a>
			</div>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<a class="lnav_item <#if selected='cashiering'>lnav_now</#if>" href="${base}/item/merchant/list"><img src="${static_base}/css/img/admin_frame/<#if selected='cashiering'>lnav_img14_w.png<#else>lnav_img14.png</#if>" class="lnav_img lnav_img14"><span class="lnav_text" style="margin-left:1px;">收银</span></a>
						<div class="lnav2_wrap <#if selected='cashiering'>lnav2_wrap_show</#if>">
						<a class="lnav2_item <#if subselected='merchant'>lnav2_now</#if>" href="${base}/item/merchant/list">商品管理</a>
						<a class="lnav2_item <#if subselected='category'>lnav2_now</#if>" href="${base}/item/merchant/category">分类管理</a>
						<a class="lnav2_item <#if subselected='record'>lnav2_now</#if>" href="${base}/inventory/recordlist">出入库记录</a>
					</div>
				</@shiro.hasAnyRoles>
				<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
					<a class="lnav_item <#if selected='cashiering'>lnav_now</#if>" href="${base}/item/list"><img src="${static_base}/css/img/admin_frame/<#if selected='cashiering'>lnav_img14_w.png<#else>lnav_img14.png</#if>" class="lnav_img lnav_img14"><span class="lnav_text" >收银</span></a>
					<div class="lnav2_wrap <#if selected='cashiering'>lnav2_wrap_show</#if>">
						<a class="lnav2_item <#if subselected='shop'> lnav2_now</#if>" href="${base}/item/list">商品管理</a>
						<a class="lnav2_item <#if subselected='setting'> lnav2_now</#if>" href="${base}/item/category">分类管理</a>
						<a class="lnav2_item <#if subselected='record'>lnav2_now</#if>" href="${base}/inventory/recordlist">出入库记录</a>
					</div>
				</@shiro.hasAnyRoles>
			</@shiro.hasAnyRoles>
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
			<a class="lnav_item <#if selected='order'>lnav_now</#if>" href="${base}/statistics/order/analyze/trend"><img src="${static_base}/css/img/admin_frame/<#if selected='order'>lnav_img15_w.png<#else>lnav_img15.png</#if>" class="lnav_img lnav_img15"><span class="lnav_text" style="margin-left:6px;">统计&明细</span></a>
			<div class="lnav2_wrap <#if selected='order'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='analyze'>lnav2_now</#if>" href="${base}/statistics/order/analyze/trend">点单分析</a>
				<a class="lnav2_item <#if subselected='item'>lnav2_now</#if>" href="${base}/statistics/item/analyze">商品分析</a>
				<a class="lnav2_item <#if subselected='item_category'>lnav2_now</#if>" href="${base}/statistics/item_category/analyze">品类分析</a>
				<a class="lnav2_item <#if subselected='order'>lnav2_now</#if>" href="${base}/order/list">点单明细</a>
				<a class="lnav2_item <#if subselected='physical_coupon_order'>lnav2_now</#if>" href="${base}/physical_coupon/used/list">实体券核销明细</a>
				<a class="lnav2_item <#if subselected='couponSales'>lnav2_now</#if>" href="${base}/statistics/coupon/sales">电子券销售统计&明细</a>
				<a class="lnav2_item <#if subselected='couponConsume'>lnav2_now</#if>" href="${base}/statistics/coupon/consume">电子券核销统计&明细</a>
				<a class="lnav2_item <#if subselected='pos_transaction'>lnav2_now</#if>" href="${base}/pos_transaction/bank_card/list">刷卡统计&明细</a>
				<#--
					<a class="lnav2_item <#if subselected='cmcc_ticket'>lnav2_now</#if>" href="${base}/pos_transaction/cmcc_ticket/list">电子券统计&明细</a>
				-->
				<a class="lnav2_item <#if subselected='alipay_qrcode'>lnav2_now</#if>" href="${base}/alipay_transaction/alipay_qrcode/list">扫码付统计&明细</a>
                <a class="lnav2_item <#if subselected='prepaid_charge'>lnav2_now</#if>" href="${base}/prepaid/charge/list">预付卡充值统计&明细</a>
                <#if _SHOP_ENABLE_SHIFT == true ??>
	               	 <a class="lnav2_item <#if subselected='shift'>lnav2_now</#if>" href="${base}/shift/list">交接班&明细</a>
                </#if>
			</div>
			</@shiro.hasAnyRoles>
			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
			<a class="lnav_item <#if selected='balance'>lnav_now</#if>" href="${base}/balance/transaction/list"><img src="${static_base}/css/img/admin_frame/<#if selected='balance'>lnav_img9_w.png<#else>lnav_img9.png</#if>" class="lnav_img lnav_img9"><span class="lnav_text" style="margin-left:3px;">账户</span></a>
			</@shiro.hasAnyRoles>
			<#--
			<a class="lnav_item" href="javascript:"><img src="${static_base}/css/img/admin_frame/lnav_img9.png" class="lnav_img lnav_img8"><span class="lnav_text">积分</span></a>
			-->

			<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
					<a class="lnav_item <#if selected='physical_coupon'>lnav_now</#if>" href="${base}/physical_coupon/merchant/list"><img src="${static_base}/css/img/admin_frame/<#if selected='physical_coupon'>lnav_img18_w.png<#else>lnav_img18.png</#if>" class="lnav_img lnav_img18"><span class="lnav_text">实体券</span></a>
					<div class="lnav2_wrap <#if selected='physical_coupon'>lnav2_wrap_show</#if>">
						<a class="lnav2_item <#if subselected='list'>lnav2_now</#if>" href="${base}/physical_coupon/merchant/list">实体券管理</a>
					</div>
				</@shiro.hasAnyRoles>
				<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
					<a class="lnav_item <#if selected='physical_coupon'>lnav_now</#if>" href="${base}/physical_coupon/shop/list"><img src="${static_base}/css/img/admin_frame/<#if selected='physical_coupon'>lnav_img18_w.png<#else>lnav_img18.png</#if>" class="lnav_img lnav_img18"><span class="lnav_text">实体券</span></a>
					<div class="lnav2_wrap <#if selected='physical_coupon'>lnav2_wrap_show</#if>">
						<a class="lnav2_item <#if subselected='list'>lnav2_now</#if>" href="${base}/physical_coupon/shop/list">实体券管理</a>
					</div>
				</@shiro.hasAnyRoles>
			</@shiro.hasAnyRoles>


			<@shiro.hasAnyRoles name="ROLE_ADMIN">
			<a class="lnav_item <#if selected='setting'>lnav_now</#if>" href="${base}/position/landmark"><img src="${static_base}/css/img/admin_frame/<#if selected='setting'>lnav_img10_w.png<#else>lnav_img10.png</#if>" class="lnav_img lnav_img8"><span class="lnav_text">设置</span></a>
			<div class="lnav2_wrap lnav2_wrap <#if selected='setting'>lnav2_wrap_show</#if>">
				<a class="lnav2_item  <#if subselected='landmark'> lnav2_now</#if>" href="${base}/position/landmark">地标设置</a>
				<a class="lnav2_item  <#if subselected='category'> lnav2_now</#if>" href="${base}/category/list">商户类型</a>
				<#--
				<a class="lnav2_item  <#if subselected='shop'> lnav2_now</#if>" href="javascript:">滑动链接样式</a>
				<a class="lnav2_item  <#if subselected='shop'> lnav2_now</#if>" href="javascript:">选中样式</a>
				-->
			</div>
		   <#--	<a class="lnav_item <#if selected='pagersetting'>lnav_now</#if>" href="${base}/pager/pager/list"><img src="${static_base}/css/img/admin_frame/<#if selected='pagersetting'>lnav_img10_w.png<#else>lnav_img10.png</#if>" class="lnav_img lnav_img8"><span class="lnav_text">文章模板</span></a>
			<div class="lnav2_wrap lnav2_wrap <#if selected='pagersetting'>lnav2_wrap_show</#if>">
				<a class="lnav2_item <#if subselected='pager'> lnav2_now</#if>" href="${base}/pager/pager/list">文章列表</a>
				<a class="lnav2_item <#if subselected='pagercategory'> lnav2_now</#if>" href="${base}/pager/category/list">文章分类</a>
				<a class="lnav2_item <#if subselected='pagertemplate'> lnav2_now</#if>" href="${base}/pager/template/list">文章模板</a>
			</div>-->
			</@shiro.hasAnyRoles>

		    <@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN,ROLE_MERCHANT_ADMIN">
			<a class="lnav_item <#if selected='setting'>lnav_now</#if>" href="${base}/shop/resetpwd"><img src="${static_base}/css/img/admin_frame/<#if selected='setting'>lnav_img10_w.png<#else>lnav_img10.png</#if>" class="lnav_img lnav_img8"><span class="lnav_text">设置</span></a>
			<div class="lnav2_wrap lnav2_wrap <#if selected='setting'>lnav2_wrap_show</#if>">
				  <a class="lnav2_item  <#if subselected='resetPwd'> lnav2_now</#if>" href="${base}/shop/resetpwd">登录密码修改</a>
				  <@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
				  <a class="lnav2_item <#if subselected='pos'> lnav2_now</#if>" href="${base}/shop/${_ACCOUNT_.businessId}/pos">终端账号管理</a>
				  <a class="lnav2_item <#if subselected='shop_printer'> lnav2_now</#if>" href="${base}/shop/shop_printer/list">打印档口设置</a>
				  <a class="lnav2_item <#if subselected='shop_printer_service'>lnav2_now</#if>" href="${base}/shop/shop_printer_service">打印服务设置</a>
				  <a class="lnav2_item <#if subselected='shift_setting'>lnav2_now</#if>" href="${base}/shift/shift_setting">交接班设置</a>
                  <a class="lnav2_item <#if subselected='pay_setting'>lnav2_now</#if>" href="${base}/shop/pay_setting">支付设置</a>
				  <#--<a class="lnav2_item <#if subselected='pos_terminal'> lnav2_now</#if>" href="${base}/shop/pos_terminal">已绑定终端</a>-->
				  </@shiro.hasAnyRoles>
			</div>
			</@shiro.hasAnyRoles>


			<div class="lnav2_bottom"></div>
		</div>
	</div>
</#macro>

<#macro p page totalpage>
	<@compress single_line=true>
	<#if (request.queryString)??>
		<#assign requestParams=request.queryString?replace("\\&?p=(\\d+)\\&?","","r") />
		<#if requestParams?has_content>
			<#assign requestParams = '&' + requestParams />
		</#if>
	</#if>
	<#assign currentPage=page?number >
	<#if currentPage-4 gt 0>
		<#assign beginPage = currentPage-4 />
	<#else>
		<#assign beginPage = 1 />
	</#if>
	<#if totalpage-currentPage lt 4>
		<#assign beginPage = beginPage - (4-totalpage + currentPage)  />
		<#if beginPage lt 1>
			<#assign beginPage = 1 />
		</#if>
	</#if>
	<#if currentPage-1 gt 0>
		<a class="page_a page_prev" href="?p=${currentPage-1}${requestParams}"></a>
	<#else>
		<span class="page_a page_prev" class="disabled"></span>
	</#if>
	<#if currentPage gt 5 && totalpage gt 10 >
		<a class="page_a" href="?p=1${requestParams}">1</a> <span class="page_dots">...</span>
	</#if>
	<#assign endPage=beginPage+8 />
	<#if endPage gt totalpage>
		<#assign endPage=totalpage />
		<#assign beginPage=endPage-8 />
	</#if>
	<#if beginPage lt 1>
		<#assign beginPage = 1 />
	</#if>
	<#if endPage lt 1>
		<#assign endPage = 1 />
	</#if>
	<#list beginPage..endPage as x>
	<#if x == currentPage>
		<span class="current page_a page_a_now">${x}</span>
	<#else>
<a class="page_a" href="?p=${x}${requestParams}">${x}</a>
	</#if>
 	</#list>
	<#if currentPage lte totalpage - 5 && totalpage gt 10>
		<span class="page_dots">...</span> <a href="?p=${totalpage}${requestParams}" class="page_a">${totalpage}</a>
	</#if>
	<#if currentPage lt totalpage>
		<a class="page_a page_next" href="?p=${currentPage+1}${requestParams}"></a>
	<#else>
		<span class="disabled page_a page_next"></span>
	</#if>
	</@compress>
</#macro>

<#macro page_footer>

</div>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
</body>
</html>
</#macro>

<#macro can_modify module>
	<#if !(_BUSINESS_.merchant)!>
		<#nested>
		<#return>
	</#if>
	<#if !(module == 'member' && (_BUSINESS_.merchant.memberCentralManagement))!>
		<#nested>
		<#return>
	</#if>
	<#if !(module == 'member' && (_BUSINESS_.merchant.memberCentralManagement))!>
		<#nested>
	</#if>
</#macro>