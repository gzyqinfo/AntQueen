package com.chetiwen.server.qucent;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


public class ParamUtil {

	/**
	 * 将Map对象数据转为指定签名格式
	 * 数据根据key排序，用等号连接value，并用逗号隔开
	 * key1=value1,key2=value2
	 * */
	public static String sortParam(Map<String, Object> map) {
		
		StringBuffer sb = new StringBuffer();
		
		for (Map.Entry<String, Object> entry : sortMapByKey(map).entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append(",");
		}
		
		if(sb.length() < 1){
			return "";
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 
	 * 根据Map中的key进行排序
	 * */
	private static Map<String, Object> sortMapByKey(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(
				new MapKeyComparator());
		sortMap.putAll(map);
		return sortMap;
	}

	// 实现一个比较器类
	private static class MapKeyComparator implements Comparator<String> {

		public int compare(String s1, String s2) {
			return s1.compareTo(s2); // 从小到大排序
		}

	}
}
