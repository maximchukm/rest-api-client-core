package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.http.HttpException;
import com.maximchuk.rest.client.oauth.OAuthCredential;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 12.07.2015.
 */
public class DefaultClient {

    private OAuthCredential credential;
    private String serverUrl;
    private String controllerName;

    public DefaultClient(String serverUrl, String controllerName) {
        this.serverUrl = serverUrl;
        this.controllerName = controllerName;
    }

    public void setCredential(OAuthCredential credential) {
        this.credential = credential;
    }

    public String executeMethod(RestApiMethod method) throws IOException, HttpException{
        String urlString = serverUrl + "/" + controllerName + "/" + method.name;
        if (method.forceQueryParams) {
            urlString += "?" + method.paramString();
        }
        HttpURLConnection connection =
                (HttpURLConnection) new URL(urlString).openConnection();
        try {
            connection.setRequestMethod(method.type.name());
            connection.setConnectTimeout(method.timeout);
            for (Map.Entry<String, String> header: method.headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
            if (credential != null) {
                connection.setRequestProperty("Authorization", "Bearer " + credential.getAccessToken());
            }
            if (!method.forceQueryParams) {
                OutputStream out = connection.getOutputStream();
                try {
                    out.write(method.paramString().getBytes());
                } finally {
                    out.close();
                }
            }
            int code = connection.getResponseCode();
            if (code >= 200 && code < 400) {
                InputStream in = connection.getInputStream();
                byte[] response = new byte[in.available()];
                in.read(response);
                return new String(response, "UTF8");
            } else {
                throw new HttpException(code);
            }
        } finally {
            connection.disconnect();
        }
    }

}
