$(function(){
/*	*/
	var btn_add=$('.btn_add');
	btn_add.live('click',function(){
		var td_wrap_add='<div class="td_wrap td_wraps"><p class="p_bg">充值金额<span class="input_wrap"><input type="text" class="input_box"><em>元</em></span>赠送<span class="td_val"><select class="select select1" selected="selected"><option>金额</option><option>金卡(9折)</option><option>金卡(7折)</option></select></span> <span class="input_wrap"><input type="text" class="input_box input_boxs"><em>元</em></span></p><a href="javascript:;" class="del_add">删除</a></div>';
		$(this).parent().before(td_wrap_add);
	})
	$('.btn_submit').on('click',function(){
		$('.pb_submit').show();
	})
	$('.del_add').live('click',function(){
		$(this).parents('.td_wrap').remove();
	})
	check_all_none($('#checkAll'), $('.mct_cb'))
    $('form').submit(function(){
        if(!$('.mct_cb:checked')[0]){
            alert('请至少选择一个商户')
            return false
        }
    })
})