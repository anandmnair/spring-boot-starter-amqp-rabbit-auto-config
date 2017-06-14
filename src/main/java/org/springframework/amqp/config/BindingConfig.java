package org.springframework.amqp.config;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Slf4j
public class BindingConfig extends AbstractConfig {
	
	private String exchange;
	
	private String queue;
	
	private String routingKey;

	@Singular
	private Map<String, Object> arguments;
		
	@Override
	public boolean validate() {
		boolean valid=true;
		if(StringUtils.isEmpty(getExchange())) {
			log.error("Invalid Exchange : Exchange must be provided for a binding");
			valid=false;
		}
		if(StringUtils.isEmpty(getQueue())) {
			log.error("Invalid Queue : Queue must be provided for a binding");
			valid=false;
		}
		if(valid) {
			log.info("Binding configuration validated successfully for Binding '{}'", this);
		}
		return valid;
	}
	
	public Binding bind(Exchange exchange, Queue queue) {
		if(ExchangeTypes.HEADERS.getValue().equals(exchange.getType()) && CollectionUtils.isEmpty(getArguments())) {
			throw new AmqpAutoConfigurationException(String
					.format("Invalid Arguments : Arguments must be provided for a header exchange for binding {%s}",this));
		}else if(StringUtils.isEmpty(getRoutingKey())) {
			throw new AmqpAutoConfigurationException(String
					.format("Invalid RoutingKey : RoutingKey must be provided for a non header exchange for binding {%s}",this));
		}
		return BindingBuilder.bind(queue).to(exchange).with(getRoutingKey()).and(getArguments());		
	}
	
}
