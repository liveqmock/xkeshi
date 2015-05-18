jQuery.fn.extend({
    click_drop : function(drop, callback1, callback2, opts){
        var html = $("html,body"),
            not_close = opts && opts['not_close'],
            add_cover = opts && opts['add_cover'],
            auto_resize = opts && opts['auto_resize']
        $(this).live('click',function(e){
            var self = $(this)
            self.blur()
            
            function _(){
                drop.hide()
                !not_close && html.unbind('click' , _)
                add_cover && $('.black_cover').remove()
                callback2 && callback2(self)
            }

            if(drop.is(":hidden")){
                drop.show()
                e.stopPropagation()
                !not_close && html.click(_)
                clicked = true
                add_cover && !$('.black_cover')[0] && $('body').prepend($('<div class="black_cover"></div>'))
                callback1 && callback1(self)
                //var totop = drop.offset().top-$(window).scrollTop()
                var drop_height = drop[0].offsetHeight
                ;(auto_resize==1 && drop_height>screen_height()) && drop.height(screen_height()-100).css({'overflow':'auto','top':'10px','margin-top':0})
            } else{
                _()
            }
        })
    },
	hover_drop : function(drop, callback1, callback2){
		$(this).hover(function(){
			drop.show()
            callback1 && callback1()			
		}, function(){
			drop.hide()
            callback2 && callback2()			
		})
	},
    hover_drop_ex: function(drop, callback1, callback2){
        var html = $("html,body")
        $(this).mouseover(function(e){
            var self = $(this)
            
            function _(){
                drop.hide()
                html.unbind('mouseover')
                callback2 && callback2(self)
            }

            if(drop.is(":hidden")){
                drop.show()
                e.stopPropagation()
                html.mouseover(_)
                $('.pop_nav, #Top').mouseover(function(e){
                    e.stopPropagation()
                })
                callback1 && callback1(self)
            } else{
                _()
            }
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
                }else{
                    alert('系统异常，请重试')
                }
            }
        })
    },
    hover_hint : function(a){
        $('body').append($('<div class="hover_hint"></div>'))
        a.hover(function(){
            var self = $(this),
                text = self.data('text'),
                height = self.height()+parseInt(self.css('padding-top'))+parseInt(self.css('padding-bottom')),
                width = self.width()+parseInt(self.css('padding-left'))+parseInt(self.css('padding-right')),
                top = self.offset().top+height,
                left = self.offset().left-((63-width)/2),
                block = $('.hover_hint')
            block.text(text).css({'left':left, 'top':top}).show()
        },function(){
            $('.hover_hint').hide()
        })
    }
})

function drop_resize(){
    var drop = $('.pop_form:visible'),
        drop_height = drop[0].offsetHeight
    if(drop_height>screen_height()){
        drop.height(screen_height()-100).css({'overflow':'auto','top':'10px','margin-top':0})
    }else{
        drop.css({'margin-top':-drop_height/2, 'top':'50%'})
    }
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


function check_all_none(all, elem){
    function check_none(){
        elem.each(function(){
            var self = $(this)
            self[0].checked = true
        })
    }
    function check_all(){
        elem.each(function(){
            var self = $(this)
            self[0].checked = false
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
        }else{
            all.removeAttr('checked')
        }
    }
    all.live('click', loop_all)
    elem.live('click', loop_elem)
    loop_elem()
}

$(function(){
    $.hover_hint($('.hover_hint_a'))    
	$('.top_avatar_wrap').click_drop($('.head_drop'),0,0,{'not_close':0, 'add_cover':0})
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

    $('.pb_img').mouseover(function(){
        var par = $(this).parent()
        par.find('.black_bg').fadeIn(200)
        par.find('.bg_text').fadeIn(200)
    })
    $('.black_bg, .bg_text').live('mouseout',function(){
        var par = $(this).parent()
        par.find('.black_bg').fadeOut(200)
        par.find('.bg_text').fadeOut(200)
    })
    $('.avatar_more').click(function(){
        var self = $(this),
            wrap = self.parent().find('.wrap_all')
        if(wrap.is(":visible")){
            wrap.hide()
            self.removeClass('avatar_more_click')
        }else{
        wrap.show()
        self.addClass('avatar_more_click')
        }
    })

    $('.nav_a:first').hover(function(){
        $('.nav_hover_bg').show()
    }, function(){
        $('.nav_hover_bg').hide()
    })

    $('.nav_open_a').hover_drop_ex($('.pop_nav'),0,0)

    $('.pop_form').each(function(){
        var self = $(this),
            h = self.height()
        self.css('margin-top', -h/2)
    })
    $('.btn_cancel, .pop_close_a').live('click', function(){
        $(this).parents('.pop_form').hide()
        $('.black_cover').remove()
    })
    $('.click_go').live('click', function(){
        var self = $(this)
        window.location.href = self.data('url')
    })

    $(window).resize(function(){
        var drop = $('.pop_form')
        ;(drop.height())>screen_height() && drop.height(screen_height()-100).css({'overflow':'auto','top':10,'margin-top':0})
    })
    add_selected()
    $('.nav_name').text($('#head_title').text())
    add_top_hint()
})

Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

var TODAY = (new Date()).Format("yyyy-MM-dd")

function arr_distinct(a) {
    var b = [], n = a.length, i, j
    for (i = 0; i < n; i++) {
        for (j = i + 1; j < n; j++)
        if (a[i] === a[j])j=++i
    b.push(a[i])}
    return b.sort(function(a,b){return a-b})
}

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
            html = '<span style="color:#999"><span>' + len + '</span>字</span>'
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

function add_selected(){
	if(SSN){
		$('.lnav_item').each(function(){
	        if($.trim($(this).text())==SSN){
	            $(this).addClass('lnav_selected')
	            return false
	        }
	    })
	}
	if(SSN2){
        var sel_text = $('.lnav_selected').text()
	    $('.lnav_item2').each(function(){
            var self = $(this)
            if(self.data('par')!=sel_text){
                self.hide()
                return
            }
	        if($.trim(self.text())==SSN2){
	            self.addClass('lnav_item2_selected')
	        }
	    })
	}else{
        $('.lnav_item2').hide()
    }
}

function add_top_hint(){
    if(window.TOP_HINT){
        var hint = window.TOP_HINT,
            hint_type = hint[0],
            hint_text = hint[1]
        $('#Top').append('<div class="top_'+hint_type+'">'+hint_text+'</div>')
        setTimeout('$(".top_'+hint_type+'").fadeOut(500)', 3000)
    }
}
