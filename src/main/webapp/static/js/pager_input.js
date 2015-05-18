function checkName(v){
	var urlName = $(v).val().trim();
 
	$.ajax({
		url:BASE+"/pager/pager/name?name="+urlName,
		type:'GET',
		dataType :'json',
		success:function(data){
			if (data.status == "faild") {
				alert("该url存在");
			}
		}
	});
}
