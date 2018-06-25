package com.theanswr.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * @author yao
 * 属性配置类
 */
import org.springframework.stereotype.Component;
/**
 * 属性配置类,resource/application.properties
 * @author yao
 *
 */
@Component
@ConfigurationProperties(prefix="com.theanswr")
public class PropertiesConfiguration {

	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
