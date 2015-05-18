<#import "/macro.ftl" as m>

<@m.page_header title="上传图片" selected='album' css="admin_frame|seller_info|add_photo"/>
<script>
	var pic = '${pic}';
	$(function(){
		var re_photo=/^png$|jpg$|gif$|bmp$|PNG$|JPG$|GIF$|BMP$/;
		$("input[name='tag'][value='${(pic.tag)!}']").attr("checked",true);
		$('.add_photo_btn').click(function(){
			var originalName = $('.name_input').val();
			var file = $('.photo_input').val();
			var str=file.slice(-3);
			if(""==originalName) {
				alert("图片名称不能为空！");
				return false;
			}
			if(file!='' && !re_photo.test(str)){
				alert("图片格式不正确！");
				return false;				
			}
			if(pic!='' && pic!=null) {
			  jQuery("form").attr("action","${base}/shop/album/edit/${(pic.id)!}?shopId=${shopId}").submit();
			}else {
				if(""==file) {
					alert("请先上传文件再保存！");
					return false;
				}
			  jQuery("form").attr("action","${base}/shop/album/add/${shopId}").submit();
			}
		})
	})
</script>

<div class="rwrap">
	<form method="POST" enctype="multipart/form-data">
	    <input type="hidden" name="submissionToken"  value="${submissionToken}"/>
		<p class="r_title"><a class="back_a" href="${base}/shop/${shopId}/album"></a>上传图片</p>
		<div class="tb_wrap">
			<table class="tb_main">
				<tr>
					<td><em>&nbsp;</em><span class="td_key">名称</span><span class="td_val"><input type="text" name="description" class="name_input" 
					<#if pic && pic.description>
						value="${pic.description}"
					</#if>
					</span></td>
				</tr>
				<tr class="tr_bg">
					<td><em>&nbsp;</em><span class="td_key">类别</span>
					<span class="td_val">
					<span class="rd_span">
					<input type="radio"  name="tag" value="菜" id="rd1" <#if (pic.tag)! && pic.tag =='菜'>checked="true"</#if> ><label for="rd1">菜</label></span><span class="rd_span">
					<input type="radio"  name="tag" value="环境" id="rd2" <#if (pic.tag)! && pic.tag =='环境'>checked="true"</#if> ><label for="rd2">环境</label></span><span class="rd_span">
					<input type="radio"  name="tag" value="其他" id="rd3" <#if (pic.tag)! && pic.tag =='其他'>checked="true"</#if>><label for="rd3">其他</label>
					</span></span></td>
				</tr>
				<#if pic>
					<tr>
						<td class="img_td"><em>&nbsp;</em><span class="td_key">文件</span><span class="td_val"><img src="${image_base}${pic}!xpos.merchant.home.avatar" class="item_img"></span></td>
					</tr>
				</#if>
				<tr class="tr_bg">
					<td><em>&nbsp;</em><span class="td_key">文件</span><span class="td_val"><input type="file" name="albumFile" class="photo_input" title="建议400px*400px"></span></td>
				</tr>
			</table>
		</div>
		<div class="btn_wrap"><button type="button" class="green_btn_l add_photo_btn">保存</button><span class="btn_split">或</span><a href="${base}/shop/${shopId}/album" class="cancel_a">取消</a></div>
	</form>
	
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>	
</div>
<@m.page_footer />