package com.maximchuk.rest.client.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Maxim Maximchuk
 *         date 12.07.2015.
 */
public class DefaultClientTest {

    private static final String SERVER_URL = "http://efarmer.mobi:9080/localization-service/api";
    private static final String CONTROLLER_NAME = "i18n";

    private DefaultClient client;

    @Before
    public void setUp() throws Exception {
        client = new DefaultClient(SERVER_URL, CONTROLLER_NAME);
    }

    @Test
    public void testExecuteMethod() throws Exception {
        try {
            RestApiMethod method = new RestApiMethod("translate/Field", RestApiMethod.Type.GET);
            String response = client.executeMethod(method);
            Assert.assertTrue(response != null);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}