package com.redhat.consulting.jasper.request;

import java.io.Serializable;
import java.util.Map;

public class JasperRequestContext implements Serializable {
	private String reportUri;
	private JasperReportType reportType;
	private Map<String, Object> parameters;
	
	public JasperRequestContext() {}
	
	public JasperRequestContext(String reportUri, JasperReportType reportType) {
		this(reportUri, reportType, null);
	}
	
	public JasperRequestContext(String reportUri, JasperReportType reportType, Map<String, Object> parameters) {
		this.reportUri = reportUri;
		this.reportType = reportType;
		this.parameters = parameters;
	}

	public String getReportUri() {
		return reportUri;
	}
	public void setReportUri(String reportUri) {
		this.reportUri = reportUri;
	}
	public JasperReportType getReportType() {
		return reportType;
	}
	public void setReportType(JasperReportType reportType) {
		this.reportType = reportType;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	
}
