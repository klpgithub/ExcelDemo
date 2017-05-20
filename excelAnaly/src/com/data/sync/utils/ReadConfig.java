package com.data.sync.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ReadConfig {
	private static Properties prop = new Properties();

	static {
		initConfig();
	}

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

	public static String getTableConfig(String tableName) {
		String value = prop.getProperty(tableName);
		if (value == null) {
			initConfig();
			return prop.getProperty(tableName);
		} else {
			return value;
		}
	}

	public static Connection getConn() throws ClassNotFoundException, SQLException {
		Class.forName(prop.getProperty("DRIVER"));
		Connection conn = DriverManager.getConnection(prop.getProperty("CONNURL"), prop.getProperty("USERNAME"),
				prop.getProperty("PASSWORD"));
		return conn;
	}

	
}
