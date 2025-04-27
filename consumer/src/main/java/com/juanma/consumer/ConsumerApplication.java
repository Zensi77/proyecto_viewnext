package com.juanma.consumer;

import org.springframework.boot.CommandLineRunner; // :contentReference[oaicite:0]{index=0}
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory; // :contentReference[oaicite:1]{index=1}

@SpringBootApplication
public class ConsumerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@Override
	public void run(String... args) {
		String brokerURL = "tcp://localhost:61616";
		String queueName = "metrics-queue";

		try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
				Connection connection = factory.createConnection("admin", "admin")) {

			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);

			MessageConsumer consumer = session.createConsumer(destination);
			System.out.println(">>> Esperando mensajes en cola '" + queueName + "'...");

			while (true) {
				Message msg = consumer.receive(5000); // espera 5 segundos por mensaje
				if (msg != null) {
					if (msg instanceof TextMessage textMessage) {
						System.out.println("Mensaje recibido: " + textMessage.getText());
					} else {
						System.out.println("Mensaje no-texto recibido: " + msg);
					}
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
