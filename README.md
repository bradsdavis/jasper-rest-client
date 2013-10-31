1) Setup your Environment:

	Environment environment = new Environment("http://[address to jasper]/", "[jasper user]", "~[jasper pass]"); 

2) Create your HTTPClient:

	HttpClient client = new HttpClient();
	client.getParams().setAuthenticationPreemptive(true);
	client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(environment.getUsername(), environment.getPassword()));
	client.getParams().setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.RFC_2965);

3) Fetch your report and enjoy:

	JasperReportContext reqContext = new JasperReportContext("/public/Samples/Reports/TopFivesReport", JasperReportType.PDF);
	JasperReportHandler reqHandler = new JasperReportHandler(client, environment.getBaseUri(), reqContext);
	InputStream is = reqHandler.requestInputStream();

4) Need to provide parameters with your call to the report?  No problem:

	Map<String, Object> parameters = new HashMap<String, Object>();
	parameters.put("ACCOUNT_NAME", "Example");
		
	JasperReportContext reqContext = new JasperReportContext("/Reports/AccountViewReport", JasperReportType.PDF, parameters);
	JasperReportHandler reqHandler = new JasperReportHandler(client, environment.getBaseUri(), reqContext);
	InputStream is = reqHandler.requestInputStream();

	     
