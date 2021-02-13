package com.exchangerate.exchange.service.impl;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.exchangerate.exchange.service.IHttpClient;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService implements IHttpClient {

    private Gson gson = new Gson();
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private SAXReader reader;
    private List<BasicHeader> defaultHeaders = null;

    // 60 sn.
    private int requestTimeout = 60 * 1000;

    // 10 sn
    private int connectionTimeout = 10000;

    // 60 sn
    private int socketTimeout = 60000;

    private boolean enableCompression = true;

    public HttpClientService() {
    }

    @Override
    public Element post(String postData, boolean retry1, String serviceUrl, String soapAction) {
        HttpPost post = new HttpPost(serviceUrl);
        String content = null;
        try {
            StringEntity strent = new StringEntity(postData, "UTF-8");
            post.setEntity(strent);
            post.addHeader("Content-Type", "text/xml");

            if (soapAction != null)
                post.addHeader("SOAPAction", soapAction);

            HttpResponse response;
            try (CloseableHttpClient httpclient = getClient()) {
                response = httpclient.execute(post);
                HttpEntity entity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    Document document = parseResponse(entity.getContent());
                    Element rootXML = document.getRootElement();
                    StringWriter sw = new StringWriter();
                    XMLWriter xw = new XMLWriter(sw);
                    xw.write(document);
                    LOG.info("::postContent  status:{} content:{}  post :{}", statusLine, rootXML.asXML(), postData);
                    return rootXML;
                }
                content = EntityUtils.toString(entity);
                LOG.error("::postContent  status:{} content:{}  post :{}", statusLine, content, postData);
            }
        } catch (Exception e) {
            LOG.error("::postContent post :{} content:{} exc:{}", postData, content, e);
        }
        return null;
    }

    @Override
    public String postAsJson(String postData, boolean retry, String serviceUrl) {
        HttpPost post = new HttpPost(serviceUrl);
        String content = null;
        try {
            StringEntity strent = new StringEntity(postData, "UTF-8");
            post.setEntity(strent);
            post.addHeader("Content-Type", "application/json");
            HttpResponse response;
            try (CloseableHttpClient httpclient = getClient()) {
                response = httpclient.execute(post);
                HttpEntity entity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    String respStr = EntityUtils.toString(response.getEntity());
                    LOG.info("::postContentJson serviceUrl:{} status:{} content:{}  post :{}", serviceUrl, statusLine,
                            respStr, postData);
                    return respStr;
                }
                content = EntityUtils.toString(entity);
                LOG.error("::postContentJson serviceUrl:{} status:{} content:{}  post :{}", serviceUrl, statusLine,
                        content, postData);
            }
        } catch (Exception e) {
            LOG.error("::postContentJson serviceUrl:{} post :{} content:{} exc:{}", serviceUrl, postData, content, e);
        }
        return null;
    }

    @Override
    public String getAsJson(String serviceUrl) {
        HttpGet get = new HttpGet(serviceUrl);
        String content = null;
        try {
            get.addHeader("Content-Type", "application/json");
            HttpResponse response;
            try (CloseableHttpClient httpclient = getClient()) {
                response = httpclient.execute(get);
                HttpEntity entity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    String respStr = EntityUtils.toString(response.getEntity());
                    LOG.info("::getContentJson  status:{} content:{}", statusLine, respStr);
                    return respStr;
                }
                content = EntityUtils.toString(entity);
                LOG.error("::getContentJson  status:{} content:{}", statusLine, content);
            }
        } catch (Exception e) {
            LOG.error("::getContentJson content:{} exc:{}", content, e);
        }
        return null;
    }

    @Override
    public byte[] getByteArray(String url) {
        HttpGet get = new HttpGet(url);
        LOG.warn("::getByteArray url:{}", url);
        try {
            HttpResponse response;
            try (CloseableHttpClient httpclient = getClient()) {
                response = httpclient.execute(get);
                HttpEntity entity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    entity.writeTo(baos);
                    byte[] byteArr = baos.toByteArray();
                    LOG.warn("::getByteArray arraySize:{}", byteArr.length);
                    return byteArr;
                } else {
                    LOG.warn("::getByteArray not ok. statusLine:{}", new Gson().toJson(statusLine));
                }
            }
        } catch (Exception e) {
            LOG.error("::getContent getUrl:{}", url, e);
        }
        return null;
    }

    private CloseableHttpClient getClient() {
        HttpProcessorBuilder builder = HttpProcessorBuilder.create();
        if (enableCompression) {
            builder.add(new RequestAcceptEncoding());
        }
        builder.add(new RequestConnControl());
        builder.add(new RequestTargetHost());
        builder.add(new ResponseContentEncoding());
        builder.add(new RequestContent());
        builder.add(new RequestClientConnControl());
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            List<Header> headers = new ArrayList<>(defaultHeaders.size());
            headers.addAll(defaultHeaders);
            builder.add(new RequestDefaultHeaders(headers));
        }
        HttpProcessor processor = builder.build();
        SSLConnectionSocketFactory socketFactory ;
        socketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", socketFactory)
                .build();
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
        this.reader = new SAXReader();
        return clientBuilder.build();
    }

    private Document parseResponse(InputStream input) throws Exception {
        return reader.read(input);
    }

    @Override
    public <T> T get(String url, String params, Map<String, String> reqProps, Class<T> clazz) {
        try (CloseableHttpClient client = getClient()) {
            String data = processedUrl(params);
            if (params != null && params.length() > 0) {
                url = url + data;
            }

            LOG.info("::httpClient get url:{}", url);

            HttpGet get = new HttpGet(url);
            setHeaderProperties(get, reqProps);
            CloseableHttpResponse response = client.execute(get);
            int httpStatus = response.getStatusLine().getStatusCode();
            if (httpStatus != 200 && httpStatus != 201) {
                LOG.error("::httpClient get url:{} status:{}", url, httpStatus);
            }
            String responseText = EntityUtils.toString(response.getEntity());
            if (clazz.equals(String.class)) {
                return (T) responseText;
            }
            T result = gson.fromJson(responseText, clazz);
            return result;
        } catch (Exception e) {
            LOG.error("::httpClient get url:{}", url, e);
            return null;
        }
    }

    @Override
    public <T> T post(String url, Map<String, String> params, Map<String, String> reqProps, Class<T> clazz,
                      Object request) {
        try (CloseableHttpClient client = getClient()) {
            url = url + queryParams(params);
            HttpPost post = new HttpPost(url);
            StringEntity strent = new StringEntity(gson.toJson(request), "UTF-8");
            post.setEntity(strent);
            setHeaderProperties(post, reqProps);
            CloseableHttpResponse response = client.execute(post);
            int httpStatus = response.getStatusLine().getStatusCode();
            if (httpStatus != 200 && httpStatus != 201) {
                LOG.error("::httpClient post url:{} status:{}", url, httpStatus);
            }
            String responseText = EntityUtils.toString(response.getEntity());
            if (clazz.equals(String.class)) {
                return (T) responseText;
            }
            T result = gson.fromJson(responseText, clazz);
            return result;
        } catch (Exception e) {
            LOG.error("::httpClient get url:{}", url, e);
            return null;
        }
    }

    private void setHeaderProperties(HttpRequest get, Map<String, String> reqProps) {
        for (String key : reqProps.keySet()) {
            get.setHeader(key, reqProps.get(key));
        }
    }

    private String queryParams(Map<String, String> params) {
        StringBuilder b = new StringBuilder();
        if (params != null) {
            b.append("?");
            for (String key : params.keySet()) {
                b.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        return b.toString();
    }

    public static String processedUrl(String data) {
        if (data == null && data.length() > 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        String newParams = "{1}?access_key={2}&base={3}&symbols={4}";

        String[] path = data.split("/");
        for (int i = 0; i < path.length; i++) {
            newParams = newParams.replace("{" + (i + 1) + "}", path[i]);
        }

        buf.append(newParams);
        return buf.toString();
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setReader(SAXReader reader) {
        this.reader = reader;
    }

    public void setDefaultHeaders(List<BasicHeader> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setEnableCompression(boolean enableCompression) {
        this.enableCompression = enableCompression;
    }
}
