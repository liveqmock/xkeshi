$(function(){
	var flag1,flag2,flag3;
	var stateval,staval,f;
	$('.pop_a').click(function(){
		f=0;
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop;
		if(self.hasClass('pop_account')){		
			pop = $('.edit_account');
		}else if(self.hasClass('pop_realname')){
			pop = $('.edit_opt')
		}else if(text=='添加'||text=='添加POS终端'){
			pop = $('.add_pos')
		}else if(text=='添加操作员账号'){
			pop = $('.add_opt')
			var list_size=$('.pop_a').data('sizeofoperatorslist');
			if (list_size==0) {
				// $('select option[value="OPERATOR"]').hide();
			}else{
				$('select option[value="OPERATOR"]').show();
			}
		}else if(text=='修改设置'){
			pop.find('.consume_type').show();
		}else if(text=='编辑') {
			var username = $(this).parent().parent().find("td").eq(0).html();
			var realName = $(this).parent().parent().find("td").eq(1).html();
			var comName = $(this).parent().parent().find("td").eq(2).html();
			var userId = $(this).data("userid");
			pop = $('.edit_opt')
			pop.find("input[name^='username']").val(username);
			pop.find("input[name^='realName']").val(realName);

			if(comName=='收银员'){
				pop.find("select option[value='OPERATOR']").attr('selected','true');
			}else{
				pop.find("select option[value='MANAGER']").attr('selected','true');
			}
			pop.find("input[name^='id']").val(userId);

			if ($(this).parent().parent().index()==1) {
				// pop.find('.pb_s_del').hide();
				// pop.find('select option[value="OPERATOR"]').hide();
			}else{
				pop.find('.pb_s_del').show();
				pop.find('select option[value="OPERATOR"]').show();
			}
            if($(this).parent().parent().find("td").eq(2).html()=='收银员'){
                pop.find('.pb_s_del').show();
                pop.find('select option[value="OPERATOR"]').show();
            }

		}else if(text=='修改') {
			var username = $(this).parent().parent().find("td").eq(0).html();
			var realName = $(this).parent().parent().find("td").eq(1).html();
			var comName = $(this).parent().parent().find("td").eq(2).html();
			var remarkName = $(this).parent().parent().find("td").eq(3).html();
			var signKeyName = $(this).parent().parent().find("td").eq(4).html();

			var userId = $(this).data("userid");
			pop = $('.edit_pos')
			pop.find("input[name^='terminal']").val(username);
			if (realName=='联动优势') {
				pop.find("select option[value='UMPAY']").attr('selected','true');
			}else if(realName=='中国银行'){
				pop.find("select option[value='BOC']").attr('selected','true');
			}else if(realName=='盛付通'){
				pop.find("select option[value='SHENGPAY']").attr('selected','true');
			}else if(realName=='支付宝'){
				pop.find("select option[value='ALIPAY']").attr('selected','true');
			}
			pop.find("input[name^='account']").val(comName);
			pop.find("input[name^='remark']").val(remarkName);
			pop.find("input[name^='signKey']").val(signKeyName);

		}else if(text=='解除绑定'){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			var termainlId = $(this).data("termainlid");
			pop = $('.del_pos');
			pop.find("#deviceNumber").val(deviceNumber);
			pop.find("input[name^='id']").val(termainlId);
		}
		
		if(self.hasClass('pop_as0')){
			
			var pay_type = $(this).parent().parent().find("td").eq(0).html();
			var deviceNumber = $(this).parent().parent().find("td").eq(1).html();
			//var termainlId = $(this).data("termainlid");

			pop = $('.edit_pos')
			pop.find("#name_account").val(deviceNumber);
			pop.find("#name").val(pay_type);
			$('#dataid1').val($(this).parent().parent().attr('data-id'));
			$('#datatype1').val($(this).parent().parent().attr('data-type'));			
		}else if(self.hasClass('pop_as1')){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			pop = $('.del_pos');
			pop.find(".deviceNumber").text(deviceNumber);
			$('#dataid9').val($(this).parent().parent().attr('data-id'));
			$('#datatype9').val($(this).parent().parent().attr('data-type'));	
			
			
			
		}else if(self.hasClass('pop_as2')){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			
			pop = $('.edit_cash');
			pop.find(".deviceNumber").text(deviceNumber);
			$('#dataid2').val($(this).parent().parent().attr('data-id'));
			$('#datatype2').val($(this).parent().parent().attr('data-type'));
			
			staval=$(this);
			
			
		}else if(self.hasClass('pop_as3')){
			var pay_account1 = $(this).parent().parent().find("td").eq(1).html();
			var pay_test1 = $(this).parent().parent().find("td").eq(2).html();
			pop = $('.edit_pay');
			pop.find("#pay_account1").val(pay_account1);
			pop.find("#pay_test1").val(pay_test1);
			$('#dataid3').val($(this).parent().parent().attr('data-id'));
			$('#datatype3').val($(this).parent().parent().attr('data-type'));	
			staval=$(this);
			
		}else if(self.hasClass('pop_as4')){
			var pay_account1 = $(this).parent().parent().find("td").eq(1).html();
			var pay_test1 = $(this).parent().parent().find("td").eq(2).html();
			pop = $('.edit_MicroLetter');
			pop.find("#pay_account2").val(pay_account1);
			pop.find("#pay_test2").val(pay_test1);
			$('#dataid4').val($(this).parent().parent().attr('data-id'));
			$('#datatype4').val($(this).parent().parent().attr('data-type'));	
			staval=$(this);
			
		}else if(self.hasClass('pop_as5')){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			pop = $('.edit_BankCard');
			pop.find(".deviceNumber").text(deviceNumber);
			$('#actDel').val($(this).parent().parent().data('id'));
			
			var pay_account1 = $(this).parent().parent().find("td").eq(1).html();
			var pay_test1 = $(this).parent().parent().find("td").eq(2).html();
			var pay_num1 = $(this).parent().parent().find("td").eq(3).html();
			var comName = $(this).parent().parent().find("td").eq(2).html();
			var remarkName = $(this).parent().parent().find("td").eq(3).html();
			var signKeyName = $(this).parent().parent().find("td").eq(4).html();

			var userId = $(this).data("userid");
			pop = $('.edit_BankCard')
			pop.find("input[name^='terminal']").val(username);
			if (signKeyName=='中国银行') {
				pop.find("select option[value='BOC']").attr('selected','true');
			}else if(signKeyName=='联动优势'){
				pop.find("select option[value='UMPAY']").attr('selected','true');
			}else if(signKeyName=='盛付通'){
				pop.find("select option[value='SHENGPAY']").attr('selected','true');
			}
			pop.find("input[name^='account']").val(comName);
			pop.find("input[name^='remark']").val(remarkName);
			pop.find("input[name^='signKey']").val(signKeyName);
			pop.find("#pay_account3").val(pay_account1);
			pop.find("#pay_test3").val(pay_test1);
			pop.find("#pay_num3").val(pay_num1);
			
			$('#dataid5').val($(this).parent().parent().attr('data-id'));
			$('#datatype5').val($('#selOpt').val());
			staval=$(this);
			f=1;
		}else if(self.hasClass('pop_as6')){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			pop = $('.edit_electronic');
			pop.find(".deviceNumber").text(deviceNumber);
			$('#dataid6').val($(this).parent().parent().attr('data-id'));
			$('#datatype6').val($(this).parent().parent().attr('data-type'));
			staval=$(this);
			f=1;
		}else if(self.hasClass('pop_as7')){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			pop = $('.edit_entity');
			pop.find(".deviceNumber").text(deviceNumber);
			$('#dataid7').val($(this).parent().parent().attr('data-id'));
			$('#datatype7').val($(this).parent().parent().attr('data-type'));
			staval=$(this);
			f=1;
		}else if(self.hasClass('pop_as8')){
			var deviceNumber = $(this).parent().parent().find("td").eq(0).html();
			pop = $('.edit_members');
			pop.find(".deviceNumber").text(deviceNumber);
			$('#dataid8').val($(this).parent().parent().attr('data-id'));
			$('#datatype8').val($(this).parent().parent().attr('data-type'));
			staval=$(this);
			var statevals= $(this).parent().parent().find("td").eq(2).attr('state-val');
			if($(staval).parent().parent().find("td").eq(5).attr('state-val') == 1 ){
				pop.find('.pb_item').eq(1).show();
			}else{
				pop.find('.pb_item').eq(1).hide();
			}

/*            if(isIE = navigator.userAgent.indexOf("MSIE")!=-1) {
				if(statevals == 1){
					pop.find('.pb_rd_item_w').eq(0).find('input').attr('checked',true);
				}else if(statevals == 0){
					//pop.find('.pb_rd_item_w').eq(1).find('input').attr('checked',true);
                    pop.find('.pb_rd_item_w2').eq(1).find('input').attr('checked',true);
				}
	   		}else{*/
				if(statevals == 1){
					pop.find('.pb_rd_item_w2').eq(0).find('input').attr('checked',true);
					pop.find('.pb_rd_item_w2').eq(0).find('label').addClass('lb_rd_on lb_rd');
					pop.find('.pb_rd_item_w2').eq(1).find('label').removeClass('lb_rd_on').addClass('lb_rd');
				}else if(statevals == 0 ){
					pop.find('.pb_rd_item_w2').eq(1).find('input').attr('checked',true);
					pop.find('.pb_rd_item_w2').eq(1).find('label').addClass('lb_rd_on lb_rd');
					pop.find('.pb_rd_item_w2').eq(0).find('label').removeClass('lb_rd_on').addClass('lb_rd');
				}
			//}
			f=1;
		}
		
		stateval= $(staval).parent().parent().find("td").eq(5).attr('state-val');
		//if(isIE = navigator.userAgent.indexOf("MSIE")!=-1) {
			
/*			if(stateval=='1'){
				pop.find('.pb_rd_item_w').eq(0).find('input').attr('checked',true);
			}else if(stateval=='0'){
				pop.find('.pb_rd_item_w').eq(1).find('input').attr('checked',true);
			}*/
			if(stateval=='1'){
				pop.find('.pb_rd_item_w').eq(0).find('input').attr('checked',true);
				pop.find('.pb_rd_item_w').eq(0).find('label').addClass('lb_rd_on lb_rd');
	  			pop.find('.pb_rd_item_w').eq(1).find('label').removeClass('lb_rd_on').addClass('lb_rd');
				
			}else if(stateval=='0'){
				pop.find('.pb_rd_item_w').eq(1).find('input').attr('checked',true);
				pop.find('.pb_rd_item_w').eq(1).find('label').addClass('lb_rd_on lb_rd');
				pop.find('.pb_rd_item_w').eq(0).find('label').removeClass('lb_rd_on').addClass('lb_rd');
			}

	  //  }else{
/*			if(stateval=='1'){
				pop.find('.pb_rd_item_w').eq(0).find('input').attr('checked',true);
				pop.find('.pb_rd_item_w').eq(0).find('label').addClass('lb_rd_on lb_rd');
				pop.find('.pb_rd_item_w').eq(1).find('label').removeClass('lb_rd_on').addClass('lb_rd');
				
			}else if(stateval=='0'){
				pop.find('.pb_rd_item_w').eq(1).find('input').attr('checked',true);
				pop.find('.pb_rd_item_w').eq(1).find('label').addClass('lb_rd_on lb_rd');
				pop.find('.pb_rd_item_w').eq(0).find('label').removeClass('lb_rd_on').addClass('lb_rd');
			}*/
	   //}
		
		$('.pb').hide();
		//alert(top);
		//alert(pop.css('height').substring(0,3));
		//pop.css({'top':top-pop.css('height').substring(0,3)-32, 'left':left-243+self.width()}).show()
		if(f==1)
		{
			pop.css({'top':top-pop.css('height').substring(0,3)-32, 'left':left-243+self.width()}).show()
		}else{
			pop.css({'top':top+16, 'left':left-243+self.width()}).show()
		}
		//$('.pb').css({'top':top+16, 'left':left-243+self.width()}).show()
	})
	$('.pb_close , .a_cancel').click(function(){
		var self = $(this);
		self.parents('.pb').hide();
	})
	$('.edit_members .pb_rd_item_w input').change(function(){
		//alert($(this).parent().find('label').html());
		var a=$(this).parent().find('label').html();
		if(a=="关闭"){
			$(this).parent().parent().parent().find('.pb_item').eq(1).hide();
			$(this).parent().parent().parent().find('.pb_item')
		}else{
			$(this).parent().parent().parent().find('.pb_item').eq(1).show();
		}
	});
	$('.pb_btn1').click(function(){
		saveOp() 
	})
	$('.pb_btn2').click(function(){
		$('#form2').submit()
	})
	$('.pb_btn3').click(function(){
		var self=$(this),flag5=false;
		add_pos(self) 

	})
	$('.pb_btn4').click(function(){
		var self=$(this);
		add_pos(self) 
	})	
	$('.pb_btn5').click(function(){
		$('.pop_as1').parent().parent().remove();
		$(this).parents('.pb').hide();
		$('#form5').submit();
	})
	$('#selOpt').change(function(){
		//alert($('#selOpt').val());
		$('#datatype5').val($('#selOpt').val());
	});
	$('.pb_s_del').click(function() {
		var opId = $('.edit_opt').find("input[name^='id']").val();
		if(confirm("确定要删除数据吗？"))
	   	{
				$.ajax({
					url: '/shop/operator/delete/'+opId,
					type:'delete',
					dataType:'json',
					success:function(data){
						if(data.status=='success')
							location.reload();
					}
				});
		}else{
			return false
		}
	})
})

function saveOp() {
	flag1 = false;
	if ( $("#name_account").val()=="" || $("#password2").val()=="" || $("#password").val()=="") {
		alert("请将信息填写完整");
		flag1 = true;
		return false;
	}
	flag2 = false;
	if($("#password2").val()!=$("#password").val()){
		alert("两次密码不一致");
		flag2 = true;
		return false;
	}
	if (flag1 || flag2) {
		return false;
	}else{
		$('#form1').submit()
	};
}
function editOp() {
	// if($("#password4").val()==''||$("#password3").val()=='') {
	// 	alert("请将信息填写完整");
	// 	return false;
	// }
	if($("#password4").val()!=$("#password3").val()){
		alert("两次密码不一致");
		return false;
	}
	$('#form2').submit()
}
function add_pos(self){
	flag3 = false;
	self.parent().find('.required').each(function(){
		if ($(this).val() == ''){
			alert("请将信息填写完整");
			flag3 = true;
			return false;
		};
	})
	if (!flag3) {
		self.parent('form').submit();
	};
}
























