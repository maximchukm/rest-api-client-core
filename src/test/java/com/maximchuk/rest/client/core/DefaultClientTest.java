package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.auth.BasicHttpCredential;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import sun.misc.BASE64Encoder;

/**
 * @author Maxim Maximchuk
 *         date 12.07.2015.
 */
public class DefaultClientTest {

    @Test
    public void testExecuteMethod() throws Exception {
        try {
            DefaultClient client = new DefaultClient("http://md5.jsontest.com", "/");
            RestApiMethod method = new RestApiMethod(RestApiMethod.Type.GET);
            method.putParam("text", "someText");
            RestApiResponse response = client.executeMethod(method);
            System.out.println(response.getString());
            Assert.assertTrue(response.getStatusCode() == 200);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testExecuteMethodWithBasicHttpCredential() throws Exception {
        try {
            String username = "testUser";
            String password = "somePassword";

            DefaultClient client = new DefaultClient("http://headers.jsontest.com", "/");
            client.setCredential(BasicHttpCredential.create(username, password));

            RestApiMethod method = new RestApiMethod(RestApiMethod.Type.GET);
            RestApiResponse response = client.executeMethod(method);

            System.out.println(response.getString());
            Assert.assertEquals(new JSONObject(response.getString()).getString("Authorization"),
                    "Basic " + new BASE64Encoder().encode((username + ":" + password).getBytes()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}