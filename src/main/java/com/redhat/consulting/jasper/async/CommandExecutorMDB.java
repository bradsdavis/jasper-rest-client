package com.redhat.consulting.jasper.async;

import java.io.InputStream;
import java.net.CookiePolicy;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.consulting.jasper.JasperService;
import com.redhat.consulting.jasper.exception.UnexpectedReportException;
import com.redhat.consulting.jasper.request.JasperReportContext;
import com.redhat.consulting.jasper.request.JasperReportHandler;

@MessageDriven(name = "CommandRequestMDB", activationConfig = {
		 @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		 @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/JasperRequestQueue")
})
public class CommandExecutorMDB implements MessageListener {

	private static final Logger LOG = LoggerFactory.getLogger(CommandExecutorMDB.class);

	@Inject
	private JasperService jasperService;
	
	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;

	private Connection connection;
	private Session session;

    @PostConstruct
    public void init() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(); 
    }
	
    
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		
		try {
			//try and call the report service..
			JasperReportContext requestContext = (JasperReportContext)objectMessage.getObject();
			JasperReportHandler requestHandler = jasperService.getRequestHandler(requestContext);
			InputStream is = requestHandler.requestInputStream();
			byte[] bytes = IOUtils.toByteArray(is);
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("Received response with size: "+bytes.length);
			}
			
			String correlation = message.getJMSCorrelationID();
			Destination responseQueue = message.getJMSReplyTo();

			if(LOG.isDebugEnabled()) {
				LOG.debug("Creating correlation: "+correlation+" to destination: "+responseQueue.toString());
			}
			
			if (responseQueue != null && correlation != null) {
		        MessageProducer producer = session.createProducer(responseQueue);
				BytesMessage responseMessage = session.createBytesMessage();
				responseMessage.writeBytes(bytes);
				
				LOG.debug("Writing to response queue.");
				producer.send(responseMessage);
			} 
			else {
				LOG.warn("Response from Command Object, but no ReplyTo and Coorelation: " + ReflectionToStringBuilder.toString(objectMessage.getObject()));
			}
		} catch (Exception e) {
			throw new UnexpectedReportException("Exception running async Jasper Report.", e);
		}

	}

}
