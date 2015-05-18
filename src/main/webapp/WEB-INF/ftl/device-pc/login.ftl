<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>商户后台——爱客仕智能收银一体机 官网</title>
<meta http-equiv="pragma" content="no-cache">
 <meta http-equiv="cache-control" content="no-cache">
 <meta http-equiv="expires" content="0">   
<link rel="stylesheet" href="${static_base}/css/reset.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/admin_base.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/admin_frame.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/base.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/admin_login.css" type="text/css">

<script src="${static_base}/js/jquery-1.8.3.min.js"></script>
<script src="${static_base}/js/base.js"></script>
<script src="${static_base}/js/admin_base.js"></script>
<script src="${static_base}/js/admin_login.js"></script>
<script src="${static_base}/js/back_base.js"></script>
</head>
<script>
	function change() {
		document.getElementById("img").src = "${base}/image/code?time="+ new Date().getTime();
	}
    var SSN = ''
	var SSN2 = ''
	var str  = '<#list list as str> ${str} </#list>';
</script>
<body>
<div id="Wrapper" class="Wrapper">
	<form id="employee-login-form" action="/login" method="POST">
		<div class="admin_login_main">
			<div class="admin_login_logo"></div>
			<div class="admin_login_wrap">
				<input id="account" name="username" class="admin_login_name inputs" type="text" value=""/>
				<input type="password" id="user_passwd" name="user_passwd" style="display:none" autocomplete="off" /> 
				<input id="password"  type="password"  oninput="user_passwd.value=this.value" name="password" class="admin_login_pwd inputs" value="" autocomplete="off"/>
				<div class="code_wrap" data-count="${errCount}" >
					<input id="auth_code" name="j_code" class="admin_login_code inputs" type="text" value=""/>
					<span class="s_code"><img src="${base}/image/code" onclick="change();" id="img"></span>
				</div>
				<div class="tips_wrap">
					<span class="tips">${msg}</span>
					<button type="submit" class="admin_login_btn0"></button>			
				</div>
			</div>
		</div>
</form>

</div>

<div id="Bottom">©爱客仕 2015</div>
</html>