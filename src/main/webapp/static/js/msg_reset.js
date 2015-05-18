$(function(){
	$('.pop_div').hide();
	var re_psw=/^[A-Za-z0-9]{6,32}$/;
	var input_box=$('.input_box'),flag1=false,flag2=false,flag3=false,flag4=false;
	$('.set_psw_btn').on('click',function(){
		input_box.each(function(){
			var self=$(this);
			if (self.val()=='') {
				flag1=true; 
			}else if (!re_psw.test(self.val())) {
				flag3=true;
			};
		})
		if ($('.new_psw').val() != $('.confirm_psw').val()) {
			flag2=true;
		};
		
		if ($('.new_psw').val() == $('.old_psw').val()) {
			flag4=true;
		}
		
		if (flag1) {
			taberr()
			Showerror('请填写完整原密码、新密码和确认新密码！')
			flag1=false; 
		}else if (flag2){
			taberr()
			Showerror('新密码与确认密码输入不一致！')
			flag2=false;
		}else if (flag3){
			taberr()
			Showerror('密码长度为6-32位字符，须同时包含字母和数字！')
			flag3=false;
		}else if (flag4){
			taberr()
			Showerror('新密码与原密码相同，请重新修改！')
			flag4=false;
		}else{
			$('.pop_div').removeClass('pop_error').addClass('pop_ok')
			$('#resetPwdForm').submit();
		};
	})
})
function Showerror(txt){
	$('.pop_div').text(txt).fadeIn(500);
	setTimeout(function(){
		$('.pop_div').fadeOut(500);
	},2000)
}
function taberr(){
	$('.pop_div').addClass('pop_error').removeClass('pop_ok')
}
$(function(){
	var CODE,STATUS;
	$('.pop_a').on('click',function(){
		var txt,
			self=$(this),
			refundForm = $('#refundForm'),
			pb_main = $('.pb_main')
		$('.pb_main_desc').show();
		$('#description').val('');
		if (self.data('status')=='ACCEPTED') {
			$('.pb_main_desc').hide();
			pb_main.text('确认通过该退款申请？')
		}else if (self.data('status')=='REJECTED') {
			pb_main.text('确认拒绝该退款申请？')
		}else if (self.data('status')=='SUCCESS') {
			pb_main.text('确认该退款已成功？')
		}else if (self.data('status')=='FAILED') {
			pb_main.text('确认该退款已失败？')
		};
		refundForm.attr('action',self.attr('href'))
		return false
	})
	
	$('.pb_btn_s').on('click',function(){
		var self=$(this);
		$('#code').val(CODE);
		$('#status').val(STATUS);
		$('form').submit();
	})
	$('.check_li').click(function(){		
		var cb = $('.check_li'),
			val = [];
		cb.each(function(){
			var self = $(this)
			if(self.is(':checked')){
				val.push(self.val())
			}
		})
		location.href=CONTEXT+'/refund/list/?status='+val.join(',')
	})
	$('.reset_check_a').click(function(){
		var self = $(this),
			wrap = self.parent()
		wrap.find('.lb_cb_on').each(function(){
			var lb =$(this) 
			lb.prev().removeAttr('checked')
			lb.removeClass('lb_cb lb_cb_on').addClass('lb_cb')
			location.href=CONTEXT+'/refund/list'
		})
	})
})
