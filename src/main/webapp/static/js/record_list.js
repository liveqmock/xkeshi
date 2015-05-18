$(function(){
	$('.tb_main').find('.tr').click(function(){
		location.href = $(this).data('url')
	})
	$('.f_form').submit(function(){
		var self = $(this)
		if(self.find('#set_time2').val()!='' && self.find('#set_time1').val()>self.find('#set_time2').val()){
			alert('起始时间不能超过截止时间')
			return false
		}
	})
})