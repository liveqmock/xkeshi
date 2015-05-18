$(function(){
	txt_maxlen($(".msg_cont_text"), $('.msg_cont_now'), 140, 2)
	function get_phones(){
		var phones = $('.add_num_text').val().split('\n'),
			_phones = []
		for(var i=0;i<phones.length;i++){
			var p = $.trim(phones[i])
			if(!isPhone(p) && p){
				continue
			}
			p && _phones.push(p)  
		}
		return _phones 
	}
	$('.add_num_text').keyup(update_num)
	function update_num(){
		var phones = get_phones()
		$('.add_num_sum').text(parseInt(phones.length))
		$('.score_now').text(parseInt($('.add_num_sum').text())/10)		
	}
	function isPhone(str) {
		 return /^(-|\+)?\d+$/.test(str) && str.length==11;
	}
	$('.msg_name_a').click(function(){
		$('.msg_cont_text').insertContent("$客户姓名$")
	})
	$('.msg_birth_a').click(function(){
		$('.msg_cont_text').insertContent("$客户生日$")
	})
	$('.msg_code_a').click(function(){
		$('.msg_cont_text').insertContent("$优惠码$")
	})
	$('.msg_link_a').click(function(){
		$('.msg_cont_text').insertContent("$优惠获取链接$")
	})
	$('.code_show_a').live('click', function(){
		$('.msg_cont_text').insertContent("$优惠查看链接$")
	})
	$('.msg_cont_text').keyup(function(){
		var cont  = $(this).val(),
			cont2 = cont.replace(/\$客户姓名\$/g, "张三").replace(/\$客户生日\$/g, "2月14日").replace(/\$优惠码\$/g, "7382191238").replace(/\$优惠获取链接\$/g, "http://v2.xkeshi.com/coupon/123/detail")
		$('.sms_preview').text(cont2)
	})
	$('.sms_form').submit(function(){
		var sms_cont = $.trim($('.msg_cont_text').val())
		if ($('.text_wrap').val() === '') {
			alert('手机号码不能为空！')
			return false			
		};
		if(sms_cont === '') {
			alert('短信内容不能为空！')
			return false
		}

		if(sms_cont.indexOf('$优惠码$')>=0 && sms_cont.indexOf('$优惠获取链接$')>=0){
			alert('短信内容中，"优惠码"和"优惠获取链接"只能二选一')
			return false
		}
		
		var  score_nowPrice = parseFloat($('.score_now').text());
		var  score_morePrice = parseFloat($('.score_more').text());
		if(score_nowPrice>score_morePrice){
			alert('余额不足，请充值！')
			return false
		}
		if($('.cust_type:checked').attr('id')=='cust_type2'){
			var phones = $('.add_num_text').val().split('\n')
			for(var i=0;i<phones.length;i++){
				var p = $.trim(phones[i])
				if(!isPhone(p)){
					alert('手机号码格式错误，请检查！')
					return false
				}
			}
		}
	})
	function change_cust_type(){
		var rd = $('.cust_type:checked'),
			id = rd.attr('id')
		if(id=='cust_type1'){
			$('.msg_input_help').show()
			$('.msg_basic_a').show()
			if(!window.IS_COUPON){
				$('.msg_coupon_a').hide()
			}
			$('.add_num_text').attr('disabled', true)
			$('.score_now').text(parseInt($('.cust_num').text())/10)
		}else{
			if(window.IS_COUPON){
				$('.msg_input_help').show()
				$('.msg_basic_a').hide()
			}else{
				$('.msg_input_help').hide()
			}
			$('.add_num_text').removeAttr('disabled')
			$('.score_now').text(parseInt($('.add_num_sum').text())/10)
		}
		if($('input[name="type"]').val()=='PACKAGE'){
			//$('.msg_code_a, .msg_split').hide()
			$('.msg_code_a').replaceWith('<a class="msg_cont_a code_show_a" href="javascript:">优惠查看链接</a>')
			$('.msg_code_hint').text('("查看链接"和"获取链接"只能二选一)')
		}
	}
	$('.cust_type').change(change_cust_type)
	change_cust_type()
	
	$('.import_cust_list').click(function(){
		var self = $(this)
		$('.black_cover, .pop_add_many').show()
	})
	$('.pop_close_a, .btn_cancel').click(function(){
		$('.black_cover, .pop_add_many').hide()
	})
	if(window.IMPORT_STATE){
		if(IMPORT_STATE){
			var result = JSON.parse(IMPORT_RESULT)
			$('.import_success_num').text(result['success'])
			$('.import_fail_num').text(result['failure'])
			$('.pop_over, .black_cover').show()
			var pb_main = $('.pop_detail').find('.pb_main')
			for(var i=0;i<result['errorMsg'].length;i++){
				var info = result['errorMsg'][i]
				pb_main.append('<p class="pb_main_item">第<em class="err_row">'+info['row']+'</em>行：<em class="err_detail">'+info['msg']+'</em></p>')				
			}
		}else{
			$('.pop_error').find('.pb_main').text(ERR_MESSAGE)
			$('.pop_error, .black_cover').show()
		}		
	}
	$('.pop_over').find('.pb_btn').click(function(){
		$('.pop_over,.black_cover').hide()
	})
	$('.pop_error').find('.pb_btn').click(function(){
		$('.pop_error,.black_cover').hide()
	})
	$('.pop_detail').find('.pb_btn').click(function(){
		$('.pop_detail,.black_cover').hide()
	})
	$('.error_detail_a').click(function(){
		$('.pop_over').hide()
		$('.pop_detail').show()
	})
})
 
