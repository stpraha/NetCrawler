<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CXD Net Spider</title>
</head>
<body>
	Welcome to net crawler!
	<br/>
	Input the website you want to crawl:
	<br/>
	<form action="CrawlerServlet">
		<input type="text" name="web2crawl"/>
		<input type="button" value="Start" onclick=/>
	</form>
	
	<script type="text/javascript">
	function postURL(){
		var url=document.getElementByName("web2crawl");
		
		$.ajax({
			type:"get",
			url:url,
			data:data,
				
		})
	}
	</script>
</body>
</html>