<#import "/macro.ftl" as m>
<@m.page_header selected='setting'  title="支付设置" subselected= 'pay_setting' css='seller_list_new|set_mime'  js="set_pay" />
<div class="rwrap">
    <div class="r_title" style="overflow:hidden;">支付设置</div>
    <form class="check_form"   action="/shop/pay_setting"  method="POST">
        <input type = "hidden"  name="_method"  value="PUT">
        <table class="tb_main">
            <tr class="service_tr">
                <td class="ltd  ">
                    <span class="td_key">是否启用多笔支付功能</span>
						<span class=" td_val td_input">
							<input type="radio" id="use1" name="enableMultiplePayment" class="input-l use_rd" value="true"  data-enableMultiplePayment = "${(enableMultiplePayment?string('true','false'))!}"
                                   <#if enableMultiplePayment == true >checked="checked"</#if>>
							<label for="use1">启用</label>
							<input type="radio" id="use2" name="enableMultiplePayment" class="input-l use_rd" value="false"
                                   <#if enableMultiplePayment == false >checked="checked"</#if>>
							<label for="use2">不启用</label>
							<label  class="required_error"></label>
						</span>
                </td>
            </tr>
        </table>
        <div class="btn_bottom">
            <button type="submit" class="pb_btn pb_btn_bottom">确定</button>
        </div>
    </form>
</div>
<#if (status == "faild")!>
<div class="pop_hint pop_hint3">${msg}</div>
</#if>
<#if (status == "success")!>
<div class="pop_hint pop_hint2">${msg}</div>
</#if>
</div>
<@m.page_footer />