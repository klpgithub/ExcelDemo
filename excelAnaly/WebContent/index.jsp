<%@page import="javax.servlet.http.HttpServletRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Excel导入</title>
<link rel="stylesheet" type="text/css" href="index.css"/>
</head>
<body>
	<form method="post" enctype="multipart/form-data" action="analy" style="margin-top: 20px;height: 20px;">
		<span>选择报表:</span>
		<select name="tableName" >
			<!-- 汇总表 -->
			<option value="fact_gjcfdlhyhzb">工交处分大类行业汇总表</option>
			<option value="dim_gjcfdlhyhzb">工交处分大类行业汇总表_维表</option>
			<option value="fact_gjczycpclhzb">工交处主要产品产量汇总表</option>
			<option value="dim_gjczycpclhzb">工交处主要产品产量汇总表_维表</option>
			<option value="fact_gjczzhzb">工交处总值汇总表</option>
			<option value="dim_gjczzhzb">工交处总值汇总表_维表</option>
			<option value="fact_gjcqxsxshz">区县市销售产值、产销率汇总表</option>
			<option value="dim_gjcqxshz">区县市销售产值、产销率汇总表_维表</option>
			<option value="fact_gjcqxsczhz">区县市总产值、增加值汇总表</option>
			<option value="dim_gjcjjzbhz">区县市总产值、增加值汇总表_维表</option>
			<option value="fact_gjcgyxsfhydl">工业销售产值分行业大类汇总表</option>
			<option value="dim_gjcgyxsfhthz">工业销售产值分行业大类汇总表_维表</option>
			
			<!-- 预警表 -->
			<option value="fact_sjpsgc">数据评审过程</option>
			<option value="fact_sjzlgyzjzpthz">报送数据预估</option>
			<option value="fact_lqwysgmzjzjzzsd">接收数据评估</option>
			<option value="fact_sjxxfb">数据信息发布</option>
			<option value="dim_sbjdzdxz">异动数据预警对应维表(区)</option>
			<option value="dim_tzcgqtzwcqk">异动数据预警对应维表(县)</option>
			<option value="fact_ydsjwgsj">微观数据预警</option>
			<option value="fact_ydsjzgsj">中观数据预警</option>
			<option value="fact_ydsjhgsj">宏观数据预警</option>
			<option value="fact_sxsjyj">市县数据预警</option>
			
		</select>
		
		<a class="file">
			<input id="fileupload" type="file" name="fileName">选择文件
			
		</a>

		<input type="submit" value="上 传" />
		<span style="color: red;">${error }</span>
		<span style="color: green;">${success }</span>
	</form>
</body>
</html>