<#import "/macro.ftl" as m>
<@m.page_header selected='setting'  title="交接班设置" subselected= 'shift_setting' css='seller_list_new|set_mime'  js="set_shift" />

<div class="rwrap" style="position: relative;">
		<div class="r_title" style="overflow:hidden;">交接班设置</div>
		<form class="check_form"   action="/shift/shift_setting"  method="POST">
			<input type = "hidden"  name="_method"  value="PUT">
			<table class="tb_main">
				<tr class="service_tr">
					<td class="ltd  ">
						<span class="td_key">是否启用交接班功能</span>
						<span class=" td_val td_input">
							<input type="radio" id="use1" name="enableShift" class="input-l use_rd" value="true"  data-enableshift = "${(enableShift?string('true','false'))!}"
								   <#if enableShift == true >checked="checked"</#if>>
							<label for="use1" class="lb_rd">启用</label>
							<input type="radio" id="use2" name="enableShift" class="input-l use_rd" value="false" 
							       <#if enableShift == false >checked="checked"</#if>>
							<label for="use2" class="lb_rd_on">不启用</label>
							<label  class="required_error"></label>
						</span>
						<span >（切换状态前请确保所有登陆收银台的账号均已退出）</span>
					</td>
				</tr>
				<tr class="service_tr">
					<td class="tr_bg ">
						
						<span class="td_key">交接班时是否显示应收数据</span>
						<span class="td_val td_input">
							<input type="radio" id="show1" name="visibleShiftReceivableData" class="input-l show_rd" value="true" data-visibleshiftreceivabledata="${(visibleShiftReceivableData?string('true','false'))!}"
							 		<#if visibleShiftReceivableData == true >checked="checked"</#if>>
							<label for="show1" class="lb_rd">显示</label>
							<input type="radio" id="show2" name="visibleShiftReceivableData" class="input-l show_rd" value="false"  
								   <#if visibleShiftReceivableData == false> checked="checked"</#if> >
							<label for="show2" class="lb_rd_on">不显示</label>
							<label  class="required_error"></label>
						</span>
					</td>
				</tr>
			</table>
		</form>
			<div class="btn_bottom">
				<button type="submit" class="pb_btn pb_btn_bottom">确定</button>
			</div>
	</div>
   <div class="pb pop_cfm">
	<div class="pb_title">再次确认</div>
	<div class="pb_main pb_main_b">
		<div class="pb_item">
			<p class="pb_title2">修改此设置前，请退出所有登录中的操作员账号。</p>
				<div class="pb_cb_item">
					<button class="pb_btn cfm_btn">是的，已经都退出</button><button class="pb_btn cancel_btn">不，还有账号在登录中</button>
				</div>
		</div>
	</div>
	</div>
	<div class="black_cover"></div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<@m.page_footer />