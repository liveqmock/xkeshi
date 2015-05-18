<#import "/macro.ftl" as m>

<@m.page_header subselected='landmark'  selected="setting"  css="seller_list_new" />
<script>
$(function(){
		$(".position").click(function(){
		var iHeight = 720 ;
	 	var iWidth  = 990 ;
    	var iTop = (window.screen.availHeight-30-iHeight)/2;        
    	var iLeft = (window.screen.availWidth-10-iWidth)/2;        
		window.open($(this).data("href"), "",
					"height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no"
					); 
		})
})
</script>
<div class="rwrap">
	<div class="r_title"><span class="fl">地标列表</span>
		<div class="search_wrap">
			<form class="search_form fl" action="${base}/position/landmark">
				<input type="text" name="key" <#if searcher?? && searcher.key?? >value="${searcher.key}"</#if> placeholder="地标名称" class="search_input"/>
				<button class="search_btn"></button>
				</form>
			<a href="javascript:void(0);"  data-href="${base}/position/maintainlandmarkposition?type=add" class="new_a position fl ml12"  ></a>
		</div>
	</div>
	<table class="tb_main">
		<#list landmarks as landmark>
			<td class="name">
				<a  class="b_a position" href="javascript:void(0);" 
					data-href="/position/maintainlandmarkposition?type=edit&id=${(landmark.id)!}&name=${(landmark.name)!}&position.id=${(landmark.position.id)!}&position.latitude=${(landmark.position.latitude )!}&position.longitude=${(landmark.position.longitude )!}&radius=${(landmark.radius )!}&cityCode=${(city_code)!}"
					 >
					<p>${(landmark.name)!}</p>
				</a>
			</td>
		</#list>
	</table>
</div>
<@m.page_footer />


 


