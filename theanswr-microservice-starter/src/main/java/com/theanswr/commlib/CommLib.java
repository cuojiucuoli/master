package com.theanswr.commlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.util.LoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CommLib {

	private final static Logger log = LoggerFactory.getLogger(CommLib.class);

	/**
	 * 
	 * @param parameters
	 *            :map集合
	 * @param code
	 *            :DEEPLOOK_ALIAS_LOOKUP_INPUT
	 * @param optional
	 *            :默认true
	 * @return key对应的value
	 */
	public static String getText(Map<String, Object> parameters, String code, boolean optional) {

		log.info("getting parameters -{" + code + "}");

		if (parameters != null && parameters.size() > 0) {
			if (parameters.get(code) != null) {
				return (String) parameters.get(code);
			} else if (optional) {
				log.info("key {" + code + "} not found,use default value");
			}
		}
		return null;
	}

	/**
	 * 
	 * @param initialAskTime
	 *            对端初始化时间
	 * @return 当前时间是否大于对端初始化时间后的24小时.true of false
	 */
	public static boolean isAlreadyAsked(Long initialAskTime) {
		log.info("the InitialAskTime is  -{" + initialAskTime + "}");
		long currentTime = System.currentTimeMillis();
		log.info("the CurrentTime is  -{" + currentTime + "}");
		return (currentTime >= initialAskTime + 86400L);
	}

	/**
	 * 
	 * @param arr
	 *            key为result的JSON数组
	 * @return
	 */
	public static boolean checkAllWeightIsZero(JSONArray arr) {

		if (arr != null) {

			for (Object object : arr) {
				JSONObject jObject = (JSONObject) object;
				String weight = (String) jObject.get("weight");
				if (Integer.parseInt(weight) > 0) {

					return false;
				}

			}
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param arr
	 *            key为result的jsonArray数组
	 * @param key
	 * @return
	 */
	public static <T> List<T> getJsonArrValueByKey(JSONArray arr, T key) {
		ArrayList<T> alist = new ArrayList<T>();
		// 把JSONArray遍历得到里面每个JSONObject对象
		for (Object object : arr) {
			JSONObject jObject = (JSONObject) object;
			// 获取每个object的value,放入list集合
			T value = (T) jObject.get(key);
			alist.add(value);
		}
		return alist;
	}
}
