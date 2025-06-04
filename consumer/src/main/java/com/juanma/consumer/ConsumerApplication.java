package com.juanma.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner; // :contentReference[oaicite:0]{index=0}
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
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
			System.out.println("[ READY ] Esperando mensajes en cola '" + queueName + "'...");

			while (true) {
				Message msg = consumer.receive(5000);
				// try {
				// 	Thread.sleep(5000);
				// } catch (InterruptedException ex) {
				// }

				if (msg != null) {
					System.out.println(msg.getBody(Map.class));
					Map<String, Object> data = msg.getBody(Map.class);
					String index = (String) data.get("id");

					if (index.equals(EventEnum.ORDER_EVENT.name())) {
						sender.sentoELK(data, EventEnum.ORDER_EVENT);
					} else if (index.equals(EventEnum.FUNNEL_EVENT.name())) {
						data.put("location", Arrays.asList(data.get("lng"), data.get("lat")));						data.remove("lat");
						data.remove("lat");
						data.remove("lng");

						sender.sentoELK(data, EventEnum.FUNNEL_EVENT);
					} else if (index.equals(EventEnum.PERFORMANCE_EVENT.name())) {
						sender.sentoELK(data, EventEnum.PERFORMANCE_EVENT);
					} else {
						System.out.println("No se ha podido enviar el mensaje a ELK");
					}
				}
			}
		} catch (JMSException e) {
			System.err.println("Error al conectar con el broker de ActiveMQ Artemis: " + e.getMessage());
		}
	}
}

//
// POST/metrics313/_doc{"root":{"element":"hello2","element2":"bye"}}
//
// PUT/metrics313{"mappings":{"properties":{"nombre":{"type":"text","ignore_above":256}}}}

// Ver los índices existentes
// GET _cat/indices?v

// Ver el mapping del índice
// GET metrics313/_mapping

// Ver el los documentos del índice
// GET metrics313/_search

// eliminar el índice
// DELETE metrics313
