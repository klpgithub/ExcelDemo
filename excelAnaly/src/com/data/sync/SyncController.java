package com.data.sync;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.data.sync.utils.ReadConfig;

@SuppressWarnings("serial")
public class SyncController extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		String table = request.getParameter("tableName");
//		System.out.println(table);
//		// 这里先写死一个报表...
//		// 获取表名///
//		InputStream is = request.getInputStream();
//		String id = request.getParameter("id");
		// 开始
		
		request = new MultipartRequestWrapper(request);
		String table = request.getParameter("tableName");
		System.out.println(table);
	}

	/**
	 * 解析Excel
	 * @param is  Excel输入流
	 * @param fileName Excel文件名
	 * @throws IOException 
	 */
	public static void analyExcel(InputStream is,String fileName,HttpServletRequest request) throws IOException {
		Workbook workbook = null;
		if (fileName.endsWith("xsl")) {
			workbook = new HSSFWorkbook(is);
		}else{
			workbook = new XSSFWorkbook(is);
		}
		JSONObject config = JSON.parseObject(ReadConfig.getTableConfig("fact_gjcfdlhyhzb"));
		JSONArray jsonArray = config.getJSONArray("code");
		int num = workbook.getNumberOfSheets();
		for (int i = 0; i < num; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			if (sheet == null) {
				continue;
			}
			// 循环行Row
			Row row = null;
			StringBuilder sb = new StringBuilder();
			List<String> data = new ArrayList<String>();
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
				// 判断第一行指标名称是否一致,如果不一致直接退出,返回错误信息
				row = sheet.getRow(rowNum);
				for (int j = 0; j < jsonArray.size(); j++) {
					if (rowNum == 0) {
						if (!jsonArray.get(j).toString().equalsIgnoreCase(getValue(row.getCell(j)))) {
							request.setAttribute("error", jsonArray.get(j) + "指标有误,请检查数据格式!");
							return;
						}
					} else {
						sb.append(getValue(row.getCell(j)));
					}
				}
				data.add(sb.toString());
			}
		}
	}

	/**
	 * 获取Excel每个单元格的值
	 * 
	 * @param cell
	 * @return
	 */
	private static String getValue(Cell cell) {
		if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			// 返回布尔类型的值
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			// 返回数值类型的值
			return String.valueOf(cell.getNumericCellValue());
		} else {
			// 返回字符串类型的值
			return String.valueOf(cell.getStringCellValue());
		}
	}

//	private static int addData(List<String> data, JSONArray keys) {
//
//	}

}
