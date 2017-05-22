package com.data.sync.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.data.sync.utils.MultipartRequestWrapper;
import com.data.sync.utils.ReadConfig;

@SuppressWarnings("serial")
public class SyncController extends HttpServlet {

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			String ip = request.getRemoteAddr();
			System.out.println(ip);
			request = new MultipartRequestWrapper(request);
			String tableName = request.getParameter("tableName");
			String fileName = request.getParameter("fileName");
			InputStream is = new FileInputStream(MultipartRequestWrapper.PATH + fileName);
			analyExcel(is, fileName, request, tableName);
			request.setAttribute("success", "数据文件导入成功!");
			request.getRequestDispatcher("index.jsp").forward(request, response);
		} catch (Exception e) {
			try {
				request.getRequestDispatcher("index.jsp").forward(request, response);
			} catch (ServletException | IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * 解析Excel
	 * 
	 * @param is
	 *            Excel输入流
	 * @param fileName
	 *            Excel文件名
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void analyExcel(InputStream is, String fileName, HttpServletRequest request, String tableName)
			throws IOException, ClassNotFoundException, SQLException {
		Workbook workbook = null;
		if (fileName.endsWith("xls")) {
			workbook = new HSSFWorkbook(is);
		} else if (fileName.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(is);
		} else {
			request.setAttribute("error", "传入的文件格式应为Excel表格,请重新导入...");
			request.setAttribute("success", "");
			return;
		}
		JSONObject config = JSON.parseObject(ReadConfig.getValue(tableName));
		if (null == config) {
			request.setAttribute("error", "表的配置信息不存在,请先配置相关信息");
			request.setAttribute("success", "");
			throw new RuntimeException("表的配置信息不存在,请先配置相关信息");
		}
		JSONArray jsonArray = config.getJSONArray("code");
		int num = workbook.getNumberOfSheets();
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < num; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			if (sheet == null) {
				continue;
			}
			// 循环行Row
			Row row = null;
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				StringBuilder sb = new StringBuilder();
				// 判断第一行指标名称是否一致,如果不一致直接退出,返回错误信息
				row = sheet.getRow(rowNum);
				// 最后一列ZBID不为空再进行拼接
				if (null == row) {
					request.setAttribute("error", "excel格式有误.");
					throw new RuntimeException();
				}
				if (!getValue(row.getCell(jsonArray.size() - 1)).get("value").toString().equals("")
						&& getValue(row.getCell(jsonArray.size() - 1)).get("value").toString() != null) {
					for (int j = 0; j < jsonArray.size(); j++) {
						Map<String, Object> map = getValue(row.getCell(j));
						Object value = map.get("value");
						if (rowNum == 0) {
							if (!jsonArray.get(j).toString().equalsIgnoreCase((String) value)) {
								request.setAttribute("error",
										tableName + "表的配置项" + jsonArray.get(j).toString() + "有误,请检查!");
								request.setAttribute("success", "");
								throw new RuntimeException(tableName + "表的配置信息有误,请检查!");
							}
						} else if (rowNum >= 2) {
							value = "\'" + (String) value + "\'";
							sb.append(",").append(value);
						}
					}
					if (rowNum >= 2) {
						// 拼接SQL中的数据
						data.add(sb.toString().substring(1));
					}
				}
			}
		}
		ReadConfig.insertToDB(data, ReadConfig.JSONArrayToString(jsonArray), tableName);
	}

	/**
	 * 获取Excel每个单元格的值,包含日期,数字,字符串的类型判断
	 * 
	 * @param cell
	 * @return cell为null直接返回null
	 * @author KLP
	 */
	public static Map<String, Object> getValue(Cell cell) {
		String result = new String();
		Map<String, Object> map = new HashMap<String, Object>();
		if (null == cell) {
			map.put("type", 0);
			map.put("value", null);
			return map;
		}
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
			if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
				SimpleDateFormat sdf = null;
				if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
					sdf = new SimpleDateFormat("HH:mm");
				} else {// 日期
					sdf = new SimpleDateFormat("yyyy-MM-dd");
				}
				Date date = cell.getDateCellValue();
				result = sdf.format(date).toString().replace(",", "");
			} else if (cell.getCellStyle().getDataFormat() == 58) {
				// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				double value = cell.getNumericCellValue();
				Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
				result = sdf.format(date).toString().replace(",", "");
			} else {
				double value = cell.getNumericCellValue();
				CellStyle style = cell.getCellStyle();
				DecimalFormat format = new DecimalFormat();
				String temp = style.getDataFormatString();
				// 单元格设置成常规
				if (temp.equals("General")) {
					format.applyPattern("#");
				}
				result = format.format(value).toString().replace(",", "");
			}
			map.put("type", 1);
			break;
		case HSSFCell.CELL_TYPE_STRING:// String类型
			result = cell.getRichStringCellValue().toString().trim();
			map.put("type", 3);
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			map.put("type", 1);
			result = "";
		default:
			map.put("type", 1);
			result = "";
			break;
		}
		map.put("value", result);
		return map;
	}

}
