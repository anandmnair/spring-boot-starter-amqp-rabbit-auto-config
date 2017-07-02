package org.springframework.amqp.configuration;

import java.util.Map.Entry;

import org.springframework.amqp.config.BindingConfig;
import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.QueueConfig;
import org.springframework.amqp.config.RabbitConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.processor.CorrelationPostProcessor;
import org.springframework.amqp.processor.DefaultCorrelationDataPostProcessor;
import org.springframework.amqp.processor.InfoHeaderMessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.CorrelationDataPostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
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

	@Bean
	@ConditionalOnMissingBean(MessageConverter.class)
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	@ConditionalOnMissingBean(RetryOperationsInterceptor.class)
	public RetryOperationsInterceptor interceptor(AmqpTemplate amqpTemplate) {
		return RetryInterceptorBuilder.stateless()
				.maxAttempts(5)
				.recoverer(new RepublishMessageRecoverer(amqpTemplate, "${rabbitmq.auto-config.exchange.global-err-exchange.name}"))
				.build();
	}

	@Bean
	public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, CorrelationDataPostProcessor correlationDataPostProcessor, MessagePostProcessor...messagePostProcessors) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessors);
		rabbitTemplate.setCorrelationDataPostProcessor(correlationDataPostProcessor);
		return rabbitTemplate;
	}

	@Bean
	public MessagePostProcessor headerMessagePostProcessor() {
		InfoHeaderMessagePostProcessor infoHeaderMessagePostProcessor = new InfoHeaderMessagePostProcessor();
		infoHeaderMessagePostProcessor.setHeaders(rabbitConfig.getInfoHeaders());
		return infoHeaderMessagePostProcessor;
	}

	@Bean
	@ConditionalOnMissingBean(CorrelationDataPostProcessor.class)
	public CorrelationDataPostProcessor correlationDataPostProcessor(CorrelationPostProcessor correlationPostProcessor) {
		return new DefaultCorrelationDataPostProcessor(correlationPostProcessor);
	}

	@Bean
	@ConditionalOnMissingBean(CorrelationPostProcessor.class)
	public CorrelationPostProcessor correlationPostProcessor() {
		return new CorrelationPostProcessor();
	}
	
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
