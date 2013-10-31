package com.redhat.consulting.jasper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.arquillian.core.api.annotation.Inject;

import com.redhat.consulting.jasper.request.JasperRequestContext;
import com.redhat.consulting.jasper.request.JasperRequestHandler;

@ApplicationScoped
public class JasperService {

	@Inject
	private HttpClient client;
	
	@Inject
	private Environment environment;
	
	@RequestScoped
	public JasperRequestHandler getRequestHandler(JasperRequestContext context) {
		return new JasperRequestHandler(client, environment.getBaseUri(), context);
	}
}
