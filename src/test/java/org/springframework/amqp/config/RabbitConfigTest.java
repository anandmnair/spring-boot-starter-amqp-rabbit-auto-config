package org.springframework.amqp.config;


import org.junit.Rule;
import org.junit.Test;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class RabbitConfigTest {

	private RabbitConfig rabbitConfig;
	
	private RabbitConfig expectedRabbitConfig;

	private String exchange= "exchange";
	
	private String queue= "queue";
	
	private String binding= "binding";

	private String routingKey= "routingKey";
	
	@Rule
	public OutputCapture outputCapture = new OutputCapture();
	
	@Test
	public void rabbitConfigEqualsTest() {
		rabbitConfig=RabbitConfig.builder().build();
		expectedRabbitConfig=RabbitConfig.builder().build();
		assertTrue(rabbitConfig.equals(expectedRabbitConfig));
	}
	
	@Test
	public void rabbitConfigeHashcodeTest() {
		rabbitConfig=RabbitConfig.builder().build();
		expectedRabbitConfig=RabbitConfig.builder().build();
		assertThat(rabbitConfig.hashCode(),equalTo(expectedRabbitConfig.hashCode()));
	}
	
	@Test
	public void defaultRabbitConfigTest() {
		rabbitConfig=RabbitConfig.builder().build();
		assertNull(rabbitConfig.getGlobalExchange());
		assertNull(rabbitConfig.getGlobalQueue());
		assertNotNull(rabbitConfig.getExchanges());
		assertNotNull(rabbitConfig.getQueues());
		assertNotNull(rabbitConfig.getBindings());
		assertTrue(CollectionUtils.isEmpty(rabbitConfig.getExchanges()));
		assertTrue(CollectionUtils.isEmpty(rabbitConfig.getQueues()));
		assertTrue(CollectionUtils.isEmpty(rabbitConfig.getBindings()));
	}
	
	@Test
	public void rabbitConfigWithValidExchangeAndQueueAndBindingAndDeadLetterAndRequeueTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(createValidDeadLetterConfig())
				.reQueueConfig(createReQueueConfig("requeue-exchange","requeue"))
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, createQueueConfig(queue))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}

	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithValidExchangeAndQueueAndBindingAndDeadLetterAndInvalidRequeueTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(createValidDeadLetterConfig())
				.reQueueConfig(createReQueueConfig(null,"requeue"))
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, createQueueConfig(queue))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}

	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithInvalidExchangeAndValidQueueAndValidBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(createValidDeadLetterConfig())
				.exchange(exchange, createExchangeConfig(null))
				.queue(queue, createQueueConfig(queue))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithValidExchangeAndInvalidQueueAndValidBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(createValidDeadLetterConfig())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, createQueueConfig(null))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithValidExchangeAndValidQueueAndInvalidBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(createValidDeadLetterConfig())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, createQueueConfig(queue))
				.binding(binding, BindingConfig.builder().queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}

	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledAndNullDeadLetterConfigTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(null)
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, createQueueConfig(queue))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledAndNullDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(createGlobalQueueConfig())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, createQueueConfig(queue))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	@Test
	public void rabbitConfigWithNoDeadLetterEnabledAndNullDeadLetterConfigTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(false).build())
				.deadLetterConfig(null)
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(queue).deadLetterEnabled(false).build())
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}
	
	@Test
	public void rabbitConfigWithNoDeadLetterEnabledAndNullDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(QueueConfig.builder().deadLetterEnabled(false).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(queue).build().applyGlobalConfig(QueueConfig.builder().deadLetterEnabled(false).build()))
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}
	
	
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndNullGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(QueueConfig.builder().deadLetterEnabled(null).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(QueueConfig.builder().deadLetterEnabled(false).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	@Test
	public void rabbitConfigWithNoDeadLetterEnabledForQueueAndNoGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(false).build())
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}
	
	
	@Test
	public void rabbitConfigWithNullDeadLetterEnabledForQueueAndNoGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(queue).deadLetterEnabled(null).build())
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}
	

	
	@Test
	public void rabbitConfigWithNoExchangeAndNoQueueAndNoBindingTest() {
		rabbitConfig=new RabbitConfig();
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(null)
				.globalQueue(null)
				.deadLetterConfig(null)
				.exchanges(new HashMap<>())
				.queues(new HashMap<>())
				.bindings(new HashMap<>())
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done successfully")));
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndNoGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(createGlobalExchangeConfig())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, createExchangeConfig(exchange))
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, createBinding(exchange,queue,routingKey))
				.build();
		rabbitConfig.validate();
	}
	
	private ExchangeConfig createGlobalExchangeConfig() {
		return createExchangeConfig(null);
	}
	
	private ExchangeConfig createExchangeConfig(String name) {
		return ExchangeConfig.builder().name(name).type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build();
	}
	
	private QueueConfig createGlobalQueueConfig() {
		return createQueueConfig(null);
	}
	
	private QueueConfig createQueueConfig(String name) {
		return QueueConfig.builder().name(name).autoDelete(true).durable(false).deadLetterEnabled(true).build();
	}

	private BindingConfig createBinding(String exchange, String queue, String routingKey) {
		return BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build();
	}

	private DeadLetterConfig createValidDeadLetterConfig() {
		return DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange.dlx").build()).build();
	}

	private ReQueueConfig createReQueueConfig(String exchange, String queue ) {
		return ReQueueConfig.builder().exchange(createExchangeConfig(exchange)).queue(createQueueConfig(queue)).routingKey("requeue.key").build();
	}
	
	
}
