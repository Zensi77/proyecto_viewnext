package com.juanma.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner; // :contentReference[oaicite:0]{index=0}
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {
	@Autowired
	private ELKSender sender;

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@Override
	public void run(String... args) {
		String brokerURL = "tcp://localhost:61616";
		String queueName = "metrics-queue";

		try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
				Connection connection = factory.createConnection("test", "test")) {

			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);

			MessageConsumer consumer = session.createConsumer(destination);
			System.out.println(">>> Esperando mensajes en cola '" + queueName + "'...");

			while (true) {
				Message msg = consumer.receive(5000);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ex) {
				}
				if (msg != null) {
					System.out.println("Mensaje no-texto recibido: " + msg.getBody(Map.class));
					sender.sentoELK(msg.getBody(Map.class));
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}

// GET metrics{"query":
// {
// "match_all": {}
// }
// }
//
// POST/metrics313/_doc{"root":{"element":"hello2","element2":"bye"}}
//
// PUT/metrics313{"mappings":{"properties":{"nombre":{"type":"text","ignore_above":256}}}}
