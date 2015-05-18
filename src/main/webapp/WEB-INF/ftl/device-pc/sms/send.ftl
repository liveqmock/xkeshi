<#import "/macro.ftl" as m>

<@m.page_header css="photo_list|seller_info|add_msg" js="add_msg" selected='marketing' subselected='list' title='添加短信任务'/>
<#if success??>
<script>
	var IMPORT_STATE = '${success}',
		ERR_MESSAGE = '${errorMsg!}',
		IMPORT_RESULT = '${result}'
</script>
</#if>
<script>
	var IS_COUPON = <#if couponInfoEid != null>1<#else>0</#if>
</script>
<div class="rwrap">
	<div class="r_title"><a href="javascript:history.back(-1);" class="back_a"></a>添加新任务</div>
	<form action="/sms/send" method="POST" class="sms_form">
		<input type="hidden" name="submissionToken" value="${submissionToken}"/>
		<input type="hidden" name="couponInfoEid" value="${couponInfoEid!}"/>
		<input type="hidden" name="type" value="${couponInfoType!}"/>
		<div class="tb_wrap">

			<div class="tb_title">选择发送对象</div>
		
			<table class="tb_main">
				<tr>
					<td class="ltd" colspan='2'>
					
					<span class="td_key"><input class="cust_type" type="radio" id="cust_type1" name="sendType" checked="checked" value="all"><label for="cust_type1" class="cust_type_lb">已有客户</label></span><span class="td_val">（共<em class="cust_num">${memberCount}</em>个客户 
					<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN"><a href="${base}/member/merchant/list" class="cust_list_a">查看客户列表</a></@shiro.hasAnyRoles>
					<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN"><a href="${base}/member/shop/list" class="cust_list_a">查看客户列表</a></@shiro.hasAnyRoles>
					<a href="javascript:" class="import_cust_list">导入客户数据</a>）</span>
					</td>
				</tr>
				<tr class="tr_bg">
					<td colspan='2'><span class="td_key"><input class="cust_type" type="radio" id="cust_type2" name="sendType" value="custom"><label for="cust_type2" class="cust_type_lb">添加手机号码</label></span><span class="td_val">（每一行输入一个手机号码）<div class="add_num_wrap num_wrap"><textarea class="add_num_text text_wrap" name="mobiles"></textarea><p class="add_num_hint">当前已添加<em class="add_num_sum">0</em>个手机号码</p></div></span></td>
				</tr>
			</table>
			<div class="tb_title tb_title2">发送短信内容</div>
			<table class="tb_main">
				<tr class="tr_bg">
					<td  colspan='2'><div class="msg_cont_wrap num_wrap"><textarea class="msg_cont_text text_wrap" name="template" placeholder="$客户姓名$，$客户生日$是你的生日，在这里祝你生日快乐"></textarea><div class="msg_cont_opt"><span class="msg_input_help">插入：<span class="msg_basic_a"><a href="javascript:" class="msg_cont_a msg_name_a">客户姓名</a><a href="javascript:" class="msg_cont_a msg_birth_a">客户生日</a></span><span class="msg_coupon_a"><a href="javascript:" class="msg_cont_a msg_code_a">优惠码</a><span class="msg_split">/</span><a href="javascript:" class="msg_cont_a msg_link_a">优惠获取链接</a><span class="msg_code_hint">("优惠码"和"优惠获取链接"只能二选一)</span></span></span><p class="msg_cont_hint">不能超过140字，当前<em class="msg_cont_now">0字</em></p></div></div></td>
				</tr>
				<tr>
					<td colspan='2'><span class="td_key td_title2">发送短信内容预览</span><span class="td_val sms_preview"></td>
				</tr>
				<tr class="tr_bg"><td class="td2 ltd" colspan='2'>
				<span class="td_key td_title2">发送时间</span><span class="td_val"><span class="msg_time"><input type="radio" class="msg_time_rd" id="msg_time_rd1" name="sendTs" value="now" checked="checked"><label for="msg_time_rd1">保存后立即发送</label></span><span class="msg_time"><input type="radio" class="msg_time_rd" id="msg_time_rd2" name="sendTs" value="later" disabled="true"><label for="msg_time_rd2">指定发送时间</label><input type="text" disabled="true" class="tcal msg_time_input" placeholder="日期时间设置"></span>
				
				</td></tr>
				<tr>
					<td colspan='2'><span class="td_key" style="margin-left:18px;">本次发送预计消费：<em class="score_now">x</em>元</span><span class="td_val" style="margin-left:45px;">当前账户可用余额：<em class="score_more">${(balance?string("0.00"))!}</em>元<#-- <a href="${base}/balance/transaction/list" class="score_charge_a">积分充值</a>--></td>
				</tr>
			</table>			
		</div>
		<div class="btn_wrap btn_warp_tap"><button class="green_btn_l add_photo_btn">保存</button><span class="btn_split">或</span><a href="${base}/sms/task/list" class="cancel_a">取消</a></div>
	</form>
	<div class="pb pop_error">
		<div class="pb_title">错误</div>
		<div class="pb_main">
			上传的客户数据文件格式错误！
		</div>
		<button class="pb_btn pb_btn_s">关闭</button><span class="pb_btn_split">
	</div>
	<div class="pb pop_over">
		<div class="pb_title">客户数据导入结束</div>
		<div class="pb_main">
			<p class="pb_main_item">导入成功：<em class="import_success_num"></em>个客户资料<a href="${base}/member/list" class="pb_main_a" target="_blank">查看</a></p>
			<p class="pb_main_item">导入失败：<em class="import_fail_num"></em>个客户资料<a href="javascript:" class="pb_main_a error_detail_a">查看</a></p>
		</div>
		<button class="pb_btn pb_btn_s">关闭</button><span class="pb_btn_split">
	</div>
	<div class="pb pop_detail">
		<div class="pb_title">客户数据导入失败</div>
		<div class="pb_main"></div>
		<button class="pb_btn pb_btn_s">关闭</button><span class="pb_btn_split">
	</div>
	<div class="pop_add_many">
		<form action="/member/import" method="post" enctype="multipart/form-data">
			<p class="pop_form_title">批量导入会员<a href="javascript:void(0)" class="pop_close_a">×</a></p>
			<div class="file_block">
				<input type="file" class="file_input" name="memberlist">
			</div>
			<div class="pop_form_btn_wrap">
				<button class="pop_form_btn pop_import_btn"></button><span class="btn_split">- 或 -</span><a href="javascript:void(0)" class="btn_cancel">取消</a>
			</div>
		</form>
	</div>
	<div class="black_cover"></div>
	<!--<form action="/member/import" method="post" enctype="multipart/form-data">
		<input type="file" class="file_ip" name="memberlist">
		<button class="pb_btn pb_btn_s">确定</button>
	</form>-->
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
</div>
<@m.page_footer />