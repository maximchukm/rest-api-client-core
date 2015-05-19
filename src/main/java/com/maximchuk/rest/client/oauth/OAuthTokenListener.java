package com.maximchuk.rest.client.oauth;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/21/13
 */
public interface OAuthTokenListener {

    public void authorize(OAuthTokenEvent event);
    public void refreshToken(OAuthTokenEvent event);

}
