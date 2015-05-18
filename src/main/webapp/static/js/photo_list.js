$(function(){
	$('.pic_del').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.pop_del')
			pop.css({'top':top+40, 'left':left-247+self.width()}).show();
			var picname = $(this).parent().parent().find("p").eq(0).html();
			var albumid =  $(this).data("albumid");
			$('.pb_cate_name').html(picname);
			if($(this).data('shopid')==null||$(this).data('shopid')=='') {
				document.pop_del_form.action = "/album/delete/"+albumid;
			}else {
				document.pop_del_form.action = "/shop/delete/"+albumid+"/"+$(this).data('shopid');
			}
	})
	$('.pb_cancel_a, .pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	$('.filter_a').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.pop_filter')
		pop.css({'top':top+40, 'left':left-247+self.width()}).show()
	})
	
	$('.pb_btn_s').click(function(){
		 $('#pop_del_form').submit();
	})
	$('.check_li_c').click(function(){
		 $("#formid").submit();
	})
	
})