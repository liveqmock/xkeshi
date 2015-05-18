$(function(){
	$('.check_form').submit(function(){
		var self = $(this)
        //商品管理管理添加功能的图片不为空验证
		// if(!self.find('.file_ip').val() && !$('.item_img:first').attr('src')){
		// 	alert('图片 为必填项')
		// 	return false
		// }
		var price = $('input[name="price"]').val().toString(),
			price_arr = price.split('.'),
			index = parseInt($('input[name="sequence"]').val())
		if(price && price_arr.length>1 && price_arr[price_arr.length-1].length>2){
			alert('单价最多精确到小数点后2位')
			return false
		}
		if(price.length>13){
			alert('单价不能超过13位')
			return false			
		}
		if(index && index>9999){
			alert('顺序值不能大于9999')
			return false
		}
		//$('.add_seller_btn').attr('disabled', 'disabled')
	})

	//验证
  	var re_photo=/^png$|jpg$|gif$|bmp$|PNG$|JPG$|GIF$|BMP$/;
	$(".add_seller_btn").click(function(){
        //遍历每个需要检验的输入框
        var form  = $("form") ;
        var file = $('.file_ip').val();
        var str=file.slice(-3);
        if(file!='' && !re_photo.test(str)){
          alert("图片格式不正确！");
          return false;       
        }
        if ($('.file_b').val()) {
        	var file_b = $('.file_b').val();
        	var str_file_b=file_b.slice(-3);
        	if(file_b!='' && !re_photo.test(str_file_b)){
        		alert("图片格式不正确！");
        		return false;       
        	}
		}
       if (requiredEach(form) ) {
    	    form.submit(); 
    	    this.disabled = true
       }
	});


})