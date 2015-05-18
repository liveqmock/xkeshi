<#import "/macro.ftl" as m>
<@m.page_header selected='activity' css="seller_info|coupon_detail|act_detail" js="activity_list|activity_detail" title="活动详情"/>
<script>
var CONT_DETAIL = '${(activity.description)!}' 
</script>
<div class="rwrap">
	<div class="r_title"><a href="${base}/activity/list" class="back_a"></a>独立活动详情
		<div class="search_wrap">
			<a class="edit_a" href="${base}/activity/edit/${(activity.eid)!}"></a><a class="del_a" href="javascript:"></a>
		</div>
	</div>
	<table class="tb_out">
		<tr>
			<td class="main_data">
				<div class="tb_wrap tb_basic">
					<div class="tb_title">基本信息</div>
					<table class="tb_main">
						<tr>
							<td><span class="td_key">创建时间</span><span class="td_val">${(activity.createDate?string('yyyy/MM/dd HH:mm:ss'))!}</span></td>
							<td><span class="td_key">活动编号</span><span class="td_val">${(activity.eid)!}</span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">名称</span><span class="td_val">${(activity.name)!}</span></td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">简介</span><span class="td_val">${(activity.intro)!}</span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">相关图片</span><span class="td_val">列表缩略图<a class="l_a img_a" href="javascript:" data-src="${image_base}${activity.thumb}">点击查看</a>详情页大图<a class="l_a img_a" href="javascript:" data-src="${image_base}/${activity.pic}">点击查看</a></span></td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">描述详情</span><span class="td_val"><a class="l_a cont_detail_a" style="margin:0;" href="javascript:">[查看详情]</a></span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">备注</span><span class="td_val">${(activity.remark)!}</span></td>
						</tr>
					</table>
				</div>
			</td>
			<td class="side_data">
				<div class="sd_main">
					<p class="sd_title">有效期</p>
					<div class="sd_cont">
						<p class="sd_p"><span><#if (activity.startDate)??>${activity.startDate?string('yyyy/MM/dd')}</#if>-<#if (activity.endDate)??>${activity.endDate?string('yyyy/MM/dd')}</#if></span><a href="${base}/activity/edit/${(activity.eid)!}/">修改</a></p>
					</div>
					<p class="sd_title">显示状态</p>
					<div class="sd_cont">
						<p class="sd_p">
							<span><#if (activity.published)!>已发布<#else>未发布</#if></span>
							<a href="javascript:" class="status_edit">修改</a>
						</p>
					</div>
					<p class="sd_title">活动推送</p>
					<div class="sd_cont sd_many">
						<p class="sd_p"><span>手机短信</span><a href="${base}/sms/send?activityId=${(activity.eid)!}">推送</a></p>
						<#--
						<p class="sd_p"><span>微信：未推送</span><a href="javascript:">推送</a></p>
						<p class="sd_p"><span>站内短信：未推送</span><a href="javascript:">推送</a></p>
						-->
					</div>
					<#--
					<div class="side_link">
						<p><a href="javascript:" class="phone_a">手机版预览</a></p>
						<p><a href="javascript:" class="pc_a">网络版预览</a></p>
					</div>
					-->
				</div>
			</td>
		</tr>
	</table>
	<div class="tb_wrap tb_other">
		<div class="tb_title">关联电子券<a href="javascript:" class="b_a_r add_coupon_a">添加关联电子券</a></div>
		<table class="tb_main">
			<tr class="th">
				<td>电子券编号</td>
				<td class="name">电子券名称</td>
				<td class="state">状态</td>
				<td>有效期</td>
				<td>总数/已使用</td>
				<td>操作</td>
			</tr>
			<#if (activity.couponInfos)?? && activity.couponInfos?size &gt; 0>
				<#list activity.couponInfos as couponInfo>
				<tr <#if couponInfo_index%2==0>class="tr_bg"</#if>>
					<td>${(couponInfo.eid)!}</td>
					<td class="name">${(couponInfo.name)!}</td>
					<td class="state"><#if (couponInfo.published)!><span class="state1">已发布</span><#else><span class="state2">未发布</span></#if></td>
					<td><#if (couponInfo.endDate)??>${couponInfo.endDate?string('yyyy/MM/dd')}</#if></td>
					<td><#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount}</#if>/${couponInfo.received}</td>
					<td><a href="javascript:" class="cancel_bind" data-couponid="${(couponInfo.eid)!}">取消关联</a></td>
				</tr>
				</#list>
			</#if>
		</table>
	</div>
	<div class="pb pop_del">
		<form action="${base}/activity/delete/${(activity.eid)!}" method="post">
			<input type="hidden" name="_method" value="delete"/> 
			<div class="pb_title">删除</div>
			<div class="pb_main">确认删除活动?</div>
			<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_cancel_bind">
		<form action="${base}/activity/${activity.eid}/unbind">
			<input type="hidden" name="coupon"/>
			<div class="pb_title">确认取消电子券关联</div>
			<div class="pb_main"><b>电子券名称</b><p class="pb_coupon_name"></p></div>
			<button type="submit" class="pb_btn pb_btn_s">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_status_edit">
		<form action="${base}/activity/${(activity.eid)!}/modify_status" method="post">
			<input type="hidden" name="_method" value="put"/>
			<div class="pb_title">显示状态修改</div>
			<div class="pb_main">
				<div class="pb_item">
					<input type="radio" id="state_rd1" class="pfrd" name="published" value="false"/><label for="state_rd1">未发布</label>
					<input type="radio" id="state_rd2" class="pfrd" name="published" value="true"/><label for="state_rd2">发布</label>
				</div>
				<div class="pb_item">
					<input type="checkbox" id="ck1" class="pfrd" name="syncCoupon" value="true"/><label for="ck1">将相关电子券设置为相同显示状态</label>
				</div>
			</div>
			<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_bind_coupon">
		<form action="${base}/activity/${activity.eid}/bind" method="POST" class="pop_coupon_form">
			<div class="pb_title">添加关联电子券</div>
			<div class="pb_main">
				<div class="pb_item">
					<input type="radio" id="relate_rd1" class="pfrd" name="bind_rd" <#if availableCouponInfos?size lte 0>disabled</#if>/><label for="relate_rd1">与已有电子券关联</label>
					<select name="coupon" class="pfsel" <#if availableCouponInfos?size lte 0>disabled</#if>>
						<#list availableCouponInfos as couponInfo>
							<option value="${couponInfo.eid}">${couponInfo.name}</value>
						</#list>
					</select>
				</div>
				<div class="pb_item">
					<input type="radio" id="relate_rd2" class="pfrd" name="bind_rd"/><label class="coupon_add_label" data-url="/coupon/add?activity=${activity.eid}" for="relate_rd2">创建新的关联电子券</label>
				</div>
			</div>
			<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
</div>
<@m.page_footer />