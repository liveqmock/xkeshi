$(function(){
	$('.new_a').click(function(){
		document.typeForm.action = BASE+"/pager/category/add"; 
		$(".pb_title_sp").text("新增大类");
		$(".pb_parent").hide();
		$("#parentName").text("");
		$("#parentId").val(0);
		$("#typeId").val("");
		$("#nameId").val("");
		$("#discountId").val("");
		$("#methodId").val("post");
		showTypeForm($(this));
	})
	$('.pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	
	$('.new_category_a').click(function(){
		document.typeForm.action = BASE+"/pager/category/add"; 
		$(".pb_title_sp").text("新增小类");
		$("#parentName").text($(this).data("categoryname"));
		$("#parentId").val($(this).data("category"));
		$("#typeId").val("");
		$("#nameId").val("");
		$("#methodId").val("post");
		showTypeForm($('.new_a'));
	})
	
	$('.category_edit').click(function(){
		document.typeForm.action = BASE+"/pager/category/update"; 
		var name  =  $(this).data("categoryname") ;
		var msg = "编辑";
		if ($(this).data("categorytype") == "big") {
			msg += "大类:<span class='pb_cate_name'>"+name+"</span>";
			$(".pb_parent").hide();
			$("#parentName").text("");
			$("#parentId").val(0);
		}else{
			msg += "小类:<span class='pb_cate_name'>"+name+"</span>";
			$("#parentName").text($(this).data("categoryparentname"));
			$("#parentId").val( $(this).data("categoryparentid"));
		}
		$(".pb_title_sp").html(msg);
		$("#nameId").val(name);
		$("#categoryId").val($(this).data("category"));
		$("#typeId").val("");
		$("#sequnce").val($(this).data("sequnce"));
		$("#methodId").val("put");
		showTypeForm($('.new_a'));
	})
	
	
	$('.pb_cancel_a').click(function(){
		var self = $(this);
		self.parents('.pbdel').hide();
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
		document.sellerCategoryForm.action= BASE+"/pager/category/delete/"+$(this).data("category");
	})

})

var  showTypeForm  = function (value){
	var self = value,
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop;
	pop = $('.add_opt');
	$('.add_opt, .edit_opt, .add_pos, .del_pos').hide();
	pop.css({'top':top+16, 'left':left-323+self.width()}).show();
}

var saveOp  =  function (){
	 if ($("#nameId").val().trim().length == 0) {
			alert(" 名称不能为空");
			return false;
	 }else if(isNaN($("#sequnce").val())){
		    alert("序列必须为整数");
		    return false;
	 }
		 $('typeForm').submit();
}