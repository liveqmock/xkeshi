$(function(){
	$(".cont_detail_a").fancybox({
		'content':CONT_DETAIL
	})
	$('.add_coupon_a').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_bind_coupon')
		pop.css({'top':top+20, 'left':left-247+self.width()}).show()
	})
	$('.pop_coupon_form').submit(function(){
		var self = $(this),
			rd = self.find('input[type="radio"]:checked')
		if(!rd[0]){
			alert('请先勾选')
			return false
		}
		if(rd.attr('id')=='rd2'){
			location.href=$('.coupon_add_label').data('url')
			return false
		}
	})
})