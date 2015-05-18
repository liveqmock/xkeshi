$(function () {
    $('.set_day').click_drop({'drop':$('.pop_check')})    
    $('.pb_cancel_a').click(function(){
        if($(this).parents('.pb').hasClass('pop_check2')){
            return false
        }
        $('.black_cover').remove()
    })
    var chart = $('.chart'),
    	SHOP_ID = null,
        TYPE = 'SALES',
        TODAY = get_today_date(),
        TEXT = '商品销量',
        QUANTIFIER = '份',
        SDATE = null,
        EDATE = null,
        isResponsed = true
    window.render_data = function(shop_id, start_date, end_date, type){
        if(!isResponsed) {
            return
        }
        isResponsed = false

        if(!type){
            type = 'AMOUNT'
        }

        var cm = $('.chart_main'),
            height = cm.height(),
            width = cm.width(),
            offset = cm.offset()
         $('body').append($('<div id="showloading" style="width:' + width + 'px; height: ' + height + 'px;'+
            'position: absolute; top: ' + offset.top + 'px; left: ' + offset.left/1.2 + 'px;z-index:2;""></div>'))
        
        $.postJSON('/statistics/category/ratio', {
            "shop_id" : shop_id,
            "start_date" : start_date,
            "end_date" : end_date,
            "type" : type
        }, function(o){
            if(!o.is_success){
                return
            }
            var data = o.detail_list,
                pie_arr = [],
                color_arr = [
	                 '#33b0ea',
	                 '#5bc0ee',
	                 '#86d0f3',
	                 '#addff8',
	                 '#e1f3fd',
	                 '#dbdbdb'
                 ]
            $('.today').find('.day_val').html(o.today+'<em>'+QUANTIFIER+'</em>')
            $('.yesterday').find('.day_val').html(o.yesterday+'<em>'+QUANTIFIER+'</em>')
            $('.seven').find('.day_val').html(o.last_week+'<em>'+QUANTIFIER+'</em>')
            $('.thirty').find('.day_val').html(o.last_month+'<em>'+QUANTIFIER+'</em>')
            day_block_resize()
            var r_html = '<tr class="th">'+
					'<td class="td_key">品类</td>'+
					'<td class="td_val">'+(TEXT=='销售总金额'?'总金额':TEXT)+'（'+QUANTIFIER+'）</td></tr>',
                r_amount = 0,
                pie_arr_ratio = 0
            for(var i=0;i<data.length;i++){
                var item = data[i],
                    name = item.name,
                    amount = item.amount,
                    amount_ratio = item.amount_ratio
                if(i<5){
                    r_html += '<tr class="tr">'+
                    '<td class="td_key" title="'+name+'"><span class="color_block" style="background:'+color_arr[i]+'"></span>'+(name.length>8?name.substr(0, 8):name)+'</td>'+
                    '<td class="td_val">'+amount+'</td></tr>'
                    pie_arr.push([name, amount_ratio])
                    pie_arr_ratio = float_plus(pie_arr_ratio, amount_ratio)
                }else{
                    r_amount += amount
                }
            }
            var pie_arr_sum = 0
            for(var i=0;i<pie_arr.length;i++){
                pie_arr_sum += pie_arr[i][1]
            }            
            var other_ratio = float_plus(100,-pie_arr_ratio)
            if(data.length>5){
                pie_arr.push(['其他总量', other_ratio])            	
                r_html += '<tr class="tr">'+
                    '<td class="td_key"><span class="color_block" style="background:#dbdbdb;"></span>其他总量</td>'+
                    '<td class="td_val">'+r_amount+'</td></tr>'
            }
            $('.chart_rtb').html(r_html)
            if(pie_arr_sum!=0){
                $('.chart_none').hide()
                chart.highcharts({
                    chart: {
                        plotBackgroundColor: null,
                        plotShadow: false
                    },
                    colors: color_arr,
                    title: {
                        text: ''
                    },
                    credits: {
                        text: ''
                    },
                    tooltip: {
                        enabled: false
                    },
                    series: [{
                        type: 'pie',
                        name: '',
                        data: pie_arr
                    }],
                    plotOptions: {
                        pie: {
                            allowPointSelect: true,
                            cursor: 'pointer',
                            dataLabels: {
                                enabled: true,
                                format: '<b>{point.name}</b>: {point.percentage:.2f} %',
                                style: {
                                    color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                                }
                            }
                        }
                    },
                    exporting: {
                        enabled: false
                    }
                })
            }else{
                $('.chart_none').show()
            }
            if(start_date == end_date){
            	$('.chart_title').text(start_date+' 的 '+TEXT).show()
            }else{
            	$('.chart_title').text(start_date+' 至 '+end_date+' 的 '+TEXT).show()
            }

            isResponsed = true
            $('#showloading').remove()

        })
        SDATE = start_date
        EDATE = end_date
    }

    $('.selex_wrap').each(function(){
        var selex = $(this),
            selex_main = selex.find('.selex_main'),
            selex_head = selex.find('.selex_head')
        selex_head.click(function(){
            $('.selex_main').not(selex_main).hide()
            if(selex_main.is(':visible')){
                selex_main.hide()   
            }else{
                selex_main.show()
            }
        })
    })
    $('.selex_item1').click(function(){
        if(!isResponsed) {
             alert('点击不要太快')
             return
        }

        var self = $(this),
            id = self.data('id'),
            wrap = self.parents('.selex_wrap')
        wrap.find('.selex_head').text(self.text())
        wrap.find('.selex_main').hide()
        wrap.find('.selex_item_on').removeClass('selex_item_on')
        self.addClass('selex_item_on')
        render_data(id, TODAY, TODAY, TYPE)
        $('.day_block_on').removeClass('day_block_on')
        $('.today').addClass('day_block_on')
        SHOP_ID = id
    })
    $('.selex_item2').click(function(){
        if(!isResponsed) {
             alert('点击不要太快')
             return
        }

        var self = $(this),
            type = self.data('type'),
            wrap = self.parents('.selex_wrap'),
            id = $('.selex_wrap1').find('.selex_item_on').data('id')
        wrap.find('.selex_head').text(self.text())
        wrap.find('.selex_main').hide()
        wrap.find('.selex_item_on').removeClass('selex_item_on')
        self.addClass('selex_item_on')
        render_data(SHOP_ID, SDATE, EDATE, type)
        TYPE = type
        TEXT = self.text()
        QUANTIFIER = type=='SALES'?'份':'元'
    })
    $('.analyse_btn').click(function(){
        if(!isResponsed) {
             alert('点击不要太快')
             return
        }

        var self = $(this),
            wrap = self.parents('.pb'),
            date1 = wrap.find('.pb_date1').val(),
            date2 = wrap.find('.pb_date2').val(),
    		day = time_days(date1, date2)            
        if(!date1 || !date2){
            alert('请选择日期')
            return false
        }
        if(date1>date2){
            alert('开始日期不能大于截止日期')
            return false
        }
        if(date2>TODAY){
            alert('开始日期或截止日期不能大于今天')
            return false
        }
        if(day>89){
        	alert('时间段跨度不能超过90天')
            return false
        }        
        self.parent().find('.pb_cancel_a').click()
        render_data(SHOP_ID, date1, date2, TYPE)
        $('.day_block_on').removeClass('day_block_on')        
        $('.set_day').addClass('day_block_on')        
    })
    $('.day_block').click(function(){
        if(!isResponsed) {
             if(!$(this).hasClass('set_day')) {
                alert('点击不要太快')
            }
             return
        }

        var self = $(this)
        if(!self.hasClass('set_day')){
            $('.day_block_on').removeClass('day_block_on')
            self.addClass('day_block_on')
        }
    })
    $('.today').click(function(){
        render_data(SHOP_ID, TODAY, TODAY, TYPE)
    })
    $('.yesterday').click(function(){
        render_data(SHOP_ID, date_plus(TODAY, -1), date_plus(TODAY, -1), TYPE)
    })
    $('.seven').click(function(){
        render_data(SHOP_ID, date_plus(TODAY, -6), TODAY, TYPE)
    })
    $('.thirty').click(function(){
        render_data(SHOP_ID, date_plus(TODAY, -29), TODAY, TYPE)
    })
    $('.today').click()
})

function date1_picked(){
	if(!$('.pb_date2').val()){
		$('.pb_date2').val($('.pb_date1').val())
	}
}