/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import jms.JMSConsumer;
import jms.JMSProducer;

/**
 *
 * @author Piotr
 */
public class Main {

    public static void main(String[] args) {
        String url = "tcp://localhost:61616"; // 
        try (JMSProducer producer = new JMSProducer(url);
                JMSConsumer consumer = new JMSConsumer(url, "amqmsg")) {
            producer.start();
            consumer.init();

            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!(line = rdr.readLine()).equalsIgnoreCase("stop")) {
                producer.send(line);
            }
            System.out.println("Bye!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
