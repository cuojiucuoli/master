package com.theanswr.util;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.theanswr.commlib.CommLib;
import com.theanswr.exceptions.StatusCodeException;
import com.theanswr.utils.UnicodeUtil;

public class HttpClientUtil {
	
	private final static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
	/**
	 * 发送http请求至目标获取对应的返回结果
	 * @param map {key:DEEPLOOK_ALIAS_LOOKUP_INPUT value:xxx}
	 * @return 第三方响应结果json字符串
	 */
	public static String getDeepLookResult(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		if (url!=null&&""!=url) {
			String unicodeToCn = null;
			try {

				
				HttpGet httpGet = new HttpGet(url);
				CloseableHttpResponse response = null;
				try {
					response = httpClient.execute(httpGet);
				} catch (Exception e) {
					log.error("this request has encounter serveral problems,please check out 'url parameters' defined by yourself ",e);			
					throw e;
				}
				//获取请求页面的状态码
				int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode>=400&&statusCode<500) {
					log.error("Before reaching the server,you have a severe error during connecting process,please check out your syntax format and url which is no match to server");
					throw new StatusCodeException("Before reaching the server,you have a severe error during connecting process,please check out your syntax format and url which is no match to server");
				}else if(statusCode>=500) {
					log.error("after reaching the server,there is a internal server error");
					throw new StatusCodeException("after reaching the server,there is a internal server error");
				}
				
				HttpEntity entity = response.getEntity();
				String json = EntityUtils.toString(entity, "utf-8");
				//对响应结果返回的json中文数据进行unicode解码
				unicodeToCn = UnicodeUtil.decodeUnicode(json);
				
			} catch (Exception e) {
				e.printStackTrace();

			}
			return unicodeToCn;

		}
			return null;
	}
}
