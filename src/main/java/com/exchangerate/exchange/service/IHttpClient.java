package com.exchangerate.exchange.service;


public interface IHttpClient {

	public <T> T get(String url, String data, Class<T> clazz);

}
 