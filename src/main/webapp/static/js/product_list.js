$(function(){
    check_all_none($('#checkAll'), $('.check_item'))
    $('.storage_a').click(function(){
    	var a = $(this),
    		href = a.data('url'),
    		ids = []
    	$('.check_item:checked').each(function(){
    		var self = $(this),
    			id = self.parents('tr').data('id')
    		id && ids.push(id)
    	})
    	if(!ids.length){
    		alert('请勾选商品')
    		return false
    	}
    	location.href = href + '?ids=' + ids.join(',')
    })
})