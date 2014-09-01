package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.http.HttpException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public abstract class AbstractClient {

    protected String executeMethod(RestApiMethod method) throws IOException, HttpException {
        return execute(method, null, null, null);
    }

    protected String executeMethod(RestApiMethod method, Map<String, String> params) throws HttpException, IOException {
        return execute(method, params, null, null);
    }

    protected String executeMethod(RestApiMethod method, String json) throws HttpException, IOException {
        String response = null;
        try {
            response = execute(method, null, "application/json", new StringEntity(json, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected String executeMethod(RestApiMethod method, String contentType, byte[] data) throws HttpException, IOException {
        return execute(method, null, contentType, new ByteArrayEntity(data));
    }

    private String execute(RestApiMethod method, Map<String, String> params, String contentType, HttpEntity httpEntity) throws HttpException, IOException {
        HttpRequestBase httpRequestBase = null;
        switch (method.type) {
            case GET: {
                StringBuilder paramBuilder = new StringBuilder();
                if (params != null && !params.isEmpty()) {
                    paramBuilder.append("?");
                    for (String key: params.keySet()) {
                        paramBuilder.append(key).append("=").append(params.get(key));
                        paramBuilder.append("&");
                    }
                    paramBuilder.deleteCharAt(paramBuilder.length() - 1);
                }

                httpRequestBase = new HttpGet(buildRequestUrl(method.name) + paramBuilder.toString());
                break;
            }
            case POST: {
                httpRequestBase = new HttpPost(buildRequestUrl(method.name));
                httpRequestBase.addHeader("charset", "UTF-8");
                if (httpEntity != null) {
                    httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                    ((HttpPost)httpRequestBase).setEntity(httpEntity);
                }
                if (params != null && !params.isEmpty()) {
                    List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
                    for (String key: params.keySet()) {
                        formParams.add(new BasicNameValuePair(key, params.get(key)));
                    }
                    ((HttpPost)httpRequestBase).setEntity(new UrlEncodedFormEntity(formParams));
                }
                break;
            }
        }

        if (method.headers != null) {
            for (Header header: method.headers) {
                httpRequestBase.addHeader(header);
            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpParams clientParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(clientParams, method.timeout);
        HttpConnectionParams.setSoTimeout(clientParams, method.timeout);
        HttpResponse response = httpClient.execute(httpRequestBase);
        int code = response.getStatusLine().getStatusCode();
        if (code >= 200 && code < 300) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String responseString;
            try {
                responseString = reader.readLine();
            } finally {
                reader.close();
            }
            return responseString;
        } else {
            throw new HttpException(response);
        }
    }

    private String buildRequestUrl(String methodName) {
        return getServiceUrl() + "/" + getControllerName() + "/" + methodName;
    }

    protected abstract String getServiceUrl();

    protected abstract String getControllerName();

}
