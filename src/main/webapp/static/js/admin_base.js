$(function(){
    left_init()    
    $(window).resize(function(){
        left_init()
    })
})


function left_init(){
	var wh = $('#Wrapper').height(),
		rh = $('.r_wrap').height(),
		th = $('#Top').height(),
		sh = screen_height(),
		mh = Math.max(wh, rh)
    if((mh+th)<sh){
    	$('#Wrapper').height(sh-th)
    }else{
    	$('#Wrapper').height(mh)
    }
    $('.l_wrap').height($('#Wrapper').height())
}

function left_init_callback(){
    $('#Wrapper, .lnav').css('height','auto')
    left_init()
}