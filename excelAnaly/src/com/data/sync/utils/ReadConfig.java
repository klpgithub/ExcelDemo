package com.data.sync.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONArray;

public class ReadConfig {
	private static Properties prop = new Properties();

	static {
		initConfig();
	}

	/**
	 * 初始化配置
	 */
	public static void initConfig() {
		try {
			prop.load(new InputStreamReader(
					ReadConfig.class.getClassLoader().getResourceAsStream("tableConfig.properties"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取配置属性值
	 * 
	 * @param name
	 * @return
	 */
	public static String getValue(String name) {
		String value = prop.getProperty(name);
		if (value == null) {
			initConfig();
			return prop.getProperty(name);
		} else {
			return value;
		}
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConn() throws ClassNotFoundException, SQLException {
		Class.forName(prop.getProperty("DRIVER"));
		Connection conn = DriverManager.getConnection(prop.getProperty("CONNURL"), prop.getProperty("USERNAME"),
				prop.getProperty("PASSWORD"));
		return conn;
	}

	/**
	 * 拼接sql 插入数据
	 * 
	 * @param datas
	 * @param codes
	 * @param tableName
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void insertToDB(List<String> datas, JSONArray array, String tableName)
			throws SQLException, ClassNotFoundException {
		String codes = JSONArrayToString(array);
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuilder sb = new StringBuilder();
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			String deleteSql = "";
			for (String data : datas) {
				// 汇总表
				if (tableName.startsWith("fact")) {
					deleteSql = deleteByBBGAndZBID(data, array, tableName);
				} else {
					deleteSql = deleteByBH(data, array, tableName);
				}
				ps = conn.prepareStatement(deleteSql);
				ps.execute();
				sb.append(" insert into  ").append(tableName).append(" ( ").append(codes).append(" ) ")
						.append(" values ");
				sb.append(" ( ").append(data).append(" ) ");
				String sql = sb.toString();
				ps = conn.prepareStatement(sql);
				ps.execute();
				sb = new StringBuilder();
			}
			conn.commit();
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}

	/**
	 * 汇总表每次添加先根据本次添加的BBQ报告期跟ZBID指标ID进行删除
	 * 
	 * @author : KLP
	 * @return
	 */
	public static String deleteByBBGAndZBID(String data, JSONArray array, String tableName) {
		int ZBIDindex = array.indexOf(array.get(array.size()-1));
		int BBQindex = array.indexOf("BBQ");
		String[] rows = data.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(tableName).append(" where BBQ = ").append(rows[BBQindex])
				.append(" and  ").append(array.get(array.size()-1)).append(" = ").append(rows[ZBIDindex]);
		return sb.toString();
	}

	/**
	 * 汇总表_维表每次添加先根据本次添加的BH编号进行删除
	 * 
	 * @author : KLP
	 * @return
	 */
	public static String deleteByBH(String data, JSONArray array, String tableName) {
		int BHindex = array.indexOf("BH");
		String[] rows = data.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(tableName).append(" where BH = ").append(rows[BHindex]);
		return sb.toString();
	}

	public static void main(String[] args) {
		JSONArray array = new JSONArray();
		array.add("id");
		array.add("mc");
		array.add("BBQ");
		array.add("ppp");
		array.add("BH");
		array.add("ZBID");
		String sql = deleteByBH("1,'aaa','201505','525252','201701'", array, "table");
		System.out.println(sql);

	}

	/**
	 * 去掉JSONArray前后的 [ ] 去掉最后一列ZY状态标识位
	 * 
	 * @param array
	 * @return 返回数据为数据表中所有的列名
	 */
	public static String JSONArrayToString(JSONArray array) {
		// array.remove(array.size() - 1);
		String string = array.toString();
		string = string.substring(1);
		string = string.substring(0, string.length() - 1).replace("\"", "");
		return string;
	}

}
