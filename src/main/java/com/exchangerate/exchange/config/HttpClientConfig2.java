package com.exchangerate.exchange.config;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.*;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

public abstract class HttpClientConfig2 implements InitializingBean {

    private HttpClient client;
    private List<BasicHeader> defaultHeaders = null;
    private static final int HTTP_TIMEOUT = 1000;

    private int requestTimeout = 60 * HTTP_TIMEOUT; // 60 sn.
    private int connectionTimeout = 10 * HTTP_TIMEOUT; // 10 sn
    private int socketTimeout = 60 * HTTP_TIMEOUT; // 60 sn

    @Override
    public void afterPropertiesSet() throws Exception {

        HttpProcessorBuilder builder = HttpProcessorBuilder.create();

        builder.add(new RequestAcceptEncoding());
        builder.add(new RequestConnControl());
        builder.add(new RequestTargetHost());
        builder.add(new ResponseContentEncoding());
        builder.add(new RequestContent());
        builder.add(new RequestClientConnControl());

        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            List<Header> headers = new ArrayList<Header>(defaultHeaders.size());
            for (Header header : defaultHeaders) {
                headers.add(header);
            }
            builder.add(new RequestDefaultHeaders(headers));
        }

        HttpProcessor processor = builder.build();

        SSLConnectionSocketFactory socketFactory = null;

        socketFactory = SSLConnectionSocketFactory.getSocketFactory();

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setDefaultMaxPerRoute(200);
        cm.setMaxTotal(400);

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(connectionTimeout);
        requestBuilder = requestBuilder.setSocketTimeout(socketTimeout);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(requestTimeout);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build());

        clientBuilder.setConnectionManager(cm);
        clientBuilder.setHttpProcessor(processor);

        client = clientBuilder.build();

    }

    public HttpClient getClient() {
        return client;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }
}
