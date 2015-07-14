package com.maximchuk.rest.client.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Maximchuk
 *         date 14.07.2015.
 */
public class StringParamBuilder {

    private Map<String, Object> params;

    public StringParamBuilder(){
        params = new HashMap<String, Object>();
    }

    public StringParamBuilder(Map<String, Object> params) {
        this.params = params;
    }

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    public String build() {
        StringBuilder paramBuilder = new StringBuilder();
        for (Map.Entry<String, Object> param: params.entrySet()) {
            paramBuilder.append(param.getKey()).append("=").append(param.getValue()).append("&");
        }
        if (paramBuilder.length() > 0) {
            paramBuilder.deleteCharAt(paramBuilder.length() - 1);
        }
        return paramBuilder.toString();
    }

}
