$(function(){
    var all,
        now,
        url
    $('.page_wrap').each(function(){
        var self = $(this),
            html = ''
        all = parseInt(self.data('all')),
        now = parseInt(self.data('now')),
        url = self.data('url')
        if(now==1){
            html += '<a href="javascript:" class="page_a page_prev page_disabled"></a> '
        }else{
            html += '<a href="'+url+'pageTo='+(now-1)+'" class="page_a page_prev"></a> '
        }
        if(all<11){
            html += render_page(1, all, url, now)
        }else{
            if(now<=3 || now>=all-2){
                html += render_page(1, 3)
                html += '<span class="page_dot">…</span>'
                html += render_page(all-2, all)
            }else if(now==4 || now==5){
                html += render_page(1, now)
                html += '<span class="page_dot">…</span>'
                html += render_page(all-2, all)
            }else if(now==all-3 || now==all-4){
                html += render_page(1, 3)
                html += '<span class="page_dot">…</span>'
                html += render_page(now, all)
            }else{
                html += render_page(1, 3)
                html += '<span class="page_dot">…</span>'
                html += render_page(now-1, now+1)
                html += '<span class="page_dot">…</span>'
                html += render_page(all-2, all)
            }
        }
        if(now==all){
            html += '<a href="javascript:" class="page_a page_next page_disabled"></a> '
        }else{
            html += '<a href="'+url+'pageTo='+(now+1)+'" class="page_a page_next"></a> '
        }
        self.append(html)
    })

    function render_page(from, to){
        var html = ""
        for(var i=from;i<=to;i++){
            if(i==now){
                html += '<a href="javascript:" class="page_a page_a_now">'+i+'</a> '
            }else{
                html += '<a href="'+url+'pageTo='+i+'" class="page_a">'+i+'</a> '
            }
        }
        return html
    }
})