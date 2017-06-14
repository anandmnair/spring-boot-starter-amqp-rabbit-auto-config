package org.springframework.amqp.exception;

public class AmqpAutoConfigurationException extends RuntimeException {
	
	private static final long serialVersionUID = -5840414225717992195L;

	public AmqpAutoConfigurationException(String message) {
		super(message);
	}
}
