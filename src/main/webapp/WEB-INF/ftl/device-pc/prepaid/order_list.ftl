<#import "/macro.ftl" as m >
<@m.page_header selected='order' title="预付卡充值&明细" subselected = "prepaid_charge" css="seller_list_new|charge_detail"  js ="My97DatePicker/WdatePicker|page" />



<div class="rwrap">
    <div class="r_title">
        <span class="fl">预付卡充值&明细</span>
        <div class="search_wrap">
            <form class="search_form fl">
                <input type="text" class="search_input" name="param" placeholder="手机号码/交易流水号" value="${(param.param)!}">
                <button class="search_btn"></button>
            </form>
            <a class="pop_a filter_a fl" href="javascript:" data-pop="pop_filter"></a>
        </div>
        <#if (param.beginTime||param.endTim||param.chargeChannelId||param.chargeAmount||param.initialCharge||param.param)>
        <div class="search_result">
            <p class="result_tit">
                <span class="result_tit_text">搜索/筛选</span>
                <#if param.param>
                <span class="b_tit b_tit1">
                    手机号码/交易流水号：${(param.param)!} <a href="${(base)!}/prepaid/charge/list?beginTime=${param.beginTime}&endTime=${param.endTime}&initialCharge=${param.initialCharge}&chargeAmount=${param.chargeAmount}&chargeChannelId=${param.chargeChannelId}"><b>x</b></a></span>

                </#if>
                <#if param.initialCharge>
                    <span class="b_tit b_tit1">
                    充值类型：<#if (param.initialCharge == 1)>首充<#else>续充</#if> <a href="${(base)!}/prepaid/charge/list?beginTime=${param.beginTime}&endTime=${param.endTime}&chargeAmount=${param.chargeAmount}&chargeChannelId=${param.chargeChannelId}&param=${param.param}"><b>x</b></a></span>
                </#if>
                <#if param.chargeChannelId>
                <span class="b_tit b_tit1">
                    <#list channelList as channel>
                        <#if channel.id == param.chargeChannelId>
                            充值方式：${(channel.name)!} <a href="${(base)!}/prepaid/charge/list?beginTime=${param.beginTime}&endTime=${param.endTime}&initialCharge=${param.initialCharge}&chargeAmount=${param.chargeAmount}&param=${param.param}"><b>x</b></a></span>
                        <#break>
                        </#if>
                    </#list>
                </#if>
                <#if param.chargeAmount>
                    <span class="b_tit b_tit1">
                    充值金额：${(param.chargeAmount)!} <a href="${(base)!}/prepaid/charge/list?beginTime=${param.beginTime}&endTime=${param.endTime}&initialCharge=${param.initialCharge}&chargeChannelId=${param.chargeChannelId}&param=${param.param}"><b>x</b></a></span>
                </#if>
                <#if param.beginTime>
                    <span class="b_tit b_tit1">
                        充值时间起：${(param.beginTime)!} <a href="${(base)!}/prepaid/charge/list?endTime=${param.endTime}&initialCharge=${param.initialCharge}&chargeAmount=${param.chargeAmount}&chargeChannelId=${param.chargeChannelId}&param=${param.param}"><b>x</b></a></span>
                </#if>
                <#if param.endTime>
                    <span class="b_tit b_tit1">
                        充值时间止：${(param.endTime)!} <a href="${(base)!}/prepaid/charge/list?beginTime=${param.beginTime}&initialCharge=${param.initialCharge}&chargeAmount=${param.chargeAmount}&chargeChannelId=${param.chargeChannelId}&param=${param.param}"><b>x</b></a></span>
                </#if>
                </span>的结果</span>
            </p>
            <a href="${(base)!}/prepaid/charge/list" class="s_clear">清空</a>
        </div>
        </#if>
     </div>
    <div class="opt_wrap">共${(query.totalRows)!}笔充值记录,充值金额合计： <em class="g_num">${(totalChargeAmount)!}元</em> </div>
    <table class="tb_main">
        <tr class="th">
            <td class="sdtime">充值时间</td>
            <td class="pay_type">充值类型</td>
            <td class="telephone" style="width:80px;">手机</td>
            <td class="" style="width:60px;">充值金额</td>
            <td class="pay_way" style="width:60px;">充值方式</td>
            <td class="pay_rule" style="width:150px;">充值规则</td>
            <td class="trans_num" style="width:120px;">交易流水号</td>
            <td class="people" >操作员</td>
        </tr>

        <#list voList as vo>
            <tr <#if vo_index%2==0>class="tr_bg"</#if>>
                <td class="sdtime">${(vo.createTime)!}</td>
                <td class="pay_type"><#if vo.initialCharge == 1>首充<#else >续充</#if></td>
                <td class="telephone">${(vo.mobileNumber)!}</td>
                <td class="">￥${(vo.chargeAmount)!'0.00'}</td>
                <td class="pay_way">${(vo.chargeChannel.name)!}</td>
                <td class="pay_rule">${(vo.chargeRuleName)!}</td>
                <td class="trans_num">${(vo.code)!}</td>
                <td class="people">${(vo.operatorName)!}</td>
            </tr>
        </#list>
    </table>
    <div class="pb pop_filter">
        <form>
            <div class="pb_title">筛选</div>
            <div class="pb_main">
                <div class="pb_item">
                    <p class="pb_item_title">充值时间</p>
                    <input type="text" class="tcal pb_input" name="beginTime" id="set_time1" value="${(param.beginTime)!}">&nbsp-&nbsp<input type="text" class="tcal pb_input" name="endTime" id="set_time2" value="${(param.endTime)!}">
                </div>
                <div class="pb_item">
                    <p class="pb_item_title">充值类型</p>
                    <select name="initialCharge" class="f_input pb_item_input select_style trades" style="width:212px;margin-left:0px;border-color:#d9d9d9;">
                        <option value="">全部</option>
                        <option value="1" <#if param.initialCharge == 1>selected="selected" </#if>>首充</option>
                        <option value="0" <#if param.initialCharge == 0>selected="selected" </#if>>续充</option>
                    </select>
                </div>
                <div class="pb_item">
                    <p class="pb_item_title">充值金额</p>
                    <input type="text" class="pb_item_input f_input " id="chargeAmount" name="chargeAmount" value="${(param.chargeAmount)!}"/>
                    </select>
                </div>
                <div class="pb_item">
                    <p class="pb_item_title">充值方式</p>
                    <select name="chargeChannelId"  class="f_input pb_item_input select_style trades" style="width:212px;margin-left:0px;border-color:#d9d9d9;">
                        <#list channelList as channel>
                            <option value="${channel.id}" <#if channel.id == param.chargeChannelId>selected="selected"</#if>>${channel.name}</option>
                        </#list>
                    </select>
                </div>
            </div>
            <button class="pb_btn pb_btn_s">确定</button>
            <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
        </form>
    </div>
    <div class="page_wrap" data-all="${(query.totalPages)!}" data-now="${(query.currentPage)!}" data-url="${base}/prepaid/charge/list?param=${(param.param)!}&"></div>
</div>

<@m.page_footer />




