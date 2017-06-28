package org.springframework.amqp.configuration;

import java.util.Map.Entry;

import org.springframework.amqp.config.BindingConfig;
import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.QueueConfig;
import org.springframework.amqp.config.RabbitConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Import(RabbitMqConfiguration.class)
@EnableRabbit
@Slf4j
@ConditionalOnBean(RabbitConfig.class)
public class RabbitMqAutoConfiguration implements ApplicationContextAware {

	private ConfigurableApplicationContext applicationContext;
	
	@Autowired
	private RabbitConfig rabbitConfig;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=(ConfigurableApplicationContext) applicationContext;	
		if(rabbitConfig!=null) {
			loadRabbitConfig();
		}
	}

	public void loadRabbitConfig() {
		Exchange deadLetterExchange = null;
		if(rabbitConfig.getDeadLetterConfig()!=null && rabbitConfig.getDeadLetterConfig().getDeadLetterExchange()!=null) {
			deadLetterExchange = rabbitConfig.getDeadLetterConfig().getDeadLetterExchange().buildExchange(rabbitConfig.getGlobalExchange());
			applicationContext.getBeanFactory().registerSingleton(deadLetterExchange.getName(), deadLetterExchange);
			log.info("Auto configuring dead letter exchange: Key = {} , DeadLetterExchange = {{}}", deadLetterExchange.getName(), deadLetterExchange);
		}
		
		if(!CollectionUtils.isEmpty(rabbitConfig.getExchanges())) {
			log.info("Auto configuring exchanges...");
			for( Entry<String, ExchangeConfig> entry : rabbitConfig.getExchanges().entrySet()) {
				Exchange exchange = entry.getValue().buildExchange(rabbitConfig.getGlobalExchange());
				applicationContext.getBeanFactory().registerSingleton(entry.getKey(), exchange);
				log.info("Auto configuring exchange: Key = {} , Exchange = {{}}", entry.getKey(), exchange);
			}
		}
		
		if(!CollectionUtils.isEmpty(rabbitConfig.getQueues())) {
			log.info("Auto configuring queues...");
			for( Entry<String, QueueConfig> entry : rabbitConfig.getQueues().entrySet()) {
				Queue queue = entry.getValue().buildQueue(rabbitConfig.getGlobalQueue(), rabbitConfig.getDeadLetterConfig());
				applicationContext.getBeanFactory().registerSingleton(entry.getKey(), queue);
				log.info("Auto configuring queue: Key = {} , Queue = {{}}", entry.getKey(), queue);
				if(entry.getValue().getDeadLetterEnabled()) {
					Queue deadLetterQueue = entry.getValue().buildDeadLetterQueue(rabbitConfig.getGlobalQueue(), rabbitConfig.getDeadLetterConfig());
					applicationContext.getBeanFactory().registerSingleton(deadLetterQueue.getName(), deadLetterQueue);
					log.info("Auto configuring dead letter queue: Key = {} , DeadLetterQueue = {{}}", deadLetterQueue.getName(), deadLetterQueue);
					Binding deadLetterBinding = BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(deadLetterQueue.getName()).noargs();
					String deadLetterBindingKey = new StringBuilder().append(deadLetterExchange.getName()).append(":").append(deadLetterQueue.getName()).toString();
					applicationContext.getBeanFactory().registerSingleton(deadLetterBindingKey, deadLetterBinding);
					log.info("Auto configuring dead letter binding: Key = {} , DeadLetterBinding = {{}}", deadLetterBindingKey, deadLetterBinding);
				}
			}
		}
		
		if(!CollectionUtils.isEmpty(rabbitConfig.getBindings())) {
			log.info("Auto configuring bindings...");
			for( Entry<String, BindingConfig> entry : rabbitConfig.getBindings().entrySet()) {
				Exchange exchange = applicationContext.getBean(entry.getValue().getExchange(),Exchange.class);
				Queue queue = applicationContext.getBean(entry.getValue().getQueue(),Queue.class);
				Binding binding = entry.getValue().bind(exchange, queue);
				applicationContext.getBeanFactory().registerSingleton(entry.getKey(), binding);
				log.info("Auto configuring bindings: Key = {} , Binding = {{}}", entry.getKey(), binding);
			}
		}
	}
}
