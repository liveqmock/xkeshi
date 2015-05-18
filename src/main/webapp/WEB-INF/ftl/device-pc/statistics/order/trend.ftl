<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected = "analyze" css="admin_frame|chart_page" js ="My97DatePicker/WdatePicker|highcharts|analyse|order_analyse" title='点单分析'/>
<script>
	DATE1="${startDate}"
	DATE2="${endDate}"
	DAY_BLOCK="${dayBlock}"
	FROM_TYPE="${fromType}"
</script>
<div class="rwrap">
		<div class="r_title"><span class="fl">点单分析</span><div class="search_wrap"><a class="report_a" href="${base}/statistics/order/trend/export"></a></div></div>
		<div class="top_sel_wrap top_zdex">
			<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
			<div class="selex_wrap selex_wrap1">
				<div class="selex_head">集团</div>
				<div class="selex_main">
					<div class="selex_item selex_item1" data-id="">集团</div>
					<#list applicableShops as shop>
						<div class="selex_item selex_item1" data-id="${(shop.id)!}">${(shop.name)!''}</div>
					</#list>
				</div>
			</div>
			</@shiro.hasAnyRoles>
			<div class="selex_wrap selex_wrap2">
				<div class="selex_head">点单量</div>
				<div class="selex_main">
					<#list orderTrendTypes as type>
						<div class="selex_item selex_item2" data-type="${type}">${type.description}</div>
					</#list>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="day_nav">
			<div class="day_block today day_block_on " data-key="today">
				<div class="day_title">今天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">
			</div>
			<div class="day_block yesterday" data-key="yesterday">
				<div class="day_title">昨天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
			<div class="day_block seven" data-key="seven">
				<div class="day_title">近7天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
			<div class="day_block thirty" data-key="thirty">
				<div class="day_title">近30天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
			<div class="day_block set_day" data-key="set_day">
				<div class="set_day1"></div>
				<div class="set_day2">自定义时间段</div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
		</div>
		<div class="chart_wrap">
			<div class="chart_nav">
				<div class="chart_nav_line"></div>
				<a class="chart_nav_a chart_nav_now" href="javascript:">点单分析</a>
				<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">
				<a class="chart_nav_a chart_nav_shop" href="${base}/statistics/order/analyze/shop_ratio">商户分析</a>
				</@shiro.hasAnyRoles>
			</div>
			<div class="chart_main" style="border-bottom:1px solid #f8f8f8;margin-bottom:16px;">
				<div class="chart" style="margin-top:42px;"></div>
				<div class="chart_r" style="height:280px;padding-top:28px;">
					<div class="chart_data chart_data1">
						<p class="chart_key"></p>
						<p class="chart_val"></p>
					</div>
					<div class="chart_data chart_data2">
						<p class="chart_key"></p>
						<p class="chart_val"></p>
					</div>
					<div class="chart_data chart_change">
						<p class="chart_key"></p>
						<p class="chart_val"></p>
					</div>
				</div>
			</div>
			<table class="tb_main tb_list"></table>
		</div>
	</div>
	<div class="pb pop_check">
		<div class="pb_title">自定义查询</div>
		<div class="pb_main">
			<div class="pb_item">
				<label class="pb_lb">选择想查询的开始日期</label><input class="pb_input pb_date1" type="text" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd', onpicked:function(){date1_picked()}})">
			</div>
			<div class="pb_item">
				<label class="pb_lb">选择想查询的结束日期</label><input class="pb_input pb_date2" type="text" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})">
			</div>
			<div class="pb_add_time">
				<input type="checkbox" class="add_time_cb" id="add_time_cb" ><label for="add_time_cb" class="lb_cb add_time_lb">我还想选个时间段来进行对比</label>
			</div>
			<div class="pb_item pb_item_hide">
				<label class="pb_lb">选择想对比的开始日期</label><input class="pb_input pb_date3" type="text" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})">
			</div>
		</div>
		<div class="pb_btn_wrap">
			<button class="pb_btn analyse_btn">开始分析</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</div>
	</div>
<!--	
	<div class="pb pop_check2">
		<div class="pb_title">自定义查询<a href="javascript:" class="pb_close"></a></div>
		<div class="pb_main">
			<div class="pb_item">
				<label class="pb_lb">输入想查询的天数</label><input class="pb_input pb_day" type="text">
			</div>
			<div class="pb_item">
				<label class="pb_lb">选择想查询的开始日期</label><input class="pb_input pb_date1" type="text" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})">
			</div>
			<div class="pb_item">
				<label class="pb_lb">选择想对比的开始日期</label><input class="pb_input pb_date2" type="text" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})">
			</div>
		</div>
		<div class="pb_btn_wrap">
			<button class="pb_btn analyse_btn2">开始分析</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</div>
	</div>
-->
<script>
	$(function(){
		$('.report_a').click(function(){
			if(confirm("是否要导出数据？")){
				return true;
			}
			return false;
		});
	});

</script>
<@m.page_footer />




