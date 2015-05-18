<#import "/macro.ftl" as m>
<@m.page_header selected='activity' css="seller_info|add_seller_new" js="My97DatePicker/WdatePicker" title="创建/编辑 活动"/>
<script>
	var IMG_BUCKET = '${img_bucket}',
		IMG_PATH = '${image_base}',
		UP_IMG_TYPE = 2,
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
	<p class="r_title"><a class="back_a" href="${base}/activity/list"></a>独立活动<#if (activity.eid)??>编辑<#else>创建</#if></p>
	<form action="${base}/activity/processCreation" method="POST" enctype="multipart/form-data">
	<div class="tb_wrap">
		<input type="hidden" name="id" value="${(activity.eid)!}" />
		<input type="hidden" name="couponInfoEid" value="${couponInfoEid!}" />
		<div class="tb_title">基本信息</div>
		<table class="tb_main">
			<tr>
				<td class="ltd" colspan='2'><em>*</em><span class="td_key">名称</span><span class="td_val"><input name="name" class="ip1" type="text" value="${(activity.name)!}"></span></td>
			</tr>
			<tr class="tr_bg">
				<td class="std" colspan='2'><em>*</em><span class="td_key">简介</span><span class="td_val"><textarea class="td_text" name="intro">${(activity.intro)!}</textarea></span></td>
			</tr>
			<tr>
				<td class="std"><em>*</em><span class="td_key">详情页顶部大图</span><span class="td_val"><input type="file" name="picFile" class="file_ip" /></span>
				<#if (activity.pic)??>
					<img src="${image_base}/${activity.pic}" style="width:400px;margin-top:20px;" />
				</#if>
				</td>
				<td class="std"><em class="v9">*</em><span class="td_key">列表用缩略图</span><span class="td_val"><input type="file" name="thumbFile" class="file_ip v9" /></span>
				<#if (activity.thumb)??>
					<img src="${image_base}${activity.thumb}" style="width:400px;margin-top:20px;" />
				</#if>
				</td>
			</tr>
			<tr class="tr_bg">
				<td colspan='2'><em>*</em><span class="td_key">有效期</span><span class="td_val"><input type="text" name="startDate" value="${(activity.startDate?string("yyyy-MM-dd HH:mm:ss"))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" style="width:120px;"/> - <input type="text" name="endDate" value="${(activity.endDate?string("yyyy-MM-dd HH:mm:ss"))!}" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" style="width:120px;"/></span></td>
			</tr>
			<tr>
				<td class="std" colspan='2'><em>*</em><span class="td_key">描述详情</span><script id="description" name="description" type="text/plain" style="width:740px;height:150px;display:inline-block;vertical-align:middle;border: 1px solid #C0C0C0;">${(activity.description)!}</script></td>
			</tr>
			<tr class="tr_bg">
				<td class="std" colspan='2'><em>&nbsp;</em><span class="td_key">备注</span><span class="td_val"><textarea class="td_text" name="remark">${(activity.remark)!}</textarea></span></td>
			</tr>
			<tr>
				<td colspan='2' class="side_link">
					<p><a href="javascript:" class="phone_a">手机版预览</a></p>
					<p><a href="javascript:" class="pc_a">网络版预览</a></p>
				</td>
			</tr>
		</table>
	</div>
	<div class="btn_wrap"><button class="add_seller_btn">保存活动</button><span>或</span><a href="${base}/activity/list" class="cancel_a">取消</a></div>
	</form>
</div>
<@m.page_footer />