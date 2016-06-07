/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jms;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author Piotr
 */
public class JMSProducer extends Thread implements AutoCloseable{
    
    private static String DEF_QUEUE = "amqmsg";
    private final ActiveMQConnectionFactory connectionFactory;
    private Connection connection = null;
    private Session session = null;
    private Queue<String> messagesQueue;
    private boolean active = true;

    
    public JMSProducer(String url)
    {
	this(url, null, null);
    }

   
    public JMSProducer(String url, String user, String password)
    {
	if (user != null && !user.isEmpty() && password != null /*&& !password.isEmpty()*/)
	    connectionFactory = new ActiveMQConnectionFactory(url, user, password);
	else
	    connectionFactory = new ActiveMQConnectionFactory(url);
	messagesQueue = new PriorityBlockingQueue<String>();
    }

    private MessageProducer init() throws JMSException
    {
	connection = connectionFactory.createConnection();
	connection.start();
	session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	Destination dest = session.createQueue(DEF_QUEUE);
	return session.createProducer(dest);
    }

    public void send(String line)
    {
	messagesQueue.add(line);
    }


    @Override
    public void run()
    {
	try
	{
	    System.out.println("Init producer...");
	    MessageProducer producer = init();
	    System.out.println("Producer successfully initialized");
	    while (active)
	    {
		try
		{
		    String text = null;
		    while (active && (text = messagesQueue.poll()) != null)
		    {
			Message msg = session.createTextMessage(text);
			msg.setObjectProperty("Created", (new Date()).toString());
			producer.send(msg);
			System.out.println("Message " + msg.getJMSMessageID() + " was sent");
		    }

		}
		catch (JMSException e)
		{
		    e.printStackTrace();
		    session.close();
		    connection.close();
		    producer = init(); // trying to reconnect
		}
	    }
	}
	catch (Exception ex)
	{
	    ex.printStackTrace();
	}
    }

    public void close()
    {
	active = false;
	if (connection != null)
	{
	    try
	    {
		connection.close();
	    }
	    catch (JMSException e)
	    {
		e.printStackTrace();
	    }
	}
    }
}
