$(function(){
	$('.plus').click(function(){
		var self = $(this),
			input = self.parent().find('.num_input'),
			val = $.trim(input.val()),
			num = val==''?0:parseInt(val)
		input.val(num+1)
	})
	$('.minus').click(function(){
		var self = $(this),
			input = self.parent().find('.num_input'),
			val = $.trim(input.val()),
			num = val==''?0:parseInt(val)
		if(num==0){
			return
		}
		input.val(num-1)
	})
	$(".cont_detail_a").fancybox({
		'content':CONT_DETAIL
	})
	$('.coupon_num_form').submit(function(){
		var self = $(this)
		self.find('.pcp_item').each(function(){
			var item = $(this),
				input = item.find('.num_input'),
				num = input.val(),
				cb = item.find('.pb_cb')
			if(!cb.is(':checked') || !num || num=='0'){
				item.remove()
			}
		})
		//return false
	})
	$('.pop_contact').find('.pop_coupon_form').submit(function(){
		var self = $(this),
			rd = self.find('input[type="radio"]:checked')
		if(!rd[0]){
			alert('请先勾选')
			return false
		}
		if(rd.attr('id')=='pb_rd1' || rd.attr('id')=='pb_rd3'){
			location.href=rd.data('url')
			return false
		}
	})
	$('.status_edit').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_status_edit')
		pop.css({'top':top+20, 'left':left-247+self.width()}).show()
	})
})