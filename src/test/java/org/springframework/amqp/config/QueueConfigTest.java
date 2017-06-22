package org.springframework.amqp.config;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.CollectionUtils;

public class QueueConfigTest {

	private QueueConfig queueConfig;
	
	private QueueConfig expectedQueueConfig;

	private QueueConfig globalQueueConfig;
	
	private DeadLetterConfig deadLetterConfig;
	
	private String queueName= "queue-1";
	
	@Rule
	public OutputCapture outputCapture = new OutputCapture();
	
	@Before
	public void setUp(){
		queueConfig=QueueConfig.builder().build();
		expectedQueueConfig=QueueConfig.builder().durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(false).build();
		globalQueueConfig=QueueConfig.builder().build();
		deadLetterConfig=DeadLetterConfig.builder().build();
	}
	
	@Test
	public void queueConfigEqualsTest() {
		queueConfig=QueueConfig.builder().build();
		expectedQueueConfig=QueueConfig.builder().build();
		assertTrue(queueConfig.equals(expectedQueueConfig));
		queueConfig.setGlobalConfigApplied(false);
		expectedQueueConfig.setGlobalConfigApplied(true);
		assertTrue(queueConfig.equals(expectedQueueConfig));
	}
	
	@Test
	public void queueConfigeHashcodeTest() {
		queueConfig=QueueConfig.builder().build();
		expectedQueueConfig=QueueConfig.builder().build();
		assertThat(queueConfig.hashCode(),equalTo(expectedQueueConfig.hashCode()));
		queueConfig.setGlobalConfigApplied(false);
		expectedQueueConfig.setGlobalConfigApplied(true);
		assertThat(queueConfig.hashCode(),equalTo(expectedQueueConfig.hashCode()));
	}
	
	@Test
	public void defaultQueueConfigWithoutGlobalConfigurationAppliedTest() {
		queueConfig=QueueConfig.builder().build();
		assertNull(queueConfig.getName());
		assertNull(queueConfig.getDurable());
		assertNull(queueConfig.getAutoDelete());
		assertNull(queueConfig.getExclusive());
		assertNull(queueConfig.getDeadLetterEnabled());
		assertFalse(queueConfig.isGlobalConfigApplied());
		assertNotNull(queueConfig.getArguments());
		assertTrue(CollectionUtils.isEmpty(queueConfig.getArguments()));	
	}
	
	@Test
	public void defaultQueueConfigWithDefaultGlobalConfigurationAppliedTest() {
		queueConfig=QueueConfig.builder().build().applyGlobalConfig(globalQueueConfig);
		expectedQueueConfig=QueueConfig.builder().durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(false).build();
		assertNull(queueConfig.getName());
		assertFalse(queueConfig.getDurable());
		assertFalse(queueConfig.getAutoDelete());
		assertFalse(queueConfig.getExclusive());
		assertFalse(queueConfig.getDeadLetterEnabled());
		assertNotNull(queueConfig.getArguments());
		assertTrue(queueConfig.isGlobalConfigApplied());
		assertTrue(CollectionUtils.isEmpty(queueConfig.getArguments()));
		assertThat(queueConfig, equalTo(expectedQueueConfig));
	}
	
	@Test
	public void queueConfigWithNameAndValidationSuccessTest() {
		queueConfig=QueueConfig.builder().name(queueName).build();
		assertTrue(queueConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Queue configuration validated successfully for queue '%s'",queueName)));
	}
	
	@Test
	public void queueConfigWithoutNameAndValidationFailTest() {
		queueConfig=QueueConfig.builder().build();
		assertFalse(queueConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Queue Configuration : Name must be provided for a queue")));
	}
	
	@Test
	public void queueConfigWithoutNameAndGlobalConfigurationWithNameAndValidationFailTest() {
		globalQueueConfig=QueueConfig.builder().name(queueName).build();
		queueConfig=QueueConfig.builder().build().applyGlobalConfig(globalQueueConfig);
		assertFalse(queueConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Queue Configuration : Name must be provided for a queue")));
	}
	
	@Test
	public void queueConfigWithOnlyQueueNameAndDefaultGlobalConfigurationAppliedTest() {
		queueConfig=QueueConfig.builder().name(queueName).build().applyGlobalConfig(globalQueueConfig);
		expectedQueueConfig=QueueConfig.builder().name(queueName).durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(false).build();
		assertThat(queueConfig, equalTo(expectedQueueConfig));
	}
	
	@Test
	public void queueConfigWithOnlyQueueNameAndNoGlobalConfigurationAppliedTest() {
		queueConfig=QueueConfig.builder().name(queueName).build();
		expectedQueueConfig=QueueConfig.builder().name(queueName).durable(null).autoDelete(null).exclusive(null).deadLetterEnabled(null).build();
		assertThat(queueConfig, equalTo(expectedQueueConfig));
	}
	
	@Test
	public void queueConfigWithOnlyQueueNameAndFewGlobalConfigurationTest() { 
		globalQueueConfig=QueueConfig.builder()
				.durable(true).autoDelete(true).exclusive(true).deadLetterEnabled(true).argument("key1", "value1")
				.build();
		
		queueConfig=QueueConfig.builder().name(queueName).build().applyGlobalConfig(globalQueueConfig);
		
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(true).autoDelete(true).exclusive(true).deadLetterEnabled(true)
				.argument("key1", "value1")
				.build();

		assertThat(queueConfig, equalTo(expectedQueueConfig));
	}
	
	@Test
	public void queueConfigByOverriddingFromGlobalConfigurationTest() { 
		globalQueueConfig=QueueConfig.builder()
				.durable(true).autoDelete(true).exclusive(true).deadLetterEnabled(true).argument("key1", "value1")
				.build();
		
		queueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false)
				.argument("key1", "NEW_VALUE").argument("key2", "value2")
				.build()
				.applyGlobalConfig(globalQueueConfig);
		
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(true).deadLetterEnabled(true)
				.argument("key1", "NEW_VALUE").argument("key2", "value2")
				.build();

		assertThat(queueConfig, equalTo(expectedQueueConfig));
	}
	
	@Test
	public void createQueueWithNoDeadLetterAndDefaultGlobalConfigurationAppliedTest(){
		globalQueueConfig=QueueConfig.builder().build();
		queueConfig=QueueConfig.builder().name(queueName).build();
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(false).arguments(new HashMap<>())
				.build();
		Queue queue = queueConfig.buildQueue(globalQueueConfig, null);
		assertQueue(queue, expectedQueueConfig);
	}
	
	@Test
	public void createQueueWithNoDeadLetterAndDefaultGlobalConfigurationPreAppliedTest(){
		globalQueueConfig=QueueConfig.builder().build();
		queueConfig=QueueConfig.builder().name(queueName).build().applyGlobalConfig(globalQueueConfig);
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(false).arguments(new HashMap<>())
				.build();
		Queue queue = queueConfig.buildQueue(globalQueueConfig, null);
		assertQueue(queue, expectedQueueConfig);
	}
	
	@Test
	public void createQueueWithFewGlobalConfigurationTest(){
		globalQueueConfig=QueueConfig.builder()
				.durable(true).autoDelete(true).exclusive(true).deadLetterEnabled(false).argument("key1", "value1")
				.build();
		
		queueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false)
				.argument("key2", "value2").argument("key1", "NEW_VALUE")
				.build()
				.applyGlobalConfig(globalQueueConfig);
		
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(true).deadLetterEnabled(true)
				.argument("key2", "value2").argument("key1", "NEW_VALUE")
				.build();
		
		Queue queue = queueConfig.buildQueue(globalQueueConfig, null);
		assertQueue(queue, expectedQueueConfig);
	}
	
	@Test
	public void createQueueWithDeadLetterAndDefaultDeadLetterConfig(){
		globalQueueConfig=QueueConfig.builder().deadLetterEnabled(true).build();
		
		queueConfig=QueueConfig.builder().name(queueName).deadLetterEnabled(true).argument("key1", "value1").build()
				.applyGlobalConfig(globalQueueConfig);
		
		deadLetterConfig=DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange").build())
				.build();
		
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(true)
				.argument("key1", "value1")
				.argument("x-dead-letter-exchange", "dead-letter-exchange").argument("x-dead-letter-routing-key", queueName+".DLQ")
				.build();
		
		Queue queue = queueConfig.buildQueue(globalQueueConfig, deadLetterConfig);
		assertQueue(queue, expectedQueueConfig);
	}
	
	
	@Test
	public void createQueueWithDeadLetterAndDeadLetterConfig(){
		globalQueueConfig=QueueConfig.builder().deadLetterEnabled(true).build();
		
		queueConfig=QueueConfig.builder().name(queueName).deadLetterEnabled(true).argument("key1", "value1").build()
				.applyGlobalConfig(globalQueueConfig);
		
		deadLetterConfig=DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange").build())
				.queuePostfix(".dlq-new")
				.build();
		
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(true)
				.argument("key1", "value1")
				.argument("x-dead-letter-exchange", "dead-letter-exchange").argument("x-dead-letter-routing-key", queueName+".dlq-new")
				.build();
		
		Queue queue = queueConfig.buildQueue(globalQueueConfig, deadLetterConfig);
		assertQueue(queue, expectedQueueConfig);
	}
	
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void createQueueWithDeadLetterAndNoDeadLetterConfig(){
		globalQueueConfig=QueueConfig.builder().deadLetterEnabled(true).build();
		
		queueConfig=QueueConfig.builder().name(queueName).build();
		
		deadLetterConfig=null;
		
		Queue queue = queueConfig.buildQueue(globalQueueConfig, deadLetterConfig);
		assertQueue(queue, expectedQueueConfig);
	}
	
	@Test
	public void createQueueWithNoDeadLetterAndNoDeadLetterConfig(){
		globalQueueConfig=QueueConfig.builder().deadLetterEnabled(false).build();
		
		queueConfig=QueueConfig.builder().name(queueName).build();
		
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName).durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(false)
				.build();
		
		deadLetterConfig=null;
		
		Queue queue = queueConfig.buildQueue(globalQueueConfig, deadLetterConfig);
		assertQueue(queue, expectedQueueConfig);
	}
	
	@Test
	public void createDeadLetterQueueWithDefaultGlobalConfigurationAppliedTest(){
		globalQueueConfig=QueueConfig.builder().deadLetterEnabled(true).build();
		queueConfig=QueueConfig.builder().name(queueName).build();
		deadLetterConfig=DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange").build())
				.queuePostfix(".dlq-new")
				.build();
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName+".dlq-new").durable(false).autoDelete(false).exclusive(false).arguments(new HashMap<>())
				.build();
		Queue queue = queueConfig.buildDeadLetterQueue(globalQueueConfig, deadLetterConfig);
		assertQueue(queue, expectedQueueConfig);
	}
	
	@Test
	public void createDeadLetterQueueWithDefaultGlobalConfigurationPreAppliedTest(){
		globalQueueConfig=QueueConfig.builder().deadLetterEnabled(true).build();
		queueConfig=QueueConfig.builder().name(queueName).build().applyGlobalConfig(globalQueueConfig);
		deadLetterConfig=DeadLetterConfig.builder().deadLetterExchange(ExchangeConfig.builder().name("dead-letter-exchange").build())
				.queuePostfix(".dlq-new")
				.build();
		expectedQueueConfig=QueueConfig.builder()
				.name(queueName+".dlq-new").durable(false).autoDelete(false).exclusive(false).deadLetterEnabled(true).arguments(new HashMap<>())
				.build();
		Queue queue = queueConfig.buildDeadLetterQueue(globalQueueConfig, deadLetterConfig);
		assertQueue(queue, expectedQueueConfig);
	}
	
	private void assertQueue(Queue queue, QueueConfig queueConfig){
		assertThat(queue.getName(),equalTo(queueConfig.getName()));
		assertThat(queue.isDurable(),equalTo(queueConfig.getDurable()));
		assertThat(queue.isAutoDelete(),equalTo(queueConfig.getAutoDelete()));
		assertThat(queue.isExclusive(),equalTo(queueConfig.getExclusive()));
		assertThat(queue.getArguments(),equalTo(queueConfig.getArguments()));
	}
		
}
