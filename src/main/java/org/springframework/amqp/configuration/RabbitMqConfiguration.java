package org.springframework.amqp.configuration;

import org.springframework.amqp.config.RabbitConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(RabbitConfig.class)
public class RabbitMqConfiguration {

	@Bean
	public RabbitConfig rabbitConfig(){
		return new RabbitConfig();
	}
	
}
