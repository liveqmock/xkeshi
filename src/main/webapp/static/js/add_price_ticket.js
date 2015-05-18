$(function(){
	check_all_none($('#mc_cb_all'), $('.mc_cb'))
	$('.up_img_btn').click(function(){
		var self = $(this),
			file = self.parents('.td_val').find('.file_ip')
		file.click()
	})
	$('.file_ip').live('change', function(){
		var self = $(this)
		self.parents('.td_val').find('.up_img_hint').text(self.val())
	})
})