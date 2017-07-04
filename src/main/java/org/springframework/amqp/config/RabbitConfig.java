package org.springframework.amqp.config;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
@ConfigurationProperties(prefix = "spring.rabbitmq.auto-config")
public class RabbitConfig {

	private boolean enabled=true;

	private ExchangeConfig globalExchange;

	private QueueConfig globalQueue;

	private DeadLetterConfig deadLetterConfig;

	private ReQueueConfig reQueueConfig;

	@Singular
	private Map<String, String> infoHeaders = new LinkedHashMap<>();

	@Singular
	private Map<String, ExchangeConfig> exchanges = new LinkedHashMap<>();

	@Singular
	private Map<String, QueueConfig> queues = new LinkedHashMap<>();

	@Singular
	private Map<String, BindingConfig> bindings =  new LinkedHashMap<>();

	@PostConstruct
	public void validate() {
		boolean valid = true;
		log.info("Validating exchanges...");
		for (Entry<String, ExchangeConfig> entry : exchanges.entrySet()) {
			valid=validate(entry.getKey(), entry.getValue(), valid);
		}
		log.info("Validating queues...");
		for (Entry<String, QueueConfig> entry : queues.entrySet()) {
			valid=validate(entry.getKey(), entry.getValue(), valid);
		}
		log.info("Validating bindings...");
		for (Entry<String, BindingConfig> entry : bindings.entrySet()) {
			valid=validate(entry.getKey(), entry.getValue(), valid);
		}	

		log.info("Validating DeadLetterConfig...");
		if(isDeadLetterEnabled()){
			if(deadLetterConfig==null || deadLetterConfig.getDeadLetterExchange()==null) {
				log.error("Validating failed. DeadLetterConfig must provided if any queue enable dead letter queue.");
				valid=false;
			} else {
				valid=validate("DeadLetterConfig", deadLetterConfig, valid);
			}
		}
		log.info("Validating ReQueueConfig...");
		if(reQueueConfig!=null) {
			valid=validate("ReQueueConfig", reQueueConfig, valid);
		}
		
		if(valid) {
			log.info("RabbitConfig Validation done successfully. RabbitConfig = {{}}", this.toString());
		}else {
			throw new AmqpAutoConfigurationException("Invalid RabbitConfig Configuration");
		}
	}

	private boolean validate(String key, AbstractConfig abstractConfig, boolean valid) {
		log.info("Validating key {} :: value {}...", key, abstractConfig);
		return abstractConfig.validate() ? valid : false;
	}
	
	private boolean isDeadLetterEnabled() {
		if(globalQueue!=null && globalQueue.getDeadLetterEnabled()!=null && globalQueue.getDeadLetterEnabled()) {
			return true;
		}
		else {
			for(QueueConfig queue : queues.values()) {
				if(queue.getDeadLetterEnabled()!=null && queue.getDeadLetterEnabled()) {
					return true;
				}
			}
			return false;
		}
	}

	
}
