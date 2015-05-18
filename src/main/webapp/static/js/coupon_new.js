$(function(){
    
    var myUe = UE.getEditor('description');
	$('.input_radio').click(function(){
		var self = $(this),
			par = self.parent(),
			show_val = par.find('.show_val'),
			show_val_wrap = par.find('.show_val_wrap')
		if(self.val()=='-1'){
			show_val.val('-1')
			show_val_wrap.hide()
		}else{
			show_val_wrap.show()
			show_val.val('')
		}
	})
	$('.input_radio1').click(function(){
		var self = $(this),
			par = self.parent(),
			show_val = par.find('.show_val1'),
			show_val_wrap = par.find('.show_val_wrap1')
		if(self.val()=='-1'){
			show_val.val('')
			show_val_wrap.hide()
		}else{
			show_val_wrap.show()
			show_val.val('')
		}
	})	
	//验证
  	var re_photo=/^png$|jpg$|gif$|bmp$|PNG$|JPG$|GIF$|BMP$/,re_price=/^[0-9]{0,8}$/;
	$(".add_seller_btn").click(function(){

        //遍历每个需要检验的输入框
        var form  = $("form") ;
        var file = $('.file_ip').val();
        var str=file.slice(-3);
        if(file!='' && !re_photo.test(str)){
          alert("图片格式不正确！");
          return false;       
        }
        var file_b = $('.file_b').val();
        var str_file_b=file_b.slice(-3);
        if(file_b!='' && !re_photo.test(str_file_b)){
          alert("图片格式不正确！");
          return false;       
        }
        // 对应电子券添加验证
        var flag = true,
            $focused = undefined,
            hasContents;

        $(".ckhed").each(function() {
            if(this.value === '') {
                addStyle(this);
                if(!$focused) {
                    $focused = $(this);
                }
                flag = false;
            }
        })

        hasContents = myUe.hasContents()
        if(!hasContents) {
            addStyle('#edui1');
        }

        if(!flag || !hasContents) {
            return false;
        }
        form.submit();

	});
		
	$(".ckhed").add('#edui1').blur(function() {
		if($(this).val()!== null && $(this).val()!==""){
			var blurId ="#"+$(this).attr("id");
			removeStyle(blurId);
		}
	});

	$("input,textarea").focus(function() {
		var content = UE.getEditor('description').hasContents();
		if (content == true) {
		 	removeStyle('#edui1');
		}
			
	});
		
})

function addStyle(e){
	$(e).addClass("form_chk");
}

function removeStyle(e){
	if(e!==null && e!==" "){
		$(e).removeClass('form_chk');
	}
}	
function check(v){
        var r=/^[0-9]{0,8}$/;
        if(!r.test(v)){ 
            alert('只能输入0~8位数字');
        }
}

function checkPhoto(v){
    var r=/^([\w\W]+)(.gif|.jpg|.jpeg|.GIF|.JPG|.JPEG|.png)$/;
    if(!r.test(v)){ 
            alert('图片格式不正确！');
    }
}