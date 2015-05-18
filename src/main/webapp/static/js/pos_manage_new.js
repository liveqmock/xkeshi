var map,marker,TYPE;
var district_name  ;
var marker_dragg_status  = false;
var bid = window.BRANCH_ID || 0 ;
var addMvPointer  ;
var ply ;
var landMarkerArry  = [];
var landMarkCir  ;
var infoWindow = new BMap.InfoWindow('当前标注的位置(可移动、可右键添加标注)',{'enableMessage':false});
var img        = BASE+"/css/img/marker_blue_sprite.png";
$(function(){
	// 百度地图API功能
	map = new BMap.Map("allmap");
	map.enableScrollWheelZoom();
	map.addControl(new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT}));    //左上角，默认地图控件
	//构造全景控件
	var stCtrl = new BMap.PanoramaControl(); 
	stCtrl.setOffset(new BMap.Size(20, 20));
	map.addControl(stCtrl);//添加全景控件
	
	var init_po = function(lng, lat){
		var po = new BMap.Point(lng, lat);    // 创建点坐标 
		addPointer(po);
		if(lng && lat){
			map.centerAndZoom(po,13);
		}else{
			map.centerAndZoom("杭州",13);
		}
	}
	
	function addMarker(point, index){
	var myIcon = new BMap.Icon("http://api.map.baidu.com/img/markers.png", new BMap.Size(23, 25), {
	    offset: new BMap.Size(10, 25),
	    imageOffset: new BMap.Size(0, 0 - index * 25)
	  });
	  var marker = new BMap.Marker(point, {icon: myIcon});
	  map.addOverlay(marker);
	  return marker;
	}
   
	// 添加信息窗口
	function addInfoWindow(marker,poi,index){
	    var maxLen = 10;
	    var name = null;
	    if(poi.type == BMAP_POI_TYPE_NORMAL){
	        name = "地址：  ";
	    }else if(poi.type == BMAP_POI_TYPE_BUSSTOP){
	        name = "公交：  ";
	    }else if(poi.type == BMAP_POI_TYPE_SUBSTOP){
	        name = "地铁：  ";
	    } 
	    // infowindow的标题
	    var infoWindowTitle = '<div style="font-weight:bold;color:#CE5521;font-size:14px">'+poi.title+'</div>';
	    // infowindow的显示信息
	    var infoWindowHtml = [];
	    infoWindowHtml.push('<table cellspacing="0" style="table-layout:fixed;width:100%;font:12px arial,simsun,sans-serif"><tbody>');
	    infoWindowHtml.push('<tr>');
	    infoWindowHtml.push('<td style="vertical-align:top;line-height:16px;width:38px;white-space:nowrap;word-break:keep-all">' + name + '</td>');
	    infoWindowHtml.push('<td style="vertical-align:top;line-height:16px">' + poi.address +  poi.point.lng+','+poi.point.lat+' </td>');
	    infoWindowHtml.push('</tr>');
	    infoWindowHtml.push('<tr><td>操作:</td><td style="vertical-align:top;line-height:16px"><button type="button" onclick="mvPointer()">将标注移至此处</button> </td>');
	    infoWindowHtml.push('</tbody></table>');
	    var infoWindow = new BMap.InfoWindow(infoWindowHtml.join(""),{title:infoWindowTitle,width:200}); 
	    var openInfoWinFun = function(){
	        marker.openInfoWindow(infoWindow);
	        for(var cnt = 0; cnt < maxLen; cnt++){
	            if(!document.getElementById("list" + cnt)){continue;}
	            if(cnt == index){
	                document.getElementById("list" + cnt).style.backgroundColor = "#f0f0f0";
	            }else{
	                document.getElementById("list" + cnt).style.backgroundColor = "#fff";
	            }
	        }
	        addMvPointer = poi.point;
	    }
	    marker.addEventListener("click", openInfoWinFun);
	    return openInfoWinFun;
	}

	var options = {
	  onSearchComplete: function(results){
	    // 判断状态是否正确
	    if (local.getStatus() == BMAP_STATUS_SUCCESS){
	        var s = [];
	        s.push('<div class="msd1">');
	        s.push('<div class="msd2">');
	        s.push('<ol class="mso1">');
	        openInfoWinFuns = [];
	        for (var i = 0; i < results.getCurrentNumPois(); i ++){
	            var marker = addMarker(results.getPoi(i).point,i);
	            var openInfoWinFun = addInfoWindow(marker,results.getPoi(i),i);
	            openInfoWinFuns.push(openInfoWinFun);
	            // 默认打开第一标注的信息窗口
	            var selected = "";
/*	            if(i == 0){
	            	selected = "msl_selected";
	                openInfoWinFun();
	            }*/
	            s.push('<li id="list' + i + '" class="msl1 ' + selected + '" onclick="openInfoWinFuns[' + i + ']()">');
	            s.push('<div class="msd3" style="background:url("'+img+'")"> </div>');
	            s.push('<div class="msd4"><span class="mss1">' + results.getPoi(i).title.replace(new RegExp(results.keyword,"g"),'<b>' + results.keyword + '</b>') + '</span>');
	            s.push('<span class="mss2">' + results.getPoi(i).address + '</span>');
	            s.push('</div></li>');
	            s.push('');
	        }
	        s.push('</ol></div></div>');
	        document.getElementById("r-result").innerHTML = s.join("");
	    }
	  },map:map,panel:"r-result"
	};

	var local = new BMap.LocalSearch(map, options);	
	$("#search").click(function(){
		local.search($("#location").val());
	});
	//开启编辑 
	$('.po_edit_a').click(function(){
		openEdit();
	})
	$('.po_cancel_a').click(function(){
		marker.disableDragging();
		marker.closeInfoWindow()
	})
	$('.po_btn').click(function(){
		var po_lng = $('.po_lng').text();
		var po_lat = $('.po_lat').text();
		var po_district_code = $('.po_district_code').val();
		var po_district_name = $('.po_district_code  option:selected').text();
		alert('提交返回数据:'+po_district_name+","+po_district_code+","+po_lng+","+po_lat);
		/*$.postJSON(CONTEXT+'/mapshow/modifyShopPosition?csrf_token='+CSRF_TOKEN,
		{'id':bid, 
			'position':{'longitude':$('.po_lng').text(), 'latitude':$('.po_lat').text(), 'id':POSITION_ID}
		},
		function(o){
			if(o){
				location.href = RETURN_CONTEXT+'/backstage/merchant/detail/'+MERCHANT_ID
			}
		})
		*/
	})	
	
	$('.pop_close_a').click(function(){
		history.go(-1)
	})
	if(window.LAT){
		$('.po_lng').text(LNG);
		$('.po_lat').text(LAT);		
	}else{
		window.LAT = window.LNG = 0
	}
	if($.trim($("#location").val()) && ISEDIT == "true"){
		local.search( $("#location").val());
	}
	init_po(LNG, LAT);
	
})
   //获取该分店所处的地标
  	var getPositionLandmartJson =  function (){
	   var city_code  = 330100;
	   /*
	   $.postJSON(CONTEXT+'/mapshow/getPositionLandmart?csrf_token='+CSRF_TOKEN+'&city_code='+city_code,
			{ 'id':bid, 
				'position':{'longitude':$('.po_lng').text(), 'latitude':$('.po_lat').text(), 'id':POSITION_ID}
			},
			function(date){
				eachPoLandMarkDate(date);
			})
			
	*/
 }
    //解析返回的'地标'数据 
  var eachPoLandMarkDate  = function (date){
	   var mp = [];
	   $.each(date, function(i, obj){
	        mp.push('<button type="button" style="margin-right: 12px; padding: 5px;5px;" onclick=showLandMark(\''+obj.id+'\',\''+
	        	obj.name+'\',\''+obj.position.latitude+'\',\''+obj.position.longitude+'\',\''+obj.position.radius+'\')>'+obj.name+'</button>');
		});
	   if (mp.length>0) {
		   $("#po_insert_landmark").html(mp.join(""));
		}else{
			$("#po_insert_landmark").html("附近暂无合适的地标");
			$(".po_a_delLandMark").attr("style","display: none");
		}
   }
   //创建圆形覆盖物
   var createCircle = function (lng, lat ,r){
	      landMarkCir = new BMap.Circle(new BMap.Point(lng, lat),r);    //建立圆形覆盖物
	      landMarkCir.setStrokeStyle("dashed");                             //设置为虚线
	      landMarkCir.setFillOpacity(0.01);
	      landMarkCir.setStrokeWeight(2);
		  map.addOverlay(landMarkCir);  
   }
   //在地图上标注'地标'
   var showLandMark  =  function (landMarkId,landMarkName,lat,lng ,r){
		  var landMarkIcon = new BMap.Icon(img, new BMap.Size(23, 25), {
			  offset: new BMap.Size(10, 25),
			  imageOffset: new BMap.Size(2.5, 0)
		  });
		  if (!delLandMarkShow(landMarkId)) {
			  var landMarker  =  new BMap.Marker(new BMap.Point(lng, lat), {icon: landMarkIcon});
			  var landMarkCir = new BMap.Circle(new BMap.Point(lng, lat),r);    //建立圆形覆盖物
		      landMarkCir.setStrokeStyle("dashed");                             //设置为虚线
		      landMarkCir.setFillOpacity(0.01);
		      landMarkCir.setStrokeWeight(2);
			  map.addOverlay(landMarkCir);  
			  var markAndCirArry = [landMarker,landMarkCir,new BMap.Point(lng, lat)];
			  landMarkerArry[landMarkId] = markAndCirArry ;          //添加进map中
			  map.addOverlay(landMarker);
			  //landMarker.setAnimation(BMAP_ANIMATION_BOUNCE);		 //添加跳动的动画
			  var address  ;
			  //反地址解析
			  new BMap.Geocoder().getLocation(markAndCirArry[2], function(rs){
	    	        var addComp = rs.addressComponents;
	    	        address = addComp.province + addComp.city  + addComp.district+ addComp.street+ addComp.streetNumber;
	    	        var infoWindowHtml = [];
	    	        infoWindowHtml.push('<table cellspacing="0">');
	    	        infoWindowHtml.push('<tr><td><div style="font-weight:bold;color:#CE5521;font-size:14px">'+landMarkName+'</div></td><td></td></tr>');
	    	        infoWindowHtml.push('<tr><td>地址:</td><td style="vertical-align:top;line-height:16px">' +address+'</td></tr>');
	    	        infoWindowHtml.push('<tr><td>操作:</td><td style="vertical-align:top;line-height:16px"><button type="button" onclick="mvLandMarkPointer('+landMarkId+')">将标注移至此处</button> </td>');
	    	        infoWindowHtml.push('</table>');
	    	        var infoWindow = new BMap.InfoWindow(infoWindowHtml.join(''), {'enableMessage':false});  // 创建信息窗口对象
	    	        landMarker.addEventListener("click",function(){
	    	        	map.openInfoWindow(infoWindow,markAndCirArry[2]); //开启信息窗口
	    	        });
	    	  });  
		}
		 if (Object.keys(landMarkerArry).length>0) {
			 $(".po_a_delLandMark").attr("style","display: ");
		}else{
			$(".po_a_delLandMark").attr("style","display: none");
		}
   }
   //删除单独'地标'及其范围
  var delLandMarkShow =  function (landMarkId){
	   if ( landMarkId in landMarkerArry) {                           //判断key是否存在
		      map.closeInfoWindow();        						  //关闭提示
			  map.removeOverlay(landMarkerArry[landMarkId][0]);       //移除地标的标注
			  map.removeOverlay(landMarkerArry[landMarkId][1]);       //移除地标范围
			  delete landMarkerArry[landMarkId] ;                     //删除key
			  return true;
		}
	   return false;
   }
   //删除全部的'地标',范围,提示
   var delAllLandMarkShow  = function (){
	   for ( var landMarkerIdkey in landMarkerArry) {
		   delLandMarkShow(landMarkerIdkey);
	  }
	   $(".po_a_delLandMark").attr("style","display: none");
   }
	//查询后直接点击添加坐标
   var mvPointer  = function  (){
	   if (ISEDIT == "true") {
		   removemarker();
		   addPointer(addMvPointer);
	   }else{
		 alert("请点击编辑后，再试!");
	   }
	}
	//移动到某个地标
	var mvLandMarkPointer = function (landMarkId){
		removemarker();
		addPointer(landMarkerArry[landMarkId][2]);
	}
	//生成marker标注
	var addPointer = function (p){
		if (p.lat && p.lng) {
			addmarker(p); 
		}else if($('#location').val()){
			// 反地址解析
			new BMap.Geocoder().getPoint($('#location').val(), function(p){
				if (!p) {
					p = new BMap.Point(120.182248, 30.264124);
				} 
				addmarker(p);
			},"杭州市");
		}else{
			p = new BMap.Point(120.182248, 30.264124);
			addmarker(p);
		}
		
	}
	var addmarker = function(p){
		myIcon = new BMap.Icon(img, new BMap.Size(20, 35));
		marker = new BMap.Marker(p,{icon: myIcon});
		map.addOverlay(marker);							          //将标注添加到地图中
		//marker.setAnimation(BMAP_ANIMATION_BOUNCE);		           //添加跳动的动画
		px = map.pointToPixel(p)
		marker.enableDragging(); 
		marker.addEventListener("dragend", getPointer);
		marker.addEventListener("click",function(){
			if (marker_dragg_status) {
				this.openInfoWindow(infoWindow);                 //开启信息窗口	
			}else{
                alert("请到修改商户资料页面修改。")
				//alert("请先单击编辑后再试。");
			}
		});
		getPointer();
	}
	//移动后的重新获取经纬度
	var getPointer  = function (){
		var point = marker.getPosition();	
		addDistrictName(point) ;
		$(".po_lat").text(point.lat);
		$(".po_lng").text(point.lng);
		//删除显示的数据
		delAllLandMarkShow();
		$("#po_insert_landmark").html("正在加载附近的地标……");
		$(".po_a_delLandMark").attr("style","display: none");
		if (ADRSEMPTY == "true") {
			//反地址解析
			getMapLocation(point);
		}
		//getPositionLandmartJson();
		if (ISEDIT == "true") {
			openEdit();
		}else{
			marker.disableDragging();
		}
	}	
	//反经纬度解析
	var  getMapLocation =  function (point){
		 new BMap.Geocoder().getLocation(point, function(rs){
		       var addComp = rs.addressComponents;
		       if (addComp) {
		    	   $("#address").val(addComp.district+ addComp.street+ addComp.streetNumber);
			}
		 })
	}
    //移除marker标注
	var removemarker  = function  (){
		map.removeOverlay(marker);  
	}
	 
	//获取行政区域名称
    var addDistrictName  = function (po){
    	new BMap.Geocoder().getLocation(po, function(rs){
    	        var addComp = rs.addressComponents;
    	        district_name = addComp.district;
    	        address = addComp.province + addComp.city  + addComp.district+ addComp.street+ addComp.streetNumber;
    	        getBoundary(district_name); //显示行政区范围
    	        if (district_name ) {
    	        	setDistrict(addComp.province,addComp.city, addComp.district);
				}else{
					alert("请重新选择，无法获取当前行政区。");
					setDefaultDistrict();
				}
    	  });  
    }
    //设置默认值地区
    var setDistrict  =  function (province ,city,district){
    	var isStatus  = true; 
    	$(".po_district_code option").each(function(i, n){
		   if($(this).text() == district){
		      $(this).attr('selected', 'selected');
		      $('.po_district_code').prop("disabled", true);
		      isStatus = false;
		   }
    	});
    	//不在下拉选择框中
    	if (isStatus) {
    		getDistrict(province ,city,district);
		}
    } 
    


    // 不属于杭州的行政区，从数据库中获取其他省份的数据
var getDistrict = function(province, city, district) {
	$('.po_district_code').empty();
	$('.po_district_code').append("<option value=''>-选择行政区-</option>");
	$.ajax({
		type : "get",
		url : "/position/district",
		async: false,
		dataType :'json',
		data : {
			"provinceName": province,
			"cityName"    : city,
			"districtName" : district
		},
		success : function(data) {
			data =  data.result ;
			$.each(data, function(i, region) {
				if (district == region.districtName || province == region.provinceName) {
					var option = "<option   "
					if (district == region.districtName) {
						option += "selected='selected'";
					}
					option += "value=" + region.districtCode + ">"
							+ region.districtName + "</option>";
					$('.po_district_code').append(option);
				}
			});
			if ($('.po_district_code').find("option").length>1 && !$(".po_district_code").val()) {
				$('.po_district_code').prop("disabled", false);
			}else{
				$('.po_district_code').prop("disabled", true);
			}
		}
	});
}
    
    // 设置默认为选择
    var setDefaultDistrict  = function (){
    	$('.po_district_code').prop("disabled", false);
		$('.po_district_code').val('000000');
    }
    //设置行政区域显示
    var getBoundary = function (show_boundary_name){
    	if (!show_boundary_name) {
    		show_boundary_name = "杭州市"
		}
    	if (ply) {
    		map.removeOverlay(ply);                   //移除ply标记
    	}
        var bdary = new BMap.Boundary();
        bdary.get(show_boundary_name, function(rs){   //获取行政区域
            var count = rs.boundaries.length;         //行政区域的点有多少个
            for(var i = 0; i < count; i++){
                 ply = new BMap.Polyline(rs.boundaries[i], {strokeWeight: 2, strokeColor: "#1D341F"}); //建立多边形覆盖物
                 ply.setStrokeStyle("dashed");        //设置为虚线
                 map.addOverlay(ply);                 //添加覆盖物
            }                
        });   
    }
    //添加鼠标右击事件
	var mapMouseMenu  = function (){
		var contextMenu = new BMap.ContextMenu();
		var txtMenuItem = [{ text : '<div style="width: 100px ; height: 22px;">在此添加标注</div>',
			callback : function(p) {
				removemarker(); //移除存在的标注
				addPointer(p);    //重新生成新的标注
			}}];
		for ( var i = 0; i < txtMenuItem.length; i++) {
			contextMenu.addItem(new BMap.MenuItem(txtMenuItem[i].text,txtMenuItem[i].callback, 100));
			if (i == 1 || i == 3) { contextMenu.addSeparator(); }
		}
		map.addContextMenu(contextMenu);
	}
	//开启编辑
	var openEdit = function (){
		mapMouseMenu();          //添加鼠标右键事件
		marker_dragg_status  = true ;
		marker.enableDragging(); //开启拖拽
		marker.openInfoWindow(infoWindow)
		$('.po_button_div').attr('style','text-align: center; display: ');
	}
	function isFloat(str){
	    if(isInt(str)){
	        return true
	    }
		return /^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$/.test(str)
	}
	function isInt(str) {
		return /^(-|\+)?\d+$/.test(str);
	}