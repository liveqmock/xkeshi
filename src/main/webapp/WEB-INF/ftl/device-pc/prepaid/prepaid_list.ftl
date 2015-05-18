<#import "/macro.ftl" as m >
<@m.page_header selected='prepaid' title="预付卡列表" subselected = "prepaid" css="seller_list_new|charge_detail|charge_list"  js ="My97DatePicker/WdatePicker|page" />
 
<div class="rwrap">
    <div class="r_title"><span class="fl">预付卡列表</span>
        <div class="search_wrap">
            <form class="search_form fl"><input type="text" name="param" class="search_input" placeholder="手机号码" value="${(param.param)!}">
                <button class="search_btn"></button>
            </form>
            <a class="pop_a filter_a fl" href="javascript:" data-pop="pop_filter"></a>
        </div>
        <#if (param.memberTypeId||param.beginDate||param.endDate||param.enable||param.param)>
            <div class="search_result">
                <p class="result_tit"><span class="result_tit_text">搜索/筛选</span>
                    <#if param.memberTypeId>
                        <span class="b_tit">会员等级：
                            <#list memberTypeList as memberType>
                                <#if memberType.id == param.memberTypeId>
                                ${(memberType.name)!}
                                <#break>
                                </#if>
                            </#list>
                        <a href="${(base)!}/prepaid/card/list?beginDate=${(param.beginDate)!}&endDate=${(param.endDate)!}&enable=${(param.enable)!}&param=${(param.param)!}"><b>x</b></a></span>
                    </#if>
                <#if param.param>
                    <span class="b_tit">手机号码：${(param.param)!}<a href="${(base)!}/prepaid/card/list?memberTypeId=${(param.memberTypeId)!}&endDate=${(param.endDate)!}&enable=${(param.enable)!}"><b>x</b></a></span>
                </#if>
                <#if param.beginDate>
                    <span class="b_tit">开卡时间起：${(param.beginDate)!}<a href="${(base)!}/prepaid/card/list?memberTypeId=${(param.memberTypeId)!}&endDate=${(param.endDate)!}&enable=${(param.enable)!}&param=${(param.param)!}"><b>x</b></a></span>
                </#if>
                <#if param.endDate>
                    <span class="b_tit">开卡时间止：${(param.endDate)!}<a href="${(base)!}/prepaid/card/list?memberTypeId=${(param.memberTypeId)!}&beginDate=${(param.beginDate)!}&enable=${(param.enable)!}&param=${(param.param)!}"><b>x</b></a></span>
                </#if>
                <#if param.enable>
                    <span class="b_tit">状态：<#if param.enable == 1>正常<#else>冻结</#if><a href="${(base)!}/prepaid/card/list?memberTypeId=${(param.memberTypeId)!}&beginDate=${(param.beginDate)!}&endDate=${(param.endDate)!}&param=${(param.param)!}"><b>x</b></a></span>
                </#if>
                    </span>的结果</span>
                </p>
                <a href="${(base)!}/prepaid/card/list" class="s_clear">清空</a>
            </div>
        </#if>
    </div>
    <table class="tb_main">
        <tr class="th">
            <td class="">手机号码</td>
            <td class="">会员等级</td>
            <td class="wd70">开卡时间</td>
            <td class="">充值次数</td>
            <td class="" >充值金额</td>
            <td class="">消费次数</td>
            <td class="wd80" style="padding-right:4px;" >最近消费时间</td>
            <td class="" >余额</td>
            <td class="" >状态</td>
            <#--<td class="operate" style="padding-left:10px;">操作</td>-->
        </tr>

        <#list voList as vo>
        <tr <#if vo_index%2==0>class="tr_bg"</#if>>
            <td class=""><a href="${base}/prepaid/${(vo.id)!}" class="b_a">${(vo.mobileNO)!}</a></td>
            <td class="">${(vo.memberTypeName)!}</td>
            <td class="wd70">${(vo.createdTime)!}</td>
            <td class="">${(vo.totalChargeTimes)!}</td>
            <td class="" >￥${(vo.totalChargeAmount)!'0.00'}</td>
            <td class="">${(vo.totalConsumeTimes)!}</td>
            <td class="wd70" style="padding-right:20px;">${(vo.latestConsumeTime)!}</td>
            <td class="" >￥${(vo.balance)!'0.00'}</td>
            <td class="" ><#if vo.enable >正常<#else>冻结</#if></td>
            <#--<td class="operate"><a href="javascript:" data-pop="pop_freeze" class="pop_a">冻结</a><a href="javascript:" data-pop="pop_unfreeze" class="pop_a">解冻</a></td>-->
        </tr>
        </#list>
    </table>
    <div class="pb pop_filter">
        <form>
            <div class="pb_title">筛选预付卡</div>
            <div class="pb_main">

                <div class="pb_item">
                    <p class="pb_item_title">会员等级</p>
                    <select name="memberTypeId" name="memberType" class="f_input pb_item_input select_style trades" style="width:212px;margin-left:0px;border-color:#d9d9d9;">
                        <#list memberTypeList as memberType>
                            <option value="${(memberType.id)!}" <#if (param.memberTypeId == memberType.id)!>selected="selected"</#if> >${(memberType.name)!}</option>
                        </#list>
                    </select>
                </div>
                <div class="pb_item">
                    <p class="pb_item_title">开卡起始时间</p>
                    <input type="text" class="tcal pb_input" name="beginDate" id="set_time1" value="${(param.beginDate)!}">&nbsp;-&nbsp;<input type="text" class="tcal pb_input" name="endDate" id="set_time2" value="${(param.endDate)!}">
                </div>
                <div class="pb_item">
                    <p class="pb_item_title">状态</p>
                    <select name="enable" class="f_input pb_item_input select_style trades" style="width:212px;margin-left:0px;border-color:#d9d9d9;">
                        <option value="" <#if (param.enable == null)!>selected="selected"</#if>>全部</option>
                        <option value="1"  <#if (param.enable == 1)!>selected="selected"</#if>>正常</option>
                        <option value="0" <#if (!param.enable == 0)!>selected="selected"</#if>>冻结</option>
                    </select>
                </div>
            </div>
            <button class="pb_btn pb_btn_s">确定</button>
            <span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
        </form>
    </div>
    <div class="pb pop_freeze">
        <form action="${base}/prepaid/">
            <input type="hidden" name="_method" value="DELETE">
            <div class="pb_title">预付卡操作</div>
            <div class="pb_main">
                冻结<span class="pb_cate_name">这张预付卡？</span>
            </div>
            <div class="pb_buttons">
                <button  class="pb_form_btn"  type="submit">确定</button>
                或
                <a href="javascript:" class="pb_cancel_a">取消</a>
            </div>
    </div>
    <div class="pb pop_unfreeze">
        <form>
            <input type="hidden" name="_method" value="DELETE">
            <div class="pb_title">预付卡操作</div>
            <div class="pb_main">
                <span class="pb_cate_name">解冻这张预付卡？</span>
            </div>
            <div class="pb_buttons">
                <button  class="pb_form_btn"  type="submit">确定</button>
                或
                <a href="javascript:" class="pb_cancel_a">取消</a>
            </div>
    </div>

<#if (query.totalRows) gt 0>

    <div class="page_wrap" data-all="${(query.totalPages)!}" data-now="${(query.currentPage)!}" data-url="${base}/prepaid/card/list?"></div>
</#if>
</div>



<@m.page_footer />




