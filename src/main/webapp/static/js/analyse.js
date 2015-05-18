function day_block_resize(){
	$('.day_block').each(function(){
		var self = $(this),
			node = self.find('.day_val'),
			a = node.clone()
		a.find('em').remove()
		var val = a.text()
		if(val.length>8){
			node.css({'font-size':24-(val.length-8)*2})
		}
	})
}
