<#import "/macro.ftl" as m >
<@m.page_header selected='prepaid' subselected = "prepaid" css="seller_info|payment_detail" title="预付卡详情"   />


    <div class="rwrap">
        <p class="r_title"><a href="${base}/prepaid/card/list" class="back_a"></a>预付卡详情</p>
        <div class="tb_wrap">
            <table class="tb_main">
                <tr class="tb_title"><td colspan="2">基本信息</td><td style="border-left:1px solid #e5e8e6; width:290px">基本信息</td></tr>
                <tr class="th">
                    <td class="td_l"><span class="span_key">手机号码</span><span class="span_val ios-tel">${(vo.mobileNumber)!}</span></td>
                    <td><span class="span_key">会员等级</span><span class="span_val">${(vo.memberTypeName)!}(${(vo.discount)!}折)</span></td>
                    <td rowspan="3">
                        <div class="sd_main sd_mains">
                            <p class="sd_title sd_titles">显示状态</p>
                            <div class="sd_cont">
                                <p class="sd_p">
                                    <#if vo.enable><span class="state1 status1">正常</span>
                                    <#else><span class="state2 status1">冻结</span>
                                    </#if>
                                    <!-- <span class="state2 status1">已发布</span> -->
                                    <a href="javascript:" class="pop_a" data-pop="pop_state">修改</a></p>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr class="tr_bg th">
                    <td class="td_l"><span class="span_key">余额</span><span class="span_val">${(vo.balance)!}</span></td>
                    <td><span class="span_key">开卡时间</span><span class="span_val">${(vo.createdDate)!}</span></td>
                </tr>
                <tr class="th">
                    <td class="td_l" colspan="2"><span class="span_key">开卡金额</span><span class="span_val">${(vo.initialChargeAmount)!}元</span></td>
                </tr>
            </table>
        </div>
        <div class="tb_wrap tb_wrap2 tb_wrap2s">
            <div class="tb_title">充值信息</div>
            <table class="tb_main">
                <tr class="th">
                    <td class="td_l"><span class="span_key">累计充值金额</span><span class="span_val">${(totalChargeAmount)!}</span></td>
                    <td><span class="span_key">累计充值次数</span><span class="span_val">${(vo.totalChargeTimes)!}</span></td>
                </tr>
                <tr class="tr_bg th">
                    <td class="td_l" colspan="2"><a href="${base}/prepaid/charge/list?param=${vo.mobileNumber}" class="btn_detail">充值明细</a></td>
                </tr>
            </table>
        </div>
        <div class="tb_wrap tb_wrap2 tb_wrap2s">
            <div class="tb_title">消费信息</div>
            <table class="tb_main">
                <tr class="th">
                    <td class="td_l"><span class="span_key">累计消费次数</span><span class="span_val">${(consumeCount)!}</span></td>
                    <td><span class="span_key">最近消费时间</span><span class="span_val">${(vo.latestConsumeTime)!}</span></td>
                </tr>
                <tr class="tr_bg th">
                    <td class="td_l" colspan="2"><a href="${base}/order/list?mobileNumber=${vo.mobileNumber}&typeSet=PREPAID" class="btn_detail expense_de">消费明细</a></td>
                </tr>
            </table>
        </div>
        <div class="pb pop_state">
            <form action="${base}/prepaid/${vo.id}/enable/update" method="post">
                <div class="pb_title">显示状态修改</div>
                <div class="pb_main">
                    <div class="pb_rd_item"><input type="radio" name="enable" class="pb_rd" id="pb_rd1" <#if vo.enable>checked</#if> value="1"><label class="pb_lb" for="pb_rd1">正常</label></div>
                    <div class="pb_rd_item"><input type="radio" name="enable" class="pb_rd" id="pb_rd2" <#if !vo.enable>checked</#if> value="0"><label class="pb_lb" for="pb_rd2">冻结</label></div>
                </div>
                <button class="pb_btn pb_btn_s">确定</button><span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
            </form>
        </div>
    </div>


<@m.page_footer />

