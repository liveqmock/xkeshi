$(function(){
	$('.js_pwd_text').keyup(function(event) {
		var pwd = this.value,
		    reg = /[^0-9a-zA-Z]/g
	    
	    $(this).next().hide()
	    if(this.id === 'new_pwd') {
	    	if(reg.test(pwd)) {
		    	$(this).next().html('密码只能是字母或数字，并且同时包含两者').show()
		    	return 
		    }
		    if (pwd.length > 32) {
		    	$(this).next().html('密码长度须为6-32位').show()
		    	return 
		    }
	    } else {
	    	if(!verficatePwd($('#new_pwd')[0], 6, 32)) {
    			return
	    	}
	    	var origin = $('#new_pwd').val()
	    	if (origin.indexOf(pwd) !== 0) {
	    		$(this).next().html('两次密码输入不一致').show()
	    	}
	    }
	})
	$('.js_pwd_text').blur(function(event) {
		var pwd = this.value,
		    reg = /[^0-9a-zA-Z]/g
	    
	    if(this.id === 'new_pwd') {
	    	if(verficatePwd(this, 6, 32)) {
	    		var pwd2 = $('#new_pwd2').val()
	    		if(pwd2 !== '' && pwd !== pwd2) {
	    			$('#new_pwd2').next().html('两次密码输入不一致').show()
	    		}
	    	} else {
	    		$('#new_pwd2').next().hide()
	    	}

	    	return
	    } else {
	    	if(!verficatePwd($('#new_pwd')[0], 6, 32)) {
	    		$(this).next().html('').hide()
	    		return
	    	} else {
	    		if(pwd === '') {
	    			$(this).next().html('').hide()
	    			return
	    		} else if(pwd !== $('#new_pwd').val()) {
	    			$(this).next().html('两次密码输入不一致').show()
	    		}
	    	}
	    }
	})

	$('#pwd_form').submit(function() {
		var pwd = $('#new_pwd').val(),
		    pwd2 = $('#new_pwd2').val()

	    if(!verficatePwd($('#new_pwd')[0], 6, 32)) {
	    	return false
	    }
	    if(pwd2 === '') {
	    	$('#new_pwd2').next().html('请再次输入新密码').show()
	    	return false
	    }
	    if(pwd !== pwd2) {
			$('#new_pwd2').next().html('两次密码输入不一致').show()
	    	return false
	    }
	})

	/* 密码校验， 6-32位数字和字母的混合体, 本方法不应用在第一行密码输入时*/
	function verficatePwd(dom, min, max) {
		var pwd = dom.value,
		    reg = /[^0-9a-zA-Z]/g,
		    len = pwd.length
	    if(pwd === '') {
	    	$(dom).next().html('请输入新密码').show()
	    	return false
	    } else if(reg.test(pwd)) {
    		$(dom).next().html('密码只能是字母或数字，并且同时包含两者').show()
    		return false
	    } else if(len > max || len < min) {
    		$(dom).next().html('密码长度须为' + min + '-' + max + '位').show()
    		return false
	    } else if (/^[a-zA-Z]{6,}$/.test(pwd) || /^[0-9]{6,}$/.test(pwd)) {
	    	$(dom).next().html('密码必须同时包含字母和数字').show()
	    	return false
	    }
	    return true 
	}

})