<#import "/macro.ftl" as m>
<@m.page_header title="退款详情" selected='coupon' subselected="refundList" css="seller_info" js=""/>
<script>
	var CONTEXT="${base}";
</script>
<style>
</style>
<div class="rwrap">
	<div class="r_title" style="padding-left:0;"><a onclick="history.back(-1);" class="back_a"></a>退款详情</div>
		<div class="tb_wrap">
			<table class="tb_main">
				<tr class="tr_bg">
					<td><span class="td_key">用户手机号：</span><span class="td_val">${(refund.coupon.mobile)!}</span></td>
					<td><span class="td_key">电子券&套票:</span>
						<span class="td_val">
							<#if (refund.coupon.type  == 'CHILD')!>
								【套票】${(refund.coupon.parent.name)!}
							<#else>
								${(refund.coupon.couponInfo.name)!}
							</#if>
						</span>
					</td>
				</tr>
				<tr>
					<td>
					    <span class="td_key">退款金额:</span>
					    <span class="td_val">${(refund.sum)!}元</span>
					</td>
					<td>
						<span class="td_key">支付方式:</span><span class="td_val">
							<#if refund.payment.type == 'ALIPAY_WAP'>
								支付宝Wap支付
							<#elseif refund.payment.type = 'WEI_XIN'>
								微信支付
							<#elseif refund.payment.type = 'UMPAY_WAP'>
								联动优势支付
							</#if>
							【支付交易号：${(refund.payment.serial)!}】
						</span>
					</td>
				</tr>
			</table>
		</div>
		<div class="tb_wrap">
			<div class="tb_title">退款进度详情</div>
			<table class="tb_main" style="wdith:894px;">
			  <#list refundLogs as refundLog>
				<tr class="tr<#if refundLog_index%2==1> tr_bg</#if>">
					<td style="padding: 16px 0 16px 20px;width:170px;border-right:none;">${refundLog.createDate?string('yyyy-MM-dd HH:mm:ss')}</td>
					<td style="width:720px;border-left:none;">${(refundLog.description)!}</td>
				</tr>
			 </#list>
			</table>
		</div>
	</div>
</div>
<@m.page_footer />

