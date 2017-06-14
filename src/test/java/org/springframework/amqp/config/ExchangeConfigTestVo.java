package org.springframework.amqp.config;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ExchangeConfigTestVo extends ExchangeConfig {
	
	private String exchangeType;
	
	public ExchangeConfig build() {
		ExchangeConfig exchangeConfig = ExchangeConfig.builder().build();
		BeanUtils.copyProperties(this, exchangeConfig);
		if(!StringUtils.isEmpty(getExchangeType())) {
			exchangeConfig.setType(ExchangeTypes.valueOf(getExchangeType()));
		}
		return exchangeConfig;
	}
}
