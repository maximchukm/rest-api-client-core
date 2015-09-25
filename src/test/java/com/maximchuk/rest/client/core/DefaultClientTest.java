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
public class DefaultClientTest extends AbstractTest {

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
            method.putParam("device_id", DEVICE_ID);
            method.putParam("case", "all");
            RestApiResponse response = client.executeMethod(method);
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