$(function(){
	$('.b_tit>b').click(function(){
		var span = $(this).parent(),
			fid = span.data('fid'),
			form = $('.f_form'),
			elem = $('.fi_'+fid),
			type = elem.attr('type')

		if ($(this).hasClass('f_x')) {
			$('.s_clear').trigger('click');              
		}else{
			if(elem.find('option')[0]){
				elem.find('option:selected').removeAttr('selected')
				elem.find('option[value=""]').attr('selected', true)
				form.submit()
				return
			}	
		}

		if(type=='text' || type=='hidden'){
			elem.val('')
			form.submit()
			return
		}
		if(type=='checkbox' || type=='radio'){
			var name = elem.attr('name')
			$('input[name="'+name+'"]').each(function(){
				$(this).removeAttr('checked')
			})
			form.submit()
			return
		}
	})
    $('.s_clear').click(function(){
        location.href=$(this).attr('href');
    })
    $('.pb_btn').click(function(){
		if ($('.text_tic')[0]) {
			if ($('.text_tic').val().length > 8) {
				alert('名称长度不能超过8位')
				return false
			};
		}; 
		if ($('.text_value').val()>99999) {
			alert('请输入不大于99999的整数')
			return false
		};
	})
	var re_elec=/^[0-9]*$/;
	$('.pb_btn_filter').click(function(){
		if (!re_elec.test($('.fi_2').val())) {
			alert('电子券编号只能为数字');
			return false
		};
		$('.f_form').submit();
	})

})
