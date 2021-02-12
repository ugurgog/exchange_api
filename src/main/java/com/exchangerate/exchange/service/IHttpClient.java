package com.exchangerate.exchange.service;


import com.exchangerate.exchange.model.ProtocolEnum;

public interface IHttpClient {

	public <T> T get(String url, String data, ProtocolEnum service, Class<T> clazz);

}
 