package org.springframework.amqp.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@Slf4j
public abstract class AbstractConfig {
	
	private boolean globalConfigApplied;
	
	protected <T> T getDefaultConfig(String name,String property, T currentValue, T globalValue, T defaultValue) {
		T value = getDefaultConfig(currentValue, globalValue);
		if(null == value) {
			log.warn("'{}' : '{}' : No '{}' configuration provided. Applying default value {} : {} ", getClass().getName(), name, property, property, defaultValue);
		}
		return value!=null?value:defaultValue;
	}
	
	protected <T> T getDefaultConfig(T currentValue, T globalValue) {
		return currentValue!=null?currentValue:globalValue;
	}
	
	protected Map<String, Object> loadArguments(Map<String, Object> currentArguments, Map<String, Object> globalArguments) {
		Map<String, Object> arguments=new HashMap<>();
		if(globalArguments!=null) {
			arguments.putAll(globalArguments);
		}
		if(currentArguments!=null) {
			arguments.putAll(currentArguments);
		}
		return arguments;
	}
	
	public abstract boolean validate();
}
