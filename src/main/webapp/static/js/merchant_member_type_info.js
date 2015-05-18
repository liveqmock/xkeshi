$(function(){
	$('.new_a').click(function(){
		document.typeForm.action="/member/merchant/type/add"; 
		$(".pb_title_sp").text("添加会员类型");
	//	$(".pb_item_title").text("名称");
		$("#set_cb1").attr("checked",false);
		$("#set_cb1").removeAttr("disabled");
		$(".pb_discount").show();
		$(".pb_item_discount_title").text("折扣");
		//$(".pb_btn").text("提交");
		$("#typeId").val("");
		$("#nameId").val("");
		$("#nameId").show();
		$("#discountId").val(10);
		$(".pb_defaulted").show();
		$("#defaulted_id").val();
		$(".lab_defaulted").removeClass("lb_cb_on");
		$(".lab_defaulted").addClass("lb_cb");
		$(".show_cover_picture").hide();
		$(".pb_card").show();
		showTypeForm($(this));
		$(".pop_del").hide();
		if($('#discountId').val()!==''||$('#discountId').val()!==null){
			$('.pb_discount .phTips').hide();
		}
		
	})
	$('.pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	$(".updateType").click(function(){
		document.typeForm.action="/member/merchant/type/update"; 
		$(".pb_title_sp").text("修改会员类型");
	//	$(".pb_item_title").text("名称");
		$(".pb_defaulted").show();
		$(".pb_item_discount_title").text("折扣");
		if ( $(this).data('defaulted') == true) {
			$("#set_cb1").attr("checked",true);
			$("#set_cb1").attr("disabled",true);
			$(".lab_defaulted").removeClass("lb_cb");
			$(".lab_defaulted").addClass("lb_cb_on");
		}else{
			$("#set_cb1").attr("checked",false);
			$("#set_cb1").removeAttr("disabled");
			$(".lab_defaulted").removeClass("lb_cb_on");
			$(".lab_defaulted").addClass("lb_cb");
		}
		$("#defaulted_id").val()
		$(".pb_discount").show();
		$(".pb_card").show();
		$(".pb_btn").text("修改");
		$("#typeId").val($(this).data("id"));
		$("#nameId").val($(this).data("name"));
		$("#nameId").show();
		$("#discountId").val($(this).data("discount"));
		//$("#memberTemplateId").val($(this).data("memberTempleateId"));
		if ($(this).data('cover_picture').length > 0) {
			var _self  =  $(".show_cover_picture") ;
			_self.attr('href',IMAGE_BASE+$(this).data('cover_picture'));
			_self.show();
		} else{
			 $(".show_cover_picture") .hide();
		}
		showTypeForm($('.updateType'));
		$(".pop_del").hide();
		if($('#discountId').val()!==''||$('#discountId').val()!==null){
			$('.pb_discount .phTips').hide();
		}
		if($('#nameId').val()!==''||$('#nameId').val()!==null){
			$('.pb_nameId .phTips').hide();
		}
	})
	$('#typeForm').submit(function(){
		$(this).find('input[type=checkbox]').removeAttr('disabled')
	})
	
	
})

function showTypeForm(value){
	var self = value,
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop;
	pop = $('.add_opt');
	$('.add_opt, .edit_opt, .add_pos, .del_pos').hide();
	pop.css({'top':top+16, 'left':left-243+self.width()}).show();
}

function saveOp() {
	var discount = $("#discountId").val().trim(),
		dc_arr = discount.toString().split('.')
	if ($.trim($("#nameId").val()).length == 0) {
		alert("会员类型不能为空");
		return false;
	} else if (!discount) {
		alert("会员折扣不能为空");
		return false;
	} else if(isNaN(discount)||(+discount) <0 || (+discount)>10 || (dc_arr.length>1 && dc_arr[1].length>3)){
		alert("会员折扣为0~10，最多精确到小数点后3位");
		return false;
	}
	$('#typeForm').submit();
}
 
