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
 	
		var msg  = "确认删除" ;
		var name  = $(this).data("categoryname") ;
		if ($(this).data("categorytype") == "big") {
			msg += "大类:<span class='pb_cate_name'>"+name+"</span>";
		}else{
			msg += "小类:<span class='pb_cate_name'>"+name+"</span>";
		}
		$(".pb_main").html(msg);
		document.sellerCategoryForm.action= BASE+"/category/delete/"+$(this).data("category");
	})

})