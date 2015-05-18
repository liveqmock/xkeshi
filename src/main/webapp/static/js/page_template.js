$(function(){
	$('.pb_cancel_a').click(function(){
		var self = $(this);
		self.parents('.pb').hide();
	})

	$(".del_a").bind("click",function(){
 	var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.pop_del');
		pop.css({'top':top+20, 'left':left-247+self.width()}).show();
		$(".pb_main").html($(this).data("title"));
		document.pageTemplateForm.action = document.pageTemplateForm.action+$(this).data("id");
	})
})