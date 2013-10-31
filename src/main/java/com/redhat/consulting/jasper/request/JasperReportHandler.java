package com.redhat.consulting.jasper.request;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.redhat.consulting.jasper.exception.ReportException;

public class JasperReportHandler {
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(JasperReportHandler.class);
	
	private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();
	private static final TransformerFactory TRANS_FACTORY = TransformerFactory.newInstance();
	private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance(); 
	
	private final HttpClient client;
	private final String baseUri; 
	private final GetMethod resourceRequestMethod;
	private final PutMethod reportRequestMethod;
	private final Map<String, Object> parameters;
	
	public JasperReportHandler(HttpClient httpClient, String baseUri, JasperReportContext context) {
		this.baseUri = StringUtils.removeEnd(baseUri, "/");
		String reportUri = StringUtils.removeStart(context.getReportUri(), "/");
		
		this.client = httpClient;
		this.resourceRequestMethod =  new GetMethod(this.baseUri+"/jasperserver-pro/rest/resource/"+reportUri);
		this.reportRequestMethod = new PutMethod(this.baseUri+"/jasperserver-pro/rest/report/"+reportUri+"?RUN_OUTPUT_FORMAT="+context.getReportType());
		this.parameters = context.getParameters();
	}
	
	
	public InputStream requestInputStream() throws ReportException {
		try {
			DocumentBuilder builder = DOC_FACTORY.newDocumentBuilder();
			
			//first, execute the request for the document resource...
			client.executeMethod(resourceRequestMethod);
			
			RequestEntity reportRequestBody = null;
			//if there are parameters...
			if(this.parameters != null && parameters.size() > 0) {
				Document reportRequestDoc = builder.parse(resourceRequestMethod.getResponseBodyAsStream());
				
				addParameters(reportRequestDoc, parameters);
				
				//now, serialize to string...
				Transformer transformer = TRANS_FACTORY.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				transformer.transform(new DOMSource(reportRequestDoc), new StreamResult(baos));
				
				reportRequestBody = new ByteArrayRequestEntity(baos.toByteArray());
			}
			else {
				//get the response.
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(resourceRequestMethod.getResponseBodyAsStream(), baos);
				reportRequestBody = new ByteArrayRequestEntity(baos.toByteArray());
			}
			
			reportRequestMethod.setRequestEntity(reportRequestBody);
			
			//execute the put.
			client.executeMethod(reportRequestMethod);
			
			//parse response..
			Document uuidResponse = builder.parse(reportRequestMethod.getResponseBodyAsStream());
			String uuid = extractUUID(uuidResponse);
			
			LOG.debug("UUID from Jasper: "+uuid);
			
			//now, get the UUID.
			GetMethod reportRequest = new GetMethod(baseUri+"/jasperserver-pro/rest/report/"+uuid+"?file=report");
			client.executeMethod(reportRequest);
			
			return reportRequest.getResponseBodyAsStream();
		} 
		catch (Exception e) {
			throw new ReportException("Exception downloading Jasper report.", e);
		}
	}
	
	private static void addParameters(Document document, Map<String, Object> parameters) {
		for(String key : parameters.keySet()) {
			//create the child node..
			Element parameter = document.createElement("parameter");
			parameter.setAttribute("name", key);
			parameter.setTextContent(parameters.get(key).toString());
			document.getDocumentElement().appendChild(parameter);
		}
	}
	
	private static String extractUUID(Document document) throws XPathExpressionException {
		XPath xpath = XPATH_FACTORY.newXPath();
		XPathExpression expr = xpath.compile("//uuid");
		String uuid = expr.evaluate(document); 
		return uuid;
	}
}
