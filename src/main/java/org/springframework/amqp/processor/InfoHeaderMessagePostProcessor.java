package org.springframework.amqp.processor;

import lombok.Data;
import lombok.Singular;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Data
public class InfoHeaderMessagePostProcessor implements MessagePostProcessor {

	@Singular
	private Map<String,String> headers = new HashMap<>();
	
	@Autowired
	private Environment environment;
	
	@Override
	public Message postProcessMessage(final Message message) throws AmqpException {
		MessageProperties messageProperties = message.getMessageProperties();
		for(Map.Entry<String, String> entry: headers.entrySet()) {
			message.getMessageProperties().getHeaders().putIfAbsent(entry.getKey(), entry.getValue());
		}
		messageProperties.getHeaders().putIfAbsent("spring-application-name",
				environment.getProperty("spring.application.name", String.class));
		return message;
	}

}
