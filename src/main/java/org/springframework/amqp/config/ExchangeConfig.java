package org.springframework.amqp.config;

import java.util.Map;

import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.CustomExchange;
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
@EqualsAndHashCode(callSuper=false)
@Slf4j
public class ExchangeConfig extends AbstractConfig {
	
	private String name;
	
	private ExchangeTypes type;

	private Boolean durable;

	private Boolean autoDelete;

	private Boolean internal;

	private Boolean delayed;

	@Singular
	private Map<String, Object> arguments;
		
	public boolean validate() {
		if(StringUtils.isEmpty(getName())) {
			log.error("Invalid Exchange Configuration : Name must be provided for an exchange");
			return false;
		}
		log.info("Exchange configuration validated successfully for exchange '{}'", getName());
		return true;
	}
	
	public ExchangeConfig applyGlobalConfig(ExchangeConfig globalExchangeConfig) {
		log.debug("Appliying GlobalExchangeConfig on the current ExchangeConfig :: ExchangeConfig = {{}} , GlobalExchangeConfig = {{}}", this, globalExchangeConfig);
		setType(getDefaultConfig(getName(), "type", getType(), globalExchangeConfig.getType(), ExchangeTypes.TOPIC));
		setDurable(getDefaultConfig(getName(), "durable", getDurable(), globalExchangeConfig.getDurable(), Boolean.FALSE));
		setAutoDelete(getDefaultConfig(getName(), "autoDelete", getAutoDelete(), globalExchangeConfig.getAutoDelete(), Boolean.FALSE));
		setInternal(getDefaultConfig(getName(), "internal", getInternal(), globalExchangeConfig.getInternal(), Boolean.FALSE));
		setDelayed(getDefaultConfig(getName(), "delayed", getDelayed(), globalExchangeConfig.getDelayed(), Boolean.FALSE));
		setArguments(loadArguments(getArguments(), globalExchangeConfig.getArguments()));
		setGlobalConfigApplied(true);
		log.info("GlobalExchangeConfig applied on the current ExchangeConfig :: ExchangeConfig = {{}} , GlobalExchangeConfig = {{}}", this, globalExchangeConfig);
		return this;
	}
	
	public AbstractExchange buildExchange(ExchangeConfig globalExchangeConfig) {
		if(!isGlobalConfigApplied()) {
			applyGlobalConfig(globalExchangeConfig);
		}
		AbstractExchange exchange = new CustomExchange(getName(), getType().getValue(),getDurable(), getAutoDelete(), getArguments());		
		exchange.setInternal(getInternal());
		exchange.setDelayed(getDelayed());
		return exchange;
	}
	
}
