<#import "/macro.ftl" as m>
<@m.page_header title="会员详情" css='add_seller_new|seller_list_new|seller_info|member_detail|member_validate'  selected="member" subselected="list" js="member_detail|validate|My97DatePicker/WdatePicker"/>
<script>
	var BASE = "${(BASE)!}";
	var MOBILE = "${(member.mobile)!}";
</script>
<div class="rwrap">
	<div class="r_title">
		<a class="back_a" href="<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">${base}/member/merchant/list</@shiro.hasAnyRoles>
							<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">${base}/member/shop/list</@shiro.hasAnyRoles>" title="返回"></a>
		<span class="member_title">会员详情</span>
		<div class="search_wrap">
			<a href="javascript:void(0)" class="edit_a" title ="编辑会员"></a>
			<a href="javascript:void(0)" class="delete_a ml6 mlie" title ="删除会员"></a>
		</div>
	</div>
	<div>
	<form action="/member/update/${type!}_type" method="post" class="memberFrom check_form">
		<input type="hidden" name ="_method" value="PUT">
		<input type="hidden" name ="id" value="${(member.id)!}">
		<table class="tb_main">	
			<tr>
				<td class="td_key1"><em>*</em><span class="td_key">姓名:</span></td>
				<td class="td_val1">
					<span class="td_val td_source_val">${(member.name)!}</span>
					<span class="td_val td_input">
						<input type="text" class="input-l isneed" data-hint="姓名" name="name" value="${(member.name)!}" data-maxlen="250">
						<label class="required_error"></label>
					</span>
				</td>
			</tr>
			<tr class="tr_bg">
				<td class="td_key1"><em></em><span class="td_key">昵称:</span></td>
				<td class="td_val1">
					<span class="td_val td_source_val">${(member.nickName)!}</span>
					<span class="td_val td_input">
						<input  type="text"  class="input-l"  name="nickName" value="${(member.nickName)!}" data-maxlen="250">
					</span>
				</td>
			</tr>
			<tr>
				<td class="td_key1"><em>*</em><span class="td_key">会员类型:</span></td>
				<td class="td_val1">
					<span class="td_val td_source_val">${(member.memberType.name)!}</span>
					<span class="td_val td_input">
						<select name ="memberType.id"  data-hint="会员类型" class="input-l isneed">
							<option value="">请选择</option>
							<#list memberTypes as memberType>
								<option value="${(memberType.id)!}" <#if memberType.default> selected="selected" </#if>>${(memberType.name)!}</option>
							</#list>
						</select>
						<label class="required_error"></label>
						</span>
				</td>
			</tr>
			<tr class="tr_bg">
				<td class="td_key1"><em>*</em><span class="td_key">手机号码:</span></td>
				<td class="td_val1">
					<span class="td_val td_source_val ios-tel">${(member.mobile)!}</span>
					<span class="td_val td_input">
						<input type="text" class="input-l isneed isphone" name="mobile" value="${(member.mobile)!}" id="mobile" data-hint="手机号码">
						<label  class="required_error"></label>
					</span>
				</td>
			</tr>
			<tr>
				<td class="td_key1"><em>*</em><span class="td_key">性别:</span></td>
				<td class="td_val1">
					<span class="td_val td_source_val">
						<#if (member.gender == "male")!> 先生 <#elseif (member.gender == "female")!> 女士 <#else> 不详 </#if>
					</span>
					<span class=" td_val td_input">
						<input type="radio" id="sex_rd1" name="gender" class="input-l isneed" data-hint="性别" value="male" <#if (member.gender == "male")!>checked="checked"</#if> />
						<label for="sex_rd1" class="gex_chk lb_rd_on">先生</label>
						<input type="radio" id="sex_rd2" name="gender" class="input-l isneed" data-hint="性别" value="female" <#if (member.gender == "female")!>checked="checked"</#if>/>
						<label for="sex_rd2" class="gex_chk lb_rd">女士</label>
						<label class="required_error"></label>
					</span>
				</td>
			</tr>
			<#-- 显示自定义模板属性 -->
			<#if member??>
			    <#if member.memberType ??>
			    <#if member.memberType.memberAttributeTemplate ??>
			    <#if member.memberType.memberAttributeTemplate.memberAttributeList ??>
				<#list member.memberType.memberAttributeTemplate.memberAttributeList as key>
					<tr class="<#if key_index%2==0>tr_bg</#if> td_source_val">
						<td class="td_key1 ">
							<em  style="margin-left:-6px"><#if (key.required)!>*<#else></#if></em>
							<span class="td_key td_source_val"> ${(key.name)!}:</span>
						</td>
						<td class="td_val1">
							<span class="td_val td_val">${(key.storedValue)!} </span>
						</td>
					</tr>
				</#list>
				<#list member.memberType.memberAttributeTemplate.memberAttributeList as key>
					<tr class="tr_bg td_input">
						<td class="td_key1">
							<em style="margin-left:-6px"><#if (key.required)!>*<#else></#if></em>
							<span class="td_key "> ${(key.name)!}:</span>
						</td>
						<td class="td_val1">
							<span class="td_val td_input">
								<input type="hidden" name ="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].id" value="${(key.id)!}">
								<#if key.attributeType == 'text'>
									<input type="text" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" id="pa${(key_index)!}"
										class="input-l <#if (key.required)!>isneed</#if>"  data-hint="${(key.name)!}" value="${(key.storedValue)!}"/>
								<#elseif key.attributeType == 'date'>
									<input type="text" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" id="pa${(key_index)!}"
										class="input-l <#if (key.required)!>isneed</#if>"   data-hint="${(key.name)!}"  data-type="date" data-datefmt="yyyy-MM-dd"
										value="${(key.storedValue)!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})"/>
								<#elseif key.attributeType == 'checkbox'>
									<#list key.attributeOptionalValues as attributeOption>
										<input id="cb_${(attributeOption_index)}" type="checkbox" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" value="${(attributeOption)!}" 
											<#if (key.storedValue)!?contains(attributeOption)>checked= "checked"</#if> class="<#if (key.required)!>isneed</#if>" data-hint="${(key.name)!}"/>
										<label for="cb_${(attributeOption_index)}">${(attributeOption)!}</label>
									</#list>
								<#elseif key.attributeType == 'number'>
									<input type="text" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" id="pa${(key_index)!}"
										class="input-l isint <#if (key.required)!>isneed</#if>"   data-hint="${(key.name)!}" data-type="number" value="${key.storedValue}"/>
								<#elseif key.attributeType == 'select'>
									<select name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" class="select-l <#if (key.required)!>isneed</#if>" data-hint="${(key.name)!}" id="pa${(key_index)!}">
										<option value="">请选择</option>
										<#list key.attributeOptionalValues as attributeOption>
											<option <#if attributeOption == key.storedValue>selected= "selected"</#if> value="${(attributeOption)!}">${(attributeOption)!}</label>
										</#list>
									</select>
								</#if>
							<label class="required_error"></label>
							</span>
						</td>
					</tr>
				</#list>
			</#if>
			</#if>
			</#if>
			</#if>
		</table>
		<span class="td_input">
			<div class="btn_wrap"><button class="add_seller_btn form_submit">修改会员</button></div>
		</span>
	</form>
	</div>
	<div class="pb pop_del">
		<form action="/member/${(member.eid)!}/delete"   method="post">
			<input type="hidden" name="_method" value="DELETE">
				<div class="pb_title">删除提示</div>
				<div class="pb_main">是否删除会员<span class="pb_cate_name">${(member.name)!}</span></div>
				<div class="pb_buttons">
					<button  class="pb_form_btn"  type="submit">确定</button>
					<button type="button" class="pb_cancel_btn">取消</button>
				</div>
				<div class="pb_warning">注意:</div>
				<div>删除后，该会员所有相关记录将一起被删除，无法恢复!</div>
		</form>
	</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />




