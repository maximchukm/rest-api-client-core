package com.maximchuk.rest.client.auth;


import sun.misc.BASE64Encoder;

/**
 * @author Maxim Maximchuk
 *         date 16.08.2015.
 */
public class BasicHttpCredential implements Credential {

    private String username;
    private String password;

    private BasicHttpCredential() {
    }

    public static BasicHttpCredential create(String username, String password) {
        BasicHttpCredential credential = new BasicHttpCredential();
        credential.setUsername(username);
        credential.setPassword(password);
        return credential;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getAuthorizationString() {
        return "Basic " + new BASE64Encoder().encode((username + ":" + password).getBytes());
    }
}
