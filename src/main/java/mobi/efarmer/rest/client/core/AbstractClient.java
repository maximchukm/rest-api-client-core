package mobi.efarmer.rest.client.core;

import mobi.efarmer.rest.client.http.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 18.08.2014.
 */
public abstract class AbstractClient {

    protected byte[] executeMethod(RestApiMethod method, Map<String, String> params) throws HttpException, IOException {
        return execute(method, params, null, null);
    }

    protected byte[] executeMethod(RestApiMethod method, String json) throws HttpException, IOException {
        byte[] response = null;
        try {
            response = execute(method, null, "application/json", new StringEntity(json, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected byte[] executeMethod(RestApiMethod method, String contentType, byte[] data) throws HttpException, IOException {
        return execute(method, null, contentType, new ByteArrayEntity(data));
    }

    private byte[] execute(RestApiMethod method, Map<String, String> params, String contentType, HttpEntity httpEntity) throws HttpException, IOException {
        HttpRequestBase httpRequestBase = null;
        switch (method.type) {
            case GET: {
                StringBuilder paramBuilder = new StringBuilder();
                if (!params.isEmpty()) {
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
                if (!params.isEmpty()) {
                    List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
                    for (String key: params.keySet()) {
                        formParams.add(new BasicNameValuePair(key, params.get(key)));
                    }
                    ((HttpPost)httpRequestBase).setEntity(new UrlEncodedFormEntity(formParams));
                }
                break;
            }
        }

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpParams clientParams = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(clientParams, method.timeout);
        HttpConnectionParams.setSoTimeout(clientParams, method.timeout);
        HttpResponse response = httpClient.execute(httpRequestBase);
        int code = response.getStatusLine().getStatusCode();
        if (code >= 200 && code < 300) {
            InputStream is = response.getEntity().getContent();
            byte[] responseData = new byte[is.available()];
            try {
                is.read(responseData);
            } finally {
                is.close();
            }
            return responseData;
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
