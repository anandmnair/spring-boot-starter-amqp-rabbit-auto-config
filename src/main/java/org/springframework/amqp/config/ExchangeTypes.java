package org.springframework.amqp.config;

import lombok.Getter;

@Getter
public enum ExchangeTypes {
	DIRECT("direct"),
	TOPIC("topic"),
	FANOUT("fanout"),
	HEADERS("headers");
	
	private String value;
	
	ExchangeTypes(String value) {
		this.value=value;
	}
}
