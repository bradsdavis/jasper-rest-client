package com.redhat.consulting.jasper.request;

public enum JasperReportType {
	PDF("pdf"), CSV("csv");
	
	private final String type;

	private JasperReportType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
