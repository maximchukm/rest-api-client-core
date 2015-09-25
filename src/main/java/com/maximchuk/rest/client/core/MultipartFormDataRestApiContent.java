package com.maximchuk.rest.client.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Maxim Maximchuk
 *         date 25-Sep-15.
 */
public class MultipartFormDataRestApiContent extends RestApiContent {

    private String boundary;
    private List<Part> parts = new ArrayList<Part>();

    public static MultipartFormDataRestApiContent create() {
        MultipartFormDataRestApiContent instance = new MultipartFormDataRestApiContent();
        instance.boundary = UUID.randomUUID().toString().split("-")[0];
        return instance;
    }

    public MultipartFormDataRestApiContent addPart(String name, RestApiContent content) {
        parts.add(new Part(name, content.getContentType(), content.getBytes()));
        return this;
    }

    public MultipartFormDataRestApiContent addFilePart(String name, String contentType, FileEntity fileEntity) {
        parts.add(new Part(name, contentType, fileEntity));
        return this;
    }

    @Override
    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            try {
                for (Part part : parts) {
                    os.write(("--" + boundary).getBytes());
                    os.write("\n".getBytes());
                    os.write(("Content-disposition: form-data; name=" + part.name).getBytes());
                    if (part.isFile) {
                        os.write(("; filename=" + part.filename).getBytes());
                    }
                    os.write("\n".getBytes());
                    os.write(("Content-type: " + part.contentType).getBytes());
                    os.write("\n\n".getBytes());
                    os.write(part.body);
                    os.write("\n".getBytes());
                }
                os.flush();
            } finally {
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }

    private class Part {

        private String name;
        private String contentType;
        private byte[] body;
        private String filename;
        private boolean isFile = false;

        public Part(String name, String contentType, byte[] body) {
            this.name = name;
            this.contentType = contentType;
            this.body = body;
        }

        public Part(String name, String contentType, FileEntity fileEntity) {
            this(name, contentType, fileEntity.getBody());
            this.filename = fileEntity.getName();
            this.isFile = true;
        }

    }

}
