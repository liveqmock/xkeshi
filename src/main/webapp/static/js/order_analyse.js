$(function () {
    $('.set_day').click_drop({'drop':$('.pop_check')})
    $('.add_time_cb').change(function(){
    	var self = $(this)
    	if(self.is(":checked")){
    		$('.pb_item_hide').show()
    	}else{
    		$('.pb_item_hide').hide()
    	}
    })
    $('.pb_cancel_a, .pb_close').click(function(){
        if($(this).parents('.pb').hasClass('pop_check2')){
            return false
        }
        $('.black_cover').remove()
    })
    var chart = $('.chart'),
        SHOP_ID = null,
        TYPE = null,
        TODAY = get_today_date(),
        TEXT = '点单量',
        QUANTIFIER = '笔',
        shop_a = $('.chart_nav_shop'),
        shop_a_href = shop_a.attr('href'),
        nav_shop = $('.chart_nav_shop'),
        report_a = $('.report_a'),
        report_a_url = report_a.attr('href'),
        SDATE1 = null,
        SDATE2 = null,
        EDATE1 = null,
        EDATE2 = null,
        isResponsed = true

        window.render_data = function(shop_id, start_date_1, end_date_1, start_date_2, end_date_2, type){
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

        $('body').append($('<div id="showloading" style="width:' + width + 'px; height: ' + height + 'px;' +
            'position: absolute; top: ' + offset.top + 'px; left: ' + offset.left/1.2 + 'px;z-index:2;"></div>'))

        $.postJSON('/statistics/order/trend', {
            "shop_id" : shop_id,
            "start_date_1" : start_date_1,
            "end_date_1" : end_date_1,
            "start_date_2" : start_date_2,
            "end_date_2" : end_date_2,
            "type" : type
        }, function(o){
            if(!o.is_success){
                return
            }
            var hdata = o.hourly_statistics,
                ddata = o.daily_statistics
            $('.today').find('.day_val').html(o.today+'<em>'+QUANTIFIER+'</em>')
            $('.yesterday').find('.day_val').html(o.yesterday+'<em>'+QUANTIFIER+'</em>')
            $('.seven').find('.day_val').html(o.last_week+'<em>'+QUANTIFIER+'</em>')
            $('.thirty').find('.day_val').html(o.last_month+'<em>'+QUANTIFIER+'</em>')
            day_block_resize()
            if(hdata.length){
                if(hdata.length==1){
                    //页面刚加载默认情况
                    var hd_arr = hdata[0].hourly_detail,
                        x_arr = [],
                        y_arr1 = [],
                        y_arr2 = [],
                        y_arr3 = [],
                        today = TODAY,
                        total = hdata[0].today_total_amount,
                        tb_html = '<tr class="th">'+
        					'<td class="td_date">时间</td>'+
        					'<td class="td_num">'+TEXT+'</td>'+
        					'<td class="td_rate1">与前一天对比</td>'+
        					'<td class="td_rate2">与上周同期对比</td></tr>'
                    for(var i=0;i<hd_arr.length;i++){
                        var item = hd_arr[i],
	                        hour = item.hour,
	                        amount = item.today_amount,
	                        yest = item.yesterday_amount,
	                        lastweek = item.last_week_amount,
	                        yest_rate = (yest*amount==0)?'-':(((amount-yest)*100)/yest).toFixed(2) + '%',
	                        lastweek_rate = (lastweek*amount)==0?'-':(((amount-lastweek)*100)/lastweek).toFixed(2) + '%'
                        x_arr.unshift(hour)
                        y_arr1.unshift(amount)
                        y_arr2.unshift(yest)
                        y_arr3.unshift(lastweek)
                        var bg = i%2==0?'tr_bg':''
                        if(float_plus(amount,-yest)>0){
                        	var arrow1 = '<em class="up_arrow"></em>'
                        }else if(float_plus(amount,-yest)==0){
                        	var arrow1 = '<em class="ping_arrow"></em>'
                        }else if(float_plus(amount,-yest)<0){
                        	var arrow1 = '<em class="down_arrow"></em>'
                        }
                        if(float_plus(amount,-lastweek)>0){
                        	var arrow2 = '<em class="up_arrow"></em>'
                        }else if(float_plus(amount,-lastweek)==0){
                        	var arrow2 = '<em class="ping_arrow"></em>'
                        }else if(float_plus(amount,-lastweek)<0){
                        	var arrow2 = '<em class="down_arrow"></em>'
                        }
                        tb_html += '<tr class="tr '+bg+'">'+
                        '<td class="td_date">'+hour+'</td>'+
                        '<td class="td_num">'+amount+'</td>'+
                        '<td class="td_rate1">'+arrow1+(float_plus(amount,-yest)>0?('+'+float_plus(amount,-yest)):float_plus(amount,-yest))+'('+yest_rate+')'+'</td>'+
                        '<td class="td_rate2">'+arrow2+(float_plus(amount,-lastweek)>0?('+'+float_plus(amount,-lastweek)):float_plus(amount,-lastweek))+'('+lastweek_rate+')'+'</td></tr>'
                    }
                    $('.tb_list').html(tb_html)
                    chart.highcharts({
                        colors:[
                            '#b7b7b7',
                            '#ffb642',
                            '#e56f41'
                        ],
                        xAxis: {
                            categories: x_arr,
                            labels: {
                                formatter:function(){
                                    return (this.value).substr(0,2)
                                }
                            }
                        },
                        yAxis: {
                            title: {
                                text: ''
                            },
                            labels: {
                                formatter:function(){
                                	if(parseInt(this.value)<0){
                                		return ''
                                	}else{
                                		return this.value
                                	}
                                }
                            }
                        },
                        series: [{
						    name: TEXT,
						    data: y_arr3
						},{
                            name: TEXT,
                            data: y_arr2
                        },{
                            name: TEXT,
                            data: y_arr1
                        }],
                        legend: {
                            enabled: false
                        },
                        title: {
                            text: ''
                        },
                        credits: {
                            text: ''
                        },
                        exporting: {
                            enabled: false
                        }
                    })
                    $('.chart_r').html('<div class="chart_data chart_data1">'+
                        '<p class="chart_key">日期：'+start_date_1+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+total+'</p></div>'+
                        '<div class="chart_data chart_data2">'+
                        '<p class="chart_key">前一天：'+date_plus(start_date_1, -1)+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+hdata[0].yesterday_total_amount+'</p></div>'+
                        '<div class="chart_data chart_data3">'+
                        '<p class="chart_key">上周同期：'+date_plus(start_date_1, -7)+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+hdata[0].last_week_total_amount+'</p></div>'
                    )
                    shop_a.attr('href', shop_a_href+'?date1='+start_date_1+'&date2='+end_date_1+'&day_block='+$('.day_block_on').data('key')+'&type='+type)
                    if(!$('.selex_item_on').data('id')){
                    	nav_shop.show()	
                    }
                    report_a.show()
                }else if(hdata.length==2){
                    var hd_arr1 = hdata[0].hourly_detail,
                        hd_arr2 = hdata[1].hourly_detail,
                        x_arr1 = [],
                        y_arr1 = [],
                        x_arr2 = [],
                        y_arr2 = [],
                        today = TODAY,
                        total1 = hdata[0].today_total_amount,
                        total2 = hdata[1].today_total_amount,                        
                        tb_html = '<tr class="th"><td style="padding-left:28px;">时间</td><td>参考日期'+TEXT+'（'+start_date_1+'）</td><td>对比日期'+TEXT+'（'+start_date_2+'）</td><td>变化</td></tr>'
                    for(var i=0;i<hd_arr1.length;i++){
                        var item1 = hd_arr1[i],
                            hour1 = item1.hour,
                            amount1 = item1.today_amount
                        x_arr1.unshift(hour1)
                        y_arr1.unshift(amount1)
                        var item2 = hd_arr2[i],
                            hour2 = item2.hour,
                            amount2 = item2.today_amount
                        x_arr2.unshift(hour2)
                        y_arr2.unshift(amount2)
                        var change = float_plus(amount1, -amount2),
                        	bg = i%2==0?'tr_bg':'',
                        	rate = (amount1*amount2)==0?'-':(((amount1-amount2)*100)/amount2).toFixed(2) + '%'
                            if(float_plus(amount1,-amount2)>0){
                            	var arrow = '<em class="up_arrow"></em>'
                            }else if(float_plus(amount1,-amount2)==0){
                            	var arrow = '<em class="ping_arrow"></em>'
                            }else if(float_plus(amount1,-amount2)<0){
                            	var arrow = '<em class="down_arrow"></em>'
                            }
                        tb_html += '<tr class="tr '+bg+'">'+
                        '<td class="td_date">'+hour1+'</td>'+
                        '<td class="td_num">'+amount1+'</td>'+
                        '<td class="td_num">'+amount2+'</td>'+
                        '<td class="td_rate3">'+arrow+'<b>'+(change>0?('+'+change):change)+'('+rate+')</b></td></tr>'
                    }
                    $('.tb_list').html(tb_html)
                    chart.highcharts({
                        colors:[
                            '#ffb642',
                            '#e56f41'
                        ],
                        xAxis: {
                            categories: x_arr1,
                            labels: {
                                formatter:function(){
                                    return (this.value).substr(0,2)
                                }
                            }
                        },
                        yAxis: {
                            title: {
                                text: ''
                            },
	                        labels: {
	                            formatter:function(){
	                            	if(parseInt(this.value)<0){
	                            		return ''
	                            	}else{
	                            		return this.value
	                            	}
	                            }
	                        }                        
                        },
                        series: [{
                            name: TEXT,
                            data: y_arr2
                        },{
                            name: TEXT,
                            data: y_arr1
                        }],
                        legend: {
                            enabled: false
                        },
                        title: {
                            text: ''
                        },
                        credits: {
                            text: ''
                        },
                        exporting: {
                            enabled: false
                        }
                    })
                    $('.chart_r').html('<div class="chart_data chart_data1">'+
                        '<p class="chart_key">'+start_date_1+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+total1+'</p></div>'+
                        '<div class="chart_data chart_data2">'+
                        '<p class="chart_key">'+start_date_2+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+total2+'</p></div>'+
                        '<div class="chart_data chart_change">'+
                        '<p class="chart_key">对比变化</p>'+
                        '<p class="chart_val">增长量 '+float_plus(total1, -total2)+'</p>'+
                        '</div>'
                    )
                    nav_shop.hide()
                    report_a.hide()
                }
            }
            if(ddata.length){
            	var x_len = 1,
            		data_len = ddata[0].date_detail.length
            	if(data_len>24){
            		x_len = Math.floor(data_len/8)
            	}
                if(ddata.length==1){
                    var dd_arr = ddata[0].date_detail,
                        x_arr = [],
                        y_arr = [],
                        today = TODAY,
                        total = ddata[0].total_amount,
                        tb_html = '<tr class="th">'+
    					'<td class="td_date">日期</td>'+
    					'<td class="td_num">'+TEXT+'</td></tr>'
    				for(var i=0;i<dd_arr.length;i++){
                        var item = dd_arr[i],
                            date = item.date,
                            amount = item.today_amount,
                            yest = item.yesterday_amount,
                            lastweek = item.last_week_amount,
                            yest_rate = yest==0?'-':(((amount-yest)*100)/yest).toFixed(2) + '%',
                            lastweek_rate = lastweek==0?'-':(((amount-lastweek)*100)/lastweek).toFixed(2) + '%'
                        x_arr.unshift(date)
                        y_arr.unshift(amount)
                        var bg = i%2==0?'tr_bg':''
                        tb_html += '<tr class="tr '+bg+'">'+
                        '<td class="td_date">'+date+'</td>'+
                        '<td class="td_num">'+amount+'</td></tr>'
                    }
                    $('.tb_list').html(tb_html)
                    if(!$('.tb_list_dd1')[0]){
                    	$('.tb_list').after('<div class="tb_fix tb_list_dd1"><table class="tb_main"><tr class="th"><td class="td_date">日期</td><td class="td_num">'+TEXT+'</td></tr></table></div>')
                    }
                    chart.highcharts({
                        colors:[
                            '#e56f41'
                        ],
                        xAxis: {
                            categories: x_arr,
                            labels: {
                                formatter:function(){
                                	if(data_len<=24){
                                		return (this.value).substr(8)
                                	}else{
                                		return (this.value).substr(5)
                                	}
                                }
                            },
                            tickInterval: x_len
                        },
                        yAxis: {
                            title: {
                                text: ''
                            },
                            labels: {
                                formatter:function(){
                                	if(parseInt(this.value)<0){
                                		return ''
                                	}else{
                                		return this.value
                                	}
                                }
                            }                            
                        },
                        series: [{
                            name: TEXT,
                            data: y_arr
                        }],
                        legend: {
                            enabled: false
                        },
                        title: {
                            text: ''
                        },
                        credits: {
                            text: ''
                        },
                        exporting: {
                            enabled: false
                        }
                    })
                    $('.chart_r').html('<div class="chart_data chart_data1">'+
                        '<p class="chart_key">'+x_arr[0]+' 至 '+x_arr[x_arr.length-1]+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+total+'</p></div>')
                    shop_a.attr('href', shop_a_href+'?date1='+start_date_1+'&date2='+end_date_1+'&day_block='+$('.day_block_on').data('key')+'&type='+type)
                    if(!$('.selex_item_on').data('id')){
                    	nav_shop.show()	
                    }
                    report_a.show()
                }else if(ddata.length==2){
                    var dd_arr1 = ddata[0].date_detail,
                        dd_arr2 = ddata[1].date_detail,
                        x_arr1 = [],
                        y_arr1 = [],
                        x_arr2 = [],
                        y_arr2 = [],
                        today = TODAY,
                        total1 = ddata[0].total_amount,
                        total2 = ddata[1].total_amount,                        
                        tb_html = '<tr class="th"><td style="padding-left:28px;">日期</td><td>'+TEXT+'</td><td>变化</td></tr>'
                    for(var i=0;i<dd_arr1.length;i++){
                        var item1 = dd_arr1[i],
                            date1 = item1.date,
                            amount1 = item1.today_amount
                        x_arr1.unshift(date1)
                        y_arr1.unshift(amount1)
                        var item2 = dd_arr2[i],
                            date2 = item2.date,
                            amount2 = item2.today_amount
                        x_arr2.unshift(date2)
                        y_arr2.unshift(amount2)
                        var change = float_plus(amount1, -amount2),
                        	bg = i%2==0?'tr_bg':'',
                        	rate = (amount1*amount2)==0?'-':(((amount1-amount2)*100)/amount2).toFixed(2) + '%'
                        if(float_plus(amount1,-amount2)>0){
                        	var arrow = '<em class="up_arrow"></em>'
                        }else if(float_plus(amount1,-amount2)==0){
                        	var arrow = '<em class="ping_arrow"></em>'
                        }else if(float_plus(amount1,-amount2)<0){
                        	var arrow = '<em class="down_arrow"></em>'
                        }
                        tb_html += '<tr class="tr '+bg+'">'+
                        '<td class="td_date" style="padding:12px 0 12px 28px;"><div class="td_split_div">'+date1+'</div>'+date2+'</td>'+
                        '<td class="td_num" style="padding-right:40px;"><div class="td_split_div">'+amount1+'</div>'+amount2+'</td>'+
                        '<td class="td_rate3" style="width:600px;">'+arrow+'<b>'+(change>0?('+'+change):change)+'('+rate+')</b></td></tr>'
                    }
                    $('.tb_list').html(tb_html)
                    if(!$('.tb_list_dd2')[0]){
                    	$('.tb_list_dd1').remove()
                    	$('.tb_list').after('<div class="tb_fix tb_list_dd2"><table class="tb_main"><tr class="th"><td style="padding-left:28px;">日期</td><td>'+TEXT+'</td><td>变化</td></tr></table></div>')
                    }
                    chart.highcharts({
                        colors:[
                            '#ffb642',
                            '#e56f41'
                        ],
                        xAxis: {
                            categories: x_arr1,
                            labels: {
                                formatter:function(){
                                	if(data_len<=24){
                                		return (this.value).substr(8)
                                	}else{
                                		return (this.value).substr(5)
                                	}
                                }
                            },
                            tickInterval: x_len
                        },
                        yAxis: {
                            title: {
                                text: ''
                            },
                            labels: {
                                formatter:function(){
                                	if(parseInt(this.value)<0){
                                		return ''
                                	}else{
                                		return this.value
                                	}
                                }
                            }                            
                        },
                        series: [{
                            name: TEXT,
                            data: y_arr2
                        },{
                            name: TEXT,
                            data: y_arr1
                        }],
                        legend: {
                            enabled: false
                        },
                        title: {
                            text: ''
                        },
                        credits: {
                            text: ''
                        },
                        exporting: {
                            enabled: false
                        },
                        tooltip: {
                            formatter: function() {
                                var arr = this.series.index==0?x_arr2:x_arr1
                                return '<b>'+arr[this.point.index] +'</b><br/>'+this.series.name +': '+ this.y
                            }
                        }
                    })
                    $('.chart_r').html('<div class="chart_data chart_data1">'+
                        '<p class="chart_key">'+start_date_1+' 至 '+end_date_1+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+total1+'</p></div>'+
                        '<div class="chart_data chart_data2">'+
                        '<p class="chart_key">'+start_date_2+' 至 '+end_date_2+'</p>'+
                        '<p class="chart_val">'+TEXT+' '+total2+'</p></div>'+
                        '<div class="chart_data chart_change">'+
                        '<p class="chart_key">对比变化</p>'+
                        '<p class="chart_val">增长量 '+(float_plus(total1, -total2))+'</p>'+
                        '</div>'
                    )
                    nav_shop.hide()
                    report_a.hide()
                }
            }
            isResponsed = true
            $('#showloading').remove()

        })
        report_a.attr('href', report_a_url+'?start_date_1='+start_date_1+' 00:00:00&end_date_1='+end_date_1+' 00:00:00&shop_id='+(shop_id?shop_id:''))
        SDATE1 = start_date_1
        SDATE2 = start_date_2
        EDATE1 = end_date_1
        EDATE2 = end_date_2
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
            wrap = self.parents('.selex_wrap'),
            url = $('.selex_wrap2').find('.selex_item_on').data('url')
        wrap.find('.selex_head').text(self.text())
        wrap.find('.selex_main').hide()
        wrap.find('.selex_item_on').removeClass('selex_item_on')
        self.addClass('selex_item_on')
        render_data(id, TODAY, TODAY, null, null, TYPE)
        $('.day_block_on').removeClass('day_block_on')
        $('.today').addClass('day_block_on')
        SHOP_ID = id
        if(!id){
        	nav_shop.show()
        }else{
        	nav_shop.hide()
        }
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
        render_data(id, SDATE1, EDATE1, SDATE2, EDATE2, type)
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
        if(!$('.add_time_cb').is(':checked')){
            render_data(SHOP_ID, date1, date2, null, null, TYPE)
        }else{
        	var date3 = wrap.find('.pb_date3').val(),
        		date4;
            if(!date3){
                alert('请选择日期')
                return false
            }
            if(date1==date3){
            	alert('比较时间段不能重合')
                return false
            }
            date4 = date_plus(date3, day)
            if(date4>TODAY){
                alert('对比开始日期或截止日期不能大于今天')
                return false
            }
            render_data(SHOP_ID, date1, date2, date3, date4, TYPE)
        }
        self.parent().find('.pb_cancel_a').click()
        $('.day_block_on').removeClass('day_block_on')
        $('.set_day').addClass('day_block_on')
    })
//    $('.analyse_btn2').click(function(){
//        var self = $(this),
//            wrap = self.parents('.pb'),
//            date1 = wrap.find('.pb_date1').val(),
//            date2 = wrap.find('.pb_date2').val(),
//            day = wrap.find('.pb_day').val()
//        if(!date1 || !date2){
//            alert('请选择日期')
//            return false
//        }
//        if(!day){
//            alert('请选择天数')
//            return false
//        }
//        if(!isInt(day) || day<=0){
//            alert('天数需为大于0的整数')
//            return false        	
//        }
//        if(date_plus(date1, day)>TODAY){
//            alert('时间范围不能超出当前日期')
//            return false
//        }
//        if(date_plus(date2, day)>TODAY){
//            alert('时间范围不能超出当前日期')
//            return false
//        }
//        $('.pb_cancel_a').click()
//        render_data(SHOP_ID, date1, date_plus(date1, day), date2, date_plus(date2,day), TYPE)
//    })
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
        render_data(SHOP_ID, TODAY, TODAY, null, null, TYPE)
    })
    $('.yesterday').click(function(){
        render_data(SHOP_ID, date_plus(TODAY, -1), date_plus(TODAY, -1), null, null, TYPE)
    })
    $('.seven').click(function(){
        render_data(SHOP_ID, date_plus(TODAY, -6), TODAY, null, null, TYPE)
    })
    $('.thirty').click(function(){
        render_data(SHOP_ID, date_plus(TODAY, -29), TODAY, null, null, TYPE)
    })
    if(DATE1 && DATE2){
    	TEXT = FROM_TYPE=='SUM'?'点单总金额':'点单量'
    	TYPE = FROM_TYPE
        QUANTIFIER = FROM_TYPE=='SUM'?'元':'笔'
        $('.selex_wrap2>.selex_head').text(FROM_TYPE=='SUM'?'点单总金额':'点单量')     	
    	render_data(null,DATE1,DATE2,null,null,FROM_TYPE)
    	$('.day_block_on').removeClass('day_block_on')
    	$('.day_block[data-key="'+DAY_BLOCK+'"]').addClass('day_block_on')
    }else{
    	$('.today').click()
    }
    var win = $(window)
    win.scroll(function(){
    	if(win.scrollTop()>=$('.tb_list').find('.tr:first').offset().top){
    		$('.tb_fix').show()
    	}else{
    		$('.tb_fix').hide()
    	}
    })
})

function date1_picked(){
	if(!$('.pb_date2').val()){
		$('.pb_date2').val($('.pb_date1').val())
	}
}