$(function(){
	$(".td_input").hide();
	$(".member_detail_edit_rt").hide();
	//开启编辑
	$(".edit_a").click(function(){
		$(".member_detail_edit_rt").show();
		$(".member_detail_edit").hide();
		$(".td_val").hide();
		$(".td_source_val").hide();
		$(this).hide();
		$(".td_input").show();
		$(".member_title").text("会员编辑");
	})
	$(".delete_a").click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_del');
		pop.css({'top':top+50, 'left':left-360+self.width() , 'width':320 }).show();
	});
	$('.pb_cancel_btn').click(function(){
		var self = $(this);
		self.parents('.pb').hide();
	})
})
