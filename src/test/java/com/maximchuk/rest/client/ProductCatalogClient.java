package com.maximchuk.rest.client;

import com.maximchuk.rest.client.core.AbstractClient;
import com.maximchuk.rest.client.core.RestApiMethod;
import com.maximchuk.rest.client.http.HttpException;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 19.08.2014.
 */
public class ProductCatalogClient extends AbstractClient {

    public String getTypes() throws IOException, HttpException {
        return executeMethod(getMethod("types", RestApiMethod.Type.GET));
    }

    public String search(String searchString, String type) throws IOException, HttpException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("keyword", searchString);
        params.put("type", type);
        return executeMethod(getMethod("search", RestApiMethod.Type.GET), params);
    }

    @Override
    protected String getServiceUrl() {
        return "http://dev2.efarmer.mobi:9080/product-catalog/api";
    }

    @Override
    protected String getControllerName() {
        return "product";
    }

    private RestApiMethod getMethod(String name, RestApiMethod.Type type) {
        RestApiMethod method = new RestApiMethod(name, type);
        method.addHeader(new BasicHeader("Authorization", "70fad48e-388c-45d5-8e5e-f5da14d5b3ad"));
        return method;
    }

}
