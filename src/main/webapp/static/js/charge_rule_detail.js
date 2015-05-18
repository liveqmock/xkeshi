$(function(){
    $('.pl_add_a').click(function(){
        var self = $(this),
            wrap = self.parent().next(),
            clone_cont = $('.pl_cont_clone').clone()
        clone_cont.removeClass('pl_cont_clone')
        wrap.append(clone_cont)
    })
    $('.pl_del_a').live('click', function(){
        $(this).parents('.pl_cont_wrap').remove()
    })
    $('.pl_rd').live('change', function(){
        var self = $(this),
            val = self.data('val'),
            wrap = self.parents('.pl_item2'),
            td_wraps = wrap.find('.pl_cont_wrap'),
            add_btn = wrap.find('.pl_add_a')
        if(val=='1'){
            td_wraps.each(function(){
                $(this).hide()
            })
            add_btn.hide()
        }else{
            td_wraps.each(function(){
                $(this).show()
            })
            add_btn.show()
        }
    })
    // $('.pl_rd').change(function(){
    //     var self = $(this),
    //         rd = $('.pl_rd:checked')
    //     if(rd.attr('id')=='pl_rd1'){
    //         $('.pl_item2').find('input[type="text"]').each(function(){
    //             $(this).attr('disabled', true)
    //             $(this).parent().addClass('disabled')
    //         })
    //         $('.pl_item2').find('a').each(function(){
    //             $(this).hide()
    //         })
    //     }else{
    //         $('.pl_item2').find('input[type="text"]').each(function(){
    //             $(this).removeAttr('disabled')
    //             $(this).parent().removeClass('disabled')
    //         })
    //         $('.pl_item2').find('a').each(function(){
    //             $(this).show()
    //         })
    //     }
    // })
    // function charge_money_change(){
    //     if($.trim($('.charge_money1').val())){
    //         $('.pl_item_cover').hide()
    //     }else{
    //         $('.pl_item_cover').show()
    //     }
    // }
    // charge_money_change()
    // $('.charge_money1').keyup(charge_money_change)

    $('.pl_input').live('focus', function(){
        $(this).parent().css({'border-color':'#c0c0c0'})
    })
    $('.select1').live('change', function(){
        var self = $(this)
        select1_check(self)
    })
    $('.select1').each(function(){
        select1_check($(this))
    })
    function select1_check(self){
        var next = self.next()
        if(self.find('option:checked').data('type')=='0'){
            next.find('input').val('')
            next.hide()
        }else{
            next.show()
        }
    }
    $('.btn_submit').on('click',function(){
        var  $this=$(this);
        var err_text = '',
            btn = $(this),
            form = btn.parents('form')
        $('.pl_input:visible').each(function(){
            var self = $(this),
                val = $.trim(self.val())
                if(!(isInt(val) && val>=0 && val<=9999999)){
                    self.parent().css({'border-color':'#f20'})
                    err_text = '金额必须为不大于9999999的整数'
                }
        })
        if(err_text){
            alert(err_text)
            return false
        }else{
            $('.gift_money').each(function(){
                var self = $(this),
                    val = $.trim(self.val()),
                    val1 = self.parents('.pl_cont').find('input:first').val()
                if(parseInt(val1)<parseInt(val)){
                    self.parent().css({'border-color':'#f20'})
                    err_text = '赠送金额不能大于首充金额，请修改'
                }
            })
        }
        if(err_text){
            alert(err_text)
            return false
        }
        c1 = form.find('.first_charge'),
            c2 = form.find('.more_charge')
        id = form.data('id'),
            tb_val_arr = [],
            item_arr1 = [],
            item_arr2 = []
/*        if(!c1.find('.pl_cont_wrap:visible')[0]){
            return
        }*/
        c1.find('.pl_cont_wrap').each(function(){
            var wrap = $(this),
                item = wrap.find('.pl_input:first').val()+'_'+wrap.find('select').find('option:selected').val()+'_'+wrap.find('.gift_money').val()
            item_arr1.push(item)
        })
        if(form.find('.pl_rd:checked').data('val')==2){
            c2.find('.pl_cont_wrap').each(function(){
                var wrap = $(this),
                    item = wrap.find('.pl_input:first').val()+'_'+wrap.find('select').find('option:selected').val()+'_'+wrap.find('.gift_money').val()
                item_arr2.push(item)
            })
        }
        
        $this.prev().val(item_arr1.join('|')+'||'+item_arr2.join('|'));
        form.attr('action','member_type/'+form.data('id')+'/rule/update?bId='+BID+'&bType='+BTYPE)
        form.submit();
    })
})