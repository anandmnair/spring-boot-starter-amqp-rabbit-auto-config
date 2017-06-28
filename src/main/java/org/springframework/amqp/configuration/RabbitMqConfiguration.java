package org.springframework.amqp.configuration;

import org.springframework.amqp.config.RabbitConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfiguration {

	@Bean
	@ConditionalOnProperty(prefix="spring.rabbitmq.auto-config", name="enabled", matchIfMissing=true)
	public RabbitConfig rabbitConfig(){
		return new RabbitConfig();
	}
	
}
