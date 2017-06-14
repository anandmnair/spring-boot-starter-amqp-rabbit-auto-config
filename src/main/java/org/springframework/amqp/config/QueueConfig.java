package org.springframework.amqp.config;

import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class QueueConfig extends AbstractConfig {

	private String name;

	private Boolean durable;

	private Boolean autoDelete;

	private Boolean exclusive;

	private Boolean deadLetterEnabled;

	@Singular
	private Map<String, Object> arguments;

	public boolean validate() {
		if (StringUtils.isEmpty(getName())) {
			log.error("Invalid Queue Configuration : Name must be provided for a queue");
			return false;
		}
		log.info("Queue configuration validated successfully for queue '{}'", getName());
		return true;
	}

	public QueueConfig applyGlobalConfig(QueueConfig globalQueueConfig) {
		log.debug("Appliying GlobalQueueConfig on the current QueueConfig :: QueueConfig = {{}} , GlobalQueueConfig = {{}}", 
				this, globalQueueConfig);
		setDurable(getDefaultConfig(getName(), "durable", getDurable(), globalQueueConfig.getDurable(), Boolean.FALSE));
		setAutoDelete(getDefaultConfig(getName(), "autoDelete", getAutoDelete(), globalQueueConfig.getAutoDelete(),Boolean.FALSE));
		setExclusive(getDefaultConfig(getName(), "exclusive", getExclusive(), globalQueueConfig.getExclusive(),	Boolean.FALSE));
		setDeadLetterEnabled(getDefaultConfig(getName(), "deadLetterEnabled", getDeadLetterEnabled(), 
				globalQueueConfig.getDeadLetterEnabled(), Boolean.FALSE));
		setArguments(loadArguments(getArguments(), globalQueueConfig.getArguments()));
		setGlobalConfigApplied(true);
		log.info("GlobalQueueConfig applied on the current ExchangeConfig :: ExchangeConfig = {{}} , GlobalQueueConfig = {{}}",
				this, globalQueueConfig);
		return this;
	}

	public Queue buildQueue(QueueConfig globalQueueConfig, DeadLetterConfig deadLetterConfig) {
		if (!isGlobalConfigApplied()) {
			applyGlobalConfig(globalQueueConfig);
		}
		Queue queue = new Queue(getName(), getDurable(), getExclusive(), getAutoDelete(), getArguments());
		if (Boolean.TRUE.equals(getDeadLetterEnabled())) {
			if(deadLetterConfig == null || deadLetterConfig.getDeadLetterExchange()==null) {		
			    throw new AmqpAutoConfigurationException(
			    		String.format("Invalid configuration %s : DeadLetterConfig/DeadLetterExchange must be provided when deadLetterEnabled=true for queue %s.",
			    				getName(), getName()));
			}
			queue.getArguments().put("x-dead-letter-exchange", deadLetterConfig.getDeadLetterExchange().getName());
			queue.getArguments().put("x-dead-letter-routing-key", deadLetterConfig.createDeadLetterQueueName(getName()));
		}
		return queue;
	}

	public Queue buildDeadLetterQueue(QueueConfig globalQueueConfig, DeadLetterConfig deadLetterConfig) {
		if (!isGlobalConfigApplied()) {
			applyGlobalConfig(globalQueueConfig);
		}
		return new Queue(deadLetterConfig.createDeadLetterQueueName(getName()), getDurable(), getExclusive(), getAutoDelete(), getArguments());
	}

}
