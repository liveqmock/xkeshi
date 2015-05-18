<#import "/macro.ftl" as m >
<@m.page_header selected='prepaid' title="添加充值规则" subselected = "prepaid" css="seller_info|recharge_detail"  js ="recharge_detail1" />


<div class="rwrap">
    <form action="${base}/merchant/${merchantId}/prepaid/shop/update" method="post">
     <input type="hidden" name="submissionToken" value="${(submissionToken)!''}">
    <div class="r_title">添加充值规则
        <p class="p_tips">该店尚未添加任何充值规则，请按以下步骤进行添加</p>
        <div class="search_wrap">
        </div>
    </div>
    <div class="tb_wrap tb_other">
        <div class="tb_title"><span class="s_t"></span>选择适用商户</div>
        <!-- 			<div class="div_border">
            <input type="checkbox" class="" id="checkAll" >全部
            <label for="checkAll" class="lb_cb"></label>
        </div> -->
        <table class="tb_main tb_list">
            <tr class="th">
                <td class="td_border" colspan="3">
                    <input type="checkbox" class="" id="checkAll" >
                    <label for="checkAll" class="lb_cb">全部</label>
                </td>
            </tr>
<#list shopList?chunk(3) as row>
<tr class="th">
    <#list row as cell>
        <td>
            <input type="checkbox" class="mct_cb" name="shopIds" value="${cell.id}" id="mct_cb${cell.id}">
            <label class="lb_cb" for="mct_cb${cell.id}">${cell.name}</label>
        </td>
        <#if cell_index gt 0 && cell_index < 2 && !cell_has_next>
            <td>&nbsp;</td>
        </#if>
    </#list>
</tr>
</#list>
        </table>

    </div>
    <div class="btn_wrap"><button class="add_seller_btn btn_submit">下一步</button></div>
    </form>
</div>

</div>

<@m.page_footer />




