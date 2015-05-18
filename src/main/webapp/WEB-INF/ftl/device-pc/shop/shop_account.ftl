<#import "/macro.ftl" as m>
<@m.page_header selected='shop' subselected="account" title="商户账号设置" css='seller_info|pos_info' js='pos_info' />
<div class="rwrap">
		<p class="r_title">商户资料</p>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
		<div class="nav_wrap">
			<a href="${base}/shop/${shopId}" class="nav_a ">商户基本信息</a>
			<a href="${base}/shop/${shopId}/about" class="nav_a ">关于商户</a>
			<a href="${base}/shop/${shopId}/album" class="nav_a ">相册列表</a>
			<a href="${base}/shop/${shopId}/account" class="nav_a nav_now">商户账号</a>
			<a href="${base}/shop/${shopId}/pos" class="nav_a">POS相关</a>
		</div>
		</@shiro.hasAnyRoles>
		<div class="tb_wrap">
			<div class="tb_title lh20"><span class="fl">爱客仕登录信息</span>
			<#if !accountList>
				<a class="pop_a pop_account" href="javascript:">添加</a>
				<#else>
				<a class="pop_a pop_account" href="javascript:">修改</a>
			</#if>
			</div>
			<table class="tb_main">
				<tr class="tr_bg th">
					<td class="td_l wd70"><b>*</b>管理员账号</td>
					<td><#if accountList>${accountList[0].username}</#if></td>
				</tr>
			</table>
		</div>
		<@shiro.hasAnyRoles name="ROLE_ADMIN">
		<div class="tb_wrap tb_wrap2">
			<div class="tb_title lh20"><span class="fl">短信和域名</span>
				<a class="pop_a pop_realname" href="javascript:">修改</a>
			</div>
			<table class="tb_main">
				<tr class="th">
					<td class="td_l wd70">短信后缀</td>
					<td>${(shopInfo.smssuffix)!}</td>
				</tr>
				<tr class="th">
					<td class="td_l wd70">二级域名</td>
					<td>${(shopInfo.xposSld)!}</td>
				</tr>
			</table>
		</div>
		</@shiro.hasAnyRoles>
	<div class="pb edit_account">
		<form action="${base}/shop/account/edit/${shopId}" method="POST" id="form1">
			<div class="pb_title">修改登录账号<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">管理员账号</p>
					<input type="text" name="username" class="pb_item_input lh14" id="name_account">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">登录密码</p>
					<input type="password" class="pb_item_input lh14" name="password" id="password" placeholder="空为原密码">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">确认密码</p>
					<input type="password" class="pb_item_input lh14" id="password2" placeholder="空为原密码">
				</div>
			</div>
			<button type="button" class="pb_btn pb_btn1">保存</button>
		</form>
	</div>
	<@shiro.hasAnyRoles name="ROLE_ADMIN">
	<div class="pb edit_opt">
		<form action="${base}/shop/shopInfo/edit/${shopId}" method="post" id="form2">
			<input type="hidden" name="_method" value="put">
			<div class="pb_title">修改短信和域名<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">短信后缀</p>
					<input type="text" class="pb_item_input" id="name_suffix" name="smssuffix">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">爱客仕二级域名</p>
					<input type="text" class="pb_item_input" id="realname" name="xposSld">
				</div>
			</div>
			<button type="button" class="pb_btn pb_btn2">保存</button>
		</form>
	</div>
	<div class="pb add_pos">
		<form action="${base}/shop/${shopId}/gatewayAccount/add" method="post" id="form3">
		    <input type="hidden" name="submissionToken"  value="${submissionToken}"/>
			<div class="pb_title">添加支付方式和终端<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">终端号</p>
					<input type="text" class="pb_item_input required" name="terminal">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">支付类型</p>
					<select name="type">
						<option value="UMPAY">联动优势</option>
						<option value="BOC">中国银行</option>
						<option value="SHENGPAY">盛付通</option>
						<option value="ALIPAY">支付宝</option>
					</select>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">商户号</p>
					<input type="text" class="pb_item_input required" name="account">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">备注</p>
					<input type="text" class="pb_item_input" name="remark">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">校验码</p>
					<input type="text" class="pb_item_input signcode" name="signKey">
				</div>
			</div>
			<button type="button" class="pb_btn pb_btn3">保存</button>
		</form>
	</div>
	<div class="pb del_pos">
		<form action="${base}/shop/${shopId}/gatewayAccount/delete" method="post" id="form5">
			<input type="hidden" name="id" value="" id="actDel"/>
			<div class="pb_title">确定解除设备绑定？<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">设备码</p>
					<p class="pb_item_cont deviceNumber" >9F6CD413-BC5C-443B-80F1-CEB1E75F60C</p>
				</div>
			</div>
			<button type="button" class="pb_btn pb_btns pb_btn5">确定</button><div class="div_cancel">或<a href="javascript:;" class="a_cancel">取消</a></div>
		</form>
	</div>
	</@shiro.hasAnyRoles>
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>	
<@m.page_footer />