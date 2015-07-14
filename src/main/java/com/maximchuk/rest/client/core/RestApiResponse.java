package com.maximchuk.rest.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * @author Maxim Maximchuk
 *         date 14.07.2015.
 */
public class RestApiResponse {

    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String FILENAME_PREF = "filename=";

    private int statusCode;
    private byte[] content;
    private FileEntity fileEntity;

    protected RestApiResponse(HttpURLConnection connection) throws IOException {
        this.statusCode = connection.getResponseCode();

        InputStream in = connection.getInputStream();
        try {
            content = new byte[in.available()];
            in.read(content);
        } finally {
            in.close();
        }

        String contentDispositionHeader = connection.getHeaderField(CONTENT_DISPOSITION_HEADER);

        if (contentDispositionHeader != null) {
            fileEntity = new FileEntity(contentDispositionHeader.substring(contentDispositionHeader.indexOf(FILENAME_PREF) + FILENAME_PREF.length())
                    .replace("\"", ""), content);
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getBytes() {
        return content;
    }

    public String getString() {
        try {
            return new String(content, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public FileEntity getFileEntity() {
        return fileEntity;
    }

    public boolean isFile() {
        return fileEntity != null;
    }
}
