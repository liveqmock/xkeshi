function isInt(str) {
 return /^(-|\+)?\d+$/.test(str);
}
function isFloat(str){
    if(isInt(str)){
        return true
    }
	return /^-?([1-9]\d*\.\d*|0\.\d*[1-9]\d*|0?\.0+|0)$/.test(str)
}
function isPlusFloat(str){
	if(isFloat(str) && parseFloat(str)>=0){
		return true
	}
	return false
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

function isPhone(str){
    var pattern = /^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/;
    if(str!=""){
        if(!pattern.exec(str)){
            return false;
        }
    }
    return true;
}
//添加对生日日期验证
function isBirth(e){
    var rg_brith=/^[0-9]{4}-[0-1]?[0-9]{1}-[0-3]?[0-9]{1}$/;
    if(e!==''||e!==null){
        if(!rg_brith.test(e)){
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


$(function(){

    $('.check_form').live('submit', function(){
        var self = $(this),
            ints = self.find('.isint'),
            floats = self.find('.isfloat'),
            plusfloats = self.find('.isplusfloat'),
            cards = self.find('.iscard'),
            mails = self.find('.ismail'),
            needs = self.find('.isneed'),
            phones = self.find('.isphone'),
            births = self.find('.isbirth'),
            can_sub = 1
        needs.each(function(){
            var self = $(this),
                //tag_name = self.prop('tagName').toLowerCase(),
                hint = self.data('hint'),
                type = self.attr('type')
            if(!self.is(":visible") && type!='checkbox' && type!='radio'){
                return
            }
            if(type=='radio' || type=='checkbox'){
                var name = self.attr('name')
                if(!self.parents('form').find('input[name="'+name+'"]:checked')[0]){
                    alert(hint+' 为必填项')
                    can_sub = 0
                    return false
                }
            }
            if(!$.trim(self.val())){
                alert(hint+' 为必填项')
                can_sub = 0
                return false
            }
        })
        if(can_sub){
            ints.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isInt(val)){
                    alert(hint+' 格式错误，必须为整数')
                    can_sub = 0
                    return false 
                }
            })      
        }
        if(can_sub){
            births.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isBirth(val)){
                    alert(hint+' 格式错误，格式如:"1990-10-01"!')
                    can_sub = 0
                    return false 
                }
            })      
        }
        if(can_sub){
            floats.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isFloat(val)){
                    alert(hint+' 格式错误，必须为数字')
                    can_sub = 0
                    return false 
                }
            })      
        }
        if(can_sub){
        	plusfloats.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isPlusFloat(val)){
                    alert(hint+' 格式错误，必须为不小于0的数字')
                    can_sub = 0
                    return false 
                }
            })      
        }        
        if(can_sub){
            cards.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isCard(val)){
                    alert(hint+' 格式错误')
                    can_sub = 0
                    return false 
                }
            })
        }
        if(can_sub){
            phones.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isPhone(val)){
                    alert(hint+' 格式错误')
                    can_sub = 0
                    return false
                }
            })
        }
        if(can_sub){
            mails.each(function(){
                var self = $(this),
                    hint = self.data('hint'),
                    val = $.trim(self.val())
                if(!self.is(":visible")){
                    return
                }
                if(val && !isMail(val)){
                    alert(hint+' 格式错误')
                    can_sub = 0
                    return false 
                }
            })
        }
        if(can_sub){
            var inputs = self.find('input')
            inputs.each(function(){
                var input = $(this)
                if(!input.is(":visible")){
                    return
                }
                if(input.data('maxlen')){
                    if(!input.data('minlen')){
                        if(cnenlen(input.val(),1)>input.data('maxlen')){
                            alert(input.data('hint')+'长度不能超过'+input.data('maxlen')+'个中文字符')
                            can_sub = 0
                            return false
                        }
                    }else{
                        if(cnenlen(input.val(),1)<input.data('minlen')){
                            alert(input.data('hint')+'长度介于'+input.data('minlen')+'-'+input.data('maxlen')+'之间')
                            can_sub = 0
                            return false
                        }
                    }
                }
            })
        }
        if(can_sub){
            var inputs = self.find('input')
            inputs.each(function(){
                var input = $(this),
                    hint = input.data('hint'),
                    val = $.trim(input.val()),
                    len = input.data('length')
                if(!input.is(":visible")){
                    return
                }
                if(len){
                    if(val.length!=len){
                        alert(input.data('hint')+'长度为'+len)
                        can_sub = 0
                        return false
                    }
                }
            })
        }
        if(!can_sub){
            return false
        }else if($(this).hasClass('wait_form')){
            var btn = $(this).find('button:last')
            btn.attr('disabled', true).addClass('btn_disabled')
        }
        if(self.hasClass('sub_form')){
            self.find('button').addClass('btn_disabled').attr('disabled', true)
        }
    })

    $('#pa1').focus(function() {
       if($(this).data('hint')=='生日'){
            $(this).addClass('isbirth');
       }
    })
})
