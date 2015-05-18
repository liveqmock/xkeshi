<#import "/macro.ftl" as m>
<@m.page_header title="电子券详情" selected='coupon' subselected="couponInfoList" css="seller_info|coupon_detail" js="coupon_list|coupon_detail"/>
<script>
var CONT_DETAIL = '${(couponInfo.description)!}' 
</script>
<div class="rwrap">
	<div class="r_title"><span class="fl"><a href="${base}/coupon/couponInfo/list" class="back_a"></a>电子券详情</span>
		<div class="search_wrap">
			<a class="edit_a" href="${base}/coupon/edit/${(couponInfo.eid)!}"></a><a class="del_a" href="javascript:"></a>
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
							<td><span class="td_key">电子券编号</span><span class="td_val">${(couponInfo.eid)!}</span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">名称</span><span class="td_val">${(couponInfo.name)!}</span></td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">适用商户</span>
								<span class="td_val">
									<#if shops >
										<#list shops as shop>
											${(shop.name)!}
										</#list>
									</#if>
								</span>
							</td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">简介</span><span class="td_val">${(couponInfo.intro)!}</span></td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">相关图片</span><span class="td_val">列表缩略图<a class="l_a img_a" href="javascript:" data-src="${image_base}${couponInfo.thumb}">点击查看</a>详情页大图<a class="l_a img_a" data-src="${image_base}/${couponInfo.pic}" href="javascript:">点击查看</a></span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">服务保障</span><span class="td_val">
									<#if (couponInfo.supportNormalRefund)!false>支持随时退</#if>&nbsp;&nbsp;<#if (couponInfo.supportExpiredRefund)!false>支持过期退</#if>
								</span>
							</td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">描述详情</span><span class="td_val"><a class="l_a cont_detail_a" style="margin-left:0;" href="javascript:">[查看详情]</a></span></td>
						</tr>
						<tr class="tr_bg">
							<td colspan='2'><span class="td_key">使用说明</span><span class="td_val">
							<#if couponInfo.instructions?? && couponInfo.instructions!=''>
							${(couponInfo.instructions)!}
							<#else>
							请持此电子券至商户收银台爱客仕核销系统扫一扫，即享优惠。
							</#if>
							</span></td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">备注</span><span class="td_val">${(couponInfo.remark)!}</span></td>
						</tr>
						<tr class="tr_bg">
							<td><span class="td_key">售价</span><span class="td_val">${(couponInfo.price)!}</span></td>
							<td><span class="td_key">原价</span><span class="td_val">${(couponInfo.originalPrice)!}</span></td>
						</tr>
						<tr>
							<td colspan='2' class="td_title"><span>获取条件</span></td>
						</tr>
						<tr class="tr_bg">
							<td><span class="td_key">获取起始时间</span>
								<span class="td_val">
									<#if (couponInfo.saleStartDate)?? >${(couponInfo.saleStartDate?string('yyyy/MM/dd HH:mm:ss'))!}<#else>不限</#if>
								</span>
							</td>
							<td><span class="td_key">获取截止时间</span>
								<span class="td_val">
									<#if (couponInfo.saleEndDate)?? >${(couponInfo.saleEndDate?string('yyyy/MM/dd HH:mm:ss'))!}<#else>不限</#if>
								</span>
							</td>
						</tr>
						<tr>
							<td colspan='2'><span class="td_key">限定获取用户</span><span class="td_val"><#if !(couponInfo.userLimitCount)?? || couponInfo.userLimitCount lte 0>不限<#else>每个ID限购${couponInfo.userLimitCount}份</#if></span></td>
						</tr>
						<#if couponInfo.limitPayTime?? && couponInfo.allowContinueSale??>
						<tr class="tr_bg">
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
						<p class="sd_p"><span><#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount}</#if>/${couponInfo.received}</span><a href="${base}/coupon/statistic/${couponInfo.eid}" >查看</a></p>
					</div>
					<p class="sd_title">已使用/未使用</p>
					<div class="sd_cont">
						<p class="sd_p"><span>${(usedCouponCount)!}/${(availableCouponCount)!}</span><a href="${base}/coupon/statistic/${couponInfo.eid}">查看</a></p>
					</div>
					<p class="sd_title">有效期</p>
					<div class="sd_cont">
						<p class="sd_p"><span><#if (couponInfo.startDate)??>${couponInfo.startDate?string('yyyy/MM/dd')}</#if>-<#if (couponInfo.endDate)??>${couponInfo.endDate?string('yyyy/MM/dd')}</#if></span><a href="${base}/coupon/edit/${(couponInfo.eid)!}">修改</a></p>
					</div>
					<#--<p class="sd_title">显示状态</p>
					<div class="sd_cont">
						<p class="sd_p">
							<span><#if couponInfo.published && couponInfo.visible>发布(显示)<#elseif couponInfo.published && !couponInfo.visible>发布(不显示)<#elseif !couponInfo.published>未发布</#if></span>
							<a href="javascript:" class="status_edit">修改</a>
						</p>
					</div>-->
					<#--<p class="sd_title">关联活动</p>
					<div class="sd_cont">
						<p class="sd_p"><span><#if activity??>${activity.name}<#else>无关联活动</#if></span><a href="javascript:" class="bind_activity">设置关联</a></p>
					</div>-->
					<p class="sd_title">电子券推送</p>
					<div class="sd_cont sd_many">
						<p class="sd_p"><span>手机短信</span><a href="${base}/sms/send?couponInfoEid=${(couponInfo.eid)!}&type=N">推送</a></p>
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
	<#if pager??>
		<div class="tb_wrap tb_other">
			<div class="tb_title">相关电子券</div>
			<table class="tb_main">
				<tr class="th">
					<td>电子券编号</td>
					<td class="name">电子券名称</td>
					<td>状态</td>
					<td>有效期</td>
					<td>适用商户</td>
					<td>总数/已使用</td>
					<td>标签</td>
				</tr>
				<#list pager.list as couponInfo>
				<tr <#if couponInfo_index%2==0>class="tr_bg"</#if>>
					<td>${(couponInfo.eid)!}</td>
					<td class="name">${(couponInfo.name)!}</td>
					<td class="state"><#if couponInfo.status == 'UNPUBLISHED'><span class="state2">未发布</span><#else><span class="state1">发布(<#if couponInfo.visible>显示<#else>不显示</#if>)</span></#if></td>
					<td><#if (couponInfo.endDate)??>${couponInfo.endDate?string('yyyy/MM/dd')}</#if></td>
					<td>
						<#if shop >
							${(shop.name)!}
						<#elseif shops>
							<#list shops as shop>
								${(shop.name)!}
							</#list>
						</#if>
					</td>
					<td><#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>不限<#else>${couponInfo.limitCount}</#if>/${couponInfo.received}</td>
					<td>-</td>
				</tr>
				</#list>
			</table>
		</div>
	</#if>
	<div class="pb pop_status_edit">
		<form action="${base}/coupon/${(couponInfo.eid)!}/modify_status" method="post">
			<input type="hidden" name="_method" value="put"/>
			<div class="pb_title">显示状态修改</div>
			<div class="pb_main">
				<div class="pb_item">
					<div class="pb_rd_item"><input type="radio" id="rd1" class="pfrd" name="status" value="UNPUBLISHED" <#if !couponInfo.published>checked</#if>/><label for="rd1">未发布</label></div> 
					<div class="pb_rd_item"><input type="radio" id="rd2" class="pfrd" name="status" value="PUBLISHED_VISIBLE" <#if couponInfo.published && couponInfo.visible>checked</#if> /><label for="rd2">发布(显示)</label></div>
					<div class="pb_rd_item"><input type="radio" id="rd3" class="pfrd" name="status" value="PUBLISHED_UNVISIBLE" <#if couponInfo.published && !couponInfo.visible>checked</#if>/><label for="rd3">发布(不显示)</label></div>
				</div>
			</div>
			<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_bind_activity">
		<form action="${base}/coupon/${couponInfo.eid}/bind" method="POST" class="pop_coupon_form">
			<div class="pb_title">活动关联设置</div>
			<div class="pb_main">
				<div class="pb_item">
					<#if activity??>
						<input type="radio" id="rd4" class="pfrd" name="bind_rd" data-url="/coupon/${(couponInfo.eid)!}/unbind?activity=${(activity.eid)!}"/><label for="rd4">无关联活动</label>
					<#else>
						<input type="radio" id="rd4" class="pfrd" name="bind_rd" disabled data-url="javascript:;"/><label for="rd4">无关联活动</label>
					</#if>
				</div>
				<div class="pb_item">
					<input type="radio" id="rd5" class="pfrd" name="bind_rd" <#if (availableActivities?size)! lte 0>disabled</#if>/><label for="rd5">与已有活动关联</label>
					<select name="activity" class="pfsel" <#if (availableActivities?size)! lte 0>disabled</#if>>
						<#list availableActivities as activity>
							<option value="${activity.eid}">${activity.name}</value>
						</#list>
					</select>
				</div>
				<div class="pb_item">
					<input type="radio" id="rd6" class="pfrd" name="bind_rd" data-url="/activity/create?coupon=${(couponInfo.eid)!}"/><label for="rd6">创建新的关联活动</label>
				</div>
			</div>
			<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_del">
		<form action="${base}/coupon/delete/${(couponInfo.eid)!}" method="post">
			<input type="hidden" name="_method" value="delete"/> 
			<div class="pb_title">删除</div>
			<div class="pb_main">确认删除电子券?</div>
			<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>	
</div>
<#if (status == "faild")!>
	<div class="pop_hint pop_hint3">${(msg)!}</div>
</#if>
<#if (status == "success")!>
	<div class="pop_hint pop_hint2">${(msg)!}</div>
</#if>
<@m.page_footer />