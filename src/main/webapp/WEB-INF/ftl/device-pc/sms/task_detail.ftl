<#import "/macro.ftl" as m>
<@m.page_header selected='marketing' subselected='list' title='短信任务详情' css="seller_info|msg_detail"/>
<div class="rwrap">
	<div class="r_title"><span class="fl"><a href="${base}/sms/task/list" class="back_a"></a>查看任务详情</span>
		<div class="search_wrap"><a class="new_a" href="${base}/sms/send"></a></div>
	</div>
	<div class="tb_wrap tb_basic">
		<table class="tb_main">
			<tr>
				<td colspan=2><span class="td_key">创建时间</span><span class="td_val">${smsTask.createDate?string('yyyy-MM-dd HH:mm:ss')}</span></td>
				<#--<td class="state"><span class="td_key">状态</span><span class="td_val"><#if smsTask.status == 'DONE'><span class="state1">已完成</span><#elseif smsTask.status == 'SENDING'><span class="state2">发送中</span></#if></span></td>-->
			</tr>
			<tr class="tr_bg">
				<td><span class="td_key">发送数量</span><span class="td_val">${smsTask.count!}</span></td>
				<td><span class="td_key">发送时间</span><span class="td_val">${smsTask.sendDate?string('yyyy-MM-dd HH:mm:ss')}</span></td>
			</tr>
		</table>
	</div>
	<div class="tb_wrap tb_other">
		<div class="tb_title">发送列表<#--<span>[ 发送成功${statistic[0]!}个</span><span>发送失败${statistic[1]!}个</span><span>发送中${statistic[2]!}个</span><span>等待发送${statistic[3]!}个 ]</span>--></div>
		<table class="tb_main tb_list">
			<tr class="th">
				<td class="id">序号</td>
				<td class="phone">手机号码</td>
				<#--<td class="state">发送状态</td>-->
				<td class="cont">发送内容</td>
			</tr>
			<#if pager?? && pager.list?size &gt; 0>
				<#list pager.list as message>
				<tr <#if message_index%2==0>class="tr_bg"<#else>class="tr"</#if>>
					<td class="id">${(pager.pageNumber - 1) * pager.pageSize + message_index + 1}</td>
					<td class="phone ios-tel">${(message.mobile)!}</td>
					<#--<td class="state">${(message.status.description)!}</td>-->
					<#-- 为防止商户通过短信内容获取优惠码，随意核销电子券，所以隐藏短信真实内容，改成显示批量短信的模板 <td class="cont">${(message.content)!}</td> -->
					<td class="cont">${smsTask.template!}</td>
				</tr>
				</#list>
			</#if>
		</table>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />
		</div>
	</div>
</div>
<@m.page_footer />