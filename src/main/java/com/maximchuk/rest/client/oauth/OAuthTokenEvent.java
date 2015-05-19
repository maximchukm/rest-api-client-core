package com.maximchuk.rest.client.oauth;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/21/13
 */
public class OAuthTokenEvent {

    private String refreshToken;
    private String accessToken;
    private Integer expires;

    protected OAuthTokenEvent(String refreshToken, String accessToken, Integer expires) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expires = expires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Integer getExpires() {
        return expires;
    }
}
