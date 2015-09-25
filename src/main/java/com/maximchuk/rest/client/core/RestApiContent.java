package com.maximchuk.rest.client.core;

/**
 * @author Maxim Maximchuk
 *         date 14.07.2015.
 */
public class RestApiContent {

    private String contentType;
    private byte[] bytes;

    protected RestApiContent() {
    }

    private RestApiContent(String contentType, byte[] content) {
        this.contentType = contentType;
        this.bytes = content;
    }

    public static RestApiContent create(String contentType, byte[] content) {
        return new RestApiContent(contentType, content);
    }

    public static RestApiContent createOctetStream(byte[] content) {
        return create("application/octet-stream", content);
    }

    public static RestApiContent createText(String text) {
        return create("text/plain", text.getBytes());
    }

    public static RestApiContent createJson(String jsonString) {
        return create("application/json", jsonString.getBytes());
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
