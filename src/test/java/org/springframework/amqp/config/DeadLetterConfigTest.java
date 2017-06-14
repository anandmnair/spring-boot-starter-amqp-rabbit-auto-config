package org.springframework.amqp.config;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;

public class DeadLetterConfigTest {

	private DeadLetterConfig deadLetterConfig;
	
	private DeadLetterConfig expectedDeadLetterConfig;

	private String exchangeName= "exchange-1";
	
	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	@Test
	public void deadLetterConfigEqualsTest() {
		deadLetterConfig=DeadLetterConfig.builder().build();
		expectedDeadLetterConfig=DeadLetterConfig.builder().build();
		assertTrue(deadLetterConfig.equals(expectedDeadLetterConfig));
		deadLetterConfig.setGlobalConfigApplied(false);
		expectedDeadLetterConfig.setGlobalConfigApplied(true);
		assertTrue(deadLetterConfig.equals(expectedDeadLetterConfig));
	}
	
	@Test
	public void deadLetterConfigeHashcodeTest() {
		deadLetterConfig=DeadLetterConfig.builder().build();
		expectedDeadLetterConfig=DeadLetterConfig.builder().build();
		assertThat(deadLetterConfig.hashCode(),equalTo(expectedDeadLetterConfig.hashCode()));
		deadLetterConfig.setGlobalConfigApplied(false);
		expectedDeadLetterConfig.setGlobalConfigApplied(true);
		assertThat(deadLetterConfig.hashCode(),equalTo(expectedDeadLetterConfig.hashCode()));
	}
	
	@Test
	public void validDeadLetterConfig() {
		deadLetterConfig=DeadLetterConfig.builder()
				.deadLetterExchange(ExchangeConfig.builder().name(exchangeName).type(ExchangeTypes.TOPIC).build())
				.queuePostfix(".dlq")
				.build();
		assertTrue(deadLetterConfig.validate());
		String deadLetterQueueName = deadLetterConfig.createDeadLetterQueueName("temp-queue");
		assertThat(deadLetterQueueName,equalTo("temp-queue.dlq"));
	}
	
	@Test
	public void validDeadLetterConfigWithDefaultQueuePostfix() {
		deadLetterConfig=DeadLetterConfig.builder()
				.deadLetterExchange(ExchangeConfig.builder().name(exchangeName).type(ExchangeTypes.TOPIC).build())
				.build();
		assertTrue(deadLetterConfig.validate());
		String deadLetterQueueName = deadLetterConfig.createDeadLetterQueueName("temp-queue");
		assertThat(deadLetterQueueName,equalTo("temp-queue.DLQ"));
	}
	
	@Test
	public void invalidDeadLetterConfigWithoutExchange() {
		deadLetterConfig=DeadLetterConfig.builder()
				.deadLetterExchange(null)
				.queuePostfix(".dlq")
				.build();
		assertFalse(deadLetterConfig.validate());
	}
	
	@Test
	public void invalidDeadLetterConfigWithoutExchangeName() {
		deadLetterConfig=DeadLetterConfig.builder()
				.deadLetterExchange(ExchangeConfig.builder().build())
				.queuePostfix(".dlq")
				.build();
		assertFalse(deadLetterConfig.validate());
	}
}
