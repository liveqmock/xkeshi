<#import "/macro.ftl" as m >
<@m.page_header selected='prepaid' title="商户列表" subselected = "shop_list" css="seller_list_new"  js ="back_base" />

<div class="rwrap">
    <div class="r_title">商户列表</div>
    <#if voList?size == 0>
    	<div class="new_hint_wrap">还没有子账号的预付卡相关信息</div>
    <#else>
	    <table class="tb_main">
	        <tr class="th">
	            <td class="" style="width:200px;">商户</td>
	            <td class="rule" style="width:220px;">充值规则</td>
	            <td class="card_num" style="width:220px;">预付卡卡数</td>
	            <td class="sum">累计充值金额</td>
	        </tr>
	        <#list voList as vo>
	            <tr <#if vo_index%2==0>class="tr_bg"<#else >class="tr"</#if>>
	                <td class="">${(vo.shopName)!}</td>
	                <td class="rule"><#if vo.hasChargeRules><a href="javascript:" class="b_a">有</a><#else >无</#if></td>
	                <td class="card_num"><#if vo.prepaidCardCount != null && vo.prepaidCardCount gt 0><a href="javascript:" class="b_a">${(vo.prepaidCardCount)!}</a><#else >0</#if></td>
	                <td class="sum">${(vo.chargeAmount)!}</td>
	            </tr>
	        </#list>
	    </table>
    </#if>
</div>

<@m.page_footer />




