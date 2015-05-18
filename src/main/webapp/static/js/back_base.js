var IE8 = $.browser.msie && ( $.browser.version == '6.0' || $.browser.version == '7.0' || $.browser.version == '8.0')
jQuery.fn.extend({
    click_drop : function(settings){
        function set_default(default_dict){
            for(var key in default_dict){
                if(settings[key]==undefined){
                    settings[key] = default_dict[key]
                } 
            } 
        } 
        set_default({
            'drop' : '',
            'callback1' : 0,
            'callback2' : 0,
            'not_close' : 1,
            'add_cover' : 1,
            'auto_resize' : 1, 
            'is_center' : 1,
            'time_out' : 0 
        })
        var html = $("html,body"),
            not_close = settings['not_close'],
            add_cover = settings['add_cover'],
            auto_resize = settings['auto_resize'],
            drop = settings['drop'],
            callback1 = settings['callback1'],
            callback2 = settings['callback2'],
            is_center = settings['is_center'],
            time_out = settings['time_out']
        $(this).live('click',function(e){
            var self = $(this)
            self.blur()

            function _(){
                drop.hide()
                !not_close && html.unbind('click' , _)
                add_cover && $('.black_cover').remove()
                callback2 && callback2()
            }

            if(drop.is(":hidden")){
                function show_drop(){
                    drop.show()
                    var has_scroll = drop[0].offsetHeight < drop[0].scrollHeight
                    ;(!has_scroll && is_center) && drop.css({'margin-top': -drop.height()/2})
                    add_cover && !$('.black_cover')[0] && $('body').prepend($('<div class="black_cover"></div>'))                    
                }
                if(time_out){
                    setTimeout(function(){show_drop()}, time_out)
                }else{
                    show_drop()
                }
                e.stopPropagation()
                !not_close && html.click(_)
                clicked = true
                callback1 && callback1(self)
                //var totop = drop.offset().top-$(window).scrollTop()
                var drop_height = drop[0].offsetHeight
                ;(auto_resize==1 && drop_height>screen_height()) && drop.height(screen_height()-100).css({'overflow':'auto','top':'10px','margin-top':0})
            } else{
                _()
            }
            // alert($('body').width() + '  \n ' +  drop.outerWidth() + ' \n dropoffset' + drop.offset().left + '\n ' + $('.top').width())
        })
    }
})

jQuery.extend({
    postJSON : function(url, data, callback){
        $.ajax({
            type: "POST",
            url: url,
            data: JSON.stringify(data),
            contentType:"application/json; charset=utf-8",
            dataType: 'json',
            complete: function(e, xhr, settings){
                if(e.status === 200){
                    callback(JSON.parse(e.responseText))
                }
            }
        })
    }
})

$(function(){
	var hint = $('.pop_hint') ;
	if(hint[0]){
		if (!!window.name) {
			$(".pop_hint").hide();
		}else{
			hint.css({'margin-top':-hint[0].offsetHeight/2, 'margin-left':-hint[0].offsetWidth/2});
			setTimeout('$(".pop_hint").fadeOut(800)', 1000);
		}
	}
	window.name = "";	
	/*$('.back_a').click(function(){
		//标记为后退
		window.name = "isback";
		history.back();
	})*/
	$('.img_a').live('click', function(){
		$.fancybox({
			'href' : $(this).data('src'),
			'type':'image'
		})
	})
	
	;(function(){
		var RE_CNCHAR, _cnenlen;

		window.txt_maxlen = function(txt, tip, maxlen, type, update, cancel) {   
		    // type默认为1，表示中文算1字，英文算1/2字。
		    // type为2，表示中文和英文均算1字。
		    var po_word_update, val, type=type||1
		    po_word_update = function(value) {
		        var diff, html, len
		        len = cnenlen(value, type)
		        diff = 0
		        if (len) {
		            diff = len - maxlen
		            if (diff > 0) {
		            html = '<span style="color:red">超出<span>' + diff + '</span>字</span>'
		        } else {
		            html = '<span style="color:#666667"><span>' + len + '</span>字</span>'
		        }
		        } else {
		            html = '0字'
		            cancel && cancel()
		        }
		        tip.html(html);
		        return diff;
		    }
		    val = txt.val()
		    if (val && val.length) {
		        po_word_update(val)
		    }
		    txt.keyup(function() {
		        return po_word_update(this.value);
		    })
		    return function() {
		        if (po_word_update(txt.val()) > 0) {
		            txt.focus()
		        return false
		        }
		        return true
		    }
		}

		RE_CNCHAR = /[^\x00-\x80]/g

		_cnenlen = function(str) {
		    var aMatch
		    if (typeof str === "undefined") {
		        return 0
		    }
		    aMatch = str.match(RE_CNCHAR)
		    return str.length + (!aMatch ? 0 : aMatch.length)
		}

		window.cnenlen = function(str, type) {
		    return type==1?Math.ceil(_cnenlen($.trim(str)) / 2):Math.ceil($.trim(str).length)
		}
	})();
	
	;	(function($) {
		$.fn.extend({
			insertContent : function(myValue, t) {
				var $t = $(this)[0];
				if (document.selection) { // ie
					this.focus();
					var sel = document.selection.createRange();
					sel.text = myValue;
					this.focus();
					sel.moveStart('character', -l);
					var wee = sel.text.length;
					if (arguments.length == 2) {
						var l = $t.value.length;
						sel.moveEnd("character", wee + t);
						t <= 0 ? sel.moveStart("character", wee - 2 * t
								- myValue.length) : sel.moveStart(
								"character", wee - t - myValue.length);
						sel.select();
					}
				} else if ($t.selectionStart
						|| $t.selectionStart == '0') {
					var startPos = $t.selectionStart;
					var endPos = $t.selectionEnd;
					var scrollTop = $t.scrollTop;
					$t.value = $t.value.substring(0, startPos)
							+ myValue
							+ $t.value.substring(endPos,
									$t.value.length);
					this.focus();
					$t.selectionStart = startPos + myValue.length;
					$t.selectionEnd = startPos + myValue.length;
					$t.scrollTop = scrollTop;
					if (arguments.length == 2) {
						$t.setSelectionRange(startPos - t,
								$t.selectionEnd + t);
						this.focus();
					}
				} else {
					this.value += myValue;
					this.focus();
				}
			}
		})
	})(jQuery);
	
    $('.tcal').live('click',function(){
        var self = $(this),
            o = {},
            begin = self.data('begin'),
            end = self.data('end')
        if(begin || end){
            o = {minDate:begin,maxDate:end}
        }
        WdatePicker(o)
    })
    $('.tcal_time').live('click',function(){
        WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})
    })
	
	$('.pop_a').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.'+self.data('pop')),
			para = self.data('para'),
			name = self.data('name'),
			code = self.data('no'),
			form = pop.find('form:first')
		if(self.data('pos')=='center'){
			pop.show()
		}else{
			var width = self.data('width') || 247,
				height = self.data('height') || 40
			pop.css({'top':top+height, 'left':left-width+self.width()}).show()
		}
		if(para){
			var action = form.attr('action')
			form.attr('action', para)
		}
		if(name){
			$('.pb_name').text(name)
		}
		if(code){
			$('.id').val(code)
		}
	})
	$('.pb_cancel_a, .pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	
	//cb-checkbox, lb-label, rd-radio
	function cb_change(cb){
		var lb = cb.next()
		lb.removeClass('lb_cb_on lb_cb')
		if(cb.is(':checked')){
			lb.addClass('lb_cb_on')
		}else{
			lb.addClass('lb_cb')
		}
		cb.hide()
	}
	function rd_change(rd){
		var lb = rd.next(),
			name = rd.attr('name')
		lb.removeClass('lb_rd_on lb_rd')
		if(rd.is(':checked')){
			$('input[name="'+name+'"]').each(function(){
				$(this).next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
			})
			lb.removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
		}else{
			lb.addClass('lb_rd')
		}
		rd.hide()
	}
	if(!IE8){
		$('input[type="checkbox"]').each(function(){
			cb_change($(this))
		})
		
		$('input[type="checkbox"]').live('change', function(){
			cb_change($(this))
		})
		
		$('input[type="radio"]').each(function(){
			rd_change($(this))
		})
		
		$('input[type="radio"]').change(function(){
			rd_change($(this))
		})
	}else{ 
		//for ie
		$('input[type="checkbox"], input[type="radio"]').each(function(){
			if($.trim($(this).next().text())=='' || $(this).next().text()=='&nbsp;'){
				$(this).show().next().remove()
			}

		})
		//for ie
		$('#add_time_cb').live('change', function(){
			var cb=$(this),lb = cb.next();
			if(cb.is(':checked')){
				lb.addClass('lb_cb_on');
			}else{
				lb.removeClass('lb_cb_on');
			}
			cb.attr('type','hidden');
		})
		//修改ie下会员类型中添加修改单选框bug
		$('#set_cb1').change(function(){
			var cb=$(this),lb = cb.next();
			if(cb.is(':checked')){
				lb.addClass('lb_cb_on');
			}else{
				lb.removeClass('lb_cb_on');
			}
			cb.attr('type','hidden');
		})
		$('.sex_rd').change( function(){
			var rd =$(this);
			var lb = rd.next(),
			name = rd.attr('name')
			// lb.removeClass('lb_rd_on lb_rd')
			if(rd.is(':checked')){
				$('input[name="'+name+'"]').each(function(){
					$(this).next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
				})
				lb.removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
			}else{
				lb.addClass('lb_rd')
			}
			eb.attr('type','hidden');
		})
		//修改ie会员编辑中单选bug
		$('.isneed').change( function(){
			var rd =$(this);
			var lb = rd.next(),
			name = rd.attr('name')
			// lb.removeClass('lb_rd_on')
			if(rd.is(':checked')){
				$('input[name="'+name+'"]').each(function(){
					$(this).next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
				})
				lb.removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
			}else{
				lb.addClass('lb_rd')
			}
			eb.attr('type','hidden');
		})
        
	}
})
 
function arr_distinct(a) {
    var b = [], n = a.length, i, j
    for (i = 0; i < n; i++) {
        for (j = i + 1; j < n; j++)
        if (a[i] === a[j])j=++i
    b.push(a[i])}
    return b.sort(function(a,b){return a-b})
}

function check_all_none(all, elem){
	function check_none(){
		elem.each(function(){
			var self = $(this)
			self[0].checked = true
			!IE8 && self.next().removeClass('lb_cb_on lb_cb').addClass('lb_cb_on')
		})
	}
	function check_all(){
		elem.each(function(){
			var self = $(this)
			self[0].checked = false
			!IE8 && self.next().removeClass('lb_cb_on lb_cb').addClass('lb_cb')
		})
	}
	function loop_all(){
		all.each(function(){
			var self = $(this)
			if(self.is(':checked')){
				check_none()
			}else{
				check_all()
			}			
		}) 
	}  
	function loop_elem(){
		var isall = 1
		elem.each(function(){
			var self = $(this)
			if(!self.is(':checked')){
				isall = 0
				return false
			}
		})
		if(isall){
			all.attr('checked', true)
			!IE8 && all.next().removeClass('lb_cb_on lb_cb').addClass('lb_cb_on')
		}else{
			all.removeAttr('checked')
			!IE8 && all.next().removeClass('lb_cb_on lb_cb').addClass('lb_cb')
		}
	}
	all.click(loop_all)
	elem.click(loop_elem)
	loop_elem()
}
function screen_height(){
    if($.browser.msie){
        return document.compatMode == "CSS1Compat"? document.documentElement.clientHeight : document.body.clientHeight
    } else {
        return self.innerHeight
    }
}

function screen_width (){
    if($.browser.msie){
    return document.compatMode == "CSS1Compat"? document.documentElement.clientWidth : document.body.clientWidth
    } else {
        return self.innerWidth
    }
}

function get_today_date(){
	var date = new Date(),
		y = date.getFullYear(),
		m = date.getMonth()+1,
		d = date.getDate()
	if(d.toString().length==1){
		d = '0'+d
	}
	if(m.toString().length==1){
		m = '0'+m
	}
	return y+'-'+m+'-'+d
}

function date_plus(date,day){
	var a, dates

	if(typeof date === 'string' && /^\d{4}-\d{1,2}-\d{1,2}$/.test(date)) {
		dates = date.split('-')
		a = new Date(parseInt(dates[0]), parseInt(dates[1]) - 1, parseInt(dates[2]))
	} else {
		alert('date格式不正确: ' + date)
		return
	}
	a = a.valueOf()
	a = a + day * 24 * 60 * 60 * 1000
	a = new Date(a)
	var date = a.getDate(),
		date_str = date.toString().length>1?date:('0'+date),
		tmp_month = a.getMonth() + 1,
		month_str = tmp_month.toString().length>1?tmp_month:('0'+tmp_month)
	return(a.getFullYear() + "-" + month_str + "-" + date_str)
}

function time_days(s1, s2){
//	var s1 = "2007-01-01"
//	var s2 = "2007-12-31"
	s1 = s1.replace(/-/g, "/") 
	s2 = s2.replace(/-/g, "/")
	s1 = new Date(s1)
	s2 = new Date(s2)
	var time = s2.getTime() - s1.getTime()
	var days = parseInt(time / (1000 * 60 * 60 * 24))
	return days
}

function float_plus(v1,v2){  
	var v1 = v1.toString(),
		v2 = v2.toString(),
		t1 = t2 = t = 0,
		pl = parseFloat
	v1=="" && (v1="0")
	v2=="" && (v2="0")
    if(v1.indexOf(".")!=-1){
    	t1 = v1.length - v1.indexOf(".") - 1
    }
	if(v2.indexOf(".")!=-1){
		t2 = v2.length - v2.indexOf(".") - 1
	} 
	if(t1>t2){
		t = (pl(v1)+pl(v2)).toFixed(t1)
	}else{
		t = (pl(v1)+pl(v2)).toFixed(t2)
	}
    return pl(t)
}
function isInt(str) {
	 return /^(-|\+)?\d+$/.test(str);
}

$(function(){

    $('img').error(function(){
        $(this).attr('src','/static/css/img/error_miss.png');
        $(this).unbind('error');
    })
    $('input[placeholder]').placeholder();
      		
})

$.fn.placeholder = function(){
	//判断浏览器是否支持 placeholder属性  
	 function isPlaceholder(){  
          var input = document.createElement('input');  
          return 'placeholder' in input;  
      }
	if(!isPlaceholder()){
		var inputs = $(this);
		inputs.each(function(){
			var input = $(this),
				text = input.attr('placeholder'),
				pdl = 0,
				height = input.outerHeight(),
				width = input.outerWidth(),
				placeholder = $('<span class="phTips">'+text+'</span>');
			try{
				pdl = input.css('padding-left').match(/\d*/i)[0] * 1;
			}catch(e){
				pdl = 5;
			}
			placeholder.css({'margin-left': -(width-pdl),'height':height,'line-height':height+"px"});
			placeholder.click(function(){
				input.focus();
			});
			if(input.val() !== ""){
				placeholder.css({display:'none'});
			}else{
				placeholder.css({display:'inline'});
			}
			placeholder.insertAfter(input);
			input.keyup(function(e){
				if($(this).val() !== ""){
					placeholder.css({display:'none'});
				}else{
					placeholder.css({display:'inline'});
				}
			});

            input.hover(function(e){
                if($(this).val() !== ""){
                    placeholder.css({display:'none'});
                }else{
                    placeholder.css({display:'inline'});
                }
            });   
		});
	}
	return this;
};

