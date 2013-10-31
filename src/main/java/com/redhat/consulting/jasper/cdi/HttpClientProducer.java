package com.redhat.consulting.jasper.cdi;

import javax.enterprise.inject.Produces;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.jboss.arquillian.core.api.annotation.Inject;

import com.redhat.consulting.jasper.Environment;

public class HttpClientProducer {

	@Inject
	private Environment environment;
	
	@Produces
	public HttpClient produceHttpClient() {
		HttpClient client = new HttpClient();
		client.getParams().setAuthenticationPreemptive(true);
	    client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(environment.getUsername(), environment.getPassword()));
        client.getParams().setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.RFC_2965);
	    
        return client;
	}
}
