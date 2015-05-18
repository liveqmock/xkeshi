<!doctype html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${uniqueTitle} -地标管理</title>
	<link rel="stylesheet" href="${static_base}/css/reset.css" type="text/css">
	<link rel="stylesheet" href="${static_base}/css/admin_base.css" type="text/css">
	<link rel="stylesheet" href="${static_base}/css/xpos_base.css" type="text/css">
	<link rel="stylesheet" href="${static_base}/css/ldmk_manage_new.css" type="text/css">
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=1abcb63868d28ba6c1140bbeb3fc4f89"></script>
	<script src="${static_base}/js/jquery-1.8.3.min.js"></script>
	<script src="${static_base}/js/base.js"></script>
	<script>
	    var SSN = '';
		var SSN2 = '';
		var CITY_CODE = '330100';
		var TYPE = '${type}';
		var LAT = '${(position.latitude)!}';
		var LNG = '${(position.longitude)!}';
		var RAD = '${(position.radius)!}';
		var BASE = '${(static_base)!}';
	</script>
</head>
<body>
		<div class="map_wrap">
			<div id="allmap"></div>
			<div class="search">
				<div class="search_wrap">
					<input id="location" placeholder="搜索或输入地址">
					<button id="search">搜索</button>
				</div>
				<div id="r-result"></div>
			</div>
		</div>
			<div class="ldmk_wrap">
			<a href="javascript:" class="ldmk_edit_a">编辑</a>
			<span class="ldmk_label">地标名称</span>
			<input type="text" class="ldmk_name" value="${(landmark.name)!}">
			<input type="hidden" class="ldmk_id" value="${(landmark.id)!}">
			<span class="ldmk_lable_district_name">行政区:</span>
			<select name="districtCode" class="ldmk_district_code"  disabled="disabled">
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
			<input type="hidden" class="po_id" value="${(position.id)!}">
			<span class="ldmk_label ldmk_lng_label">经度</span>
			<span class="po_lng">${(position.latitude)!}</span>
			<span class="ldmk_label"  style="color:">纬度</span>
			<span class="po_lat">${(position.longitude)!}</span>
			<span class="ldmk_label"  style="color:">地标范围</span>
			<input type="text" class="ldmk_rad"  value="${(landmark.radius)!}"> 
			<em class="meter">米</em>
			<span style="display: none;" class='ldmk_button_div' >
				<button class="post_btn">提交</button>
			</span>
</body>
</html>
<script src="${static_base}/js/landmark_manage_map.js"></script>