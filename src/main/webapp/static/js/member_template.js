var isAddOpt =  true;
$(function(){
	$('.attention_msg').hide();
	$('.new_a').click(function(){
		isAddOpt =  true;
		document.attrPostForm.action= BASE+"/member/attribute/save"; 
		$(".pb_title_sp").text("添加会员属性");
		$("#templateAttributeName").val("");
		$("#productAttributeType").val("");
		$("#memberAttributeId").val('');
		$('.option-div').remove();
		$("p").remove("#p_select_wrap");
		showSelectType("");
		showRadioIsChecked('enabled',"");
		showRadioIsChecked('required', "");
		$("#sequence").val("")
		$("#methodId").val("post");
		$('.attention_msg').hide();
		showTypeForm($(this));
	})
	
	$('.pb_close').click(function(){
		var self = $(this)
		self.parents('.pbmt').hide()
	})
	$('.pb_cancel_form_btn').click(function(){
		var self = $(this)
		self.parents('.pbmt').hide()
	})
	$('.pb_cancel_form_del').click(function(){
		var self = $(this)
		self.parents('.pbdel').hide()
	})
	 
	$('.del_attribute').click(function(){
		deleteAttribute($(this).data('id'),$(this).data('name'),$(this));
	})
	$('.pb_add_form_btn').click(function(){ 
		//验证表单
		if ($("#templateAttributeName").val().trim().length == 0) {
			alert("名称不能为空");
			return false;
		}else if ($("#productAttributeType").val().trim().length == 0) {
			alert("类型不能为空");
			return false;
		}else if ($('input:radio[name="required"]:checked').val() == null) {
			alert("是否必填/启用");
			return false;
		}else{
			if ($('#productAttributeType').val()=='checkbox'||$('#productAttributeType').val()=='select'){
				$('.pb_add_form_btn').hide();
				$("input[name='attributeOptionStore']").each(function(i,v){
					    this.value = replaceVal(this.value);
						if(this.value.trim() == ''){
							$('.pb_add_form_btn').show();
							alert('请填写完整可选项');
							return false;
						} else{
							this.value ='"'+this.value.replace("'","")+'"';
							if (i+1 == $("input[name='attributeOptionStore']").length) {
								$('#attrPostForm').submit();
							}
						}
				})
			}else{
				$('.option-div').remove();
				$('#attrPostForm').submit();
			}
			return true;
		}
		return false;
	});
	$('#productAttributeType').change(function(){
		if ($('.icon-del').size() == 0){
			$('.option-add').parent().before('<p class="input_item"  id="p_select_wrap"><input type="text" class="input-m pb_input" name="attributeOptionStore" value="" id="attributeOptionStore"><a href="javascript:" class="b_a icon-del">删除</a></p>');
			$('.icon-del').first().hide();
			$('.option-add').show();
		} 
		showSelectType($(this).val());
	});
	$('.option-add').click(function(){
		if ($('.icon-del').size()>0){
			$('.icon-del').first().show();
			$(this).parent().before('<p class="input_item"  id="p_select_wrap"><input type="text" class="input-m pb_input" name="attributeOptionStore" value="" id="attributeOptionStore"><a href="javascript:" class="b_a icon-del">删除</a></p>');
			if ($('.icon-del').size()>=5){
				$('.option-add').hide();
			}
		} 
	});
	$('.select-l').change(function(){
		var opt = $(this).find('option:checked'),
			val = opt.val()
		if(val=='select' || val=='checkbox'){
			$('.pb_select_wrap').show()
			$('.pb_select_none').hide()
		}else{
			$('.pb_select_wrap').hide()
			$('.pb_select_none').show()
		}
	})
	$('.icon-del').live('click',function(){
		$(this).parents('.input_item').remove();
		if ($('.icon-del').size() == 1){
			$('.icon-del').first().hide();
		} else{
			$('.option-add').show();
		}
	})
	$('.pb_cancel_btn').click(function(){
		var self = $(this);
		self.parents('.pbdel').hide();
	})
})
 var replaceVal  = function (value){
	value = value.replace(/\,/g,"");
	value = value.replace(/\"/g,"");
	value = value.replace(/\[/g,"");
	value = value.replace(/\]/g,"");
	value = value.replace(/\'/g,"");
	return  value;
}
/**
 * 向form表单中注入修改前的默认值
 */
var updateAttirbute  = function(ts,id,name,attributeType,required,enabled,sequence){
	isAddOpt =  false;
	document.attrPostForm.action= BASE+"/member/attribute/update";
	showAttributeOptionStore($(ts).data("attributeoptionstore"));
	$(".pb_title_sp").text("修改会员属性");
	$("#memberAttributeId").val(id);
	$("#templateAttributeName").val(name);
	$("#productAttributeType").val(attributeType);
	showSelectType(attributeType);
	showRadioIsChecked('enabled',enabled);
	showRadioIsChecked('required', required);
	$("#sequence").val(sequence);
	$("#methodId").val("put");
	$('.attention_msg').show();
	showTypeForm($('.new_a'));
}
/**
 * 注入会员属性下拉的值
 */
var showAttributeOptionStore = function (attributeOptionSstore){
	$("p").remove("#p_select_wrap");
	if (attributeOptionSstore.length>0) {
		$.each(attributeOptionSstore,function(i,v){
			if(replaceVal(v).trim() != ''){
				  $('.option-add').parent().before('<p class="input_item"  id="p_select_wrap"><input type="text" class="input-m pb_input" name="attributeOptionStore" value="'+v
						  +'" id="attributeOptionStore"><a href="javascript:" class="b_a icon-del">删除</a></p>');
			}
		})
	}
	$('.pb_select_wrap').show();
	$('.pb_select_none').hide();
	if ($('.icon-del').size() == 1){
		$('.icon-del').first().hide();
	} else{
		$('.option-add').show();
	}
	if ($('.icon-del').size() == 0){
		$('.option-add').parent().before('<p class="input_item"  id="p_select_wrap"><input type="text" class="input-m pb_input" name="attributeOptionStore" value="" id="attributeOptionStore"><a href="javascript:" class="b_a icon-del">删除</a></p>');
		$('.icon-del').first().hide();
		$('.option-add').show();
	} 
}
/**
 * 设置默认的单选按钮的状态
 */
var showRadioIsChecked = function (name , value){
	if (value == '') {
		if ($("input[name="+name+"][value="+true+"]").attr("checked")) {
			$("input[name="+name+"][value="+true+"]").attr("checked",false);
		}else{
			$("input[name="+name+"][value="+false+"]").attr("checked",false);
		}
	}else{
		$("input[name="+name+"][value="+value+"]").attr("checked","checked");
	}
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
/**
 * 注入select下拉类型
 */
var showSelectType  = function(value){
	if (value =='checkbox'||value =='select'|| value == 'radio'){
		$('.pb_select_none').hide();
		$('.pb_select_wrap').show();
	}else{
		$('.pb_select_wrap').hide();
		$('.pb_select_none').show();
	}
}

/**
 * 显示form表单
 */
var showTypeForm = function (value){
	
	var self = value,
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop;
	pop = $('.add_opt');
	$('.add_opt, .edit_opt, .add_pos, .del_pos').hide();
	pop.css({'top':top+50, 'left':left-250+self.width()}).show();
}

var check_option  = function (){
	var error=0;
	if ($('#productAttributeType').val()=='checkbox'||$('#productAttributeType').val()=='select'){
		if ($('.option-div').size()==0){
			$('#option-tip').show().html('<i></i>请至少增加一个选项。');
			return false;
		}else{
			$.each($('.option-div input'),function(i,d){
				if ($(d).val()==''){
					error++;
				}			
			});
			if (error>0){
				$('#option-tip').show().html('<i></i>请至少增加一个选项。');
				return false;
			}		
		}
	}
}
/**
 *  删除属性
 */
var deleteAttribute  =  function(id ,name ,self){
	$(".pb_cate_name").html(name);
	document.memberTemplateForm.action= BASE+"/member/attribute/delete/"+id;
	var left = self.offset().left,
	top = self.offset().top,
	text = self.text(),
	pop;
	pop = $('.pop_del');
	pop.css({'top':top+20, 'left':left-247+self.width()}).show();
}
