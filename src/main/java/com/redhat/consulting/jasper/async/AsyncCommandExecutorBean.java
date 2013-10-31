package com.redhat.consulting.jasper.async;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.consulting.jasper.exception.ReportException;
import com.redhat.consulting.jasper.request.JasperRequestContext;


@LocalBean
@Stateless
public class AsyncCommandExecutorBean {

	private static final Logger LOG = LoggerFactory.getLogger(AsyncCommandExecutorBean.class);
	
	@Resource(mappedName = "java:/JmsXA")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/jms/JasperRequestQueue")
	private Queue requestQueue;

	@Resource(mappedName = "java:/jms/JasperResponseQueue")
	private Queue responseQueue;

	private Connection connection;
	private Session session;
	private MessageProducer producer;
	

    @PostConstruct
    public void init() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession();
        producer = session.createProducer(requestQueue); 
    }
	
	
	public String requestReport(JasperRequestContext context) throws ReportException {
		String uuid = UUID.randomUUID().toString();
		try {
			ObjectMessage request = session.createObjectMessage();
			request.setJMSCorrelationID(uuid);
			request.setObject(context);
			request.setJMSReplyTo(responseQueue);
			producer.send(request);
			
			return uuid.toString();
		} catch (JMSException e) {
			throw new ReportException("Exception sending Command Message.", e);
		}
	}

	public Object pollResponse(String correlation) throws ReportException {
		final String correlationSelector = "JMSCorrelationID='" + correlation + "'";
		
		try {
			MessageConsumer consumer = session.createConsumer(responseQueue, correlationSelector);
			Message response = consumer.receive(10000);

			if(response == null) {
				LOG.debug("Message not yet recieved: "+correlation);
				return null;
			}
			else {
				LOG.debug("Recieved message for correlation: "+correlation);
				return ((ObjectMessage)response).getObject();
			}
		}
		catch (JMSException e) {
			throw new ReportException("Exception receiving Command Message Response.", e);
		}
	}

}
