<#import "/macro.ftl" as m >
<@m.page_header selected='prepaid' title="充值规则明细" subselected = "rule_management" css="seller_info|charge_rule_detail"  js ="charge_rule_detail" />

<script>
    var BID= ${(businessId)!};
    var BTYPE= "${(businessType)!}";
</script>

<div class="rwrap">
    <div class="r_title">充值规则明细<div class="search_wrap"></div>
    </div>
        <#if checkedShopList??>
        <div class="tb_wrap tb_other">
            <div class="tb_title lh20"><span class="fl">适用商户</span><#if (editable)!><a class="pop_a edit_mct_a" data-pop="pop_mct" data-width="390" href="javascript:">修改适用商户</a></#if> </div>
            <table class="tb_main clbh">
                <#list checkedShopList?chunk(2) as row>
                    <tr>
                        <#list row as cell>
                            <td style="width:896px;"><span class="td_key">商户</span><span class="td_val">${cell.name}</span></td>
                        </#list>
                    </tr>
                </#list>
            </table>
        </div>
        </#if>
    <div class="tb_wrap tb_other">
        <div class="tb_title">充值方案</div>
        <table class="tb_main tb_plan">
            <tr class="th2 ">
                <td class="grade pl20">会员级别</td>
                <td class="type">充值类型</td>
                <td class="sum">充值金额</td>
                <td class="present">赠送金额</td>
                <#if (editable)!><td class="opt">操作</td></#if>
            </tr>
            <#list ruleList as rule>
                <tr class="tr tr_bg">
                    <td class="grade pl20">${(rule.memberTypeName)!}</td>
                    <td class="type">
                        <#list rule.firstChargeRuleList as firstCharge>
                            <#if firstCharge_index == 0>
                                首充
                            </#if>
                            <#if firstCharge_index != 0>
                                <span>首充</span>
                            </#if>
                        </#list>
                        <#if rule.rechargeRuleList?size == 0 && rule.firstChargeRuleList?size != 0>
                            <span>续充(同首充)</span>
                        <#elseif rule.rechargeRuleList?size != 0 && rule.firstChargeRuleList?size != 0>
                            <#list rule.rechargeRuleList as recharge>
                                <span>续充</span>
                            </#list>
                        </#if>
                    </td>
                    <td class="sum">
                        <#list rule.firstChargeRuleList as firstCharge>
                            <#if firstCharge_index == 0>
                                ${(firstCharge.chargeAmount)!}
                            </#if>
                            <#if firstCharge_index != 0>
                                <span>${(firstCharge.chargeAmount)!}</span>
                            </#if>
                        </#list>
                        <#if rule.rechargeRuleList?size == 0 && rule.firstChargeRuleList?size != 0>
                                <span>&nbsp;</span>
                        <#elseif rule.rechargeRuleList?size != 0 && rule.firstChargeRuleList?size != 0>
                            <#list rule.rechargeRuleList as recharge>
                                <span>${(recharge.chargeAmount)!}</span>
                            </#list>
                        </#if>
                    </td>
                    <td class="present">
                        <#list rule.firstChargeRuleList as firstCharge>
                            <#if firstCharge_index == 0>
                            ${(firstCharge.chargeGiftAmount)!}
                            </#if>
                            <#if firstCharge_index != 0>
                                <span>${(firstCharge.chargeGiftAmount)!}</span>
                            </#if>
                        </#list>
                        <#if rule.rechargeRuleList?size == 0 && rule.firstChargeRuleList?size != 0>
                            <span>&nbsp;</span>
                        <#elseif rule.rechargeRuleList?size != 0 && rule.firstChargeRuleList?size != 0>
                            <#list rule.rechargeRuleList as recharge>
                                <span>${(recharge.chargeGiftAmount)!}</span>
                            </#list>
                        </#if>
                    </td>
                    <#if (editable)!><td class="opt"><a href="javascript:" class="pop_a edit_plan_a  show_edit" data-pop="pop_plan${rule.memberTypeId}" data-height="16" data-width="390">修改</a></td></#if>
                </tr>
            </#list>
        </table>
    </div>
<#if checkedShopList??>
    <div class="pb pop_mct">
        <form method="post" action="${base}/merchant/${(businessId)!}/prepaid/shop/update">
            <div class="pb_title">修改适用商户</div>
            <div class="pb_main">
                <table class="tb_main tb_mct">
                <#list shopList?chunk(2) as row>
                    <tr class="tr">
                        <#list row as cell>
                            <td>
                                <input type="checkbox"
                                       <#list checkedShopList as checkedShop>
                                           <#if checkedShop.id == cell.id>checked</#if>
                                       </#list>
                                       class="pb_cb" id="pb_cb${cell.id}" name="shopIds" value="${cell.id}"><label class="pb_cb" for="pb_cb${cell.id}">${cell.name}</label>
                            </td>
                        </#list>
                    </tr>
                </#list>

                </table>
            </div>
            <div class="btn_wrap">
                <button class="pb_btn">确定</button><span class="pb_btn_split">或</span><a href="javascript:;" class="pb_cancel_a">取消</a>
            </div>
        </form>
    </div>
</#if>
    <div class="pl_cont_wrap pl_cont_clone">
        <div class="pl_cont">
            <span class="pl_cont_title">充值金额</span>
            <span class="pl_input_wrap"><input class="pl_input" type="text" data-hint='充值金额'><em>元</em></span>
            <span class="pl_cont_title">赠送</span>
            <select class="select select1">
                <option value="1">金额</option>
                <option data-type="0" value="0">无</option>
            </select>
            <span class="pl_input_wrap"><input class="pl_input gift_money" type="text" data-hint='赠送金额'><em>元</em></span>
        </div>
        <a class="pl_del_a" href="javascript:">删除</a>
    </div>
    <#list ruleList as rule>

        <div class="pb pop_plan pop_plan${rule.memberTypeId}">
            <form class="pb_form" data-id="${rule.memberTypeId}" method="POST" >
                <input type="hidden" name="submissionToken" value="${(submissionToken)!''}">
                <div class="pb_title">修改充值方案</div>
                <div class="pb_main">
                    <div class="pl_item">
                        <label class="pl_lb">会员级别</label><span class="pl_item_title">${(rule.memberTypeName)!}</span>
                    </div>
                    <div class="pl_item first_charge">
                        <p class="pl_title">首充<a class="pl_add_a" href="javascript:">新增</a></p>
                        <div class="pl_cont_wrap_wrap">
                            <#list rule.firstChargeRuleList as firstRule>
                                <div class="pl_cont_wrap">
                                    <div class="pl_cont">
                                        <span class="pl_cont_title">充值金额</span>
                                        <span class="pl_input_wrap"><input class="pl_input charge_money1" data-hint='充值金额' value="${firstRule.chargeAmount}" type="text"><em>元</em></span>
                                        <span class="pl_cont_title">赠送</span>
                                        <select class="select select1">
                                            <option value="1" <#if firstRule.chargeGiftTypeId != null && firstRule.chargeGiftTypeId == 2>selected="selected"</#if>>金额</option>
                                            <option data-type="0" <#if firstRule.chargeGiftTypeId != null && firstRule.chargeGiftTypeId == 1>selected="selected"</#if> value="0">无</option>
                                        </select>
                                        <span class="pl_input_wrap">
                                            <input class="pl_input gift_money" data-hint='赠送金额' type="text" value="${firstRule.chargeGiftAmount}"><em>元</em>
                                        </span>

                                    </div>
                                    <a class="pl_del_a" href="javascript:">删除</a>
                                </div>
                            </#list>
                        </div>
                    </div>
                    <div class="pl_item pl_item2 more_charge">
                        <div class="pl_item_cover"></div>
                        <p class="pl_title">续充<span class="pl_rd_wrap">
						<input class="pl_rd" type="radio" name="pl_rd${rule.memberTypeId}" id="pl_rd${rule.memberTypeId}_1" data-val="1" <#if (rule.rechargeRuleList?size == 0)>checked</#if> ><label class="pl_lb" for="pl_rd${rule.memberTypeId}_1">同首充</label>
					</span><span class="pl_rd_wrap">
						<input class="pl_rd" type="radio" name="pl_rd${rule.memberTypeId}" id="pl_rd${rule.memberTypeId}_2" data-val="2"  <#if (rule.rechargeRuleList?size > 0)>checked</#if>><label class="pl_lb" for="pl_rd${rule.memberTypeId}_2">自定义</label>
					</span><a class="pl_add_a" href="javascript:" <#if (rule.rechargeRuleList?size == 0)> style="display:none;"</#if>>新增</a></p>
                        <div class="pl_cont_wrap_wrap">
                            <#list rule.rechargeRuleList as reRule>
                                <div class="pl_cont_wrap" <#if (rule.rechargeRuleList?size == 0)>style="display:none;"</#if>>
                                    <div class="pl_cont">
                                        <span class="pl_cont_title">充值金额</span>
                                        <span class="pl_input_wrap"><input class="pl_input charge_money1" data-hint='充值金额' type="text" value="${(reRule.chargeAmount)!}"><em>元</em></span>
                                        <span class="pl_cont_title">赠送</span>
                                        <select class="select select1">
                                            <option value="1" <#if reRule.chargeGiftTypeId != null && reRule.chargeGiftTypeId == 2>selected="selected"</#if>>金额</option>
                                            <option data-type="0" value="0" <#if reRule.chargeGiftTypeId != null && reRule.chargeGiftTypeId == 1>selected="selected"</#if>>无</option>
                                        </select>
                                        <span class="pl_input_wrap"><input class="pl_input gift_money" data-hint='赠送金额' type="text"  value="${(reRule.chargeGiftAmount)!}"><em>元</em></span>
                                    </div>
                                    <a class="pl_del_a" href="javascript:">删除</a>
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>
                <div class="btn_wrap">
                    <input type="hidden" name="rule" id="ruleId" />
                    <button class="pb_btn btn_submit" type="button">确定</button><span class="pb_btn_split">或</span><a href="javascript:;" class="pb_cancel_a">取消</a>
                </div>
            </form>
        </div>
    </#list>
</div>

</div>

<@m.page_footer />




