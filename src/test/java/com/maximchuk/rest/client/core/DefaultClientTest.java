package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.auth.BasicHttpCredential;
import com.maximchuk.rest.client.auth.OAuthBearerCredential;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

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
            String username = "test_user";
            String password = "some_password";

            DefaultClient client = new DefaultClient("http://headers.jsontest.com", "/");
            client.setCredential(BasicHttpCredential.create(username, password));

            RestApiMethod method = new RestApiMethod(RestApiMethod.Type.GET);
            RestApiResponse response = client.executeMethod(method);

            System.out.println(response.getString());

            Assert.assertEquals("Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes()),
                    new JSONObject(response.getString()).getString("Authorization"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testExecuteMethodWithOAuthBearerCredential() throws Exception {
        try {
            String accessToken = "some_token";

            DefaultClient client = new DefaultClient("http://headers.jsontest.com", "/");
            client.setCredential(OAuthBearerCredential.create(accessToken));

            RestApiMethod method = new RestApiMethod(RestApiMethod.Type.GET);
            RestApiResponse response = client.executeMethod(method);

            System.out.println(response.getString());

            Assert.assertEquals("Bearer " + accessToken,
                    new JSONObject(response.getString()).getString("Authorization"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}