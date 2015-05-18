var username = "";
$(function(){
	check_all_none($('#checkAll'), $('.checkbox_merchant'))	
	$('.filter_a').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.pop_filter')
		pop.css({'top':top+40, 'left':left-247+self.width()}).show()
		$('.pb_merchant').hide()
	})
	$('.pb_cancel_a, .pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	$('.pb_merchant_del').click(function(){
	  $(this).hide()
	})
	
	$('.new_a').click(function(){
		$('.pop_filter').hide()
	    $('.pb_input').val('');
		$('#password').removeClass('required').addClass('required').bind("blur",requiredBlur).attr('placeholder','账户密码');
		document.merchantForm.action = "/merchant/save";
		$('.required_error').hide().text("");
		$('input[name="memberCentralManagement"]:radio[value=true]').attr('checked','true');
    	$('input[name="balanceCentralManagement"]:radio[value=true]').attr('checked','true');
		$('.pb_title_b').text('添加集团');
		$('#avatar').removeClass('required').addClass('required').attr('placeholder','集团Logo');
		// $('.show_avatar').hide();
		username = ''; 
		showTypeForm($(this));
	})
	$('#checkAll').click(function(){
		$.each($('input[name="merchantIds"]'),function(i,e){
			$(e).attr('checked', $('#checkAll').attr("checked") == 'checked');
		});
	});
	//公开
	 $('.opt1').click(function(){
		 if( $("input[name='merchantIds']:checked").length == 0){
			 var self = $('.opt1'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
				pop;
				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left+self.width()}).show();
			 return false;
		  }
		 document.merchantPlatform.action = "/merchant/publish/true";
		 $('#methodPlatformId').val("PUT");
		 $('#merchantPlatform').submit(); 
	 });
	//隐藏
	 $('.opt2').click(function(){
		 if( $("input[name='merchantIds']:checked").length == 0){
			 var self = $('.opt2'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
				pop;
				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left-100+self.width()}).show();
			 return false;
		  }
		 document.merchantPlatform.action = "/merchant/publish/false";
		 $('#methodPlatformId').val("PUT");
		 $('#merchantPlatform').submit(); 
	 });
	//修改
	 
	 $('.opt3').click(function(){
		    var ts  = $("input[name='merchantIds']:checked") ;
		    if( ts.length == 1){
		    	$('.pb_title_b').text('修改集团');
		    	$('.required_error').hide().text("");
		    	$('#fullName').val(ts.data('name'));
		    	$('#merchantId').val(ts.data('id'));
		    	$('#username').val(ts.data('username'));
		    	$('#accountId').val(ts.data('accountid'));
		    	$('#password').val('').attr('placeholder','空为原密码');
		    	$('.pb_mm .phTips').text('空为原密码');//for ie
		    	$('#password').removeClass('required').unbind("blur"); //移除click;
		    	$('#smsSuffix').val(ts.data('smssuffix'));
		    	$('input[name="memberCentralManagement"]:radio[value='+ts.data('mcm')+']').attr('checked','true');
		    	$('input[name="balanceCentralManagement"]:radio[value='+ts.data('bcm')+']').attr('checked','true');
		    	radioChecked();
		    	$('#avatar').removeClass('required').addClass('required').attr('placeholder','集团Logo');
		    	$('#avatar').val('').attr('placeholder','空为原Logo');
		    	if($('#fullName').val()!==''||$('#fullName').val()!==null){
					$('.pb_jt .phTips').hide();
				}
				if($('#usernam').val()!==''||$('#usernam').val()!==null){
					$('.pb_zh .phTips').hide();
				}
				if($('#smsSuffix').val()!==''||$('#smsSuffix').val()!==null){
					$('.pb_dx .phTips').hide();
				}
		    	if(ts.data('avatar')){
		    		$('#avatar').removeClass('required');    
		    		var avatar = $('.show_avatar').attr('href');
		    		$('.show_avatar').attr('href',avatar+ts.data('avatar'));
		    		$('.show_avatar').show();
		    	}else{
		    		$('.show_avatar').hide();
		    	}
		    	document.merchantForm.action = "/merchant/"+ts.data('id')+"/update";
		    	showTypeForm($('.new_a'));
		    	username = ts.data('username') ;
		    }else if($("input[name='merchantIds']:checked").length == 0){
		    	var self = $('.opt3'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
				pop;
				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left-100+self.width()}).show();
                $('.pb_main2').text("请选择需要操作的集团");
            }else{
		    	var self = $('.opt3'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
				pop;
				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left-100+self.width()}).show();
		    }
		});
	 //删除
	$('.opt4').click(function(){ 
		 if( $("input[name='merchantIds']:checked").length == 0){
			 var self = $('.opt4'),
				left = self.offset().left,
				top = self.offset().top,
				text = self.text(),
				pop;
				pop = $('.pb_warn');
				pop.css({'top':top+50, 'left':left-100+self.width()}).show();
			 return false;
		  } else {
			  $('.pb_item_merchant').empty();
			  var merchantName = '<br>';
				 $("input[type=checkbox][name=merchantIds]:checked").each(function(i){
					   merchantName  += (i+1)+". "+$(this).data('name')+"<br>";
				 })
				 $('.pb_item_merchant').append(merchantName);
		  }
		 document.merchantPlatform.action = "/merchant/delete";
		 $('#methodPlatformId').val("DELETE");
		 var self = $('.opt4'),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop;
			pop = $('.pb_merchant_del');
			pop.css({'top':top+16, 'left':left-243+self.width()}).show();
	 })
	$('.pb_btn_merchant_del').click(function (){
		 $('#merchantPlatform').submit(); 
	 })
	$(".pb_btn_merchant").click(function(){
		showError($('#username'),"");
		// if(username == $('#username').val().trim()){//ie8以下不支持.trim()
		   if(username == $.trim($('#username').val())){
			 if ( requiredEach($("#merchantForm")) ){
	    		 $('.pb_btn_merchant').attr('disabled',false);
	    		 $('#merchantForm').submit(); 
	    	 }
		}else{
			$.ajax({
				   type: "get",
				   async: false,
				   dataType :'json',
				   // url: "/merchant/account/"+$('#username').val().trim(),//ie8以下不支持.trim() 
				   url: "/merchant/account/"+$.trim($('#username').val()),
				   success: function(data){
				     if (data.status == 'faild') {
				    	 showError($('#username'),data.description || '账户存在');
				     } else  if ( requiredEach($("#merchantForm")) ) {
				    	$('#merchantForm').submit(); 
				     }
				   }
			})
		}
	});

	$('.required').blur(requiredBlur);
})

var checkUserName  =  function (){
	
}


var radioChecked   =  function (){
	$('input[type="radio"]').each(function(){
		var self = $(this),
			lb = self.next()
		lb.removeClass('lb_rd_on lb_rd')
		if(self.is(':checked')){
			lb.addClass('lb_rd_on')
		}else{
			lb.addClass('lb_rd')
		}
		self.hide()
	})
}
var showTypeForm  = function (value){
	var self = value,
	left = self.offset().left,
	top = self.offset().top,
	text = self.text(),
	pop;
	pop = $('.pb_merchant');
	pop.css({'top':top+40, 'left':left-243+self.width()}).show();//添加面板位置
}
var requiredBlur = function(){
	 $(this).nextAll('.required_error').hide().text("");
	 $(this).attr("title",'');
	 if($.trim( $(this).val())){
		 requiredCheck($(this),$("#merchantForm"));
     }else{
    	 showError($(this),$(this).data('msg')+'为必填项'); 	
     }
}
var requiredFile = function(ts){
	$(ts).nextAll('.required_error').hide().text("");
	$(ts).attr("title",'');
	if($.trim( $(ts).val())){
		requiredCheck($(ts),$("#merchantForm"));
	}else{
		showError($(ts),$(ts).data('msg')+'为必填项'); 	
	}
}
var showError =  function(self, msg){
	 self.nextAll('.required_error').show().text(msg);
	 self.attr("title",msg);
}

var requiredEach  = function(form){
	 var can_sub = true;
	form.find('.required').each(function(){
 	    var  self  = $(this);
 	    var  type = self.attr('type');
	  	if($.trim(self.val()) || type=='radio' || type=='checkbox'){
	  		  can_sub = requiredCheck(self,form)&&can_sub;
	    }else{
	      	  showError(self,self.data('msg')+'为必填项'); 	
	      	  can_sub = false;
	    }
   })
   return can_sub;
}
var requiredCheck = function(self,form){
	  var msg  = self.data('msg');
    var type = self.attr('type');
    var textType = self.data('type');
    var dateFmt  = self.data('datefmt');
    if(type=='radio' || type=='checkbox'){
        var name = self.attr('name');
        if(!form.find('input[name="'+name+'"]:checked')[0]){
      	  showError(self,msg+'为必填项');   return false;
        }
    }else if (type=="text" && textType=="number" && isNaN(self.val())) {
  	  showError(self,msg+'为数字');	  return false;
	  }else if (type=="text" && textType=="mobile" && !isPhone(self.val())) {
  	  showError(self,msg+'输入有误');  return false;
    }else if (type=="text" && textType=="date" && !$.trim(self.val())) {
  	  showError(self,msg+'为必填项,格式为:'+dateFmt);	 return false; 
    }
    return true;
}