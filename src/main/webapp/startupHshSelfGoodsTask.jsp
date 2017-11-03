<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>启动京东top100热销商品爬虫</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript">
	function startup(){
		$.ajax({
			url : "<%=request.getContextPath()%>/startupHshSelfGoodsTask",
			cache : false,
			dataType: "json",
			success : function(result) {
				if(result==true){
					$("#statusSpan").text("已运行，请稍后...");
				}
			}
		});
	}
</script>
</head>
<body>
	<div style="margin-top: 60px" align="center">
		<div><button onclick="startup()" id="startBtn">启动</button></div>
		<div style="margin-top: 15px" id="statusSpan"></div>
	</div>

</body>
</html>