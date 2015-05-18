<#import "/macro.ftl" as m >
<@m.page_header title="登录密码修改" selected='setting' subselected="resetPwd"  css="seller_info|msg_reset" js="msg_reset" />
	<div class="rwrap">
		<div class="pop_div pop_error">输入的密码不正确</div>
		<div class="r_title">登录密码修改</div>	
		<div class="tb_wrap tb_other">
			<form action="/shop/resetpwd" method="POST"  id="resetPwdForm">
				<table class="tb_main tb_list">
					<tr class="">
						<td><label>原密码</label><input type="password" name="oldPwd" class="input_box old_psw" /></td>
					</tr>
					<tr class="tr_bg">
						<td><label>新密码</label><input type="password" name="newPwd" class="input_box new_psw" /></td>
					</tr>
					<tr class="">
						<td><label>确认新密码</label><input type="password" class="input_box confirm_psw" /></td>
					</tr>
				</table>
			</form>	
		</div>
		<div class="btn_wrap">
			<button type="button" class="green_btn_l set_psw_btn">保存</button>
			<span class="btn_split">或</span>
			<a href="/" class="cancel_a">取消</a>
		</div>		
	</div>
	<#if status?? && status>
		<div class="pop_hint pop_hint2">${msg}</div>
	<#elseif status?? && !status>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
<@m.page_footer />




