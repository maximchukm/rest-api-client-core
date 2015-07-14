package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.util.StringParamBuilder;
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

    protected Type type;
    protected String name;
    protected Map<String, String> headers = new HashMap<String, String>();
    protected boolean forceQueryParams = false;
    protected RestApiContent content;

    @Deprecated
    private List<Header> apacheHeaders;

    protected int timeout = 10000;

    @Deprecated
    protected StatusLine statusLine;

    private Map<String, Object> params = new HashMap<String, Object>();

    @Deprecated
    private String contentType;

    @Deprecated
    private HttpEntity httpEntity;

    public RestApiMethod(String name, Type type) {
        this.name = name;
        this.type = type;
        if (type == Type.GET || type == Type.DELETE) {
            forceQueryParams = true;
        }
    }

    public RestApiMethod(Type type) {
        this(null, type);
    }

    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Deprecated
    public void addHeader(Header header) {
        if (apacheHeaders == null) {
            apacheHeaders = new ArrayList<Header>();
        }
        apacheHeaders.add(header);
    }

    public void forceQueryParams(boolean force) {
        forceQueryParams = force;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void putParam(String name, Object value) {
        params.put(name, value);
    }

    public void setContent(RestApiContent content) {
        this.content = content;
    }

    @Deprecated
    public void setStringData(String string) throws UnsupportedEncodingException {
        httpEntity = new StringEntity(string, ENCODING);
        addHeader(new BasicHeader("charset", ENCODING));
    }

    @Deprecated
    public void setJsonStringData(String jsonString) throws UnsupportedEncodingException {
        contentType = "application/json";
        httpEntity = new StringEntity(jsonString, ENCODING);
        addHeader(new BasicHeader("charset", ENCODING));
    }

    @Deprecated
    public void setByteData(byte[] data, String contentType) {
        this.contentType = contentType;
        httpEntity = new ByteArrayEntity(data);
    }

    @Deprecated
    public void setByteData(byte[] data) {
        setByteData(data, "application/octet-stream");
    }

    @Deprecated
    public void setHttpEntity(HttpEntity httpEntity, String contentType) {
        this.httpEntity = httpEntity;
        this.contentType = contentType;
    }

    @Deprecated
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
                httpRequestBase = new HttpGet(serverControllerUrl + paramString());
            }
            break;
            case DELETE: {
                httpRequestBase = new HttpDelete(serverControllerUrl + paramString());
            }
            break;
            case POST: {
                httpRequestBase = forceQueryParams ? new HttpPost(serverControllerUrl + paramString()) : new HttpPost(serverControllerUrl);
                if (httpEntity == null && !forceQueryParams) {
                    httpEntity = buildEncodedFormEntity();
                }
                if (httpEntity != null) {
                    ((HttpPost) httpRequestBase).setEntity(httpEntity);
                }
                if (contentType != null) {
                    httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                }
            }
            break;
            case PUT: {
                httpRequestBase = forceQueryParams ? new HttpPut(serverControllerUrl + paramString()) : new HttpPut(serverControllerUrl);
                if (httpEntity == null && !forceQueryParams) {
                    httpEntity = buildEncodedFormEntity();
                }
                if (httpEntity != null) {
                    ((HttpPut) httpRequestBase).setEntity(httpEntity);
                }
                if (contentType != null) {
                    httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                }
            }
            break;
        }

        if (apacheHeaders != null) {
            for (Header header : apacheHeaders) {
                httpRequestBase.addHeader(header);
            }
        }

        return httpRequestBase;
    }

    protected String paramString() {
        return new StringParamBuilder(params).build();
    }

    @Deprecated
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
