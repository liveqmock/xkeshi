<!doctype html>

<head>
<meta charset="utf-8">
<title>注册修改密码 - 爱客仕</title>
<link rel="stylesheet" href="${static_base}/css/reset.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/admin_frame.css" type="text/css">
<link rel="stylesheet" href="${static_base}/css/modify_pwd_second.css" type="text/css">
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
			<p class="success_text">新密码设置成功！</p>
			<p class="ps_msg">请用新密码重新登录，系统将在<span id="time_reduction">5</span>秒后自动退出...</p>
		</div>
	</div>
	
	<script>
        var i = 4; 
		var intervalid; 
		intervalid = setInterval("timeout()", 1000); 
		function timeout() { 
			if (i == 0) { 
				window.location.href = "${base}/logout"; 
				clearInterval(intervalid); 
			}
			if(i>0){
				document.getElementById("time_reduction").innerHTML = i;
			}
			i--;
		}
    </script>
</body>
<html>