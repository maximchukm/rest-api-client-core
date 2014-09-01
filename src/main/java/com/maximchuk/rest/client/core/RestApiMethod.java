package com.maximchuk.rest.client.core;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public final class RestApiMethod {

    protected String name;
    protected Type type;
    protected int timeout = 10000;
    protected List<Header> headers;

    public RestApiMethod(String name, Type type) {
        this.name = name;
        this.type = type;
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

    public enum Type {
        GET, POST
    }

}
