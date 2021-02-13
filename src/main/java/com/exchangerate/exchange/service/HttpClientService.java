package com.exchangerate.exchange.service;

import com.exchangerate.exchange.config.HttpClientConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService extends HttpClientConfig implements IHttpClient {

    private Logger LOG = LoggerFactory.getLogger(getClass());
    private ObjectMapper objectMapper;

    public HttpClientService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T get(String url, String data, Class<T> clazz) {
        String params = processedUrl(data);
        if (params != null && params.length() > 0) {
            url = url + params;
        }
        try {
            HttpGet get = new HttpGet(url);

            get.addHeader("Content-Type", "application/json");
            get.addHeader("Accept", "*/*");

            String result = null;

            try {
                HttpResponse response = getClient().execute(get);
                HttpEntity entity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    result = EntityUtils.toString(entity, "UTF-8");
                    LOG.info("::get url:{} returnJson:{} ", url, result);
                } else {
                    String content = null;
                    if (entity != null)
                        content = EntityUtils.toString(entity);

                    int httpErrorCode = statusLine.getStatusCode();
                    LOG.error("::get Error url:{}  content:{} status:{}", url, content, httpErrorCode);
                }
            } catch (Exception e) {
                LOG.error("::get  ", e);
            } finally {
                get.releaseConnection();
            }
            return objectMapper.readValue(result, clazz);
        } catch (Exception e) {
            LOG.error("::get Error ", e);
        }

        return null;
    }

    private String processedUrl(String data) {
        if (data == null && data.length() > 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        String newParams = "{1}?base={2}&symbols={3}";

        String[] path = data.split("/");
        for (int i = 0; i < path.length; i++) {
            newParams = newParams.replace("{" + (i + 1) + "}", path[i]);
        }

        buf.append(newParams);
        return buf.toString();
    }
}
