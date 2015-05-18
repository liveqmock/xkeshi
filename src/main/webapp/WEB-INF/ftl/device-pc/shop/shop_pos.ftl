<#import "/macro.ftl" as m>
<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
	<@m.page_header selected='shop' subselected='pos' css='seller_info|pos_info|pos_related' js='pos_oper|validate' />
</@shiro.hasAnyRoles>

<@shiro.hasAnyRoles name="ROLE_SHOP_ADMIN">
	<@m.page_header selected='setting' subselected='pos' css='seller_info|pos_info' js='pos_info' />
</@shiro.hasAnyRoles>

<div class="rwrap">
	<div class="r_title">商户资料</div>
	<#list error as e>
		${e}
	</#list>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
		<div class="nav_wrap">
			<a href="${base}/shop/${shopId}" class="nav_a ">商户基本信息</a>
			<a href="${base}/shop/${shopId}/about" class="nav_a ">关于商户</a>
			<a href="${base}/shop/${shopId}/album" class="nav_a ">相册列表</a>
			<a href="${base}/shop/${shopId}/account" class="nav_a ">商户账号</a>
			<a href="${base}/shop/${shopId}/pos" class="nav_a nav_now">POS相关</a>
		</div>
		</@shiro.hasAnyRoles>
		<div class="tb_wrap">
			<div class="tb_title lh20"><span class="fl">POS终端账户</span><a class="pop_a" data-pop="add_opt" href="javascript:" data-sizeOfOperatorsList="${sizeOfOperatorsList}">添加操作员账号</a></div>
			<table class="tb_main ">
				<tr class="th">
					<td><b>账号</b></td>
					<td><b>姓名</b></td>
					<td><b>职务</b></td>
					<td><b class="rb">操作</b></td>
				</tr>
				<#list operatorList as operator>
				<tr <#if operator_index%2==0>class="tr_bg"</#if>>
					<td>${operator.username}</td>
					<td>${operator.realName}</td>
					<td><#if operator.level =='OPERATOR'>收银员<#elseif operator.level =='MANAGER'>店长<#else>未知</#if></td>
					<td><a class="pop_a" href="javascript:" data-userid="${operator.id}">编辑</a></td>
				</tr>
				</#list>
			</table>
			<div class="pb add_opt">
		<form action="${base}/shop/operator/add/${shopId}" method="POST" id="form">
			<input type="hidden" name="submissionToken"  value="${submissionToken}"/>
			<div class="pb_title">添加操作员账号</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">账号</p>
					<input type="text" class="pb_item_input" name="username" id="username" autocomplete="off" placeholder="账号">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">姓名</p>
					<input type="text" class="pb_item_input" name="realName" id="realname" placeholder="姓名">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">登录密码</p>
					<input type="password" class="pb_item_input" name="password" id="password" placeholder="登录密码">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">确认密码</p>
					<input type="password" class="pb_item_input" id="password2" placeholder="确认密码">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">职务</p>
					<select name="level">
                        <option value="MANAGER">店长</option>
						<option value="OPERATOR">收银员</option>

					</select>
				</div>
			</div>
			<button class="pb_btn pb_btn_s" onclick="return saveOp();">保存</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb edit_opt">
		<form action="${base}/shop/operator/edit/${shopId}" method="POST" id="form2">
			<input type="hidden" name="_method" value="put">
			<div class="pb_title">修改操作员账号<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">账号</p>
					<input type="text" class="pb_item_input lh14"  name="username" disabled="disabled">
					<input type="hidden" name="username" >
					<input type="hidden" name="id">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">姓名</p>
					<input type="text" class="pb_item_input lh14"  name="realName" disabled="disabled">
					<input type="hidden" name="realName">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">登录密码</p>
					<input type="password" class="pb_item_input lh14" name="password" id="password3" placeholder="空为原密码">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">确认密码</p>
					<input type="password" class="pb_item_input lh14" id="password4" placeholder="空为原密码">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">职务</p>
					<select name="level">
						<option value="OPERATOR">收银员</option>
						<option value="MANAGER">店长</option>
					</select>
				</div>
			</div>
			<button class="pb_s_save"  onclick="return editOp();">保存</button>
		</form>
			<button class="pb_s_del">删除账号</button>
	</div>
	<div class="pb add_pos">
		<form action="${base}/shop/terminal/add/${shopId}" method="POST" >
		    <input type="hidden" name="submissionToken"  value="${submissionToken}"/>
			<div class="pb_title">添加POS机终端<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">设备码</p>
					<input type="text" class="pb_item_input" name="deviceNumber">
				</div>
			</div>
			<button class="pb_btn">确定添加终端</button>
		</form>
	</div>
	<div class="pb del_pos">
		<form action="${base}/shop/terminal/delete/${shopId}" method="POST">
			<input type="hidden" name="_method" value="delete">
			<div class="pb_title">确定解除设备绑定？<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
				    <div>解除绑定前，请退出当前设备中的操作员账号。<br></div>
					<p class="pb_item_title">设备码</p>
					<input class="pb_item_cont" id="deviceNumber"  style='border-left:0px;border-top:0px;border-right:0px;border-bottom:0px;width:200px;' >
					<input type="hidden" name="id">
				</div>
			</div>
			<button class="pb_btn">解除绑定</button>
		</form>
	</div>
	<div class="pb consume_type">
		<form action="${base}/shop/${shopId}/consumeType/edit" method="POST">
			<div class="pb_title">确定修改点单和付款设置？<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">点单付款设置</p>
				</div>
				<div class="pb_item">
					<input type="radio" name="consumeType"<#if (shopInfo.consumeType)! && shopInfo.consumeType=='PAY_FIRST_WITH_SEAT'> checked ="true"</#if> value="PAY_FIRST_WITH_SEAT" id="pay_with_seat"><label for="pay_with_seat">先付款后消费(有座)</label>
				</div>
				<div class="pb_item">
					<input type="radio" name="consumeType" <#if (shopInfo.consumeType)! &&  shopInfo.consumeType=='CONSUME_FIRST_WITH_SEAT' > checked ="true"</#if> value="CONSUME_FIRST_WITH_SEAT" id="consume_with_seat"><label for="consume_with_seat">先消费后付款(有座)</label>
				</div>
				<div class="pb_item">
					<input type="radio" name="consumeType" <#if (shopInfo.consumeType)! && shopInfo.consumeType=='PAY_FIRST_WITHOUT_SEAT' > checked ="true"</#if> value="PAY_FIRST_WITHOUT_SEAT" id="pay_without_seat"><label for="pay_without_seat">先付款后消费(无座)</label>
				</div>
			</div>
			<button class="pb_btn">保存</button>
		</form>
	</div>
		</div>
		<@shiro.hasAnyRoles name="ROLE_ADMIN,ROLE_MERCHANT_ADMIN">
		<div class="tb_wrap tb_wrap3">
			<div class="tb_title lh20"><span class="fl">点单和付款设置（设置成功后，请勿在营业时间修改设置。）</span>
				<a class="pop_a" data-pop="consume_type" href="javascript:">修改设置</a>
			</div>
			<table class="tb_main">
				<tr>
					<td><b>点单设置</b>：${(shopInfo.consumeType.description)!}</td>
				</tr>
			</table>
		</div>
		<div class="tb_wrap tb_wrap2">
			<div class="tb_title">已绑定POS终端
			<@shiro.hasAnyRoles name="ROLE_ADMIN">
				<a class="pop_a" data-pop="add_pos" href="javascript:">添加POS终端</a>
			</@shiro.hasAnyRoles>
			</div>
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
						<td>
                            <@shiro.hasAnyRoles name="ROLE_ADMIN">
                                <a class="pop_a" href="javascript:" data-termainlid="${termainl.id}">解除绑定</a>
                            </@shiro.hasAnyRoles>
                        </td>
					</tr>
				</#list>
			</table>
		</div>
		</@shiro.hasAnyRoles>
        <@shiro.hasAnyRoles name="ROLE_ADMIN">
        <div class="tb_wrap tb_pos3">
            <div class="tb_title">支付方式设置</div>
            <table class="tb_main">
                <tr class="th">
                    <td class="td_w6"><b>支付方式 </b></td>
                    <td class="td_w7"><b>商户号</b></td>
                    <td class="td_w8"><b>检验码</b></td>
                    <td class="td_w9"><b>终端号</b></td>
                    <td class="td_w10"><b>支付平台</b></td>
                    <td class="td_w6"><b>状态</b></td>
                    <td class="td_w11"><b>操作</b></td>
                </tr>
                <tr class="th tr_bg" data-id="">
                    <td class="td_w6">现金</td>
                    <td class="td_w7"></td>
                    <td class="td_w8"></td>
                    <td class="td_w9"></td>
                    <td class="td_w10"></td>
                    <td class="td_w6" state-val="${(shopInfo.enableCash)!}">
                        <#if shopInfo?? && shopInfo.enableCash == 1>
                            开启
                        <#else>
                            关闭
                        </#if>
                    </td>
                    <td class="td_w11"><a class="pop_a pop_as pop_as2" href="javascript:">修改</a></td>
                </tr>
                <tr class="th" data-id="">
                    <td class="td_w6">支付宝</td>
                    <td class="td_w7">${(alipay.account)!}</td>
                    <td class="td_w8">${(alipay.signKey)!}</td>
                    <td class="td_w9"></td>
                    <td class="td_w10"></td>
                    <td class="td_w6" state-val="${(alipay.enable)!}">
                        <#if alipay?? && alipay.enable == 1>
                            开启
                        <#else>
                            关闭
                        </#if>
                    </td>
                    <td class="td_w11"><a class="pop_a pop_as pop_as3" href="javascript:">修改</a></td>
                </tr>
                <tr class="th tr_bg" data-id="">
                    <td class="td_w6">微信</td>
                    <td class="td_w7">${(wechat.account)!}</td>
                    <td class="td_w8">${(wechat.signKey)!}</td>
                    <td class="td_w9"></td>
                    <td class="td_w10"></td>
                    <td class="td_w6" state-val="${(wechat.enable)!}">
                        <#if wechat?? && wechat.enable == 1>
                            开启
                        <#else>
                            关闭
                        </#if>
                    </td>
                    <td class="td_w11"><a class="pop_a pop_as pop_as4" href="javascript:">修改</a></td>
                </tr>
                <tr class="th" data-id="">
                    <td class="td_w6">银行卡</td>
                    <td class="td_w7">${(bankCard.account)!}</td>
                    <td class="td_w8">${(bankCard.signKey)!}</td>
                    <td class="td_w9">${(bankCard.terminal)!}</td>
                    <td class="td_w10"><#if bankCard?? && bankCard.enable==1 && bankCard.type == 'BOC'>中国银行<#elseif bankCard?? && bankCard.enable==1 && bankCard.type == 'UMPAY'>联动优势<#elseif bankCard?? && bankCard.enable==1 && bankCard.type == 'SHENGPAY'>盛付通</#if></td>
                    <td class="td_w6" state-val="${(bankCard.enable)!}">
                        <#if bankCard?? && bankCard.enable == 1>
                            开启
                        <#else>
                            关闭
                        </#if>
                    </td>
                    <td class="td_w11"><a class="pop_a pop_as pop_as5" href="javascript:">修改</a></td>
                </tr>
            </table>
        </div>

        <div class="tb_wrap tb_pos3">
            <div class="tb_title">优惠方式设置</div>
            <table class="tb_main">
                <tr class="th">
                    <td class="td_w6"><b>优惠方式 </b></td>
                    <td class="td_w7"><b></b></td>
                    <td class="td_w8"><b></b></td>
                    <td class="td_w9"><b></b></td>
                    <td class="td_w10"><b></b></td>
                    <td class="td_w6"><b>状态</b></td>
                    <td class="td_w11"><b>操作</b></td>
                </tr>
                <#list disCountWayList as list>
                    <tr class="th <#if list_index%2==0>tr_bg</#if>">
                        <td class="td_w6">${(list.discountWayName.name)!}</td>
                        <td class="td_w7"></td>
                        <td class="td_w8" state-val="${list.enablePrepaidCard}">
                            <#if list.enablePrepaidCard?? && list.enablePrepaidCard == 1>
                                含预付卡
                            </#if>
                        </td>
                        <td class="td_w9"></td>
                        <td class="td_w10"></td>
                        <td class="td_w6" state-val="${list.enable}">
                            <#if list.enable == 1>
                                开启
                            <#else>
                                关闭
                            </#if>
                        </td>
                        <td class="td_w11"><a class="pop_a pop_as
                        <#if list.discountWayName.id == 1>
                            pop_as6
                        <#elseif list.discountWayName.id == 2>
                            pop_as7
                        <#else>
                            pop_as8
                        </#if>
                        " href="javascript:">修改</a></td>
                    </tr>
                </#list>
            </table>
        </div>

        <div class="pb edit_cash">
            <form id="form4" class="check_form" action="${base}/shop/payment/update">
                <input type="hidden" name="shopId" value="${shopId}">
                <div class="pb_title">现金支付<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd16" name="enableCash" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd16">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd17" name="enableCash" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd17">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>



        <div class="pb edit_pay">
            <form method="post" id="form5" class="check_form" action="${base}/shop/${shopId}/payment/update">
                <input type="hidden" name="type" value="ALIPAY">
                <div class="pb_title">支付宝支付<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd14" name="enableZFB" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd14">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd15" name="enableZFB" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd15">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">商户号</p>
                        <input type="text" class="pb_item_input required" name="account" id="pay_account1" data-hint="商户号" >
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">校验码</p>
                        <input type="text" class="pb_item_input required" name="signKey" id="pay_test1" data-hint="校验码">
                    </div>
                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>




        <div class="pb edit_MicroLetter ">
            <form id="form6" class="check_form" method="post" action="${base}/shop/${shopId}/payment/update">
                <input type="hidden" name="type" value="WECHAT">
                <div class="pb_title">微信支付<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd12" name="enableWX" value="1" class="input-l required" data-msg="是否" checked ><label class="pb_lb" for="pb_rd12">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd13" name="enableWX" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd13">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">商户号</p>
                        <input type="text" class="pb_item_input required" name="account" id="pay_account2" data-hint="商户号">
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">校验码</p>
                        <input type="text" class="pb_item_input required" name="signKey" id="pay_test2" data-hint="校验码">
                    </div>
                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>
        <div class="pb edit_BankCard">
            <form id="form7" class="check_form" method="post" action="${base}/shop/${shopId}/payment/updateBandCard">
                <div class="pb_title">银行卡支付<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd10" name="enableYHK" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd10">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd11" name="enableYHK" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd11">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">支付平台</p>
                        <select name="type">
                            <option value="BOC">中国银行</option>
                            <option value="UMPAY">联动优势</option>
                            <option value="SHENGPAY">盛付通</option>
                        </select>
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">商户号</p>
                        <input type="text" class="pb_item_input required" name="account" id="pay_account3" data-hint="商户号">
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">校验码</p>
                        <input type="text" class="pb_item_input required" name="signKey" id="pay_test3" data-hint="校验码">
                    </div>
                    <div class="pb_item">
                        <p class="pb_item_title">终端号</p>
                        <input type="text" class="pb_item_input required" name="terminal" id="pay_num3"  data-hint="终端号">
                    </div>


                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>
        <div class="pb edit_electronic">
            <form id="form8" class="check_form" action="${base}/shop/discount/update/1">
                <input type="hidden" name="shopId" value="${shopId}">
                <div class="pb_title">电子券<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd8" name="enableDZQ" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd8">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd9" name="enableDZQ" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd9">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>
        <div class="pb edit_entity">
            <form  id="form9" class="check_form" action="${base}/shop/discount/update/2">
                <input type="hidden" name="shopId" value="${shopId}">
                <div class="pb_title">实体券<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd6" name="enableSTQ" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd6">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd7" name="enableSTQ" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd7">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>
        <div class="pb edit_members">
            <form  id="form10" class="check_form" action="${base}/shop/discount/update/3">
                <input type="hidden" name="shopId" value="${shopId}">
                <div class="pb_title">会员卡<a href="javascript:" class="pb_close"></a></div>
                <div class="pb_main">
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd4" name="enableHYK" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd4">开启</label></div>
                        <div class="pb_rd_item pb_rd_item_w"><input type="radio" id="pb_rd5" name="enableHYK" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd5">关闭</label></div>
                        <div class="div_cl"></div>
                    </div>
                    <div class="pb_item">
                        <div class="pb_rd_item pb_rd_item_w2"><input type="radio" id="pb_rd1" name="enablePrepaidCard" value="1" class="input-l required" data-msg="是否" checked><label class="pb_lb" for="pb_rd1">开通预付卡</label></div>
                        <div class="pb_rd_item pb_rd_item_w2"><input type="radio" id="pb_rd2" name="enablePrepaidCard" value="0" class="input-l required" data-msg="是否"><label class="pb_lb" for="pb_rd2">不开通预付卡</label></div>
                    </div>
                </div>
                <button type="button" class="pb_btn pb_btn4">保存</button>
            </form>
        </div>
        </@shiro.hasAnyRoles>
	</div>
	
</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
<@m.page_footer />