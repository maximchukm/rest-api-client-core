package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.http.HttpException;
import com.maximchuk.rest.client.oauth.OAuthCredential;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Maxim Maximchuk
 *         date 12.07.2015.
 */
public class DefaultClientTest {

    private static final String SERVER_URL = "http://dev2.efarmer.mobi:8080/RESTService";
    private static final String CONTROLLER_NAME = "RESTService/document";
    private static final String DEVICE_ID = "unit_test";

    private static final String CLIENT_ID = "-174102166@eFarm_Pilot_android";
    private static final String SECRET = "f6f36a04-0ae0-4f1c-85f9-36c85b9845d9";
    private static final String REDIRECT_URI = "http://efarmpilot.com";

    private static final String USERNAME = "aliciya@i.ua";
    private static final String PASSWORD = "devRt";

    private DefaultClient client;

    @Before
    public void setUp() throws Exception {
        client = new DefaultClient(SERVER_URL, CONTROLLER_NAME);
        client.setCredential(getCredentials());
    }

    @Test
    public void testExecuteMethod() throws Exception {
        try {
            RestApiMethod method = new RestApiMethod("sync-case", RestApiMethod.Type.GET);
            method.putParam("device_id", "rest-core-unit-test");
            method.putParam("case", "all");
            String response = client.executeMethod(method);
            Assert.assertTrue(response != null);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    protected OAuthCredential getCredentials() throws IOException, HttpException {
        OAuthCredential credential = OAuthCredential.builder()
                .url(SERVER_URL + "/oauth/token")
                .clientId(CLIENT_ID)
                .clientSecret(SECRET)
                .redirectUri(REDIRECT_URI)
                .username(USERNAME)
                .password(PASSWORD)
                .build();
        credential.authorize();
        return credential;
    }
}