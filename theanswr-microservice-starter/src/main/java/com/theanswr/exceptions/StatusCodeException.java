package com.theanswr.exceptions;

import org.apache.http.HttpException;
/**
 * 自定义http请求返回的状态码异常
 * @author yao
 *
 */
public class StatusCodeException extends HttpException{

	public StatusCodeException() {
		super();
	}

	public StatusCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public StatusCodeException(String message) {
		super(message);
	}

}
