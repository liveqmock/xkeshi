<#import "/macro.ftl" as m >
<@m.page_header selected='order' subselected = "item_category" css="admin_frame|chart_page|merchant_analyse" js ="My97DatePicker/WdatePicker|highcharts|analyse|cat_analyse" title='品类分析'/>
<script>
	DATE1="${startDate}"
	DATE2="${endDate}"
	DAY_BLOCK="${dayBlock}"
	FROM_TYPE="${fromType}"
</script>
	<div class="rwrap">
		<div class="r_title"><span class="fl">品类分析</span><div class="search_wrap"></div></div>
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
				<div class="selex_head">商品销量</div>
				<div class="selex_main">
					<#list itemCategoryRatioTypes as type>
						<div class="selex_item selex_item2" data-type="${type}">${type.description}</div>
					</#list>
				</div>
			</div>
			<div class="clear"></div>
		</div>
		<div class="day_nav">
			<div class="day_block today">
				<div class="day_title">今天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">
			</div>
			<div class="day_block yesterday">
				<div class="day_title">昨天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
			<div class="day_block seven">
				<div class="day_title">近7天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
			<div class="day_block thirty">
				<div class="day_title">近30天</div>
				<div class="day_val"></div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
			<div class="day_block set_day">
				<div class="set_day1"></div>
				<div class="set_day2">自定义时间段</div>
				<div class="day_shadow"></div>
				<img class="day_block_img" src="${static_base}/css/img/admin_frame/day_block_on.png">				
			</div>
		</div>
		<div class="chart_wrap">
			<div class="chart_nav">
				<div class="chart_nav_line"></div>
				<a class="chart_nav_a chart_nav_now" href="javascript:">热销品类</a>
			</div>
			<div class="chart_main">
				<div class="chart_none"></div>
				<div class="chart_l">
					<div class="chart_title"></div>
					<div class="chart"></div>
				</div>
				<div class="chart_r">
					<table class="chart_rtb"></table>
				</div>
			</div>
		</div>
		<table class="tb_main tb_list">
		</table>
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
		</div>
		<div class="pb_btn_wrap">
			<button class="pb_btn analyse_btn">开始分析</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</div>
	</div>
<@m.page_footer />




