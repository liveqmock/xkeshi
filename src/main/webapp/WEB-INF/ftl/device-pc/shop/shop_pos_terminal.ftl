<#import "/macro.ftl" as m>
<@m.page_header selected='setting' subselected = 'pos_terminal' css='seller_info|pos_info' js='pos_info' />
<div class="rwrap">
		<div class="r_title">已绑定POS终端</div>	
		<div class="tb_wrap tb_wrap2">
			<table class="tb_main">
				<tr class="th">
					<td><b>设备码</b></td>
					<td><b>最后登录信息</b></td>
					<td><b>设备类型</b></td>
					<td><b>设备编号</b></td>
					<td><b class="rb">操作</b></td>
				</tr>
				<#list terminalList as termainl>
					<tr <#if termainl_index%2==0>class="tr_bg"</#if>>
						<td>${(termainl.deviceNumber)!}</td>
						<td>${(termainl.lastLogin?string('yyyy-MM-dd HH:mm:ss'))!}</td>
						<td>${(termainl.terminalType.desc)!'收银台'}</td>
						<td>${(termainl.code)!''}</td>
						<td><a class="pop_a" href="javascript:" data-termainlid="${termainl.id}">解除绑定</a></td>
					</tr>
				</#list>
			</table>
		</div>
	</div>
 
	<div class="pb del_pos">
		<form action="${base}/shop/terminal/delete/${shopId}" method="POST">
			<input type="hidden" name="_method" value="delete">
			<div class="pb_title">确定解除设备绑定？</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">设备码</p>
					<input class="pb_item_cont" id="deviceNumber"  style='border-left:0px;border-top:0px;border-right:0px;border-bottom:0px' >
					<input type="hidden" name="id">
				</div>
			</div>
			<button class="pb_btn pb_btn_s">解除绑定</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
<@m.page_footer />