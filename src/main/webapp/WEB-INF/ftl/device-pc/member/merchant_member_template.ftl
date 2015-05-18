<#import "/macro.ftl" as m>
<@m.page_header title="会员属性模板" css='seller_list_new|member_template' selected="member" subselected='template' js="merchant_member_template|list_filter|validate" />
<script type="text/javascript">
  var BASE =  "${(base)!}";
  var TEMPLATE_ID  = "${(template.id)!}";
  $(document).ready(function(){
  $('form').submit(function(){
  var sequence = $('.seq_num').val();
  if(sequence < 0 || sequence > 100){
   alert('排列序号大于等于0小于等于100');
   return false;
  }else{
  return true;
  }
  })
  })
</script>
<div class="rwrap">
	<div class="r_title"><span class="fl">会员属性模板</span>
		<div class="search_wrap">
		<#if shopEid??>
			<form action="${base}/member/merchant/${shopEid}/template/list" class="search_form f_form fl">
		<#else>
			<form action="${base}/member/merchant/template/list" class="search_form f_form fl">
		</#if>
				<input type="text" name="key"  value="${(searcher.key)!}" placeholder="名称" class="search_input fi_1">
				<button class="search_btn"></button>
			</form>
			<a  href="javascript:" class="new_a fl ml12"  ></a>
		</div>
		 <#if searcher != null && searcher.hasParameter >
			<div class="search_result">
				<p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
					<#if searcher.key><span class="b_tit" data-fid="1">${(searcher.key)!}<b>x</b></span></#if>
					</span>的结果</span>
				</p>
				<#if shopEid??>
					<a href="${base}/member/merchant/${shopEid}/template/list" class="s_clear">清空</a>
				<#else>
					<a href="${base}/member/merchant/template/list" class="s_clear">清空</a>
				</#if>
			</div>
		</#if>
	</div>	
	<table class="tb_main">
		<tr class="th">
			<td class="" style="width:50px;">编号</td>
			<td class="" style="width:130px;">名称</td>
			<td class="" style="width:50px;">类型</td>
			<td class="" style="width:150px;">选项</td>
			<td class="" style="width:50px;">启用</td>
			<td class="" style="width:50px;">顺序</td>
			<td class="" style="width:120px;">操作</td>
		</tr>
		<#list attirbuteList as attribute>
		<tr <#if attribute_index%2==0>class="tr_bg"</#if>>
			<td class="" style="width:50px;">${(attribute_index+1)!}</td>
			<td class="" style="width:130px;">${(attribute.name)!}</td>
			<td class="" style="width:50px;"><@spring.message "AttributeType.${(attribute.attributeType)!}"/></td>
			<td class="" ><div style="max-width:150px;">${(attribute.optionalValues)!}</div></td>
			<td class="" style="width:50px;">
			    <#if (attribute.enabled)!>
			          启用
			     <#else>
			          未启用 
			    </#if>
			</td>
			<td class="" style="width:50px;">${(attribute.sequence)!}</td>
			<td class="" style="white-space: nowrap">
				  <a href="javascript:void(0)" onclick="updateAttirbute(this,'${(attribute.id)!}','${(attribute.name)!}',
				                           '${(attribute.attributeType)!}','${(attribute.required)!?string("true","false")}',
				                           '${(attribute.enabled)!?string("true","false")}','${(attribute.sequence)!}')"
				   data-optionalvalues='${(attribute.optionalValues)!}' class="b_a">修改</a> 
					<a href="javascript:void(0)"  data-id="${(attribute.id)!}"  style="width:30px;" data-name="${(attribute.name)!}" class="del_attribute b_a">删除</a>
			</td>
		</tr>
		</#list>
	</table>
	<div class="pb pbmt add_opt tb_wrap">
	<#if shopEid??>
		<form name="attrPostForm"  id="attrPostForm" action="/member/merchant/${shopEid}/attribute/save" method="post">
	<#else>
		<form name="attrPostForm"  id="attrPostForm" action="/member/merchant/attribute/save" method="post">
	</#if>
	    	<input type="hidden" name="memberAttributeTemplate.id" value="${(template.id)!}"   />
			<input type="hidden" name="_method" value="put"  id="methodId">
			<input type="hidden" name="id"  id="memberAttributeId">
			<input type="hidden" name="shopEid"  value="${(shopEid)!}"  id="shopEid">
			<input type="hidden" name="submissionToken" value="${submissionToken}"/>
			<div class="pb_title"><span class = "pb_title_sp">添加会员属性</span></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_title2">排列序号</p>
					 <input type="text" name="sequence" id="sequence" class="pb_input seq_num" value="${(productAttribute.sequence)!50}" title="只允许输入零或正整数" />
				</div>
				<div class="pb_item">
					<p class="pb_title2">是否必填</p>
					<div class="pb_rd_item"><input type="radio" class="pb_rd" id="pb_rd1" name="required" value="true"<#if (productAttribute.required == true)!> checked</#if>><label class="pb_lb" for="pb_rd1">必填</label></div>
					<div class="pb_rd_item"><input type="radio" class="pb_rd" id="pb_rd2" name="required" value="false"<#if (productAttribute.required == false)!> checked</#if>><label class="pb_lb" for="pb_rd2">选填</label></div>
				</div>
				<div class="pb_item">
					<p class="pb_title2">是否启用</p>
					<div class="pb_rd_item"><input type="radio" class="pb_rd" id="pb_rd3" name="enabled" value="true"<#if (productAttribute.enabled == true)!> checked</#if>><label class="pb_lb" for="pb_rd3">启用</label></div>
					<div class="pb_rd_item"><input type="radio" class="pb_rd" id="pb_rd4" name="enabled" value="false"<#if (productAttribute.enabled == false)!> checked</#if>><label class="pb_lb" for="pb_rd4">停用</label></div>
				</div>
				<div class="pb_item">
					<p class="pb_title2">名称</p>
					<input type="text" class="pb_input lh14" name="name" id="templateAttributeName" >
				</div>
				<div class="pb_item">
					<p class="pb_title2">类型</p>
					<select id="productAttributeType" name="attributeType" class="select-l pb_sel" >
						<option value="">请选择...</option>
						<#list allAttributeType as list>
							<option value="${(list)!''}"<#if (list == productAttribute.attributeType)!> selected </#if>>
							<@spring.message "AttributeType.${(list.toString())!}"/>
						    </option>
						</#list>
					</select>
				</div>
				<div class="pb_item pb_select_wrap">
					<p class="pb_title2">可选内容</p>
					<#if (productAttribute.attributeType == "select" || productAttribute.attributeType == "checkbox")!>
					<#list productAttribute.attributeOptionList as list>
						<p class="input_item lh14"  id="p_select_wrap"><input type="text" class="input-m pb_input" name="optionalValues" value="${(list)!}" id="optionalValues"><a href="javascript:" class="b_a icon-del">删除</a></p>					    
					</#list>
					</#if>
					<p class="input_item"><a href="javascript:void(0)" class="b_a btn-s option-add">添加更多可选内容</a></p>
				</div>
				<div class="pb_item pb_select_none">
					<p class="pb_title2">可选内容</p>
					<p style="color:#666667;font-size:12px;">无</p>
				</div>
			</div>
			<button class="pb_add_form_btn pb_btn pb_btn_s"  type="button">确定</button>
			<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />  
	</div>
	<div class="pb pop_del">
		<form id="memberTemplateForm"  name="memberTemplateForm"  method="post">
		    <input type="hidden" name="_method" value="DELETE">
		    <input type="hidden" name="shopEid"  value="${(shopEid)!}"  id="shopEid">
		    <div class="pb_title">
		    <span class="pb_title">确认删除会员属性？</span>
		    </div>
			<div class="pb_main">
			<p class="pb_item_title" style="padding:14px 0;">会员属性名称： <span class="pb_cate_name"></span></p>
			</div>		
				<button  class="pb_btn pb_btn_s""  type="submit">确定删除</button>
				<span class="pb_btn_split">或</span><a href= "javascript:void(0);" class="pb_cancel_a">取消</a>
			<div>
			</div>
		</form>
	</div>		
	<#if (status == "faild")!>
		<div class="pop_hint pop_hint3">${(msg)!}</div>
	</#if>
	<#if (status == "success")!>
		<div class="pop_hint pop_hint2">${(msg)!}</div>
	</#if>
<@m.page_footer />




