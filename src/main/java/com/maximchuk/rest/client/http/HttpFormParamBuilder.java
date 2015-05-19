package com.maximchuk.rest.client.http;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/20/13
 */
public class HttpFormParamBuilder {

    private List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public List<BasicNameValuePair> getParams() {
        return params;
    }

}
