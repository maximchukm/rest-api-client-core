package com.maximchuk.rest.client.core;

import org.apache.http.Header;
import org.apache.http.StatusLine;

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
    protected StatusLine statusLine;

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

    protected void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public enum Type {
        GET, POST, PUT, DELETE
    }

}
