$(function(){
	$("#sequence").blur(function(){
		sequence_function($(this).val());
	})
	$(".td_sequence").blur(function(){
		sequence_function($(this).val());
	})
})
var sequence_function = function(sequence){
	if (isNaN(sequence)) {
		$('.add_seller_btn').attr("disabled","disabled");
		alert("排序必须为数字");
	}else{
		$('.add_seller_btn').attr("disabled",false);
	}
}