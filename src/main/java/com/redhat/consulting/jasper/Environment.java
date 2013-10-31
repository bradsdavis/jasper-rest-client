package com.redhat.consulting.jasper;

public class Environment {

	private final String baseUri;
	private final String username;
	private final String password;
	
	public Environment(String baseUri, String username, String password) {
		this.baseUri = baseUri;
		this.username = username;
		this.password = password;
	}
	public String getBaseUri() {
		return baseUri;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
}
