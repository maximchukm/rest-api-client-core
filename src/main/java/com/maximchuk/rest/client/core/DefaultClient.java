package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.http.HttpException;
import com.maximchuk.rest.client.oauth.OAuthCredential;

import java.io.IOException;
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

    public RestApiResponse executeMethod(RestApiMethod method) throws IOException, HttpException{
        StringBuilder urlBuilder = new StringBuilder(serverUrl);
        if (controllerName != null) {
            urlBuilder.append("/").append(controllerName);
        }
        if (method.name != null) {
            urlBuilder.append("/").append(method.name);
        }
        if (method.forceQueryParams) {
            urlBuilder.append("?").append(method.paramString());
        }
        HttpURLConnection connection =
                (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
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
                writeRequest(connection, method.paramString().getBytes());
            } else if (method.content != null) {
                connection.setRequestProperty("Content-Type", method.content.contentType);
                writeRequest(connection, method.content.bytes);
            }
            RestApiResponse restApiResponse = new RestApiResponse(connection);
            if (restApiResponse.getStatusCode() >= 200 && restApiResponse.getStatusCode() < 400) {
                return restApiResponse;
            } else {
                throw new HttpException(restApiResponse);
            }
        } finally {
            connection.disconnect();
        }
    }

    private void writeRequest(HttpURLConnection connection, byte[] bytes) throws IOException {
        OutputStream out = connection.getOutputStream();
        try {
            out.write(bytes);
        } finally {
            out.close();
        }
    }

}
