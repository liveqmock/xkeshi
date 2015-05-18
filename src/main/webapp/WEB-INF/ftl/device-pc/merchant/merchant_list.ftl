<#import "/macro.ftl" as m >
<@m.page_header selected='shop' subselected = "merchant" css="seller_list_new"  js ="merchant_list|My97DatePicker/WdatePicker|list_filter" />

<div class="rwrap">
	<div class="r_title"><span class="fl">集团列表</span>
	<div class="search_wrap">
		<form action="${base}/merchant/list" class="search_form fl">
		<input type="text" name="key" <#if searcher&&searcher.key>value="${(searcher.key)!}"</#if> placeholder="集团名称" class="search_input fi_1">
			<button class="search_btn"></button>
		</form>
		<a href="javascript:void(0)" class="filter_a fl"></a>
		<a href="javascript:void(0)" class="new_a fl ml12"></a>
	</div>
	  <#if (searcher != null && searcher.hasParameter)! >
			<div class="search_result ofw">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					
					<#if searcher.key>
						<span class="b_tit" data-fid="1">集团:${(searcher.key)!}<b>x</b></span>
					</#if>
					<#if searcher.createStartDate><span class="b_tit" data-fid="2">入驻起始时间:${(searcher.createStartDate?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					<#if searcher.createEndDate><span class="b_tit" data-fid="3">入驻结束时间:${(searcher.createEndDate?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					<#if searcher.modifyStartDate><span class="b_tit" data-fid="4">修改起始时间：${(searcher.modifyStartDate?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					<#if searcher.modifyEndDate><span class="b_tit" data-fid="5">修改结束时间：${(searcher.modifyEndDate?string('yyyy-MM-dd HH:mm:ss'))!}<b>x</b></span></#if>
					</span>
					<span class="w_tit">的结果</span></span>
				</p>
				<a href="${base}/merchant/list" class="s_clear">清空</a>
			</div>
	</#if>
	</div>
		<div class="opt_wrap">
			<div class="opt_main2">
				<a href="javascript:" class="opt1">公开</a>
				<a href="javascript:" class="opt2">隐藏</a>
				<a href="javascript:" class="opt3">修改</a>
				<a href="javascript:" class="opt4">删除</a>
			</div>
		</div>
		<form  id="merchantPlatform"  name="merchantPlatform"  method="post">
		<input type="hidden" name = "_method" id="methodPlatformId" value="" >
		<table class="tb_main tb_main1 tb_cte">
			<tr class="th">
				<td class="opt"><input type="checkbox" id="checkAll" /><label for="checkAll" >&nbsp;</label></td>
				<td class="id">集团ID</td>
				<td class="name">集团名称</td>
				<td class="name">子商户数量</td>
				<td class="name">入驻时间</td>
				<td class="name">修改时间</td>
				<td class="state">状态</td>
			</tr>
			<#list pager.list as merchant>
			<tr <#if merchant_index%2==0>class="tr_bg"</#if>>
				<td class="opt" style="padding:0 20px;">
				<input type="checkbox" name="merchantIds" class="checkbox_merchant" value="${merchant.id}" id="cb${merchant.id}" 
						  data-name="${(merchant.fullName)!}"  data-id="${(merchant.id)!}" data-smssuffix = "${(merchant.smsSuffix)!}"
						  data-avatar="${(merchant.avatar)!}"
				     <#list accounts as account>
				       <#if (merchant.id == account.businessId  && account.businessType == 'MERCHANT' )! >
				        data-username="${(account.username)!}"
				        data-accountid="${(account.id)!}"
				       </#if>	
				     </#list>
				     data-mcm ="${(merchant.memberCentralManagement?string('true','false'))!}"
				     data-bcm ="${(merchant.balanceCentralManagement?string('true','false'))!}"
				 ><label for="cb${merchant.id}">&nbsp;</label></td>
				<td class="">${(merchant.id)!}</td>
				<td class="name"  >
				     ${(merchant.fullName)!}
				</td>
				<td class="name">
				  ${(merchant.shops?size)!}
				  <a href = "/shop/list?merchantId=${(merchant.id)!}"  class="b_a">[查看商户]</a>
				</td>
				<td class="name">${(merchant.createDate?string('yyyy-MM-dd'))!'-'}</td>
				<td class="name">${(merchant.modifyDate?string('yyyy-MM-dd'))!'-'}</td>
				<td class="state">
				<#if merchant.visible >
					<span class="state1">公开</span>
				<#else>
					<span class="state2">隐藏</span>
				</#if>
				</td>
			</tr>
			</#list>
		</table>
		</form>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>
	
	 <div class="pb pop_filter">
			<form action="${base}/merchant/list" method="get" class="f_form">
			<div class="pb_title">筛选</div>
			<div class="pb_main pb_main_b">
				<div class="pb_item">
					<p class="pb_item_title">入驻时间起:</p>
					<input type="text" class="pb_item_input f_input fi_2" name="createStartDate"  
						 value="${(searcher.createStartDate?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">至:</p>
					<input type="text" class="pb_item_input f_input fi_3" name="createEndDate"
					       value="${(searcher.createEndDate?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">修改时间起:</p>
					<input type="text" class="pb_item_input f_input fi_4" name="modifyStartDate"  
						 value="${(searcher.modifyStartDate?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
				<div class="pb_item pb_item_b">
					<p class="pb_item_title">至:</p>
					<input type="text" class="pb_item_input f_input fi_5" name="modifyEndDate"
					       value="${(searcher.modifyEndDate?string('yyyy-MM-dd HH:mm:ss'))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
				</div>
			</div>
			<button class="pb_btn pb_btn_s" type="submit">确定</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>	
	
	<div class="pb pb_merchant">
		<form action="${base}/merchant/save" method="post" id="merchantForm"  name="merchantForm" enctype="multipart/form-data">
		    <input type="hidden" name="submissionToken" value="${submissionToken}">
			<div class="pb_title">
			   <b class="pb_title_b">添加集团</b>			   
			 </div>
			<div class="pb_main pb_main_b">
				<div class="pb_item pb_jt">
					<p class="pb_item_title ">集团名称:</p>
					 <input type="text"  class="pb_input required lh14 da_msg" name="fullName" id ="fullName" data-msg="集团名称" placeholder="集团名称" >
				     <label  class="required_error"></label>
				</div>
				<div class="pb_item pb_zh">
					<p class="pb_item_title">账号:</p>
					<input  type="text" name="username"   class="pb_input lh14 required"  id="username" data-msg="登录账号名称"  placeholder="账户名" >
					<input  type="hidden"    name="id" class="pb_input" id="accountId"   >
					<label  class="required_error"></label>
				</div>
				<div class="pb_item pb_mm">
					<p class="pb_item_title">密码:</p>
					<input  type="password"  name="password" class="pb_input lh14 required"  id="password"  data-msg="登录账号密码"  placeholder="账户密码" >
					<label  class="required_error"></label>
				</div>
				<div class="pb_item pb_dx">
					<p class="pb_item_title">短信后缀:</p>
					<input  type="text"  name="smsSuffix"  class="pb_input lh14 required"  id="smsSuffix" data-msg ="集团短信后缀" placeholder="短信后缀">
					<label  class="required_error"></label>
				</div>
				<div class="pb_item">
					<p class="pb_item_title mb7">会员统一管理:</p>
					 <input  type="radio"  name="memberCentralManagement"  checked="checked" class="pb_rd" id="pb_rd1"  value="true"><label for="pb_rd1">是</label>
					 <input  type="radio"  name="memberCentralManagement"  class="pb_rd" id="pb_rd2"  value="false"><label for="pb_rd2">否</label>
				</div>
				<div class="pb_item pb_tum">
					 <p class="pb_item_title">集团Logo:</p>
					 <input  type="file" name="avatarFile" id="avatar" class="pb_input " onchange="requiredFile(this)" value="" data-msg ="集团Logo" ><br>
					 <a href="${image_base}" target="_blank"  class="pb_input show_avatar">点击查看Logo</a>
					 <label  class="required_error"></label>
				</div>
			</div>
			<button class="pb_btn pb_btn_merchant pb_btn_s" type="button">保存</button>
		    <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	
	<div class="pb pb_merchant_del">
			<div class="pb_title">
			   <b class="pb_title_b">确认删除集团</b>
			   <a href="javascript:" class="pb_close"></a>
			 </div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title ">集团名称:</p>
					<p class="pb_item_merchant"></P>
				</div>
				<div class="pb_item">
					<p class="pb_item_title ">提示:</p>
					<p class="">删除集团后，将会解除已关联的商户</P>
				</div>
			</div>
		<button class="pb_btn pb_btn_merchant_del" type="submit">确认删除</button>
	</div>
	<div class="pb pb_warn">
				<div class="pb_title">警告<a href="javascript:" class="pb_close"></a></div>
				<div class="pb_main pb_main2">请选择需要操作的集团！</div>
				<button type="button" class="pb_btn pb_close" >确定</button>
	</div>
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>

<@m.page_footer />




