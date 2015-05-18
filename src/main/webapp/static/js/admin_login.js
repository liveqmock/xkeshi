
$(window).load(function(){
	 
	var self=$('.admin_login_btn0');
	if ($('.code_wrap').data('count') >=3) {
		self.parent().parent().addClass('admin_login_wrap2');
		$('.code_wrap').show();
	}else{
		$('.admin_login_wrap2').removeClass('admin_login_wrap2');
		$('.code_wrap').hide();
	}

	$('.admin_login_btn0').click(function(){
		var self=$(this);
		if(self.hasClass('admin_login_btn')){
			$('form').submit();
		}else{
			return false
		}	
	})

	$('.inputs').each(function(){
		var self=$(this);
		self.bind('paste cut keydown keyup focus blur',function(){
			
			setTimeout(function() {
				self.trigger('keyup');
			},300)
		})	
		self.bind('keyup',function(){
			var self=$(this);
			Keyup()
		})
	})
})
function Keyup(){
	if ($('.code_wrap').data('count') >=3) {
		$('.admin_login_btn').parent().parent().addClass('admin_login_wrap2');
		$('.code_wrap').show();
		if ($('#account').val()=='' || $('#password').val()==''|| $('#auth_code').val()=='') {
			$('.admin_login_btn').removeClass('admin_login_btn');
			$('.admin_login_btn0').attr('disabled',true);
			return false
		}else{
			$('.admin_login_btn0').addClass('admin_login_btn').attr('disabled',false);
		}
	}else{
		$('.admin_login_wrap2').removeClass('admin_login_wrap2');
		$('.code_wrap').hide();
		if ($('#account').val()=='' || $('#password').val()=='') {
			$('.admin_login_btn').removeClass('admin_login_btn');
			$('.admin_login_btn0').attr('disabled',true);
		}else{
			$('.admin_login_btn0').addClass('admin_login_btn').attr('disabled',false);
		}
	}
}