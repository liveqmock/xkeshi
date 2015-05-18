<!doctype html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${uniqueTitle} -地标管理</title>
	<link rel="stylesheet" href="${static_base}/css/reset.css" type="text/css">
	<link rel="stylesheet" href="${static_base}/css/admin_base.css" type="text/css">
	<link rel="stylesheet" href="${static_base}/css/xpos_base.css" type="text/css">
	<link rel="stylesheet" href="${static_base}/css/pos_manage_new.css" type="text/css">
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=1abcb63868d28ba6c1140bbeb3fc4f89"></script>
	<script src="${static_base}/js/jquery-1.8.3.min.js"></script>
	<script src="${static_base}/js/base.js"></script>
	<script src="${static_base}/js/admin_base.js"></script>
	<!-- <script>var RETURN_CONTEXT = "${xpossHostContext}" </script> -->
	<script>var RETURN_CONTEXT = "${static_base}" </script>
</head>
	<script>
		var CITY_CODE = '${city_code}';
		var SSN  = '';
		var SSN2 = '';
		var TYPE = '${type}';
		var LNG  = '${(lng)!}';
		var LAT  =  '${(lat)!}';
		var ADRSEMPTY = '${(adrsEmpty)!}';
		var ISEDIT  =  '${(isEdit)!}';
		var BASE  =  '${(static_base)!}';
	</script>
<body>
		<div class="map_wrap">
			<div id="allmap"></div>
			<div class="search">
				<div class="search_wrap" style="">
					<input id="location" placeholder="搜索或输入地址" value="${(address)!}">
					<button id="search">搜索</button>
				</div>
				<div id="r-result"></div>
			</div>
		</div>
		<div class="po_wrap">
			<span class="po_lable_district_name">行政区:</span>
			<select name="districtCode" class="po_district_code"  disabled="disabled">
			 	    <option value="000000">请选择</option>
					<option value="330102">上城区</option>
					<option value="330103">下城区</option>
					<option value="330104">江干区</option>
					<option value="330105">拱墅区</option>
					<option value="330106">西湖区</option>
					<option value="330108">滨江区</option>
					<option value="330109">萧山区</option>
					<option value="330110">余杭区</option>
					<option value="330122">桐庐县</option>
					<option value="330127">淳安县</option>
					<option value="330182">建德市</option>
					<option value="330183">富阳市</option>
					<option value="330185">临安市</option>
			</select>
			<span class="po_label po_lng_label">经度:</span>
			<span class="po_lng">${(lng)!}</span>
			<span class="po_label"  style="color:">纬度:</span>
			<span class="po_lat">${(lat)!}</span>
			<span style="display: none;" class='po_button_div' >
				<button class="post_btn">确定</button>
				<input type="hidden" id="address" >
			</span>

	<script type="text/javascript">
	$('.post_btn').click(function(){
		if($('.po_district_code').val()){
			window.opener.setPosition($('.po_district_code option:selected').val(),
									  $('.po_lng').text(), $('.po_lat').text(),
									  $('#address').val());
			window.close();
		}else{
			alert('选择行政区');
		}
	});

	</script>
</body>		
</html>
<script src="${static_base}/js/pos_manage_new.js"></script>