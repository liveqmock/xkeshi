<#import "/macro.ftl" as m>
<@m.page_header selected='coupon' subselected="couponInfoPackageList" css="seller_info|coupon_detail|ticket_detail" js="ticket_detail" title="套票详情"/>
<script>
var CONT_DETAIL = '${(couponInfo.description)!}' 
</script>
<div class="rwrap">
	<div class="r_title"><a href="${base}/coupon/couponInfoPackage/list" class="back_a"></a>套票详情（独立）
		<div class="search_wrap">
			<a class="edit_a" href="${base}/coupon/edit/package/${(couponInfo.eid)!}"></a><a class="pop_a del_a" data-pop="pop_del" href="javascript:"></a>
		</div>
	</div>
	<table class="tb_out">
		<tr>
			<td class="main_data">
				<div class="tb_wrap tb_basic">
					<div class="tb_title">基本信息</div>
					<table class="tb_main">
						<tr>
							<td><span class="td_key">创建时间</span><span class="td_val">${(couponInfo.createDate?string('yyyy/MM/dd HH:mm:ss'))!}</span></td>
							<td><span class="td_key">套票编号</span><span class="td_val">${(couponInfo.eid)!}</span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">名称</span><span class="td_val">${(couponInfo.name)!}</span></td>
						</tr>
						<tr class="tr">
							<td colspan='2'><span class="td_key">简介</span><span class="td_val">${(couponInfo.intro)!}</span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">相关图片</span><span class="td_val">列表缩略图<a class="l_a img_a" href="javascript:" data-src="${image_base}${couponInfo.thumb}">点击查看</a>详情页大图<a class="l_a img_a" data-src="${image_base}/${couponInfo.pic}" href="javascript:">点击查看</a></span></td>
						</tr>
						<tr class="tr">
							<td colspan='2'><span class="td_key">服务保障</span><span class="td_val">
									<#if (couponInfo.supportNormalRefund)!false>支持随时退</#if>&nbsp;&nbsp;<#if (couponInfo.supportExpiredRefund)!false>支持过期退</#if>
								</span>
							</td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">描述详情</span><span class="td_val"><a class="l_a cont_detail_a" style="margin-left:0;" href="javascript:">[查看详情]</a></span></td>
						</tr>
						<tr class="tr">
							<td colspan='2'><span class="td_key">使用说明</span><span class="td_val">
							<#if couponInfo.instructions?? && couponInfo.instructions!=''>
							${(couponInfo.instructions)!}
							<#else>
							请持此电子券至商户收银台爱客仕核销系统扫一扫，即享优惠。
							</#if>
							</span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">备注</span><span class="td_val">${(couponInfo.remark)!}</span></td>
						</tr>
						<tr class="tr">
							<td colspan='2'><span class="td_key">售价</span><span class="td_val td_price"><em>￥</em>${(couponInfo.price)!'0.00'}</span></td>
						</tr>
					</table>
				</div>
			</td>
			<td class="side_data">
					<div class="sd_main sd_mains sd_marg23">
						<p class="sd_title sd_titles ">显示状态</p>
						<div class="sd_cont">
						<p class="sd_p">
							<#if !couponInfo.published><span class="state2 status1">未发布</span></#if>
							<#if couponInfo.published && couponInfo.visible><span class="state1 status1">已发布(显示)</span></#if>
							<#if couponInfo.published && !couponInfo.visible><span class="state1 status1">已发布(不显示)</span></#if>
							<a href="javascript:" class="status_edit">修改</a>
						</p>
						</div>
					</div>
				<div class="sd_main sd_marg13">
					<p class="sd_title">总数/已发放</p>
					<div class="sd_cont">
						<p class="sd_p"><span><#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount}</#if>/${couponInfo.received}</span></p>
					</div>
					<p class="sd_title">有效期</p>
					<div class="sd_cont">
						<p class="sd_p"><span><#if (couponInfo.startDate)??>${couponInfo.startDate?string('yyyy/MM/dd')}</#if>-<#if (couponInfo.endDate)??>${couponInfo.endDate?string('yyyy/MM/dd')}</#if></span><a href="${base}/coupon/edit/package/${(couponInfo.eid)!}">修改</a></p>
					</div>
					<#--<p class="sd_title">显示状态</p>
					<div class="sd_cont">
						<p class="sd_p">
							<span><#if couponInfo.published && couponInfo.visible>发布(显示)<#elseif couponInfo.published && !couponInfo.visible>发布(不显示)<#elseif !couponInfo.published>未发布</#if></span>
							<a href="javascript:" class="pop_a" data-pop="pop_state">修改</a>
						</p>
					</div>-->
					<p class="sd_title">关联活动</p>
					<div class="sd_cont">
						<p class="sd_p"><span><#if activity??>${activity.name}<#else>无关联活动</#if></span><a href="javascript:" class="pop_a" data-pop="pop_contact">设置关联</a></p>
					</div>
					<p class="sd_title">电子券推送</p>
					<div class="sd_cont">
						<p class="sd_p"><span>手机短信</span><a href="${base}/sms/send?couponInfoEid=${(couponInfo.eid)!}&type=P">推送</a></p>
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
		<div class="tb_title">获取条件</div>
		<table class="tb_main tb_tiaojian">
			<tr>
				<td><span class="td_key">获取起始时间</span><span class="td_val"><#if (couponInfo.saleStartDate)?? >${(couponInfo.saleStartDate?string('yyyy/MM/dd HH:mm:ss'))!}<#else>不限</#if></span></td>
				<td><span class="td_key">获取截止时间</span><span class="td_val"><#if (couponInfo.saleEndDate)?? >${(couponInfo.saleEndDate?string('yyyy/MM/dd HH:mm:ss'))!}<#else>不限</#if></span></td>
			</tr>
			<tr class="tr_bg">
				<td><span class="td_key">限定获取用户</span><span class="td_val">不限</span></td>
				<td></td>
			</tr>
			<#if couponInfo.limitPayTime?? && couponInfo.allowContinueSale??>
			<tr>
				<td colspan='2'><span class="td_key">支付超时</span><span class="td_val">拍下 ${couponInfo.limitPayTime}分钟 未支付，系统自动关闭交易，关闭交易后，电子券
				<#if couponInfo.allowContinueSale== true >
						可继续销售
				<#else>
						不可继续销售
				</#if>
				</span></td>
			</tr>
			</#if>
		</table>
	</div>
	<div class="tb_wrap tb_other">
		<div class="tb_title">套票电子券<#if couponInfo.received == 0 ><a class="tb_title_a pop_a" data-pop="pop_coupon" data-pos="center" href="javascript:">电子券关联设置</a></#if></div>
		<table class="tb_main">
			<tr class="th">
				<td>电子券编号</td>
				<td class="name">电子券名称</td>
				<td class="sum">份数</td>
				<td>适用商户</td>
				<td>总数/已使用</td>
			</tr>
			<#list couponInfo.items as item>
			<tr <#if item_index%2==0>class="tr_bg"</#if>>
				<td>${(item.eid)!}</td>
				<td class="name">${(item.name)!}</td>
				<td>${(item.quantity)!}</td>
				<td><#list item.scope as shopId> ${applicableShopMap[''+shopId].name} </#list></td>
				<td>
					<#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount * item.quantity}</#if>/${usedCouponCountMap[''+item.id]}
					<a href="/coupon/package/${couponInfo.eid}/statistic/${item.eid}" >查看</a>
				</td>
			</tr>
			</#list>
		</table>
	</div>
	<div class="pb pop_del">
		<form action="${base}/coupon/delete/package/${(couponInfo.eid)!}" method="post">
			<input type="hidden" name="_method" value="delete"/> 
			<div class="pb_title">确认删除</div>
			<div class="pb_main">
				确认删除套票<span class="pb_cate_name">${(couponInfo.name)!}</span>?
			</div>
			<button class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_status_edit">
		<form action="${base}/coupon/${(couponInfo.eid)!}/modify_status" method="post">
			<input type="hidden" name="_method" value="put"/>
			<div class="pb_title">显示状态修改</div>
			<div class="pb_main"> 
				<div class="pb_rd_item"><input type="radio" id="pb_rd4" class="pb_rd" name="status" value="UNPUBLISHED" <#if !couponInfo.published>checked</#if>/><label class="pb_lb" for="pb_rd4">未发布</label></div>
				<div class="pb_rd_item"><input type="radio" id="pb_rd5" class="pb_rd" name="status" value="PUBLISHED_VISIBLE" <#if couponInfo.published && couponInfo.visible>checked</#if>/><label class="pb_lb" for="pb_rd5">发布(显示)</label></div>
				<div class="pb_rd_item"><input type="radio" id="pb_rd6" class="pb_rd" name="status" value="PUBLISHED_UNVISIBLE" <#if couponInfo.published && !couponInfo.visible>checked</#if>/><label class="pb_lb" for="pb_rd6">发布(不显示)</label></div>
			</div>
			<button class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_contact">
		<form action="${base}/coupon/${couponInfo.eid}/bind" method="POST" class="pop_coupon_form">
			<div class="pb_title">活动关联设置</div>
			<div class="pb_main">
				<div class="pb_rd_item">
					<#if activity??>
						<input type="radio" id="pb_rd1" class="pb_rd" name="bind_rd" data-url="/coupon/${(couponInfo.eid)!}/unbind?activity=${(activity.eid)!}"/><label class="pb_lb" for="pb_rd1">无关联活动</label>
					<#else>
						<input type="radio" id="pb_rd1" class="pb_rd" name="bind_rd" disabled data-url="javascript:;"/><label class="pb_lb" for="pb_rd1">无关联活动</label>
					</#if>
				</div>
				<div class="pb_rd_item">
					<input type="radio" id="pb_rd2" class="pb_rd" name="bind_rd" <#if (availableActivities?size)! lte 0>disabled</#if>/><label class="pb_lb" for="pb_rd2">与已有活动关联</label>
					<select name="activity" class="act_sel" <#if (availableActivities?size)! lte 0>disabled</#if>>
						<#list availableActivities as activity>
							<option value="${activity.eid}">${activity.name}</option>
						</#list>
					</select>
				</div>
				<div class="pb_rd_item">
					<input type="radio" id="pb_rd3" class="pb_rd" name="bind_rd" data-url="/activity/create?coupon=${(couponInfo.eid)!}"/><label class="pb_lb" for="pb_rd3">创建新的关联活动</label>
				</div>
			</div>
			<button class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<#if couponInfo.received == 0 >
		<div class="pb pop_coupon">
			<form action="${base}/coupon/modify/package/${(couponInfo.eid)!}" method="POST" class="coupon_num_form">
				<div class="pb_title">电子券关联设置</div>
					<div class="pb_main">
						<#list pager.list as related>
							<div class="pcp_item<#if related_index%2==0> pcp_item_bg</#if>">
								<#if usedCouponCountMap[''+related.id]??>
									<table class="pcp_tb"><tr><td><input type="checkbox" class="pb_cb" id="pb_cb${related_index}" name="itemId" value="${related.id}" checked="checked"/><label for="pb_cb${related_index}" class="pb_lb">${related.name}</label></td><td><a href="javascript:" class="minus"></a><input type="text" name="itemQuantity" value="" class="num_input" onkeyup="this.value=this.value.replace(/\D/g,'')"  onafterpaste="this.value=this.value.replace(/\D/g,'')"><a href="javascript:" class="plus"></a></td></tr></table>
								<#else>
									<table class="pcp_tb"><tr><td><input type="checkbox" class="pb_cb" id="pb_cb${related_index}" name="itemId" value="${related.id}"/><label for="pb_cb${related_index}" class="pb_lb">${related.name}</label></td><td><a href="javascript:" class="minus"></a><input type="text" name="itemQuantity" value="" class="num_input" onkeyup="this.value=this.value.replace(/\D/g,'')"  onafterpaste="this.value=this.value.replace(/\D/g,'')"><a href="javascript:" class="plus"></a></td></tr></table>
								</#if>
							</div>
						</#list>
					</div>
				<div class="pb_btn_wrap"><button class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a></div>
			</form>
		</div>
	</#if>
</div>
<#if (status == "faild")!>
	<div class="pop_hint pop_hint3">${(msg)!}</div>
</#if>
<#if (status == "success")!>
	<div class="pop_hint pop_hint2">${(msg)!}</div>
</#if>
<@m.page_footer />