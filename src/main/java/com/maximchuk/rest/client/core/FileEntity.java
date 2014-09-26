package com.maximchuk.rest.client.core;

/**
 * @author Maxim Maximchuk
 *         date 26.09.2014.
 */
public class FileEntity {

    private String name;
    private byte[] body;

    protected FileEntity(String name, byte[] body) {
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public byte[] getBody() {
        return body;
    }
}
