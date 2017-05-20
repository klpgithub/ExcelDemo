<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form method="post" enctype="multipart/form-data" action="analy">
		<span>选择报表:</span>
		<select name="tableName" >
			<option value="fact_gjcjjzbhz_bak">工交处经济指标汇总表</option>
			<option value="he">工交处经济指标汇总表</option>
		</select>
		<span>上传文件:</span>
		<input id="fileupload" type="file" name="file1">
		<input type="submit" value="上传" />
	</form>
</body>
</html>