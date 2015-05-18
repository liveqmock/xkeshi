var mobileStatus =  false;
$(function(){
    //验证
    var re_photo=/^png$|jpg$|gif$|bmp$|PNG$|JPG$|GIF$|BMP$/;
    $(".add_seller_btn").click(function(){
        //遍历每个需要检验的输入框
        var form  = $("#shopForm") ;
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
        if (requiredEach(form)) {
            validate(function(data){
                if(data.status == 'success'){
                    alert("该商户全称已存在，请重新填写！");
                    $("[name='fullName']").val("");
                    return false;
                }
                form.submit();
            })
        }
    });
//	$('.required').blur(function(){
//		 $(this).nextAll('.required_error').hide().text("");
//		 $(this).attr("title",'');
    $(".add_seller_btn").click(function(){
        if($.trim( $(this).val())){
            requiredCheck($(this),$(".memberFrom"));
        }else{
            showError($(this),$(this).data('msg')+'为必填项');
        }
    });
    $('select').change(function (){
        $(this).nextAll('.required_error').hide().text("");
        $(this).attr("title",'');
        if($.trim( $(this).val())){
            requiredCheck($(this),$(".memberFrom"));
        }else{
            showError($(this),$(this).data('msg')+'为必填项');
        }
    })

    $("[name='fullName']").blur(function(){
        if($(this).val() === "")
            return;
        validate(function(data){
            if(data.status == 'success'){
                alert("该商户全称已存在，请重新填写！");
                $("[name='fullName']").val("");
            }
        })
    })
})

var validate = function(callback){
    //验证是否有重复的商户全称
    $.ajax({
        url:'/shop/validate',
        type:'get',
        data:{shopId:$("[name='id']").val(),fullName: $.trim($("[name='fullName']").val())},
        dataType :'json',
        success: callback
    });
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
    if($('#latitude').val() === '' || $('#longitude').val()=='' ){
        $('.position').show().text('商户位置为必填项(请点击地图设置)');
        can_sub = false;
    }
    return can_sub;
}
/*检查具体某项*/
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
function isInt(str) {
    return /^(-|\+)?\d+$/.test(str);
}
function isFloat(str){
    if(isInt(str)){
        return true
    }
    return /^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$/.test(str)
}
function isPhone(str) {
    return /^(-|\+)?\d+$/.test(str) && str.length==11;
}
function isMail(str){
    var pattern =/^[a-zA-Z0-9_\-]{1,}@[a-zA-Z0-9_\-]{1,}\.[a-zA-Z0-9_\-.]{1,}$/;
    if(str!=""){
        if(!pattern.exec(str)){
            return false;
        }
    }
    return true;
}

function isCard(num){
    num = num.toUpperCase();
    if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(num))) {
        return false;
    }
    // 校验位按照ISO 7064:1983.MOD 11-2的规定生成，X可以认为是数字10。
    // 下面分别分析出生日期和校验位
    var len, re; len = num.length;
    if (len == 15) {
        re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
        var arrSplit = num.match(re);  // 检查生日日期是否正确
        var dtmBirth = new Date('19' + arrSplit[2] + '/' + arrSplit[3] + '/' + arrSplit[4]);
        var bGoodDay; bGoodDay = (dtmBirth.getYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
        if (!bGoodDay) {
            return false;
        } else { // 将15位身份证转成18位 //校验位按照ISO 7064:1983.MOD
            // 11-2的规定生成，X可以认为是数字10。
            var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
            var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
            var nTemp = 0, i;
            num = num.substr(0, 6) + '19' + num.substr(6, num.length - 6);
            for (i = 0; i < 17; i++) {
                nTemp += num.substr(i, 1) * arrInt[i];
            }
            num += arrCh[nTemp % 11];
            return true;
        }
    }
    if (len == 18) {
        re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
        var arrSplit = num.match(re);  // 检查生日日期是否正确
        var dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
        var bGoodDay; bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
        if (!bGoodDay) {
            return false;
        }
        else { // 检验18位身份证的校验码是否正确。 //校验位按照ISO 7064:1983.MOD
            // 11-2的规定生成，X可以认为是数字10。
            var valnum;
            var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
            var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
            var nTemp = 0, i;
            for (i = 0; i < 17; i++) {
                nTemp += num.substr(i, 1) * arrInt[i];
            }
            valnum = arrCh[nTemp % 11];
            if (valnum != num.substr(17, 1)) {
                return false;
            }
            return true;
        }
    }

    return false;
}
function isAreaCode(s){
    var patrn = /^(\d){6}$/;
    return patrn.exec(s);
}
