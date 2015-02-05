package com.maximchuk.rest.client.core;

import com.maximchuk.rest.client.http.HttpException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public abstract class AbstractClient {

    protected static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    protected static final String FILENAME_PREF = "filename=";
    private static final String ENCODING = "UTF-8";

    protected String executeMethod(RestApiMethod method) throws IOException, HttpException {
        return executeMethod(method, null, null, null);
    }

    protected FileEntity executeDownloadMethod(RestApiMethod method) throws IOException, HttpException {
        return executeDownloadMethod(method, null);
    }

    protected String executeMethod(RestApiMethod method, Map<String, String> params) throws HttpException, IOException {
        return executeMethod(method, params, null, null);
    }

    protected FileEntity executeDownloadMethod(RestApiMethod method, Map<String, String> params) throws HttpException, IOException {
        return executeDownloadMethod(method, params, null, null);
    }

    protected String executeMethod(RestApiMethod method, String json) throws HttpException, IOException {
        String response = null;
        try {
            response = executeMethod(method, "application/json", new StringEntity(json, ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected String executeMethod(RestApiMethod method, String contentType, byte[] data) throws HttpException, IOException {
        return executeMethod(method, contentType, new ByteArrayEntity(data));
    }

    protected String executeMethod(RestApiMethod method, String contentType, HttpEntity httpEntity) throws HttpException, IOException {
        return executeMethod(method, null, contentType, httpEntity);
    }

    protected FileEntity executeDownloadMethod(RestApiMethod method, Map<String, String> params, String contentType, HttpEntity httpEntity) throws HttpException, IOException {
        HttpResponse response = execute(method, params, contentType, httpEntity);
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

    protected String executeMethod(RestApiMethod method, Map<String, String> params, String contentType, HttpEntity httpEntity) throws HttpException, IOException {
        String responseString = null;
        HttpEntity entity = getResponseEntity(execute(method, params, contentType, httpEntity));
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

    private HttpResponse execute(RestApiMethod method, Map<String, String> params, String contentType, HttpEntity httpEntity) throws HttpException, IOException {
        HttpRequestBase httpRequestBase = null;
        switch (method.type) {
            case GET: {
                httpRequestBase = new HttpGet(buildRequestUrl(method.name) + buildQueryParamString(params));
                break;
            }
            case DELETE: {
                httpRequestBase = new HttpDelete(buildRequestUrl(method.name) + buildQueryParamString(params));
                break;
            }
            case POST: {
                httpRequestBase = new HttpPost(buildRequestUrl(method.name));
                httpRequestBase.addHeader("charset", ENCODING);
                if (httpEntity != null) {
                    httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                    ((HttpPost) httpRequestBase).setEntity(httpEntity);
                }
                if (params != null && !params.isEmpty()) {
                    List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
                    for (String key : params.keySet()) {
                        formParams.add(new BasicNameValuePair(key, params.get(key)));
                    }
                    ((HttpPost) httpRequestBase).setEntity(getUrlEncodedFormEntity(formParams));
                }
                break;
            }
            case PUT: {
                httpRequestBase = new HttpPut(buildRequestUrl(method.name));
                httpRequestBase.addHeader("charset", ENCODING);
                if (httpEntity != null) {
                    httpRequestBase.addHeader(new BasicHeader("content-type", contentType));
                    ((HttpPut) httpRequestBase).setEntity(httpEntity);
                }
                if (params != null && !params.isEmpty()) {
                    List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
                    for (String key : params.keySet()) {
                        formParams.add(new BasicNameValuePair(key, params.get(key)));
                    }
                    ((HttpPut) httpRequestBase).setEntity(getUrlEncodedFormEntity(formParams));
                }
                break;
            }
        }

        if (method.headers != null) {
            for (Header header : method.headers) {
                httpRequestBase.addHeader(header);
            }
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpParams clientParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(clientParams, method.timeout);
        HttpConnectionParams.setSoTimeout(clientParams, method.timeout);
        HttpResponse response = httpClient.execute(httpRequestBase);
        method.setStatusLine(response.getStatusLine());
        return response;
    }

    private UrlEncodedFormEntity getUrlEncodedFormEntity(List<BasicNameValuePair> formParams) throws UnsupportedEncodingException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, ENCODING);
        entity.setContentEncoding(ENCODING);
        return entity;
    }

    private String buildRequestUrl(String methodName) {
        return getServiceUrl() + "/" + getControllerName() + "/" + methodName;
    }

    private String buildQueryParamString(Map<String, String> params) {
        StringBuilder paramBuilder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            paramBuilder.append("?");
            for (String key : params.keySet()) {
                paramBuilder.append(key).append("=").append(params.get(key));
                paramBuilder.append("&");
            }
            paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        }
        return paramBuilder.toString();
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
