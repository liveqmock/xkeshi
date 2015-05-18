$(function(){
	$('.pb_close').click(function(){
		$('.pb_rel_merchant').hide();
	});
	$('.rel_merchant').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pb_rel_merchant');
		pop.css({'top':top+30, 'left':left-243+self.width()}).show()
		var shopName  = "<br>";
		 $('.shopIds').empty();
		 if($("input[type=checkbox][name=shopIds]:checked").length  ==0 ){
			 $('.pb_btn_shop').attr('disabled',true);
		 }else{
			 $('.pb_btn_shop').attr('disabled',false);
			 $("input[type=checkbox][name=shopIds]:checked").each(function(i){
				   $('.shopIds').append("<input type='hidden' name='shopIds' value="+$(this).val()+">");
				      shopName  += (i+1)+". "+$(this).data('name')+"<br>";
			 })
			 $('.shopIds').append(shopName);
		 }
	});
	$('.filter_a').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_filter');
		pop.css({'top':top+30, 'left':left-243+self.width()}).show();
	});
	
	$('.house_icon').hide();
	var otr=$('.tb_main').find('tr');
	otr.mouseover(function(){
		otr.find('.house_icon').hide();
		$(this).find('.house_icon').show();
	})
	otr.mouseout(function(){
		otr.find('.house_icon').hide();
	})
	check_all_none($('#checkAll'), $('.checkbox_shop'))
})
