package com.exchangerate.exchange.service;

import org.dom4j.Element;

import java.util.Map;

public interface IHttpClient {

    Element post(String postData, boolean retry, String serviceUrl, String soapAction);
    String postAsJson(String postData, boolean retry, String serviceUrl);
    String getAsJson(String serviceUrl);
    byte[] getByteArray(String url);
    <T> T get(String string, String paramData, Map<String, String> requestProperties,
              Class<T> clazz);
    <T> T post(String url, Map<String, String> params, Map<String, String> headerProperties, Class<T> clazz,
               Object request);
}
