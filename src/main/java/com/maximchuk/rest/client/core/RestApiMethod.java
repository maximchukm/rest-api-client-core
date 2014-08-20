package com.maximchuk.rest.client.core;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public final class RestApiMethod {

    protected String name;
    protected Type type;
    protected int timeout = 10000;

    public RestApiMethod(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public void setTimeout(int timeoutMillis) {
        this.timeout = timeoutMillis;
    }

    public enum Type {
        GET, POST
    }

}
