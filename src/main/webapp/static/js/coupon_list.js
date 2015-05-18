$(function(){
	$('.del_a').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_del')
		pop.css({'top':top+20, 'left':left-247+self.width()}).show()
	})
	$('.pb_cancel_a, .pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
	$('.filter_a').click(function(){
		var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.pop_filter')
		pop.css({'top':top+40, 'left':left-247+self.width()}).show()
	})
	$('.status_edit').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_status_edit')
		pop.css({'top':top+20, 'left':left-247+self.width()}).show()
	})
	$('.bind_activity').click(function(){
		var self = $(this),
		left = self.offset().left,
		top = self.offset().top,
		text = self.text(),
		pop = $('.pop_bind_activity')
		pop.css({'top':top+20, 'left':left-247+self.width()}).show()
	})
})