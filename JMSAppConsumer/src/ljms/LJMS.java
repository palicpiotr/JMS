/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ljms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Scanner;

/**
 *
 * @author Piotr
 */
public class LJMS {
    
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            Connection connection = null;
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
            Destination destination = null;
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //System.out.println("Choose the queue: amgmsg || foo.bar");
            switch(s){
                case "amqmsg":
                   destination = (Destination) session.createQueue("amqmsg");  
                   break;
                case "foo.bar":
                  destination = (Destination) session.createQueue("foo.bar");
                  break;
                  default:{
                      System.out.println("Sorry");
                  }
            }
            MessageConsumer consumer = session.createConsumer(destination);
            Message msg = consumer.receive();
            System.out.println("The message which was handled is: " + ((TextMessage)msg).getText());
            connection.close();
        } catch (JMSException ex) {
            System.out.println("Code error: " + ex.getErrorCode() + ( ex.getMessage()) +" - text of Exception ");
            //Logger.getLogger(LJMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
