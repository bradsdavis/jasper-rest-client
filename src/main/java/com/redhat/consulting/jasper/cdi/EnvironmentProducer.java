package com.redhat.consulting.jasper.cdi;

import javax.enterprise.inject.Produces;

import com.redhat.consulting.jasper.Environment;

public class EnvironmentProducer {

	@Produces
	public Environment produceEnvironment() {
		Environment environment = new Environment("http://10.0.1.135/", "superuser", "~G/\"zpw#F'YU{JE");
		return environment;
	}
	
}
