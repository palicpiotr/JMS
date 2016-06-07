/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author Piotr
 */
public class JMSConsumer implements MessageListener, AutoCloseable {

    private final ActiveMQConnectionFactory connectionFactory;
    private Connection connection = null;
    private Session session = null;
    private MessageConsumer consumer;
    private String queueName;

    /**
     * Конструктор используется в случае, когда брокер не требует авторизации.
     * Здесь я не стал добавлять вариант с авторизацией. Он показан в
     * producer-е. Брокер ActiveMQ из коробки настроен на работу без
     * авторизации.
     */
    public JMSConsumer(String url, String queue) {
        connectionFactory = new ActiveMQConnectionFactory(url);
        queueName = queue;
    }

    /**
     * Инициализация consumer-а. Обратите внимание на добавление экземпляра
     * этого класса в качестве подписчика на событие получения сообщений.
     *
     */
    public void init() throws JMSException {
        System.out.println("Init consumer...");

        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = session.createQueue(queueName);
        consumer = session.createConsumer(dest);
        consumer.setMessageListener(this); // подписываемся на событие onMessage

        System.out.println("Consumer successfully initialized");

    }

    /**
     * Обработчик события появления сообщения в целевом объекте. Этот метод
     * является частью реализации интерфейса MessageListener.
     */
    public void onMessage(Message msg) {
        if (msg instanceof TextMessage) {
            try {
                System.out.println("Received message: " + ((TextMessage) msg).getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Received message: " + msg.getClass().getName());
        }
    }

    /**
     * Метод закрывает созединения перед разрушением объекта. Этот метод
     * является реализацией интерфейса Autoclosable, добавленого в Java7 и
     * используемого в блоке try-with-resources.
     */
    public void close() throws Exception {
        try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException jmsEx) {
            jmsEx.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
