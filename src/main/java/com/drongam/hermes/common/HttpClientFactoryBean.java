package com.drongam.hermes.common;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class HttpClientFactoryBean implements InitializingBean, FactoryBean<HttpClient> {
	
    private HttpClient httpClient;
    private String username;
    private String password;
    private String authenticationHost;
    private String authenticationRealm;
    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationHost() {
        return authenticationHost;
    }

    public void setAuthenticationHost(String authenticationHost) {
        this.authenticationHost = authenticationHost;
    }

    public String getAuthenticationRealm() {
        return authenticationRealm;
    }

    public void setAuthenticationRealm(String authenticationRealm) {
        this.authenticationRealm = authenticationRealm;
    }

    public void afterPropertiesSet() throws Exception {
        httpClient = new HttpClient();
        Credentials creds = new UsernamePasswordCredentials(username, password);  
        httpClient.getState().setCredentials(AuthScope.ANY, creds);
      
    }

    public HttpClient getObject() throws Exception {
        return httpClient;
    }

    public Class<HttpClient> getObjectType() {    
        return HttpClient.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
