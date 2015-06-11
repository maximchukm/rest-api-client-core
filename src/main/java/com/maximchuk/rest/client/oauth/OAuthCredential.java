package com.maximchuk.rest.client.oauth;

import com.maximchuk.rest.client.http.HttpException;
import com.maximchuk.rest.client.http.HttpFormParamBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maxim L. Maximchuk
 *         Date: 11/20/13
 */
public class OAuthCredential {

    private String tokenEndPointUrl;

    private String authCode;
    private String username;
    private String password;
    private String facebookToken;
    private String redirectUri;
    private String clientSecret;
    private String clientId;

    private String refreshToken;
    private String accessToken;
    private Integer expires;

    private int timeout = 10000;

    private List<OAuthTokenListener> listeners = new ArrayList<OAuthTokenListener>();

    /**
     * Create and return builder of OAuthCredential
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Obtain tokens from oauth provider server
     *
     * @throws IOException
     * @throws HttpException
     */
    public void authorize() throws IOException, HttpException {
        HttpFormParamBuilder paramBuilder = null;

        if (username != null && password != null) {
            paramBuilder = new HttpFormParamBuilder();
            paramBuilder.addParam("grant_type", "password");
            paramBuilder.addParam("redirect_uri", redirectUri);
            paramBuilder.addParam("username", username);
            paramBuilder.addParam("password", password);
            paramBuilder.addParam("client_secret", clientSecret);
            paramBuilder.addParam("client_id", clientId);
        }

        HttpPost post = new HttpPost(tokenEndPointUrl);

        HttpEntity httpEntity = new UrlEncodedFormEntity(paramBuilder.getParams(), "UTF-8");
        post.setEntity(httpEntity);
        HttpResponse resp = clientExecute(post);

        if (resp.getStatusLine().getStatusCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            try {
                JSONObject json = new JSONObject(reader.readLine());
                accessToken = json.getString("access_token");
                refreshToken = json.getString("refresh_token");
                expires = json.getInt("expires_in");
                fireAuthorizeListeners();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
        } else {
            throw new HttpException(resp);
        }
    }

    public void authorize(OAuthUser user) {
        accessToken = user.accessToken;
        refreshToken = user.refreshToken;
        fireAuthorizeListeners();
    }

    public List<OAuthUser> authorizeSocial() throws IOException, HttpException {
        HttpFormParamBuilder paramBuilder = new HttpFormParamBuilder();
        paramBuilder.addParam("grant_type", "authorization_code");
        paramBuilder.addParam("client_id", clientId);
        paramBuilder.addParam("client_secret", clientSecret);
        paramBuilder.addParam("redirect_uri", redirectUri);
        paramBuilder.addParam("code_type", "facebook");
        paramBuilder.addParam("code", facebookToken);

        HttpPost post = new HttpPost(tokenEndPointUrl);

        HttpEntity httpEntity = new UrlEncodedFormEntity(paramBuilder.getParams(), "UTF-8");
        post.setEntity(httpEntity);
        HttpResponse resp = clientExecute(post);

        List<OAuthUser> users = new ArrayList<OAuthUser>();
        if (resp.getStatusLine().getStatusCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            try {
                JSONArray jsonArray = new JSONArray(reader.readLine());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    OAuthUser user = new OAuthUser();
                    user.name = jsonObject.getString("name");
                    user.accessToken = jsonObject.getString("access_token");
                    user.refreshToken = jsonObject.getString("refresh_token");
                    user.expires = jsonObject.getInt("expires_in");
                    users.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                reader.close();
            }
        } else {
            throw new HttpException(resp);
        }
        return users;
    }

    /**
     * Refersh access token by refresh token.
     * Refresh token must be set, or obtained from oauth provider server
     *
     * @throws IOException
     * @throws HttpException
     */
    public void refresh() throws IOException, HttpException {
        if (refreshToken != null) {
            HttpPost post = new HttpPost(tokenEndPointUrl);

            HttpFormParamBuilder paramBuilder = new HttpFormParamBuilder();
            paramBuilder.addParam("grant_type", "refresh_token");
            paramBuilder.addParam("redirect_uri", redirectUri);
            paramBuilder.addParam("client_secret", clientSecret);
            paramBuilder.addParam("client_id", clientId);
            paramBuilder.addParam("refresh_token", refreshToken);

            HttpEntity httpEntity = new UrlEncodedFormEntity(paramBuilder.getParams(), "UTF-8");
            post.setEntity(httpEntity);
            HttpResponse resp = clientExecute(post);

            if (resp.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                try {
                    JSONObject json = new JSONObject(reader.readLine());
                    accessToken = json.getString("access_token");
                    expires = json.getInt("expires_in");
                    fireRefreshTokenListeners();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    reader.close();
                }
            } else {
                throw new HttpException(resp);
            }
        } else {
            throw new IllegalArgumentException("refresh token is null");
        }
    }

    /**
     * Add listener for authorize and refresh
     *
     * @param listener
     */
    public void addListener(OAuthTokenListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove listener for authorize and refresh
     *
     * @param listener
     */
    public void removeListener(OAuthTokenListener listener) {
        listeners.remove(listener);
    }

    /**
     * Get refresh token
     *
     * @return refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Get access token
     *
     * @return access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Get access token expires
     *
     * @return access token expires
     */
    public Integer getExpires() {
        return expires;
    }

    private void fireAuthorizeListeners() {
        for (OAuthTokenListener listener: listeners) {
            listener.authorize(new OAuthTokenEvent(refreshToken, accessToken, expires));
        }
    }

    private void fireRefreshTokenListeners() {
        for (OAuthTokenListener listener: listeners) {
            listener.refreshToken(new OAuthTokenEvent(refreshToken, accessToken, expires));
        }
    }

    private HttpResponse clientExecute(HttpPost httpPost) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        HttpConnectionParams.setSoTimeout(params, timeout);
        return httpClient.execute(httpPost);
    }

    /**
     * Builder of OAuthCredential
     */
    public static class Builder {
        private static OAuthCredential credential;

        private Builder() {
            credential = new OAuthCredential();
        }

        /**
         * Validate and return OAuthCredential
         *
         * @return OAuthCredential
         */
        public OAuthCredential build() {
            if (credential.tokenEndPointUrl == null
                    || credential.clientId == null
                    || credential.clientSecret == null
                    || credential.redirectUri == null) {
                throw new IllegalArgumentException("url, clientId, clientSecret and redirectUri must be set");
            }
            return credential;
        }

        /**
         * Set auth server token end point url. Required
         *
         * @param tokenEndPointUrl token end point url
         * @return this builder
         */
        public Builder url(String tokenEndPointUrl) {
            credential.tokenEndPointUrl = tokenEndPointUrl;
            return this;
        }

        /**
         * Set client id for OAuthCredential. Required
         *
         * @param clientId oauth client id
         * @return this builder
         */
        public Builder clientId(String clientId) {
            credential.clientId = clientId;
            return this;
        }

        /**
         * Set client secret for OAuthCredential. Required
         *
         * @param secret oauth client secret
         * @return this builder
         */
        public Builder clientSecret(String secret) {
            credential.clientSecret = secret;
            return this;
        }

        /**
         * Set redirect uri for OAuthCredential. Required
         *
         * @param redirectUri oauth redirect uri
         * @return this builder
         */
        public Builder redirectUri(String redirectUri) {
            credential.redirectUri = redirectUri;
            return this;
        }

        /**
         * Set user name. Required for password authorization;
         *
         * @param username user name
         * @return this builder
         */
        public Builder username(String username) {
            credential.username = username;
            return this;
        }

        /**
         * Set user password. Required for password authorization;
         *
         * @param password user password
         * @return this builder;
         */
        public Builder password(String password) {
            credential.password = password;
            return this;
        }

        /**
         * Set facebookToken. Required for authorization via facebook
         *
         * @param facebookToken facebook access token
         * @return this builder
         */
        public Builder facebookToken(String facebookToken) {
            credential.facebookToken = facebookToken;
            return this;
        }

        /**
         * Set auth code. Required for authorization by authorization code
         *
         * @param authCode authorization code
         * @return this builder
         */
        public Builder authCode(String authCode) {
            credential.authCode = authCode;
            return this;
        }

        /**
         * Set refresh code. Required for refreshing access token without authorization
         *
         * @param refreshToken refresh token
         * @return this builder
         */
        public Builder refreshToken(String refreshToken) {
            credential.refreshToken = refreshToken;
            return this;
        }

        /**
         * Set response timeout in millis. Optional, default value is 10000
         *
         * @param timeout
         * @return
         */
        public Builder timeout(int timeout) {
            credential.timeout = timeout;
            return this;
        }

    }

    public static class OAuthUser implements Serializable {
        private static final long serialVersionUID = 7599886283366419927L;

        protected String name;
        protected String accessToken;
        protected String refreshToken;
        protected Integer expires;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
