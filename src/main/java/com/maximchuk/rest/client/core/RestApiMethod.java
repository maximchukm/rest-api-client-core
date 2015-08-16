package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.util.StringParamBuilder;

import java.util.HashMap;
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

    protected int timeout = 10000;

    private Map<String, Object> params = new HashMap<String, Object>();

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

    public void forceQueryParams() {
        forceQueryParams = true;
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

    protected String paramString() {
        return new StringParamBuilder(params).build();
    }

    public enum Type {
        GET, POST, PUT, DELETE
    }

}
