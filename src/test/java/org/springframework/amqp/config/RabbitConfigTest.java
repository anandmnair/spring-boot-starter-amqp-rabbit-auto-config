package org.springframework.amqp.config;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.CollectionUtils;

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
	public void rabbitConfigWithValidExchangeAndQueueAndBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(true).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange.dlx").build()).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(queue).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done succussfully")));
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithInvalidExchangeAndValidQueueAndValidBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(true).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange.dlx").build()).build())
				.exchange(exchange, ExchangeConfig.builder().type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(queue).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithValidExchangeAndInvalidQueueAndValidBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(true).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange.dlx").build()).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithValidExchangeAndValidQueueAndInvalidBindingTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(true).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange.dlx").build()).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).build())
				.binding(binding, BindingConfig.builder().queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledAndNullDeadLetterConfigTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(true).build())
				.deadLetterConfig(null)
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledAndNullDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(true).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test
	public void rabbitConfigWithNoDeadLetterEnabledAndNullDeadLetterConfigTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().autoDelete(true).durable(false).deadLetterEnabled(false).build())
				.deadLetterConfig(null)
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(queue).deadLetterEnabled(false).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done succussfully")));
	}
	
	@Test
	public void rabbitConfigWithNoDeadLetterEnabledAndNullDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().deadLetterEnabled(false).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).build().applyGlobalConfig(QueueConfig.builder().deadLetterEnabled(false).build()))
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done succussfully")));
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndNullDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndNullGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().deadLetterEnabled(null).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(QueueConfig.builder().deadLetterEnabled(false).build())
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
	}
	
	@Test
	public void rabbitConfigWithNoDeadLetterEnabledForQueueAndNoGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(false).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done succussfully")));
	}
	
	
	@Test
	public void rabbitConfigWithNullDeadLetterEnabledForQueueAndNoGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(queue).deadLetterEnabled(null).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done succussfully")));
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void rabbitConfigWithDeadLetterEnabledForQueueAndNoGlobalDeadLetterExchangeTest() {
		rabbitConfig=RabbitConfig.builder()
				.globalExchange(ExchangeConfig.builder().type(ExchangeTypes.TOPIC).autoDelete(true).durable(false).build())
				.globalQueue(null)
				.deadLetterConfig(DeadLetterConfig.builder().deadLetterExchange(null).build())
				.exchange(exchange, ExchangeConfig.builder().name(exchange).type(ExchangeTypes.TOPIC).build())
				.queue(queue, QueueConfig.builder().name(exchange).deadLetterEnabled(true).build())
				.binding(binding, BindingConfig.builder().exchange(exchange).queue(queue).routingKey(routingKey).build())
				.build();
		rabbitConfig.validate();
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
		assertThat(outputCapture.toString(),containsString(String.format("RabbitConfig Validation done succussfully")));
	}
}
