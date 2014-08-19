package com.maximchuk.rest.client;

import com.maximchuk.rest.client.core.AbstractClient;
import com.maximchuk.rest.client.core.RestApiMethod;
import com.maximchuk.rest.client.http.HttpException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 19.08.2014.
 */
public class ProductCatalogClient extends AbstractClient {

    public String getTypes() throws IOException, HttpException {
        RestApiMethod method = new RestApiMethod("types", RestApiMethod.Type.GET);
        return executeMethod(method);
    }

    public String search(String searchString, String type) throws IOException, HttpException {
        RestApiMethod method = new RestApiMethod("search", RestApiMethod.Type.GET);
        Map<String, String> params = new HashMap<String, String>();
        params.put("keyword", searchString);
        params.put("type", type);
        return executeMethod(method, params);
    }

    @Override
    protected String getServiceUrl() {
        return "http://localhost:8081/product-catalog-0.3/api";
    }

    @Override
    protected String getControllerName() {
        return "product";
    }

}
