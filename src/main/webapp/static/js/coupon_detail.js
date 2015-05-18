$(function(){
	$(".cont_detail_a").fancybox({
		'content':CONT_DETAIL
	})
	$('.stcb').click(function(){
		var cb = $('.stcb'),
			val = []
		cb.each(function(){
			var self = $(this)
			if(self.is(':checked')){
				val.push(self.val())
			}
		})
		location.href=CONTEXT+"/coupon/statistic/"+$('.coupon_id').text()+'?status='+val.join(',')
	})
	$('.pop_coupon_form').submit(function(){
		var self = $(this),
			rd = self.find('input[type="radio"]:checked')
		if(!rd[0]){
			alert('请先勾选')
			return false
		}
		if(rd.attr('id')=='rd1' || rd.attr('id')=='rd3'){
			location.href=rd.data('url')
			return false
		}
	})	
})
