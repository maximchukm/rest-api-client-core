package com.maximchuk.rest.client.core;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public final class RestApiMethod {

    private static final String ENCODING = "UTF-8";
    private String name;
    private Type type;
    private List<Header> headers;

    protected int timeout = 10000;
    protected StatusLine statusLine;

    private Map<String, Object> params = new HashMap<String, Object>();

    private String contentType;
    private HttpEntity httpEntity;

    public RestApiMethod(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public RestApiMethod(Type type) {
        this(null, type);
    }

    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    public void addHeader(Header header) {
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        headers.add(header);
    }

    public void putParam(String name, Object value) {
        params.put(name, value);
    }

    public void setStringData(String string) throws UnsupportedEncodingException {
        httpEntity = new StringEntity(string);
    }

    public void setJsonStringData(String jsonString) throws UnsupportedEncodingException {
        contentType = "application/json";
        httpEntity = new StringEntity(jsonString);
    }

    public void setByteData(byte[] data, String contentType) {
        this.contentType = contentType;
        httpEntity = new ByteArrayEntity(data);
    }

    public void setHttpEntity(HttpEntity httpEntity, String contentType) {
        this.httpEntity = httpEntity;
        this.contentType = contentType;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    protected HttpRequestBase prepareHttpRequest(String serverControllerUrl) throws IOException{
        HttpRequestBase httpRequestBase = null;
        if (name != null) {
            serverControllerUrl += "/" + name;
        }
        switch (type) {
            case GET: {
                httpRequestBase = new HttpGet(serverControllerUrl + buildQueryParamString());
            } break;
            case DELETE: {
                httpRequestBase = new HttpDelete(serverControllerUrl + buildQueryParamString());
            } break;
            case POST: {
                httpRequestBase = new HttpPost(serverControllerUrl);
                httpRequestBase.addHeader("charset", ENCODING);
                if (httpEntity == null) {
                    httpEntity = buildEncodedFormEntity();
                }
                if (httpEntity != null) {
                    ((HttpPost) httpRequestBase).setEntity(httpEntity);
                }
                if (contentType != null) {
                   httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                }
            } break;
            case PUT: {
                httpRequestBase = new HttpPut(serverControllerUrl);
                httpRequestBase.addHeader("charset", ENCODING);
                if (httpEntity == null) {
                    httpEntity = buildEncodedFormEntity();
                }
                if (httpEntity != null) {
                    ((HttpPut) httpRequestBase).setEntity(httpEntity);
                }
                if (contentType != null) {
                    httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                }
            } break;
        }

        if (headers != null) {
            for (Header header : headers) {
                httpRequestBase.addHeader(header);
            }
        }

        return httpRequestBase;
    }

    private String buildQueryParamString() {
        StringBuilder paramBuilder = new StringBuilder();
        if (!params.isEmpty()) {
            paramBuilder.append("?");
            for (String key : params.keySet()) {
                paramBuilder.append(key).append("=").append(params.get(key));
                paramBuilder.append("&");
            }
            paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        }
        return paramBuilder.toString();
    }

    private UrlEncodedFormEntity buildEncodedFormEntity() throws UnsupportedEncodingException {
        UrlEncodedFormEntity entity = null;
        if (!params.isEmpty()) {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                formParams.add(new BasicNameValuePair(key, params.get(key).toString()));
            }
            entity = new UrlEncodedFormEntity(formParams, ENCODING);
            entity.setContentEncoding(ENCODING);
        }
        return entity;
    }

    public enum Type {
        GET, POST, PUT, DELETE
    }

}
