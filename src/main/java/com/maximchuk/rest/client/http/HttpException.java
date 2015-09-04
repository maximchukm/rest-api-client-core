package com.maximchuk.rest.client.http;

import com.maximchuk.rest.client.core.RestApiResponse;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/20/13
 */
public class HttpException extends Exception {

    private int errorCode;

    @Deprecated
    private String reasonPhrase;

    private String body;

    @Deprecated
    public HttpException(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        errorCode = statusLine.getStatusCode();
        reasonPhrase = statusLine.getReasonPhrase();
        if (response.getEntity() != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                body = reader.readLine();
            } catch (IOException ex) {
                //ignore
            }
        }

    }

    public HttpException(RestApiResponse restApiResponse) {
        this.errorCode = restApiResponse.getStatusCode();
        this.body = restApiResponse.getString();
    }

    @Override
    public String getMessage() {
        StringBuilder messageBuilder = new StringBuilder(String.valueOf(errorCode));
        if (reasonPhrase != null) {
            messageBuilder.append(" ").append(reasonPhrase);
        }
        if (body != null) {
            messageBuilder.append(" ").append(body);
        }
        return messageBuilder.toString();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Deprecated
    public String getBody() {
        return body;
    }
}
