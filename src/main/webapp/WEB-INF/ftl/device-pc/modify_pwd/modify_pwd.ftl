<!doctype html>

<head>
<meta charset="utf-8">
<title>注册修改密码 - 爱客仕</title>
<link rel="stylesheet" href="${static_base}/css/reset.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/admin_frame.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/modify_pwd.css" type="text/css">
<script src="${static_base}/js/jquery-1.8.3.min.js"></script>
<script src="${static_base}/js/back_base.js"></script>
</head>

<body>
	<div class="top">
		<div class="top_main">
			<span class="logo"></span><span class="middle_line title_text">|</span><span class="title_text">修改初始密码</span>
		</div>
	</div>
	<div class="context_main">
		<div class="step_header">
			<div class="step_1">
				<p><span>第一步：修改初始密码</span></p>
			</div>
			<div class="step_2">
				<p><span>第二步：新密码设置成功</span></p>
			</div>
		</div>
		<div class="step_main">
			<p class="warning_text">为了您的账号安全，首次登录时请修改初始密码。</p>
			<p class="pwd_rule">密码设置说明：密码长度为6-32位字符，须同时包含字母和数字</p>
			<div class="pwd_form_wrap">
				<form id="pwd_form" class="pwd_form" action="${base}/setpwd" method="POST">
				    <input  type="hidden"  name="_method" value="PUT">
					<div>
						<label for="new_pwd">新密码：<input id="new_pwd" class="js_pwd_text" name="pwd" type="password"><span class="error">密码只能是字母或数字</span></label>
					</div>
					<div class="mt25">
						<label for="new_pwd2">确认新密码：<input id="new_pwd2" class="js_pwd_text" name="pwd2" type="password"><span class="error2">密码只能是字母或数字</span></label>
					</div>
					<div class="submit_wrap">
						<input id="pwd_submit" class="pwd_submit" type="submit" value="确定"><span class="or">或</span><span class="logout"><a href="${base}/logout">退出登录</a></span>
					<div>
				</form>
			</div>
		</div>
	</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
	<script src="${static_base}/js/modify_pwd.js"></script>
</body>
<html>