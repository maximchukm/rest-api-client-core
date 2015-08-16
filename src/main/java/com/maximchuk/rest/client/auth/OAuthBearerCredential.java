package com.maximchuk.rest.client.auth;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/20/13
 */
public class OAuthBearerCredential implements Credential {

    private String accessToken;

    private OAuthBearerCredential() {}

    public static OAuthBearerCredential create(String accessToken) {
        OAuthBearerCredential credential = new OAuthBearerCredential();
        credential.setAccessToken(accessToken);
        return credential;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getAuthorizationString() {
        return "Bearer " + accessToken;
    }
}
