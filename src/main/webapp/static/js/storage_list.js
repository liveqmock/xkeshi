$(function(){
	$('.pb_cancel_a').click(function(){
		$('.black_cover').hide()
	})
	$('.sub_btn').click(function(){
		var tb_html = '',
			can_sub = 1,
			re_num = /^[-+]?[0-9]*$/;
		$('.tb_all').find("tr").each(function(){
			var self = $(this),
				input = self.find('.storage_input'),
				val = parseInt($.trim(input.val())),
				pb_tb = $('.pb_tb'),
				name = self.find('.name').text(),
				store1 = self.find('.amount').text(),
				change = parseInt(val)>0?('+'+parseInt(val)):val
			if(val && !isInt(val)){
				can_sub = 0
				alert('出库/入库数值必须为整数')
				return false
			}
			if(val>99999 || val<-99999){
				can_sub = 0
				alert('输入范围为-99999至+99999')
				return false
			}
			if(val){
				tb_html += '<tr class="tr" data-id="'+self.data('id')+'">'+
						'<td class="name" style="width:130px;">'+name+'</td>'+
						'<td class="store1">'+store1+'</td>'+
						'<td class="change">'+change+'</td>'+
						'<td class="store2">'+(parseInt(store1)+parseInt(change))+'</td></tr>'
			}
		})
		var s_input = $('.storage').find('.storage_input');
		if (!re_num.test(s_input.val())) {
			alert("请填写正确的出入库数量")
			return false
		};
		if(!can_sub){
			return false
		}
		if(tb_html){
			$('.pop_change, .black_cover').show()
			var tb_html = '<tr class="th">'+
						'<td class="name" style="width:130px;">商品名称</td>'+
						'<td class="store1">修改前库存</td>'+
						'<td class="change">库存变化</td>'+
						'<td class="store2">修改后库存</td></tr>'+tb_html
			$('.pb_tb').html(tb_html)
			var pop = $('.pop_change'),
				pop_height = pop[0].offsetHeight,
				sh = screen_height()
			if(pop_height>(sh-100)){
				pop.height(sh-100)
			}
			pop.css({'margin-top':-pop.height()/2-14})			
		}else{
			alert('请填写出库/入库数值')
		}
	})
	$('.change_btn').click(function(){
		var data = [],
			form = $('.change_form'),
			can_sub = 1
		$('.pb_tb').find('.tr').each(function(){
			var self = $(this),
				id = self.data('id'),
				quantity = Math.abs(parseInt(self.find('.change').text())),
				inventoryType = parseInt(self.find('.change').text())>0
//			if(parseInt(self.find('.store2').text())<0){
//				can_sub = 0
//				return false
//			}
			data.push({"item":{"id":id}, "quantity":quantity, "inventoryType":inventoryType})
		})
//		if(!can_sub){
//			alert('修改后库存不能小于0')
//			return false
//		}
		$.postJSON('/inventory/batch/add?submissionToken='+$('#submissionToken').val(),data,function(o){
			if(o.status=='success'){
				location.href = o.result.url
			}else if(o.status=='faild'){
				var o = o.result;
				if(o.msg){
					alert(msg)
					return false
				}
				var list = o.list,
					tb_error_html = '<tr class="th">'+
					'<td class="name">商品名称</td>'+
					'<td class="error">错误信息</td>'+
					'</tr>'
				for(var i=0;i<list.length;i++){
					var item = list[i],
						name = item.itemName,
						id = item.itemId,
						error = item.error
						tb_error_html += '<tr class="tr" data-id="'+id+'">'+
						'<td class="name">'+name+'</td>'+
						'<td class="error">'+error+'</td></tr>'
				}
				$('.pb_err_tb').html(tb_error_html)
				$('.pop_change').hide()
				$('.pop_error').show()
			}
		})
	})
	$('.error_btn').click(function(){
		var id_arr = [],
			self = $(this),
			loc = ''
		self.parents('.pb').find('.tr').each(function(){
			var tr = $(this)
			id_arr.push(tr.data('id'))
		})
		$('.tb_all').find('.tr').each(function(){
			var tr = $(this)
			if(!tr.find('.storage_input').val()){
				id_arr.push(tr.data('id'))
			}
		})
		loc = location.href.split('?')
		location.href = loc[0] + '?ids=' + id_arr.join(',')
	})
})