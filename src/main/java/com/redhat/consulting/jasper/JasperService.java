package com.redhat.consulting.jasper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.arquillian.core.api.annotation.Inject;

import com.redhat.consulting.jasper.request.JasperReportContext;
import com.redhat.consulting.jasper.request.JasperReportHandler;

@ApplicationScoped
public class JasperService {

	@Inject
	private HttpClient client;
	
	@Inject
	private Environment environment;
	
	@RequestScoped
	public JasperReportHandler getRequestHandler(JasperReportContext context) {
		return new JasperReportHandler(client, environment.getBaseUri(), context);
	}
}
