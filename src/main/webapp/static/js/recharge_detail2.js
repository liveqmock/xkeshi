$(function(){
/*	*/
	var btn_add=$('.btn_add');
	btn_add.live('click',function(){
		var td_wrap_add='<div class="td_wrap td_wraps"><p class="p_bg">充值金额<span class="input_wrap"><input type="text" class="input_box"><em>元</em></span>赠送<span class="td_val"><select class="select select1" selected="selected"><option value="1">金额</option><option data-type="0" value="0">无</option></select></span> <span class="input_wrap"><input type="text" class="input_box input_boxs gift_money"><em>元</em></span></p><a href="javascript:;" class="del_add">删除</a></div>'
		$(this).parents('.td_border').append(td_wrap_add);
	})
	$('.input_box').live('focus', function(){
		$(this).parent().css({'border-color':'#c0c0c0'})
	})
	$('.del_add').live('click',function(){
		var self = $(this)
		// if(self.parents('.td_border').find('.td_wrap').length==1){
		// 	alert('至少要有一条充值规则')
		// 	return false
		// }
		self.parents('.td_wrap').remove();
	})
	$('.charge_rd').live('change', function(){
		var self = $(this),
			val = self.data('val'),
			wrap = self.parents('.td_border'),
			td_wraps = wrap.find('.td_wrap'),
			add_btn = wrap.find('.btn_add')
		if(val=='1'){
			td_wraps.each(function(){
				$(this).hide()
			})
			add_btn.hide()
		}else{
			td_wraps.each(function(){
				$(this).show()
			})
			add_btn.show()
		}
	})
    $('.select1').live('change', function(){
        var self = $(this)
        select1_check(self)
    })
    $('.select1').each(function(){
        select1_check($(this))
    })
    function select1_check(self){
        var next = self.next()
        if(self.find('option:checked').data('type')=='0'){
            next.find('input').val('')
            next.hide()
        }else{
            next.show()
        }
    }
	$('.btn_submit').on('click',function(){
		var err_text = ''
		$('.input_box:visible').each(function(){
			var self = $(this),
				val = $.trim(self.val())
				if(!(isInt(val) && val>=0 && val<=9999999)){
					self.parent().css({'border-color':'#f20'})
					err_text = '金额必须为不大于9999999的整数'
				}
		})
		if(err_text){
			alert(err_text)
			return false
		}else{
			$('.gift_money').each(function(){
				var self = $(this),
					val = $.trim(self.val()),
					val1 = self.parents('.td_wrap').find('input:first').val()
				if(parseInt(val1)<parseInt(val)){
					self.parent().css({'border-color':'#f20'})
					err_text = '赠送金额不能大于首充金额，请修改'
				}
			})
		}
		if(err_text){
			alert(err_text)
			return false
		}else{
			var charge_num = 0
			$('.first_charge').each(function(){
				var self = $(this)
				if(self.find('.td_wrap:visible').length){
					charge_num++
				}
			})
			if(!charge_num){
				err_text = '请至少设置一种充值规则'
			}
		}
		if(err_text){
			alert(err_text)
			return false
		}
		var form = $('.main_form')
		form.find('input[name="rule"]').remove()
		$('.tb_list').each(function(){
			var tb = $(this),
				c1 = tb.find('.first_charge'),
				c2 = tb.find('.more_charge')
				id = tb.data('id'),
				tb_val_arr = [],
				item_arr1 = [],
				item_arr2 = []
			if(!c1.find('.td_wrap:visible')[0]){
				return
			}
			c1.find('.td_wrap').each(function(){
				var wrap = $(this),
					item = wrap.find('.input_box:first').val()+'_'+wrap.find('select').find('option:selected').val()+'_'+wrap.find('.gift_money').val()
				item_arr1.push(item)
			})
            if(tb.find('.charge_rd:checked').data('val')==2) {
                c2.find('.td_wrap').each(function () {
                    var wrap = $(this),
                        item = wrap.find('.input_box:first').val() + '_' + wrap.find('select').find('option:selected').val() + '_' + wrap.find('.gift_money').val()
                    item_arr2.push(item)
                })
            }
			form.append('<input type="hidden" name="rule" value="'+id+'||'+item_arr1.join('|')+'||'+item_arr2.join('|')+'">')
		})
		//return false
	})
})