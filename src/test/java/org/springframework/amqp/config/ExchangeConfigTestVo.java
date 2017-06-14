package org.springframework.amqp.config;

import java.util.HashMap;

import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.ExchangeTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ExchangeConfigTestVo extends ExchangeConfig {
	
	private String exchangeType;
	
	public ExchangeConfigTestVo(String name, String exchangeType, Boolean durable, Boolean autoDelete, Boolean internal, Boolean delayed, boolean globalConfigApplied) {
		super(name, ExchangeTypes.valueOf(exchangeType), durable, autoDelete, internal, delayed, new HashMap<>());
	}
	
	public ExchangeConfig build() {
		ExchangeConfig exchangeConfig = ExchangeConfig.builder().build();
		BeanUtils.copyProperties(this, exchangeConfig);
		if(!StringUtils.isEmpty(exchangeType)) {
			exchangeConfig.setType(ExchangeTypes.valueOf(exchangeType));
		}
		return exchangeConfig;
	}
}
