$(function () {

    // $('.chart').highcharts({
    //     colors:[
    //         '#e56f41'
    //     ],
    //     xAxis: {
    //         categories: ['一月', '二月', '三月', '四月', '五月']
    //     },
    //     yAxis: {
    //         title: {
    //             text: ''
    //         }
    //     },
    //     series: [{
    //         name: 'Tokyo',
    //         data: [7.0, 6.9, 9.5, 14.5, 6.2]
    //     }, {
    //         name: 'New York',
    //         data: [-0.2, 0.8, 5.7, 11.3, 7.9]
    //     }]
    // });
    $('.set_day').click_drop({'drop':$('.pop_check')})    
    $('.pb_cancel_a').click(function(){
        if($(this).parents('.pb').hasClass('pop_check2')){
            return false
        }
        $('.black_cover').remove()
    })
    var chart = $('.chart'),
        TYPE = 'AMOUNT',
        TODAY = get_today_date(),
        TEXT = '点单量',
        QUANTIFIER = '笔',        
        order_a = $('.chart_nav_a:first'),
        order_a_href = order_a.attr('href'),
        report_a = $('.report_a'),
        report_a_url = report_a.attr('href'),
        SDATE = null,
        EDATE = null
        isResponsed = true
    window.render_data = function(start_date, end_date, type){
    	 if(!isResponsed) {
             alert('点击不要太快')
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
             
         $('body').append($('<div id="showloading" style="width:' + width + 'px; height: ' + height + 'px;' +
            'position: absolute; top: ' + offset.top + 'px; left: ' + offset.left/1.2 + 'px;z-index:2;"></div>'))     
 
        $.postJSON('/statistics/order/shop/ratio', {
            "start_date" : start_date,
            "end_date" : end_date,
            "type" : type
        }, function(o){
            if(!o.is_success){
                return
            }
            var data = o.shop_ratios,
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
            var tb_html = '<tr class="th">'+
                '<td class="td_mht">商户</td>'+
                '<td class="td_amount">点单量</td>'+
                '<td class="td_amount_ratio">点单量占比</td>'+
                '<td class="td_sum">总金额</td>'+
                '<td class="td_sum_ratio">总额占比</td></tr>',
                r_html = '<tr class="th">'+
					'<td class="td_key">商户</td>'+
					'<td class="td_val">'+TEXT+'</td></tr>',
                r_amount = 0,
                pie_arr_ratio = 0
            for(var i=0;i<data.length;i++){
                var item = data[i],
                    id = item.shop_id,
                    name = item.shop_name,
                    amount = item.amount,
                    amount_ratio = item.amount_ratio,
                    sum = item.sum,
                    sum_ratio = item.sum_ratio
                var bg = i%2==0?'tr_bg':''
                tb_html += '<tr class="tr '+bg+'">'+
                '<td class="td_mht">'+name+'</td>'+
                '<td class="td_amount">'+amount+'</td>'+
                '<td class="td_amount_ratio">'+ (amount_ratio !== 0 ? amount_ratio.toFixed(2) : 0) +'%</td>'+
                '<td class="td_sum">'+sum+'</td>'+
                '<td class="td_sum_ratio">'+ (sum_ratio !== 0 ? sum_ratio.toFixed(2) : 0) +'%</td></tr>'
                if(i<5){
                    r_html += '<tr class="tr">'+
                    '<td class="td_key" title="'+name+'"><span class="color_block" style="background:'+color_arr[i]+'"></span>'+(name.length>8?name.substr(0, 8):name)+'</td>'+
                    '<td class="td_val">'+(type=='SUM'?sum:amount)+'</td></tr>'
                    pie_arr.push([name, type=='SUM'?sum_ratio:amount_ratio])
                    pie_arr_ratio = float_plus(pie_arr_ratio, (type=='SUM'?sum_ratio:amount_ratio))
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
            $('.tb_list').html(tb_html)
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
            order_a.attr('href', order_a_href+'?date1='+start_date+'&date2='+end_date+'&day_block='+$('.day_block_on').data('key')+'&type='+type)
            if(start_date == end_date){
            	$('.chart_title').text(start_date+' 的 '+TEXT).show()
            }else{
            	$('.chart_title').text(start_date+' 至 '+end_date+' 的 '+TEXT).show()
            }
            isResponsed = true
            $('#showloading').remove()
        })
        report_a.attr('href', report_a_url+'?start_date='+start_date+' 00:00:00&end_date='+end_date+' 00:00:00&type='+type)
        SDATE = start_date
        EDATE = end_date
    }

    $('.selex_wrap').each(function(){
        var selex = $(this),
            selex_main = selex.find('.selex_main'),
            selex_head = selex.find('.selex_head')
        selex_head.click(function(){
        	$('.selex_main').hide()
            if(selex_main.is(':visible')){
                selex_main.hide()   
            }else{
                selex_main.show()
            }
        })
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
        render_data(SDATE, EDATE, type)
        TYPE = type
        TEXT = self.text()
        QUANTIFIER = type=='SUM'?'元':'笔'        
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
        render_data(date1, date2, TYPE)
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
        render_data(TODAY, TODAY, TYPE)
    })
    $('.yesterday').click(function(){
        render_data(date_plus(TODAY, -1), date_plus(TODAY, -1), TYPE)
    })
    $('.seven').click(function(){
        render_data(date_plus(TODAY, -6), TODAY, TYPE)
    })
    $('.thirty').click(function(){
        render_data(date_plus(TODAY, -29), TODAY, TYPE)
    })
    if(DATE1 && DATE2){
    	TEXT = FROM_TYPE=='SUM'?'点单总金额':'点单量'
        TYPE = FROM_TYPE    		
    	QUANTIFIER = FROM_TYPE=='SUM'?'元':'笔'
        $('.selex_wrap2>.selex_head').text(FROM_TYPE=='SUM'?'点单总金额':'点单量')    	    		
    	render_data(DATE1, DATE2, FROM_TYPE)
    	$('.day_block_on').removeClass('day_block_on')
    	$('.day_block[data-key="'+DAY_BLOCK+'"]').addClass('day_block_on')
    }else{
    	$('.today').click()
    }
})

function date1_picked(){
	if(!$('.pb_date2').val()){
		$('.pb_date2').val($('.pb_date1').val())
	}
}