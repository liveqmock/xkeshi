<#import "/macro.ftl" as m>
<@m.page_header selected='marketing' subselected='list' title='短信任务列表' css="seller_list_new|msg_list"/>
<div class="rwrap">
	<div class="r_title"><span class="fl">短信任务</span>
	<div class="search_wrap"><a href="${base}/sms/send" class="new_a"></a></div></div>
	<table class="tb_main">
		<tr class="th">
			<#--<td class="id">电子券编号</td>-->
			<td class="time">创建时间</td>
			<#--<td class="state">状态</td>-->
			<td class="num">发送数量</td>
			<td class="cont">发送内容</td>
		</tr>
		<#list pager.list as smsTask>
		<tr <#if smsTask_index%2==0>class="tr_bg"</#if>>
			<#--<td class="id">${(couponInfo.eid)!}</td>-->
			<td class="time"><a class="b_a" href="/sms/task/detail/${smsTask.eid}">${smsTask.createDate?string('yyyy-MM-dd HH:mm:ss')}</a></td>
			<#--<td class="state"><#if smsTask.status == 'DONE'><span class="state1">已完成</span><#elseif smsTask.status == 'SENDING'><span class="state2">发送中</span><#elseif smsTask.status == 'PAUSE'><span class="state3">暂停</span></#if></td>-->
			<td class="num">${smsTask.count!}</td>
			<td class="cont">${smsTask.template!}</td>
		</tr>
		</#list>
	</table>
	
	<div class="page_wrap">
		<@m.p page=pager.pageNumber totalpage=pager.pageCount />
	</div>
</div>
<@m.page_footer />

