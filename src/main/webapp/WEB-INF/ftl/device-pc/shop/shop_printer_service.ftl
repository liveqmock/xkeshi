<#import "/macro.ftl" as m>
<@m.page_header selected='setting' subselected= 'shop_printer_service' css='seller_list_new|set_mime' />
<script type="text/javascript">
    $(document).ready(function(){
    var checked = $(".required:checked").val();
    		if(checked == '1'){
    			$(".import").css("display","block") ;
    			$(".shade").css("display","none");   		 			
    		}
    		else{
    			$(".import").css("display","none") ;
    			$(".shade").css("display","block");
    		}
    $('form').submit(function(){
    var textnum = $('.isint').val().length;
    if(textnum > 50){
    alert('端口请输入小于50个字符');
    return false;
    }else{
    return true
    }
    })
   	$(".required").click(function(){
    		var checked = $(".required:checked").val();
    		if(checked == '1'){
    			$(".import").css("display","block") ;
    			$(".shade").css("display","none");   		 			
    		}
    		else{
    			$(".import").css("display","none") ;
    			$(".shade").css("display","block");
    		} 	
    	})
   		var isChecked = $('#use2').attr('checked'); 
	    $(".pb_btn").click(function(){
	    	if (isChecked) {
	    		$('form').submit();
	    		return false;
	    	}else{
				var ip = $(".limit_ip").val();
	    	    var re = /^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
	    	    var int = $(".isint").val();
	    	    var isint= /^(-|\+)?\d+$/;
	    	    if (!re.test(ip)){
				    alert("请输入正确的IP地址");
				    return false;
		        }	
		        if(!isint.test(int)){
			    	alert("请输入正确的端口");
			    	return false;
			    }
	    	}
    	}) 
	})
</script>

<div class="rwrap">
	<div class="r_title" style="overflow:hidden;">打印服务设置</div>
	<form action="${base}/shop/shop_printer_service" method="POST">	
	<table class="tb_main">		
		<tr class="service_tr">
			<td class="ltd bmt">
				<span class="td_key">是否启用打印服务器（电脑）</span>
			<span class=" td_val td_input">
				   <input type="radio" id="use1" name="printerEnable" class="input-l required" data-msg="是否"
				   		  value="1" <#if (shop.printerEnable)! == true>  checked="checked" </#if> /><label for="use1">启用</label>
				   <input type="radio" id="use2" name="printerEnable" class="input-l required" data-msg="是否"
				          value="0"  <#if (shop.printerEnable)! == false>  checked="checked" </#if> /><label for="use2">不启用</label>
				   <label  class="required_error"></label>
				</span>
			</td>
		</tr>
		<tr class="service_tr import tr_bg">
			<td class="ltd ntmt">
				<span class="td_key">打印服务器（电脑）配置</span>
				<span class="td_val p3">IP</span>
				<input type="text" name="printerIp" value="${(shop.printerIp)!}" class="pb_main_input limit_ip">			
				<span class="td_val p3">打印端口</span>
				<input type="text" name="printerPort" value="${(shop.printerPort)!}" class="pb_main_input isint">			
			</td>
		</tr>	
		<tr class="service_tr shade tr_bg">
			<td class="ltd ntmt">					
				<span class="td_key">打印服务器（电脑）配置</span>
				<span class="td_val">IP</span>
				<input type="text" class="pb_main_input limit_ip service_tr_div_readonly" disabled>
				<span class="td_val">打印端口</span>
				<input type="text" class="pb_main_input isint service_tr_div_readonly" disabled>
			</td>
		</tr>	
	</table>
	<div class="btn_bottom">
	<button type="submit" class="pb_btn pb_btn_bottom">确定</button>
	</div>
</form>
</div>
	<#if (status == "failed")!>
		<div class="pop_hint pop_hint3">${msg}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${msg}</div>
	</#if>
</div>
<@m.page_footer />