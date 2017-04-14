<%@ page language="java" contentType="text/html;charset=gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=gb2312">
<title>Insert title here</title>
<script type="text/javascript">
function ajax(){
	var ajaxData = {
	  type:arguments[0].type || "GET",
	  url:arguments[0].url || "",
	  async:arguments[0].async || "true",
	  data:arguments[0].data || null,
	  dataType:arguments[0].dataType || "text",
	  contentType:arguments[0].contentType || "application/x-www-form-urlencoded",
	  beforeSend:arguments[0].beforeSend || function(){},
	  success:arguments[0].success || function(){},
	  error:arguments[0].error || function(){}
	} 
	ajaxData.beforeSend();
	var xhr = createxmlHttpRequest();
	xhr.responseType=ajaxData.dataType;
	xhr.open(ajaxData.type,ajaxData.url,ajaxData.async);
	xhr.setRequestHeader("Content-Type",ajaxData.contentType);
	xhr.send(convertData(ajaxData.data));
	//xhr.send(ajaxData.data);
	xhr.onreadystatechange = function() {  
	  if (xhr.readyState == 4) {  
	    if(xhr.status == 200){ 
	      ajaxData.success(xhr.response);
	    }else{ 
	      ajaxData.error();
	    }  
	  } 
	}  
} 
	  
function createxmlHttpRequest() {  
	if (window.ActiveXObject) {  
	  return new ActiveXObject("Microsoft.XMLHTTP");  
	} else if (window.XMLHttpRequest) {  
	  return new XMLHttpRequest();  
	}  
} 
  
function convertData(data){ 
	if( typeof data === 'object' ){
	  var convertResult = "" ;
	  for(var c in data){
	    convertResult+= c + "=" + data[c] + "&";
	  }
	  convertResult=convertResult.substring(0,convertResult.length-1)
	  return convertResult;
	}else{
	  return data;
	} 
}

function subsql(){
	if(confirm("确定要生成数据吗？")){
		var data = {};
		var tbname = document.getElementById ("tbname").value;
		data["tbname"] = tbname;
		var sql = document.getElementById ("sql").value;
		data["sql"] = sql;
		var options = {
			type : 'POST',
			url:'GenEntity',
			dataType : 'json',
			contentType : 'application/x-www-form-urlencoded; charset=utf-8',
			data :data,
			success : function(data, status) {
				var res = data;
				alert("ok");
			},
			error : function() {
				alert('操作异常！请重试！');
			}
		};
		ajax(options);
	}
	
	return null;
}
</script>
</head>
<body>
	<div>
	<form>
		表名：<input type="text" id="tbname">
		<br>
		sql：<input type="text" id="sql" style="width:100%;height:auto;">
	</form>
		<button onclick="javascript:subsql()">提交</button>
	</div>
</body>
</html>