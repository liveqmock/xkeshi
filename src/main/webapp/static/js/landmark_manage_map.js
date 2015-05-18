var map,marker,TYPE;
var district_name  ;
var marker_dragg_status  = false;
var bid = window.BRANCH_ID || 0 ;
var addMvPointer  ;
var ply ;
var shopArry  = [];
var landMarkCir  ;
var infoWindow = new BMap.InfoWindow('当前坐标位置',{'enableMessage':false});
$(function(){
	// 百度地图API功能
	map = new BMap.Map("allmap");
	map.enableScrollWheelZoom();
	map.addControl(new BMap.MapTypeControl({anchor: BMAP_ANCHOR_TOP_LEFT}));    //左上角，默认地图控件

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
	            if(i == 0){
	            	selected = "msl_selected";
	                openInfoWinFun();
	            }
	            s.push('<li id="list' + i + '" class="msl1 ' + selected + '" onclick="openInfoWinFuns[' + i + ']()">');
	            s.push('<div class="msd3" style="background:url(http://api.map.baidu.com/images/markers.png) 0 ' + ( 0 - i*25 ) + 'px no-repeat;"> </div>');
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
		var location = $("#location").val();
		local.search(location);
	});
	
	
	//开启编辑 
	$('.ldmk_edit_a').click(function(){
		openEdit();
	})
	$('.ldmk_cancel_a').click(function(){
		marker.disableDragging();
		marker.closeInfoWindow()
	})
 	$('.post_btn').click(function(){
			if(!$.trim($('.ldmk_name').val())){
				alert('地标名称必填')
				return false
			}
			if(!$.trim($('.ldmk_rad').val())){
				alert('地标范围必填')
				return false
			}
			if(isNaN($('.ldmk_rad').val())||!isFloat($.trim($('.ldmk_rad').val())) || parseFloat($.trim($('.ldmk_rad').val()))<=0){
				alert('地标范围必须为大于0的数字')
				return false
			}
			$(this).attr("disabled","disabled");
			$.postJSON('/position/' + TYPE + '/landmark',
			{
				'id' : $('.ldmk_id').val(),
				'position' : {
					'id' : $('.po_id').val(),
					'longitude' : $('.po_lng').text(),
					'latitude'  : $('.po_lat').text()
				},
				'radius'    : $('.ldmk_rad').val(),
				'name' : $('.ldmk_name').val(),
				'cityCode' : CITY_CODE,
				'district' : $('.ldmk_district_code').val() 
			}, function(o) {
				if (o.status = 'success') {
					$(this).attr("disabled","");
					window.opener.location.reload();
					window.close()
				}
			})
		})	 
	
	$('.ldmk_del_a').click(function(){
		if(!confirm('确定删除该地标？')){
			return false
		}
		$.postJSON(CONTEXT+'/mapshow/delLandmark?csrf_token='+CSRF_TOKEN, {'id':$('.ldmk_name').data('id')}, function(o){
			if(o){
				location.href = RETURN_CONTEXT+'/backstage/maintain/landmark'
			}
		})
	})

	$('.pop_close_a').click(function(){
		history.go(-1)
	})
	
	$('.ldmk_rad').blur(function(){
		 if (!isNaN($('.ldmk_rad').val())) {
			 map.removeOverlay(landMarkCir);
			 createCircle($('.po_lng').text(), $('.po_lat').text(),$('.ldmk_rad').val());
			 //getPositionShopJson();
		}
	})
	if (!marker_dragg_status) {
		$(".ldmk_rad").attr("disabled",true);
		$(".ldmk_name").attr("disabled",true);
	}
		init_ldmk(LNG, LAT);
});
    // 初始化js地标信息
	var init_ldmk = function(lng, lat){
		var po = new BMap.Point(lng, lat);    
		addPointer(po);
		if(lng && lat){
			map.centerAndZoom(po,13);
		}else{
			map.centerAndZoom("杭州",13);
		}
	}
   // 获取该地标所处的商店
  	var getPositionShopJson =  function (){
	   var city_code  = 330100;
	   if (TYPE != 'add') {
		   $.postJSON(CONTEXT+'/mapshow/getPositionShop?csrf_token='+CSRF_TOKEN,
				   { 'id':ID, 
			        'cityCode':CITY_CODE,
			       'position':{'longitude':$('.ldmk_lng').text(), 'latitude':$('.po_lat').text(), 'radius':$('.po_rad').val(),'id':ID}
				   },
				   function(date){
					   eachPoShopDate(date);
				   })
	}
 }
    //解析返回的'商店'数据 
  var eachPoShopDate  = function (date){
	   var mp = [];
	   $.each(date, function(i, obj){
	        mp.push('<button type="button" style="margin-right: 12px; padding: 5px;5px;" onclick=showShop(\''+obj.id+'\',\''+
	        	obj.nickName+'\',\''+obj.position.latitude+'\',\''+obj.position.longitude+'\',\''+obj.position.radius+'\')>'+obj.nickName+'</button>');
		});
	   if (mp.length>0) {
		   $("#ldmk_insert_shop").html(mp.join(""));
		}else{
		   $("#ldmk_insert_shop").html("附近暂无合适的商铺");
		   $(".ldmk_a_delShop").attr("style","display: none");
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
   //在地图上标注'商店'
   var showShop  =  function (shopId,landMarkName,lat,lng ,r){
		  var landMarkIcon = new BMap.Icon(BASE+"/css/img/marker_blue_sprite.png", new BMap.Size(23, 25), {
			  offset: new BMap.Size(10, 25),
			  imageOffset: new BMap.Size(2.5, 0)
		  });
		  if (!delShopShow(shopId)) {
			  var landMarker  =  new BMap.Marker(new BMap.Point(lng, lat), {icon: landMarkIcon});
			  var markAndCirArry = [landMarker,new BMap.Point(lng, lat)];
			  shopArry[shopId] = markAndCirArry ;          //添加进map中
			  map.addOverlay(landMarker);
			 // landMarker.setAnimation(BMAP_ANIMATION_BOUNCE);		 //添加跳动的动画
			  var address  ;
			  //反地址解析
			  new BMap.Geocoder().getLocation(markAndCirArry[1], function(rs){
	    	        var addComp = rs.addressComponents;
	    	        address = addComp.province + addComp.city  + addComp.district+ addComp.street+ addComp.streetNumber;
	    	        var infoWindowHtml = [];
	    	        infoWindowHtml.push('<table cellspacing="0">');
	    	        infoWindowHtml.push('<tr><td><div style="font-weight:bold;color:#CE5521;font-size:14px">'+landMarkName+'</div></td><td></td></tr>');
	    	        infoWindowHtml.push('<tr><td>地址:</td><td style="vertical-align:top;line-height:16px">' +address+'</td></tr>');
	    	        infoWindowHtml.push('<tr><td>操作:</td><td style="vertical-align:top;line-height:16px"><button type="button" onclick="mvShopPointer('+shopId+')">将标注移至此处</button> </td>');
	    	        infoWindowHtml.push('</table>');
	    	        var infoWindow = new BMap.InfoWindow(infoWindowHtml.join(''), {'enableMessage':false});  // 创建信息窗口对象
	    	        landMarker.addEventListener("click",function(){
	    	        	map.openInfoWindow(infoWindow,markAndCirArry[1]); //开启信息窗口
	    	        });
	    	  });  
		}
		 if (Object.keys(shopArry).length>0) {
			 $(".ldmk_a_delShop").attr("style","display: ");
		}else{
			$(".ldmk_a_delShop").attr("style","display: none");
		}
   }
   //删除单独'shop'
  var delShopShow =  function (shopId){
	   if ( shopId in shopArry) {                           //判断key是否存在
		      map.closeInfoWindow();        				 //关闭提示
			  map.removeOverlay(shopArry[shopId][0]);       //移除地标的标注
			  delete shopArry[shopId] ;                     //删除key
			  return true;
		}
	   return false;
   }
   //删除全部的'地标',范围,提示
   var delAllShopShow  = function (){
	   for ( var landMarkerIdkey in shopArry) {
		   delShopShow(landMarkerIdkey);
	  }
	   $(".ldmk_a_delShop").attr("style","display: none");
   }
	//查询后直接点击添加坐标
   var mvPointer  = function  (){
	   if (marker_dragg_status) {
		   removemarker();
		   addPointer(addMvPointer);
	   }else{
		   alert("请点击编辑后再试！");
	   }
	  }
	//移动到某个地标
	var mvShopPointer = function (shopId){
		removemarker();
		addPointer(shopArry[shopId][1]);
	}
	//生成marker标注
	var addPointer = function (p){
		if (p.lat && p.lng) {
			addmarker(p); 
		}else{
			p = new BMap.Point(120.182248, 30.264124);
			addmarker(p);
			openEdit();
		}
	}
	var addmarker = function(p){
		var landMarkIcon = new BMap.Icon(BASE+"/css/img/marker_blue_sprite.png", new BMap.Size(23, 25), {
			  offset: new BMap.Size(10, 25),
			  imageOffset: new BMap.Size(2.5, 0)
		 });
		marker = new BMap.Marker(p, {icon: landMarkIcon});
		map.addOverlay(marker);							          //将标注添加到地图中
		//marker.setAnimation(BMAP_ANIMATION_BOUNCE);		           //添加跳动的动画
		px = map.pointToPixel(p)
		marker.addEventListener("dragend", getPointer);
		marker.addEventListener("click",function(){
			if (marker_dragg_status) {
				this.openInfoWindow(infoWindow);                 //开启信息窗口	
			}else{
				alert("请先单击编辑后再试。");
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
		map.removeOverlay(landMarkCir);
		delAllShopShow(); //移除全部的shop标注显示
		//getPositionShopJson();
		createCircle(point.lng, point.lat ,$('.ldmk_rad').val());
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
    	        getBoundary(district_name); //显示行政区范围
    	        if (district_name ) {
    	        	setDistrict(district_name);
				}else{
					setDefaultDistrict();
				}
    	  });  
    }
    //设置默认值地区
    var setDistrict  =  function (district_name){
    	var isStatus  = true; 
    	$(".ldmk_district_code option").each(function(i, n){
		   if($(this).text() == district_name){
		      $(this).attr('selected', 'selected');
		      $('.ldmk_district_code').prop("disabled", true);
		      isStatus = false;
		   }
    	});
    	//不在下拉选择框中
    	if (isStatus) {
    		setDefaultDistrict();
		}
    } 
    //设置默认为选择
    var setDefaultDistrict  = function (){
    	$('.ldmk_district_code').prop("disabled", false);
		$('.ldmk_district_code').val('000000');
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
		$(".ldmk_del_a").attr('style','visibility:');
		$(".ldmk_name").attr('disabled',false);
		$(".ldmk_rad").attr('disabled',false);
		$('.ldmk_button_div').attr('style','text-align: center; display: ');
	}
	var isFloat = function (str){
	    if(isInt(str)){
	        return true;
	    }
		return /^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$/.test(str);
	}
	var isInt  = function (str) {
		return /^(-|\+)?\d+$/.test(str);
	}