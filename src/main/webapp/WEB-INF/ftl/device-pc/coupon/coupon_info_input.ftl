<#import "/macro.ftl" as m>
<#if couponInfoType == 'NORMAL'>
	<@m.page_header title="创建 电子券" selected='coupon' subselected='couponInfoList' css="add_seller_new|seller_info" js="validate|My97DatePicker/WdatePicker|coupon_new"/>
<#else>
	<@m.page_header title="创建 套票" selected='coupon' subselected='couponInfoPackageList' css="add_seller_new|seller_info" js="validate|My97DatePicker/WdatePicker|coupon_new"/>
</#if>
<script>
	var IMG_BUCKET = '${img_bucket}',
		IMG_PATH = '${image_base}',
		UP_IMG_TYPE = 3,
		SHOP_ID = ${(shopId)!},
		CONTEXT = '${base}'
</script>
<script type="text/javascript" src="/static/ueditor/ueditor.config.js"></script>
<script type="text/javascript" src="/static/ueditor/ueditor.all.js"></script>
<script type="text/javascript" src="/static/ueditor/lang/zh-cn/zh-cn.js"></script>
<!-- 实例化编辑器 -->
<script type="text/javascript">
	UE.getEditor('description');
</script>
<div class="rwrap">
	<p class="r_title"><a class="back_a" href="javascript:history.go(-1);"></a><#if couponInfoType == 'NORMAL'>电子券<#else>套票</#if><#if (couponInfo.eid)??>编辑<#else>创建</#if></p>
	<form action="${base}<#if couponInfoType == 'NORMAL'>/coupon/processCreation<#else>/coupon/process/creation/package</#if>" method="POST" enctype="multipart/form-data" class="check_form">
	<input type="hidden" name="submissionToken" value="${submissionToken}"/>
	<div class="tb_wrap">
		<input type="hidden" name="id" value="${(couponInfo.eid)!}" />
		<input type="hidden" name="activityEid" value="${activityEid!}" />
		<input type="hidden" name="type" value="${couponInfoType!'NORMAL'}"/>
		<div class="tb_title">基本信息</div>
		<table class="tb_main">
			<tr>
				<td class="ltd" colspan='2'><em>*</em><span class="td_key">名称</span><span class="td_val"><input name="name" id="name" class="ip1 isneed ckhed" type="text" value="${(couponInfo.name)!}" data-hint="名称"></span></td>
			</tr>
			<tr class="tr_bg">
				<td class="ltd" colspan='2'><em>*</em><span class="td_key">简介</span><span class="td_val"><textarea class="td_text isneed ckhed" id="intro" name="intro" data-hint="简介">${(couponInfo.intro)!}</textarea></span></td>
			</tr>
			<tr>
				<td class="std" style="vertical-align:top;"><em>*</em><span class="td_key">详情页大图</span><span class="td_val"><input type="file" name="picFile" class="file_ip isneed ckhed"  id="picFile" data-hint="详情页大图"/></span>
				<#if (couponInfo.pic)??>
					<img src="${image_base}/${couponInfo.pic}" style="width:300px;margin-top:20px;margin-left:104px;" />
				</#if>
				</td>
				<td class="std" style="vertical-align:top;"><em class="v9">*</em><span class="td_key">列表用缩略图</span><span class="td_val"><input type="file" name="thumbFile" id="thumbFile" class="file_ip isneed v9 file_b ckhed" data-hint="缩略图" /></span>
				<#if (couponInfo.thumb)??>
					<img src="${image_base}${couponInfo.thumb}" style="width:400px;margin-top:20px;" />
				</#if>
				</td>
			</tr>
			<tr class="tr_bg">
				<td><em>*</em><span class="td_key">有效期</span><span class="td_val">		
				<input type="text" class="ckhed isneed" name="startDate" data-hint="有效期" id="startDate"  value="${(couponInfo.startDate?string("yyyy-MM-dd HH:mm:ss"))!}" onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'endDate\')||\'2020-10-01\'}',dateFmt:'yyyy-MM-dd HH:mm:ss'})" /> - 
				<input type="text" class="ckhed isneed" name="endDate" data-hint="有效期" id="endDate" value="${(couponInfo.endDate?string("yyyy-MM-dd HH:mm:ss"))!}" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'startDate\')}',maxDate:'2020-10-01',dateFmt:'yyyy-MM-dd HH:mm:ss'})" />
				</span></td>
				<td><em style="margin-left:6px;"></em><span class="td_key">服务保障</span>
					<span class="td_val">
						<input type="checkbox" id="cb1" name="supportNormalRefund" value="true" <#if (couponInfo.supportNormalRefund)!false>checked=true</#if>/><label for="cb1"  style="margin-right:16px;">支持随时退款</label>
						<input type="checkbox" id="cb2" name="supportExpiredRefund" value="true" <#if (couponInfo.supportExpiredRefund)!false>checked=true</#if>/><label for="cb2">支持过期退款</label>
					</span>
				</td>
			</tr>
			<tr>
				<td class="std" colspan='2'><em>*</em><span class="td_key">描述详情</span><script id="description" name="description" type="text/plain" style="width:855px;height:300px;">${(couponInfo.description)!}</script>
				<input type="hidden" class="td_msg" />

			</tr>
			<tr class="tr_bg">
				<td class="ltd" colspan='2'><em style="margin-left:6px;"></em><span class="td_key">使用说明</span><span class="td_val"><textarea class="td_text" name="instructions">${(couponInfo.instructions)!}</textarea></span></td>
			</tr>
			<tr>
				<td class="ltd" colspan='2'><em style="margin-left:6px;"></em><span class="td_key">备注</span><span class="td_val"><textarea class="td_text" name="remark">${(couponInfo.remark)!}</textarea></span></td>
			</tr>
			<tr class="tr_bg">
				<td class="std"><em style="margin-left:6px;"></em><span class="td_key">总数限制</span>
					<span class="td_val">
						<input class="input_radio" id="rd1" name="input_radio1" type="radio" value="-1" <#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>checked=true</#if>/><label for="rd1">不限</label>
						<input class="input_radio" id="rd2" name="input_radio1" type="radio" value="1" <#if (couponInfo.limitCount)?? && couponInfo.limitCount &gt; 0>checked=true</#if>/><label for="rd2">限</label>
						<span class="show_val_wrap" <#if !(couponInfo.limitCount)?? || couponInfo.limitCount lte 0>style="display:none;"</#if>><input type="text" class="show_val short" name="limitCount" value="<#if (couponInfo.limitCount)?? && couponInfo.limitCount &gt; 0>${(couponInfo.limitCount)!}</#if>"/> 份</span>
					</span>
				</td>
				<td><em style="margin-left:6px;"></em><span class="td_key">每个用户限抢购</span>
					<span class="td_val">
						<input class="input_radio" id="rd7" name="input_radio2" type="radio" value="-1" <#if !(couponInfo.userLimitCount)?? || couponInfo.userLimitCount lte 0>checked=true</#if>/><label for="rd7">不限</label>
						<input class="input_radio" id="rd8" name="input_radio2" type="radio" value="1" <#if (couponInfo.userLimitCount)?? && couponInfo.userLimitCount &gt; 0>checked=true</#if>/><label for="rd8">限</label>
						<span class="show_val_wrap" <#if !(couponInfo.userLimitCount)?? || couponInfo.userLimitCount lte 0>style="display:none;"</#if>><input type="text" class="show_val short" name="userLimitCount" value="<#if (couponInfo.userLimitCount)?? && couponInfo.userLimitCount &gt; 0>${(couponInfo.userLimitCount)!}</#if>"/> 份</span>
					</span>
				</td>
			</tr>
		</table>
	</div>
	<div class="tb_wrap">
		<table class="tb_main">
			<div class="tb_title">获取条件</div>
			<tr>
				<td><em style="margin-left:6px;"></em><span class="td_key">售价</span><span class="td_val"><input type=“text" id="price" name="price" value="${(couponInfo.price)!}" class="short" onpropertychange='check(this.value)' oninput='check(this.value)'/>&nbsp;&nbsp;&nbsp;元</span></td>
				<td>
				<#if couponInfoType == 'NORMAL'>
					<em style="margin-left:6px;"></em><span class="td_key">原价</span><span class="td_val"><input type=“text" id="originalPrice" name="originalPrice" value="${(couponInfo.originalPrice)!}" class="short" onpropertychange='check(this.value)' oninput='check(this.value)'/>&nbsp;&nbsp;&nbsp;元</span>
				</#if>
				</td>
			</tr>
			<tr class="tr_bg">
				<td><em style="margin-left:6px;"></em><span class="td_key">获取起始时间</span>
					<span class="td_val">
						<input class="input_radio1" id="rd3" type="radio" name="saleStartDateRadio1"  value="-1" <#if !(couponInfo.saleStartDate)?? >checked</#if>/><label for="rd3">不限</label>
						<input class="input_radio1" id="rd4" type="radio" name="saleStartDateRadio1" value="1" <#if (couponInfo.saleStartDate)??>checked</#if>/><label for="rd4">限制</label>
						<span class="show_val_wrap1" <#if !(couponInfo.saleStartDate)??>style="display:none;"</#if>> 自 <input type="text" class="show_val1" name="saleStartDate" value="<#if (couponInfo.saleStartDate)??>${(couponInfo.saleStartDate?string('yyyy-MM-dd HH:mm:ss'))!}</#if>" class="short" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" style="width:120px;"/> 起</span>
					</span>
				</td>
				<td><em style="margin-left:6px;"></em><span class="td_key">获取截止时间</span>
					<span class="td_val">
						<input class="input_radio1" id="rd5" type="radio" name="saleEndDateRadio2" value="-1" <#if !(couponInfo.saleEndDate)?? >checked</#if>/><label for="rd5">不限</label>
						<input class="input_radio1" id="rd6" type="radio" name="saleEndDateRadio2" value="1" <#if (couponInfo.saleEndDate)??>checked</#if>/><label for="rd6">限制</label>
						<span class="show_val_wrap1" <#if !(couponInfo.saleEndDate)??>style="display:none;"</#if>> 至 <input class="show_val1" type="text" name="saleEndDate" value="<#if (couponInfo.saleEndDate)??>${(couponInfo.saleEndDate?string('yyyy-MM-dd HH:mm:ss'))!}</#if>" class="short" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" style="width:120px;"/> 止</span>
					</span>
				</td>
			</tr>
			<tr>
				<td colspan='2' class="ltd"><em style="margin-left:6px;"></em><span class="td_key">支付超时</span><span class="td_val">拍下
					<select type="select" name="limitPayTime">
						<#list limitPayTimeList as lpt>
						    <option <#if couponInfo?? && lpt == couponInfo.limitPayTime>selected</#if> value="${lpt}">${lpt}分钟</option>
						</#list>
					</select>
					未支付，系统自动关闭交易，关闭交易后，电子券
					<select type="select" name="allowContinueSale">
					    <option <#if couponInfo?? && ( !(couponInfo.allowContinueSale)?? || couponInfo.allowContinueSale==true) >selected</#if> value="1">可继续销售</option>
					    <option <#if couponInfo?? && (couponInfo.allowContinueSale?? && couponInfo.allowContinueSale == false) >selected</#if> value="0">不可继续销售</option>
					</select>
				</span></td>
			</tr>
		</table>
	</div>
	<#if applicableShops?? >
	<div class="tb_wrap">
		<table class="tb_main">
			<div class="tb_title">适用商户</div>
			<#list applicableShops as shop>
				<#if shop_index % 2==0>
					<tr <#if  (((shop_index)/2)?int)%2==1>class="tr_bg"</#if>>
				</#if>
					<td class="std"><input type="checkbox" name="scope" id="user_cb${shop_index}" value="${shop.id}" <#if (couponInfo.scope)?? && couponInfo.scope?seq_contains(shop.id)>checked=true</#if>/><label for="user_cb${shop_index}" class="td_val">${shop.name}</label></td>
				<#if shop_index % 2==1 || (shop_index+1)==applicableShops?size>
					</tr>
				</#if>
			</#list>
		</table>
	</div>
	</#if>
	<#if  tagMaps >
	<div class="tb_wrap">
		<table class="tb_main">
			<div class="tb_title">电子券标签</div>
			<tr >
			<#list tagMaps?keys as key>
				<td   colspan='2'>
				    <em>*</em>
				    <span class="td_key">${(key)!}</span>
					<span >
					    <#list tagMaps?values[key_index] as tag>
					         <input  type="checkbox" name="tag"  value="${(tag.name)!}" id="label_cb${tag_index}_${key_index}" 
					         		<#if (couponInfo.tag?index_of(tag.name)>-1)! >checked</#if>   ><label for="label_cb${tag_index}_${key_index}" class="td_val">${(tag.name)!}</label>
					    </#list>
					</span>
				</td>
			</#list>
			</tr>
		</table>
	</div>
	</#if>
	<div class="btn_wrap"><button class="add_seller_btn">保存<#if couponInfoType == 'NORMAL'>电子券<#else>套票</#if></button><span>或</span><a href="${base}/coupon/couponInfo/list" class="cancel_a">取消</a></div>
	</form>
</div>
    <#if (status == "faild")!>
        <div class="pop_hint pop_hint3">${msg}</div>
    </#if>
    <#if (status == "success")!>
        <div class="pop_hint pop_hint2">${msg}</div>
    </#if>

<@m.page_footer />