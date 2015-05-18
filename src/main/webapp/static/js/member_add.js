$(function() {
	$('.form_submit').click(function(){
		var re_num=/^[0-9]*$/,re_text=/^[\u4e00-\u9fa5a-zA-Z]+$/;
		$('input').each(function(){
			var self=$(this);
			if (self.data('type')=='number') {
				if(!re_num.test(self.val())){
					alert('请输入纯数字');
					return false;
				};
			}
		})
	})
})