<!doctype html>
<head>
<meta charset="utf-8">
<meta version="tag 1.0.5">
<title>出错了 - 爱客仕</title>
<link rel="stylesheet" href="/static/css/reset.css" type="text/css">
<link rel="stylesheet" href="/static/css/fancybox/jquery.fancybox.css" type="text/css">
<link rel="stylesheet" href="/static/css/admin_frame.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/static/css/seller_list_new.css">
<link rel="stylesheet" type="text/css" href="/static/css/error.css">
<script src="/static/js/jquery-1.8.3.min.js"></script>
<script src="/static/js/json2.js"></script>
<script src="/static/js/fancybox/jquery.fancybox.pack.js"></script>
<script src="/static/js/back_base.js"></script>
</head>
<body>
<div class="top">
	<div class="top_main">
		<span class="logo"></span>
		<a href="javascript:" class="msg">
			<span class="msg_text">站内信</span><span class="msg_num">0</span>
		</a>
	<div class="user">${(_ACCOUNT_.username)!''}<a href="${base}/logout">退出</a></div>
	</div>
</div>
<div class="wrapper">
	<div class="error">请勿重复提交 &nbsp;&nbsp;<a href="${(redirectURL)!'/'}" ><#if redirectURL??>返回<#else>返回首页</#if></a></div>
</div>
</body>
</html>