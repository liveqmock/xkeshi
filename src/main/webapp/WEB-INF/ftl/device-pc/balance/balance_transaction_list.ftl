<#import "/macro.ftl" as m>
<@m.page_header selected='balance' css="seller_list_new" title='账户变化' />
<style>
.pay-li{
padding: 4px 0;
}
.pay-title{
padding-bottom: 8px;
}
.pb_main{
border-top: none;
padding: 0 0 6px;
}
</style>
<div class="rwrap">
		<div class="r_title"><span class="fl">账户变化</span><div class="search_wrap"></div></div>
		<div class="top_data">可用余额: <em>${(balance?string("0.00"))!}</em>元<#--  <a class="pop_a b_a" style="margin-left:12px;" href="javascript:">充值	</a> --></div>
	
		<table class="tb_main">
			<tr class="th">
				<td class="ctime" style="width:100px;">时间</td>
				<td class="type" style="width:100px;">类型</td>
				<td class="name" style="width:80px;">变化金额</td>
				<td class="" style="width:200px;">说明</td>
				<td class="state" style="width:100px;">交易后金额</td>
			</tr>
			<#list pager.list as transaction>
			<tr <#if transaction_index%2==0>class="tr_bg"</#if>>
				<td class="">${(transaction.createDate?string('YYYY-MM-dd HH:mm'))!}</td>
				<td class="type"><#if transaction.type='CHARGE'>增加<#else>减少</#if></td>
				<td class="name">￥${(transaction.amount)!'0.00'}</a></td>
				<td class="">${(transaction.description)!}</td>
				<td class="state">￥${(transaction.afterTransaction)!'0.00'}</td>
			</tr>
			</#list>
		</table>
		
		<div class="page_wrap">
			<@m.p page=pager.pageNumber totalpage=pager.pageCount />
		</div>
		<div class="pb pop_filter">
			<form>
			<div class="pb_title">筛选<a href="javascript:" class="pb_close"></a></div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">电子券编号</p>
					<input type="text" class="pb_item_input">
				</div>
				<div class="pb_item">
					<p class="pb_item_title">按状态</p>
					<select><option>公开</option></select>
					<select><option>隐藏</option></select>
				</div>
				<div class="pb_item">
					<p class="pb_item_title">异业商家简称</p>
					<input type="text" class="pb_item_input">
				</div>
			</div>
			<button class="pb_btn">确定</button>
			</form>
		</div>		
	</div>
	
	<div class="pb payment">
		<form action="${base}/balance/charge" method="POST" >
			<div class="pb_title">充值</div>
			<div class="pb_main">
				<div class="pb_item">
					<p class="pb_item_title">余额</p>
					￥${(balance)!'0.00'}
				</div>
				<div class="pb_item">
					<p class="pb_item_title">选择充值金额</p>
					<select name="amount">
						<option value="1">1元</opion>
						<option value="10">10元</opion>
						<option value="50">50元</opion>
						<option value="100">100元</opion>
					</select>
				</div>
				<ul class="bank">
					<li class="pay-title">使用网银（需开通网上支付功能）</li>
					<li class="pay-li"><input type="radio" id="ICBC" class="ICBC" name="bank" method="bankPay" value="ICBCB2C" /><label for="ICBC" class="ICBC">中国工商银行</label></li>
					<li class="pay-li"><input type="radio" id="CMB" class="CMB" name="bank" method="bankPay" value="CMB" /><label for="CMB" class="CMB">招商银行</label>
					<li class="pay-li"><input type="radio" id="CCB" class="CCB" name="bank" method="bankPay" value="CCB" /><label for="CCB" class="CCB">中国建设银行</label></li>
					<li class="pay-li"><input type="radio" id="BOC" class="BOC" name="bank" method="bankPay" value="BOC" /><label for="BOC" class="BOC">中国银行</label></li>
					<li class="pay-li"><input type="radio" id="ABC" class="ABC" name="bank" method="bankPay" value="ABC" /><label for="ABC" class="ABC">中国农业银行</label></li>
					<li class="pay-li"><input type="radio" id="ALIPAY" class="ABC" name="bank" method="bankPay" value="ALIPAY" /><label for="ALIPAY" class="ALIPAY">支付宝</label></li>
				</ul>
			</div>
			<button class="pb_btn pb_btn_s">确定</button>
		<span class="pb_btn_split">或</span><a href="javascript:" class="pb_cancel_a">取消</a>
		</form>
	</div>
	
</div>

<script type="text/javascript">
$('.pop_a').click(function(){
	var self = $(this),
	left = self.offset().left,
	top = self.offset().top,
	text = self.text()
	
	pop = $('.payment');
	pop.css({'top':top+16, 'left':left-243+self.width()}).show();
});

	$('.pb_close').click(function(){
		var self = $(this)
		self.parents('.pb').hide()
	})
</script>
<@m.page_footer />

