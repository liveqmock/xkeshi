$(function(){
	seller_list_init()
	$('.add_many_user').click_drop($('.pop_add_many'),0,0,{'not_close':1, 'add_cover':1})
	$('.add_user').click_drop($('.pop_add'),0,0,{'not_close':1, 'add_cover':1})
	$('.pop_ir_btn').click_drop($('.pop_import_result'),0,0,{'not_close':1, 'add_cover':1})	
	$('.filter_pos_form').submit(function(){
		var self = $(this),
			key = $('.search_form').find('.list_search_input').val(),
			btime = $('.begin_time').val(),
			etime =$('.end_time').val() 
		if(btime && etime && btime>etime){
			alert('起始时间不能大于结束时间')
			return false
		}
		self.append('<input type="hidden" name="key" value="'+key+'">')
	})
	$('.search_reset_a').click(function(){
		$('.list_search_input').val()
		$('.list_search_btn').click()
	})
})

function import_user_cb(rs){
	var arr = rs.split('&'),
		format = arr[0].split('=')[1],
		result = arr[1].split('=')[1],
		message = arr[2].split('=')[1],
		err_arr = message.split('|'),
		error_block = $('.import_error_info')
	$('.pop_add_many').find('.pop_close_a').click()
	if(format=="false"){
		alert('导入失败，上传文件格式有误')
		return
	}
	$('.pop_ir_btn').click()
	$('.ir_success').text(result.split('|')[0])
	$('.ir_fail').text(result.split('|')[1])
	error_block.html('')
	for(var i=0;i<err_arr.length-1;i++){
		var err = err_arr[i].split('_'),
			err_line = err[0],
			err_type = [],
			err_str = []
			for(var j=1;j<err.length;j++){
				err_type.push(err[j])
			}
			for(var k=0;k<err_type.length;k++){
				err_str.push(IMPORT_USER_ERROR[err_type[k]])
			}
		error_block.append('<p class="import_err_item">第'+err_line+'条数据：' + err_str.join(' , ') + '</p>')
	}
}