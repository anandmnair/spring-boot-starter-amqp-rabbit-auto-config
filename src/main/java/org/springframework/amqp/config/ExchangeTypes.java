package org.springframework.amqp.config;

public enum ExchangeTypes {
	DIRECT("direct"),
	TOPIC("topic"),
	FANOUT("fanout"),
	HEADERS("headers");
	
	private String value;
	
	public String getValue() {
		return value;
	}

	ExchangeTypes(String value) {
		this.value=value;
	}
}
