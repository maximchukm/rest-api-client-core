package com.maximchuk.rest.client.core;

import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author Maxim Maximchuk
 *         date 25-Sep-15.
 */
public class MultipartFormDataRestApiContentTest extends AbstractTest {

    @Test
    public void testCreate() {
        try {
            MultipartFormDataRestApiContent content = MultipartFormDataRestApiContent.create();
            content.addPart("param1", RestApiContent.createText("helllo!"));
            content.addFilePart("file1", "image/jpeg", new FileEntity("image1", "bytes".getBytes()));
            content.addFilePart("file2", "image/jpeg", new FileEntity("image2", "bytes".getBytes()));
            content.addFilePart("file3", "image/jpeg", new FileEntity("image3", "bytes".getBytes()));
            System.out.println(content.getContentType());
            System.out.println(new String(content.getBytes()));
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
}