package com.maximchuk.rest.client.auth;


import javax.xml.bind.DatatypeConverter;

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
        return "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes());
    }
}
