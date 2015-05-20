package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.http.HttpException;
import com.maximchuk.rest.client.oauth.OAuthCredential;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public abstract class AbstractClient {

    protected static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    protected static final String FILENAME_PREF = "filename=";

    private OAuthCredential credential;

    public void setCredential(OAuthCredential credential) {
        this.credential = credential;
    }

    protected FileEntity executeDownload(RestApiMethod method) throws HttpException, IOException {
        HttpResponse response = execute(method);
        FileEntity fileEntity = null;
        HttpEntity responseEntity = getResponseEntity(response);

        if (responseEntity != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            responseEntity.writeTo(byteStream);
            String header = response.getFirstHeader(CONTENT_DISPOSITION_HEADER).getValue();
            fileEntity = new FileEntity(header.substring(header.indexOf(FILENAME_PREF) + FILENAME_PREF.length())
                    .replace("\"", ""), byteStream.toByteArray());
        }
        return fileEntity;
    }

    protected String executeMethod(RestApiMethod method) throws IOException, HttpException {
        String responseString = null;
        HttpEntity entity = getResponseEntity(execute(method));
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
            try {
                responseString = reader.readLine();
            } finally {
                reader.close();
            }
        }
        return responseString;
    }

    private HttpResponse execute(RestApiMethod method) throws HttpException, IOException {
        HttpRequestBase httpRequestBase = method.prepareHttpRequest(buildServerControllerUrl());

        if (credential != null) {
            httpRequestBase.addHeader(new BasicHeader("Authorization", "Bearer " + credential.getAccessToken()));
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpParams clientParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(clientParams, method.timeout);
        HttpConnectionParams.setSoTimeout(clientParams, method.timeout);
        HttpResponse response = httpClient.execute(httpRequestBase);
        method.statusLine = response.getStatusLine();
        ClientConnectionManager connectionManager = httpClient.getConnectionManager();
        connectionManager.closeIdleConnections(method.timeout, TimeUnit.MILLISECONDS);
        return response;
    }

    private String buildServerControllerUrl() {
        return getServiceUrl() + "/" + getControllerName();
    }

    private HttpEntity getResponseEntity(HttpResponse response) throws IOException, HttpException {
        int code = response.getStatusLine().getStatusCode();
        if (code >= 200 && code < 400) {
            return response.getEntity();
        } else {
            throw new HttpException(response);
        }
    }

    protected abstract String getServiceUrl();

    protected abstract String getControllerName();

}
