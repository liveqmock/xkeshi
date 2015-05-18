$(function(){
	$('.account_a').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop
		if(text=='添加商户账号'){
			pop = $('.add_acct')
		}else if(text=='修改商户账号'){
			pop = $('.edit_acct')
		}else if(text=='设置短信和支付信息') {
			pop = $('.edit_shopInfo')
		}
		$('.add_acct, .edit_acct, .edit_shopInfo').hide()
		pop.css({'top':top+16, 'left':left-243+self.width()}).show()
	})
	
	$('.pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	
	$('.pb_s_del').click(function(){
		$.ajax({
			url:'/shop/account/delete/'+shopId,
			type:'delete',
			dataType :'json',
			success:function(data){
				if(data.status == 'success')
					location.reload();
			}
		});
	})

})


function save() {
	if ( $("#username").val()=="" || $("#password2").val()=="" || $("#password").val()=="") {
		alert("请将信息填写完整");
		return false;
	}
	if($("#password2").val()!=$("#password").val()){
		alert("两次密码不一致");
		return false;
	}
	 $('form').submit();
}
function edit() {
	if ( $("#username2").val()=="" || $("#password4").val()=="" || $("#password3").val()=="") {
		alert("请将信息填写完整");
		return false;
	}
	if($("#password4").val()!=$("#password3").val()){
		alert("两次密码不一致");
		return false;
	}
	$('form2').submit();
}
