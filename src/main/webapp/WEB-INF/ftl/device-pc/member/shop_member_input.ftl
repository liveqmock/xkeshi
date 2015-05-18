<#import "/macro.ftl" as m>
<@m.page_header title="添加会员" css='add_seller_new|seller_info|member_validate' selected="member" subselected='list' js="validate|My97DatePicker/WdatePicker|member_add" />
<script>
  var MOBILE ;
</script>
<div class="rwrap">
	<div class="r_title">
	<a class="back_a" href="<@shiro.hasAnyRoles name="ROLE_MERCHANT_ADMIN">${base}/member/merchant/list</@shiro.hasAnyRoles>
							<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">${base}/member/shop/list</@shiro.hasAnyRoles>" title="返回"></a>添加会员</div>
	<div class="tb_wrap">
	<div class="tb_title">会员资料</div>
	<form 
		<#if type == 'MERCHANT'>action="${base}/member/shop/addByMerchant"</#if>
		<#if type == 'SHOP'>action="${base}/member/shop/addByShop"</#if>
		method="post" class="memberFrom check_form">
		<table class="tb_main">
			<tr>
				<td class="ltd"><em>*</em>
					<span class="td_key">姓名</span>
					<span class="td_val">
						<input type="hidden" name="submissionToken" value="${submissionToken}"/>
						<input type="text" class="input-l isneed" data-hint="姓名" name="name" data-maxlen="250">
						<label class="required_error"></label>
					</span>
				</td>
			</tr>
			<tr>
				<td class="ltd"><em style="padding-left:3px;"></em>
					<span class="td_key">昵称</span>
					<span class="td_val">
						<input type="text" class="input-l" name="nickName" data-hint="昵称" data-maxlen="250">
					</span>
				</td>
			</tr>
			<tr class="tr_bg">
				<td class="ltd"><em>*</em>
				<span class="td_key">会员类型</span>
				<span class="td_val">
				  <select name ="memberType.id" class="input-l isneed" data-hint="会员类型">
						<option value="">请选择</option>
						<#list memberTypes as memberType>
							<option value="${memberType.id}"
								<#if memberType.default>selected = "selected"</#if>
							>${memberType.name}</option>
						</#list>
					</select>
					<label class="required_error"></label>
				  </span>
				</td>
			</tr>
			<tr>
				<td class="ltd"><em>*</em>
				<span class="td_key">手机号码</span>
					<span class="td_val">
						<input type="text" class="input-l isneed isphone" name="mobile" id="mobile"
							data-hint="手机号码">
						<label class="required_error"></label>
					</span>
				</td>
			</tr>
			<tr class="tr_bg">
				<td class="ltd"><em>*</em>
				<span class="td_key">性别</span>
					<span class="td_val">
						<input type="radio" id="sex_rd1" name="gender" class="input-l isneed" data-hint="性别" value="male"/>
						<label for="sex_rd1" class="lb_rd_on">先生</label>
						<input type="radio" id="sex_rd2" name="gender" class="input-l isneed" data-hint="性别" value="female"/>
						<label for="sex_rd2" class="lb_rd">女士</label>
						<label  class="required_error"></label>
					</span>
				</td>
			</tr>
			<#list (memberTypes[0].memberAttributeTemplate.memberAttributeList)! as key>
				<input type="hidden" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].id" value="${key.id}" />
				<tr <#if key_index%2==1>class="tr_bg"</#if>>
					<td class="ltd"><em><#if key.required>*</#if></em>
					<span class="td_key">${key.name}</span><span class="td_val">
						<#if key.attributeType == 'text'>
							<input type="text" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" id="pa${key_index}"
									class="input-l <#if (key.required)!>isneed</#if>"  data-hint="${key.name}" />
						<#elseif key.attributeType == 'date'>
							<input type="text" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" id="pa${key_index}"
									class="input-l <#if (key.required)!>isneed</#if>"   data-hint="${key.name}"  data-type="date"
									data-datefmt="yyyy-MM-dd" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd'})" />
						<#elseif key.attributeType == 'checkbox'>
							<#list key.attributeOptionalValues as attributeOption>
								<input type="checkbox" id="cb_${key_index}_${(attributeOption_index)}" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue"
									value="${attributeOption}" class="<#if (key.required)!>isneed</#if>" data-hint="${key.name}" />
									<label for="cb_${key_index}_${(attributeOption_index)}">${attributeOption}</label>
							</#list>
						<#elseif key.attributeType == 'number'>
							<input type="text" name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue" id="pa${key_index}"
								class="input-l <#if (key.required)!>isneed</#if>" data-hint="${key.name}" data-type="number"/>
						<#elseif key.attributeType == 'select'>
							<select name="memberType.memberAttributeTemplate.memberAttributeList[${key_index}].storedValue"
								class="select-l <#if (key.required)!>isneed</#if>" data-hint="${key.name}">
								<option value="">请选择</option>
								<#list key.attributeOptionalValues as attributeOption>
									<option value="${attributeOption}">${attributeOption}</label>
								</#list>
							</select>
						</#if>
						<label  class="required_error"></label>
						</span>
					</td>
				</tr>
			</#list>
		</table>
		<div class="btn_wrap"><button class="add_seller_btn form_submit">添加会员</button></div>
	</form>
</div>
<@m.page_footer />




