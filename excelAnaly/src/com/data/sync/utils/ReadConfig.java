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
	public static void insertToDB(List<String> datas, String codes, String tableName)
			throws SQLException, ClassNotFoundException {
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuilder sb = new StringBuilder();
		try {
			conn = getConn();
			conn.setAutoCommit(false);
			sb.append(" insert into  ").append(tableName).append(" ( ").append(codes).append(" ) ").append(" values ");
			for (String data : datas) {
				sb.append(" ( ").append(data).append(" ) ").append(",");
			}
			String sql = sb.toString();
			sql = sql.substring(0, sql.length() - 1);
			ps = conn.prepareStatement(sql);
			ps.execute();
			conn.commit();
		} finally {
			if (null != conn) {
				conn.close();
			}
		}
	}

	/**
	 * 去掉JSONArray前后的 [ ] 去掉最后一列ZY状态标识位
	 * 
	 * @param array
	 * @return 返回数据为数据表中所有的列名
	 */
	public static String JSONArrayToString(JSONArray array) {
//		array.remove(array.size() - 1);
		String string = array.toString();
		string = string.substring(1);
		string = string.substring(0, string.length() - 1).replace("\"", "");
		return string;
	}

}
