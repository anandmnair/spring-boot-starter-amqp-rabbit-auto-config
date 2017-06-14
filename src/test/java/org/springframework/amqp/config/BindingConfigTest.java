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
import org.springframework.amqp.config.BindingConfig;
import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.ExchangeTypes;
import org.springframework.amqp.config.QueueConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.exception.AmqpAutoConfigurationException;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.CollectionUtils;

public class BindingConfigTest {

	private BindingConfig bindingConfig;
	
	private BindingConfig expectedBindingConfig;

	private String exchaneg= "exchaneg";
	
	private String queue= "queue";

	private String routingKey= "routingKey";
	
	@Rule
	public OutputCapture outputCapture = new OutputCapture();
	
	@Before
	public void setup(){
		bindingConfig=BindingConfig.builder().build();
		expectedBindingConfig=BindingConfig.builder().exchange(null).queue(null).routingKey(null).arguments(new HashMap<>()).build();
	}
	
	@Test
	public void bindingConfigEqualsTest() {
		bindingConfig=BindingConfig.builder().build();
		expectedBindingConfig=BindingConfig.builder().build();
		assertTrue(bindingConfig.equals(expectedBindingConfig));
		bindingConfig.setGlobalConfigApplied(false);
		expectedBindingConfig.setGlobalConfigApplied(true);
		assertTrue(bindingConfig.equals(expectedBindingConfig));
	}
	
	@Test
	public void bindingConfigeHashcodeTest() {
		bindingConfig=BindingConfig.builder().build();
		expectedBindingConfig=BindingConfig.builder().build();
		assertThat(bindingConfig.hashCode(),equalTo(expectedBindingConfig.hashCode()));
		bindingConfig.setGlobalConfigApplied(false);
		expectedBindingConfig.setGlobalConfigApplied(true);
		assertThat(bindingConfig.hashCode(),equalTo(expectedBindingConfig.hashCode()));
	}
	
	@Test
	public void defaultBindingConfigTest() {
		bindingConfig=BindingConfig.builder().build();
		assertNull(bindingConfig.getExchange());
		assertNull(bindingConfig.getQueue());
		assertNull(bindingConfig.getRoutingKey());
		assertFalse(bindingConfig.isGlobalConfigApplied());
		assertNotNull(bindingConfig.getArguments());
		assertTrue(CollectionUtils.isEmpty(bindingConfig.getArguments()));	
	}
	
	@Test
	public void bindingConfigValidationSuccessTest() {
		bindingConfig=BindingConfig.builder().exchange(exchaneg).queue(queue).routingKey(routingKey).build();
		assertTrue(bindingConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Binding configuration validated successfully for Binding '%s'", bindingConfig)));
	}
	
	@Test
	public void bindingConfigWithoutExchnageAndValidationFailTest() {
		bindingConfig=BindingConfig.builder().exchange(null).queue(queue).routingKey(routingKey).build();
		assertFalse(bindingConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Exchange : Exchange must be provided for a binding")));
	}
	
	@Test
	public void bindingConfigWithoutQueueAndValidationFailTest() {
		bindingConfig=BindingConfig.builder().exchange(exchaneg).queue(null).routingKey(routingKey).build();
		assertFalse(bindingConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Queue : Queue must be provided for a binding")));
	}
	
	@Test
	public void bindingConfigNoExchangeAndNoQueueAndValidationFailTest() {
		bindingConfig=BindingConfig.builder().exchange(null).queue(null).routingKey(routingKey).build();
		assertFalse(bindingConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Exchange : Exchange must be provided for a binding")));
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Queue : Queue must be provided for a binding")));
	}
	
	
	@Test
	public void createBindingWithExchangeAndQueueTest(){
		Exchange bindingExchange = ExchangeConfig.builder().name(exchaneg).type(ExchangeTypes.DIRECT).build().buildExchange(ExchangeConfig.builder().build());
		Queue bindingQueue = QueueConfig.builder().name(queue).build().buildQueue(QueueConfig.builder().build(),null);

		Binding binding = BindingConfig.builder().exchange(exchaneg).queue(queue).routingKey(routingKey).build().bind(bindingExchange, bindingQueue);
		
		expectedBindingConfig=BindingConfig.builder()
				.exchange(exchaneg).queue(queue).routingKey(routingKey).arguments(new HashMap<>())
				.build();
		assertBinding(binding, expectedBindingConfig);
	}
	
	@Test
	public void createBindingWithHeaderExchangeAndArgumentsTest(){
		Exchange bindingExchange = ExchangeConfig.builder().name(exchaneg).type(ExchangeTypes.HEADERS).build().buildExchange(ExchangeConfig.builder().build());
		Queue bindingQueue = QueueConfig.builder().name(queue).build().buildQueue(QueueConfig.builder().build(),null);
		BindingConfig bindingConfig = BindingConfig.builder().exchange(exchaneg).queue(queue).routingKey(routingKey).argument("key", "value").build();
		bindingConfig.bind(bindingExchange, bindingQueue);
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void createBindingWithHeaderExchangeAndNoArgumentsTest(){
		Exchange bindingExchange = ExchangeConfig.builder().name(exchaneg).type(ExchangeTypes.HEADERS).build().buildExchange(ExchangeConfig.builder().build());
		Queue bindingQueue = QueueConfig.builder().name(queue).build().buildQueue(QueueConfig.builder().build(),null);
		BindingConfig bindingConfig = BindingConfig.builder().exchange(exchaneg).queue(queue).routingKey(routingKey).build();
		bindingConfig.bind(bindingExchange, bindingQueue);
	}
	
	@Test
	public void createBindingWithNonHeaderExchangeAndRoutingKeyTest(){
		Exchange bindingExchange = ExchangeConfig.builder().name(exchaneg).type(ExchangeTypes.DIRECT).build().buildExchange(ExchangeConfig.builder().build());
		Queue bindingQueue = QueueConfig.builder().name(queue).build().buildQueue(QueueConfig.builder().build(),null);
		BindingConfig bindingConfig = BindingConfig.builder().exchange(exchaneg).queue(queue).routingKey(routingKey).build();
		bindingConfig.bind(bindingExchange, bindingQueue);
	}
	
	@Test(expected=AmqpAutoConfigurationException.class)
	public void createBindingWithNonHeaderExchangeAndNoRoutingKeyTest(){
		Exchange bindingExchange = ExchangeConfig.builder().name(exchaneg).type(ExchangeTypes.DIRECT).build().buildExchange(ExchangeConfig.builder().build());
		Queue bindingQueue = QueueConfig.builder().name(queue).build().buildQueue(QueueConfig.builder().build(),null);
		BindingConfig bindingConfig = BindingConfig.builder().exchange(exchaneg).queue(queue).routingKey(null).build();
		bindingConfig.bind(bindingExchange, bindingQueue);
	}
	
	private void assertBinding(Binding binding, BindingConfig bindingConfig){
		assertThat(binding.getExchange(),equalTo(bindingConfig.getExchange()));
		assertThat(binding.getDestination(),equalTo(bindingConfig.getQueue()));
		assertThat(binding.getDestinationType(),equalTo(DestinationType.QUEUE));
		assertThat(binding.getArguments(),equalTo(bindingConfig.getArguments()));
	}
	
}
