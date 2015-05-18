<#import "/macro.ftl" as m>
<@m.page_header selected='setting' subselected= 'shop_printer' js='My97DatePicker/WdatePicker' css='seller_list_new|set_mime' />
<script type="text/javascript">
	$(document).ready(function(){

        $(".limit_name").focus(function(){
        	$(".hint_name").css("display","none");
        });
        $(".limit_ip").focus(function(){
        	$(".hint_ip").css("display","none");
        });
        $(".limit_ps").focus(function(){
        	$(".hint_ps").css("display","none");
        });
         $(".pb_close").click(function(){
        	$(".pb_item_hint").css("display","none");
        });
   		$(".pop_a").click(function(){
	    	var fill_name = $(this).parent().siblings(".name_mime").text();		
		    var fill_ip = $(this).parent().siblings(".ip").text();
		    var fill_ps = $(this).parent().siblings(".ps").text();
		    var fill_id = $(this).parent().siblings(".id").val();
		    $(".limit_name").val(fill_name);
		    $(".limit_ip").val(fill_ip);
		    $(".limit_ps").val(fill_ps);
		    $(".limit_id").val(fill_id);
        })     
		$(".pb_btn").click(function(){
			var wrap = $(this).parents('.pb'),
				name = wrap.find(".limit_name").val(),		
		    	ip = wrap.find(".limit_ip").val(),
		    	ps = wrap.find(".limit_ps").val()	    
		    var re = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
			if(name.length == "" || name.length > 8){
				$(".hint_name").css("display","block");
				return false;
			}
			if (!re.test(ip)){
				$(".hint_ip").css("display","block");
				return false;
			}
			if (ps.length > 200) {
			$(".hint_ps").css("display","block");
			return false;
		    };
			return true;	
		});
	

	})
	
	
</script>
<div class="rwrap">
		<div class="r_title" style="overflow:hidden;"><span class="fl">
			打印档口设置</span>
			<div class="search_wrap">
				<a href="javascript:" class="pop_a new_a" data-pop="pop_filter" ></a>
			</div>
		</div>
		<table class="tb_main tb_pointer">
		<#if !pager.list?? || pager.list?size==0>
			<div class="new_hint_wrap"><div class="new_hint_bg"></div>您还没有创建打印档口，点击右上角<img src="${static_base}/css/img/admin_frame/new_a.png">按钮创建新的打印档口吧</div>
		<#else>
			<tr class="th tr_bg">
				<td class="name_mime">打印档口名称</td>
				<td class="ip">IP</td>
				<td class="status">状态</td>
				<td class="ps ps_w">备注</td>
				<td class="operate">操作</td>
			</tr>
			<#list pager.list as shopPrinter>
				<tr <#if shopPrinter_index%2!=0>class="tr_bg"</#if>>
					<input type="hidden" class="id" name="id" value="${(shopPrinter.id)!}">
					<td class="name_mime">${(shopPrinter.name)!}</td>
					<td class="ip">${(shopPrinter.ip)!}</td>
					<td class="status"><#if (shopPrinter.enable)! == true>正常<#else>暂停</#if></td>
					<td class="ps">${(shopPrinter.comment)!}</td>
					<td class="operate">
						<#if (shopPrinter.enable)! == true>
							<a href="${base}/shop/shop_printer/${shopPrinter.id}/update?enable=false" class="b_a">暂停</a>
						<#else>
							<a href="${base}/shop/shop_printer/${shopPrinter.id}/update?enable=true" class="b_a">启用</a>
						</#if>						
						<a href="javascript:" class="b_a pop_a" data-pop="pop_edit">修改</a>
						<a href="javascript:" class="del_attribute b_a pop_a" data-pop="pop_del" data-para="${base}/shop/shop_printer/${shopPrinter.id}/delete">删除</a></td>
					</td>
				</tr>
			</#list>
		</#if>
		</table>
		<div class="pb pop_filter">
			<form action="${base}/shop/shop_printer/add" method="POST">
				<input type="hidden"   value=""  id="defaulted_id">
				<input type="hidden" name="submissionToken" value="${submissionToken}"/>
				<div class="pb_title">
					<span class = "pb_title_sp">添加档口</span>
					</a>
				</div>
				<div class="pb_main">
					<div class="pb_item">
						<p class="pb_item_title">打印档口名称</p>
						<input type="text" class="pb_item_input f_input fi_2 limit_name" name="name" value=""/>
						<p class="pb_item_hint hint_name">打印档口名称不多于8个汉字</p>
					</div>
					<div class="pb_item">
						<p class="pb_item_title">IP</p>
						<input type="text" class="pb_item_input f_input fi_2 limit_ip" name="ip" value=""/>
						<p class="pb_item_hint hint_ip">请输入正确的IP地址</p>
					</div>
					<div class="pb_item">
						<p class="pb_item_title">备注</p>
						<textarea class=" pb_item_input f_input fi_2 limit_ps" name="comment"></textarea>
						<p class="pb_item_hint hint_ps">请输入200字以内文字</p>
					</div>
				</div>
				<button class="pb_btn pb_btn_s">保存</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
		<div class="pb pop_edit">
			<form action="${base}/shop/shop_printer/edit" method="POST">
				<input type="hidden" name="id" class="limit_id" value="">
				<div class="pb_title">
					<span class = "pb_title_sp">修改档口</span>
					
				</div>
				<div class="pb_main">
					<div class="pb_item">
						<p class="pb_item_title">档口打印名称</p>
						<input type="text" class="pb_item_input f_input fi_2 limit_name" name="name" value=""/>
						<p class="pb_item_hint hint_name">不多于8个汉字的打印档口名称</p>
					</div>
					<div class="pb_item">
						<p class="pb_item_title">IP</p>
						<input type="text" class="pb_item_input f_input fi_2 limit_ip" name="ip" value=""/>
						<p class="pb_item_hint hint_ip">请输入正确的IP地址</p>
					</div>
					<div class="pb_item">
						<p class="pb_item_title">备注</p>
						<textarea class=" pb_item_input f_input fi_2 limit_ps" name="comment"></textarea>
						<p class="pb_item_hint hint_ps">请输入200字以内文字</p>
					</div>
				</div>
				<button class="pb_btn pb_btn_s">保存</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
		<div class="pb pop_del">
			<form action="" method="post">
				<input type="hidden" name="_method" value="delete"/> 
				<div class="pb_title">删除</div>
				<div class="pb_main">确认删除打印档口?</div>
				<button type="submit" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
		</div>
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
		</div>
	</div>
</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<@m.page_footer />