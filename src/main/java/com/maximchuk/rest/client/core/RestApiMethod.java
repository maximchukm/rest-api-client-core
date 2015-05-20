package com.maximchuk.rest.client.core;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public final class RestApiMethod {

    private static final String ENCODING = "UTF-8";
    protected String name;
    protected Type type;
    protected int timeout = 10000;
    protected List<Header> headers;
    protected StatusLine statusLine;

    private Map<String, Objects> params = new HashMap<String, Objects>();

    private String contentType;
    private HttpEntity httpEntity;

    public RestApiMethod(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public RestApiMethod(Type type) {
        this("", type);
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

    public void putParam(String name, Objects value) {
        params.put(name, value);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    protected void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    protected HttpRequestBase prepareHttpRequest(String serverControllerUrl) throws IOException{
        HttpRequestBase httpRequestBase = null;
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
                httpRequestBase = new HttpPut();
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
