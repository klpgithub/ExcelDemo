package com.data.sync.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TestSync {

	public static void main(String[] args) {
		
		String json = "{desc:'工交处总值汇总表_维表',major:'工业','code': ['MC','PID','BH'],'codeName': ['名称','pid','编号']}";
		JSONObject object = JSON.parseObject(json);
		
	}

}
