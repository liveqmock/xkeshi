$(function(){
	//分页
	$(".page_btn_top").click(pageHref);
	$(".page_btn_back").click(pageHref);
	$(".page_btn_top_first").click(pageHref);
	$(".page_btn_back_last").click(pageHref);
	function pageHref(){
		window.location.href = "?pageNumber="+ $(this).data("page")
        						+"&key="+$(".search_input").val().trim()
        						;
	};
	
	$('.filter_a').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.pop_filter')
		pop.css({'top':top+40, 'left':left-247+self.width()}).show()
	})
	$('.pb_cancel_a, .pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
})