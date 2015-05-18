<#import "/macro.ftl" as m >
<@m.page_header selected='prepaid' title="添加充值规则" subselected = "rule_management" css="seller_info|add_seller_new|recharge_detail"  js ="recharge_detail2" />

<div class="rwrap">
    <div class="r_title">添加充值规则
        <p class="p_tips">该店尚未添加任何充值规则，请按以下步骤进行添加</p>
        <div class="search_wrap">
        </div>
    </div>
    <div class="tb_wrap tb_other">
        <form class="main_form" action="${base}/prepaid/rule/update?bId=${businessId}&bType=${businessType}" method="post">
            <input type="hidden" name="submissionToken" value="${(submissionToken)!''}">
            <div class="tb_title"><span class="s_t"></span>设置基础充值规则</div>
                <#list memberTypeList as type>
                <table class="tb_main tb_list" data-id="${type.id}">
                <tr class="th">
                        <td class="td_border td_charge_title" colspan="3">${type.name}</td>
                    </tr>
                    <tr class="th first_charge">
                        <td class="td_border" colspan="3">
                            <h3 class="h3_key">首充<a href="javascript:;" class="btn_add">新增</a>
                            </h3>
                            <div class="td_wrap">
                                <p class="p_bg">
                                    充值金额<span class="input_wrap"><input type="text" class="input_box"><em>元</em></span>赠送
								<span class="td_val">
									<select class="select select1" selected="selected">
                                        <option value="1">金额</option>
                                        <option data-type="0" value="0">无</option>
                                    </select>
								</span>
                                    <span class="input_wrap"><input type="text" class="input_box gift_money"><em>元</em></span>
                                </p><a href="javascript:;" class="del_add">删除</a>
                            </div>
                        </td>
                    </tr>
                    <tr class="th more_charge">
                        <td class="td_border" colspan="3">
                            <h3 class="h3_key">续充<span class="span_radio"><input type="radio" id="rd${type.id}" name="charge_rd${type.id}" class="charge_rd click_b" checked data-val="1"><label for="rd${type.id}" class="lb_rd">同首充</label><input type="radio" id="rd2_${type.id}" class="charge_rd click_b" name="charge_rd${type.id}" data-val="2"><label for="rd2_${type.id}" class="lb_rd">自定义</label></span><a href="javascript:;" class="btn_add" style="display:none;">新增</a></h3>
                            <div class="td_wrap" style="display:none;">
                                <p class="p_bg">
                                    充值金额<span class="input_wrap"><input type="text" class="input_box"><em>元</em></span>赠送
								<span class="td_val">
									<select class="select select1" selected="selected">
                                        <option value="1">金额</option>
                                        <option data-type="0" value="0">无</option>
                                    </select>
								</span>
                                    <span class="input_wrap"><input type="text" class="input_box gift_money"><em>元</em></span>
                                </p><a href="javascript:;" class="del_add">删除</a>
                            </div>
                        </td>
                    </tr>
                </table>
                </#list>

            <div class="btn_wrap"><input type="submit" class="add_seller_btn btn_submit" value="提交"/><span>或</span><a href="javascript:" class="cancel_a">取消</a></div>
        </form>
    </div>
</div>


<@m.page_footer />




