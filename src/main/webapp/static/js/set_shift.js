$(function(){
    function rd_use_change(){
        if(!IE8){
            if($('.use_rd:checked').attr('id')=='use1'){
                 $('.show_cover').hide()
                 $('.show_rd').removeAttr('disabled')
                $('.service_tr .tr_bg').css({'background-color':'#fbfbfb'})
                if ($('#show1').data('visibleshiftreceivabledata') == false) {
                    $('#show1').removeAttr('checked', 'checked')
                    $('#show1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
                    $('#show2').attr('checked', 'checked')
                    $('#show2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
                } 
            }else{
                $('.show_cover').show()
                $('.service_tr .tr_bg').css({'background-color':'#f1f1f1'})
                $('.show_rd').attr('disabled', 'disabled')
                $('#show1').removeAttr('checked', 'checked')
                $('#show1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
                $('#show2').attr('checked', 'checked')
                $('#show2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
            }
        }else{
             if($('.use_rd:checked').attr('id')=='use1'){
                 $('.show_rd').attr('disabled',false)
                 $('#use1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
                 $('#use2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
                 $('.service_tr .tr_bg').css({'background-color':'#fbfbfb'})
                 $('.show_rd').change(function(){
                    if($('.show_rd:checked').attr('id')=='show1'){
                         $('#show1').attr('disabled',false)
                         $('#show1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
                         $('#show2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
                    }else{
                         $('#show2').attr('disabled',false)
                         $('#show2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
                         $('#show1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
                    }
                 });
            }else{
                $('.show_cover').show()
                $('.show_rd').attr('disabled',true)
                $('#use2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
                $('#use1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
                $('.service_tr .tr_bg').css({'background-color':'#f1f1f1'})

            }
        }        
    }
    $('.use_rd').change(rd_use_change);

    if ( $('#use1').data('enableshift') == false && $('#show1').data('visibleshiftreceivabledata') == false) {
    	$('#show1').removeAttr('checked', 'checked')
        $('#show1').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd')
        $('#show2').attr('checked', 'checked')
        $('#show2').next().removeClass('lb_rd_on lb_rd').addClass('lb_rd_on')
    }else{
    	$('.show_cover').hide()
        $('.service_tr .tr_bg').css({'background-color':'#f1f1f1'})
    }
    $('.pb_btn_bottom').click(function(){
        $('.black_cover, .pop_cfm').show()
    })
    $('.cfm_btn').click(function(){
        $('.check_form:first').submit()
    })
    $('.pb_close').click(function(){
        $('.black_cover').hide()
    })
    $('.cancel_btn').click(function(){
    	$('.black_cover , .pop_cfm').hide()
    })

})