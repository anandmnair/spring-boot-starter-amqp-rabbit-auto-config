package org.springframework.amqp.processor;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;

import java.util.UUID;

public class CorrelationPostProcessor implements MessagePostProcessor {
	
	@Override
	public Message postProcessMessage(final Message message) throws AmqpException {
		MessageProperties messageProperties = message.getMessageProperties();
		String correlationId = messageProperties.getCorrelationIdString();
		if (correlationId == null) {
			correlationId = UUID.randomUUID().toString();
			messageProperties.setCorrelationIdString(correlationId);
		}
		messageProperties.getHeaders().put("correlation-id", correlationId);
		return message;
	}

}
