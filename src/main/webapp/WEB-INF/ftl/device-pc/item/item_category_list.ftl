<#import "/macro.ftl" as m>

<@m.page_header title="分类管理" subselected='setting' js='validate' css='seller_list_new' selected='cashiering' />
<div class="rwrap">
	
	<div class="r_title"><span class="fl">商品类目管理</span><div class="search_wrap"><a href="javascript:void(0);" class="pop_new new_a"></a></div></div>
	
	<table class="tb_main">
		<tr class="th">
			<td class="pro_name" style="width:300px">名称</td>
			<td class="sequence" style="width:300px">顺序</td>
			<td class="opt">操作</td>
		</tr>
		<#list categories as category>
		<tr <#if category_index%2==0>class="tr_bg"</#if>>
			<td class="pro_name" style="width:300px">${(category.name)!}</td>
			<td class="sequence" style="width:300px">${(category.sequence)!}</td>
			<td class="opt">
			  <a href="javascript:void(0);" class="edit_item b_a" data-id="${(category.id)!}" style="margin-right:16px;">编辑</a>
			  <#--
			  <a  class="b_a"  href="javascript:deleteItem(${(category.id)!});">删除</a>-->
			  <a  class="del_attribute b_a pop_a"  href="javascript:" data-no="${(category.id)!}" data-name="${(category.name)!}" data-pop="pop_del" data-para="${base}/item/category/${(category.id)!}">删除</a>
			</td>
		</tr>
		</#list>
	</table>
	
	<#if error>
		<div class="pop_hint pop_hint3">${error}</div>
	</#if>
	<#if success>
		<div class="pop_hint pop_hint2">${success}</div>
	</#if>

	<div class="pb add_opt">
		<form action="${base}/item/category" method="POST" class="check_form">
		<input type="hidden" name="submissionToken" value="${(submissionToken)!''}"/>
		<input type="hidden" name="id" value="" />
		<div class="pb_title"><span class="sp_title">添加商品分类</span></div>
		<div class="pb_main">
			<div class="pb_item" style="border:none;">
				<p class="pb_item_title">名称</p>
				<input type="text" name="name" data-hint="名称" class="pb_item_input isneed" />
			</div>
			<div class="pb_item">
				<p class="pb_item_title">顺序</p>
				<input type="text" name="sequence" data-hint="顺序" value="50" class="pb_item_input isneed isint sequence_val">
			</div>
		</div>
		<button class="pb_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="pb pop_del">
			<form action="" method="post">
				<input type="hidden" name="_method" value="delete"/> 
				<input type="text" class="id" name="id" style="display:none">
				<div class="pb_title">删除</div>
				<div class="pb_main2">确认删除类目<span class="pb_name name_key"></span></div>
				<button type="button" id="smt_btn" class="pb_btn pb_btn_s" >确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
			</form>
	</div>
</div>
	<script type="text/javascript">
		function deleteItem(id){
				if(id!==''||id!==null){
					$.ajax({
						url:'${base}/item/category/'+id,
						type:'delete',
						dataType :'json',
						success:function(data){
							if(data.status == 'success') {
								$('.pop_suss').text(data.description).addClass('pop_hint2').show();
								location.reload();
							}else if(data.status == 'faild') {
								$('.pop_suss').text(data.description).addClass('pop_hint3').show();
								location.reload();
							}else {
								$('.pop_suss').text(data.description).addClass('pop_hint3').show();
								location.reload();
							}
							
						}
					});
				}
		}
	$(function(){
		$('.pop_new').click(function(){
			$('.sp_title').text('添加商品分类');
			var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.add_opt');
			pop.find("input[name='name']").val('');
			pop.find("input[name='id']").val('');
			pop.find("input[name='sequence']").val('50');
			pop.css({'top':top+16, 'left':left-243+self.width()}).show();
		});
		
		$('.edit_item').click(function(){
			$('.sp_title').text('修改商品分类');
			var self = $(this),
			left = self.offset().left,
			top = self.offset().top,
			text = self.text(),
			pop = $('.add_opt');
			pop.find("input[name='name']").val($(this).parent().parent().find(".pro_name").html());
			pop.find("input[name='id']").val($(this).data('id'));
			pop.find("input[name='sequence']").val($(this).parent().parent().find(".sequence").html());
			pop.css({'top':top+16, 'left':left-243+self.width()}).show();
		});

		$('.pb_close').click(function(){
			var self = $(this);
			self.parents('.pb').hide();
		})
		$('.pb_btn_s').click(function(){
	    	if($('.sequence_val').val()>100){
	    		alert('顺序的值不能大于100');
	    		return false;
	    	}
	    })
	    $('.pop_del .pb_btn_s').click(function(){
				deleteItem($('.id').val());
		})

	})
	
	</script>

 <div class="pop_hint pop_suss" ></div>
<@m.page_footer />




