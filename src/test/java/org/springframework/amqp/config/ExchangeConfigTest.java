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
import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.ExchangeTypes;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.util.CollectionUtils;

public class ExchangeConfigTest {

	private ExchangeConfig exchangeConfig;
	
	private ExchangeConfig expectedExchangeConfig;

	private ExchangeConfig globalExchangeConfig;
	
	private String exchangeName= "exchange-1";
	
	@Rule
	public OutputCapture outputCapture = new OutputCapture();
	
	@Before
	public void setup(){
		exchangeConfig=ExchangeConfig.builder().build();
		expectedExchangeConfig=ExchangeConfig.builder().type(ExchangeTypes.TOPIC).durable(false).autoDelete(false).delayed(false).internal(false).build();
		globalExchangeConfig=ExchangeConfig.builder().build();
	}
	
	@Test
	public void exchangeConfigEqualsTest() {
		exchangeConfig=ExchangeConfig.builder().build();
		expectedExchangeConfig=ExchangeConfig.builder().build();
		assertTrue(exchangeConfig.equals(expectedExchangeConfig));
		exchangeConfig.setGlobalConfigApplied(false);
		expectedExchangeConfig.setGlobalConfigApplied(true);
		assertTrue(exchangeConfig.equals(expectedExchangeConfig));
	}
	
	@Test
	public void exchangeConfigeHashcodeTest() {
		exchangeConfig=ExchangeConfig.builder().build();
		expectedExchangeConfig=ExchangeConfig.builder().build();
		assertThat(exchangeConfig.hashCode(),equalTo(expectedExchangeConfig.hashCode()));
		exchangeConfig.setGlobalConfigApplied(false);
		expectedExchangeConfig.setGlobalConfigApplied(true);
		assertThat(exchangeConfig.hashCode(),equalTo(expectedExchangeConfig.hashCode()));
	}
	
	@Test
	public void defaultExchangeConfigWithoutGlobalConfigurationAppliedTest() {
		exchangeConfig=ExchangeConfig.builder().build();
		assertNull(exchangeConfig.getName());
		assertNull(exchangeConfig.getType());
		assertNull(exchangeConfig.getDurable());
		assertNull(exchangeConfig.getAutoDelete());
		assertNull(exchangeConfig.getDelayed());
		assertNull(exchangeConfig.getInternal());
		assertFalse(exchangeConfig.isGlobalConfigApplied());
		assertNotNull(exchangeConfig.getArguments());
		assertTrue(CollectionUtils.isEmpty(exchangeConfig.getArguments()));	
	}
	
	@Test
	public void defaultExchangeConfigWithDefaultGlobalConfigurationAppliedTest() {
		exchangeConfig=ExchangeConfig.builder().build().applyGlobalConfig(globalExchangeConfig);
		expectedExchangeConfig=ExchangeConfig.builder().type(ExchangeTypes.TOPIC).durable(false).autoDelete(false).delayed(false).internal(false).build();
		assertNull(exchangeConfig.getName());
		assertThat(exchangeConfig.getType(), equalTo(ExchangeTypes.TOPIC));
		assertFalse(exchangeConfig.getDurable());
		assertFalse(exchangeConfig.getAutoDelete());
		assertFalse(exchangeConfig.getDelayed());
		assertFalse(exchangeConfig.getInternal());
		assertNotNull(exchangeConfig.getArguments());
		assertTrue(exchangeConfig.isGlobalConfigApplied());
		assertTrue(CollectionUtils.isEmpty(exchangeConfig.getArguments()));
		assertThat(exchangeConfig, equalTo(expectedExchangeConfig));
	}
	
	@Test
	public void exchangeConfigWithNameAndValidationSuccessTest() {
		exchangeConfig=ExchangeConfig.builder().name(exchangeName).build();
		assertTrue(exchangeConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Exchange configuration validated successfully for exchange '%s'",exchangeName)));
	}
	
	@Test
	public void exchangeConfigWithoutNameAndValidationFailTest() {
		exchangeConfig=ExchangeConfig.builder().build();
		assertFalse(exchangeConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Exchange Configuration : Name must be provided for an exchange")));
	}
	
	@Test
	public void exchangeConfigWithoutNameAndGlobalConfigurationWithNameAndValidationFailTest() {
		globalExchangeConfig=ExchangeConfig.builder().name(exchangeName).build();
		exchangeConfig=ExchangeConfig.builder().build().applyGlobalConfig(globalExchangeConfig);
		assertFalse(exchangeConfig.validate());
		assertThat(outputCapture.toString(),containsString(String.format("Invalid Exchange Configuration : Name must be provided for an exchange")));
	}
	
	@Test
	public void exchangeConfigWithOnlyExchangeNameAndDefaultGlobalConfigurationAppliedTest() {
		exchangeConfig=ExchangeConfig.builder().name(exchangeName).build().applyGlobalConfig(globalExchangeConfig);
		expectedExchangeConfig=ExchangeConfig.builder().name(exchangeName).type(ExchangeTypes.TOPIC).durable(false).autoDelete(false).delayed(false).internal(false).build();
		assertThat(exchangeConfig, equalTo(expectedExchangeConfig));
	}
	
	@Test
	public void exchangeConfigWithOnlyExchangeNameAndNoGlobalConfigurationAppliedTest() {
		exchangeConfig=ExchangeConfig.builder().name(exchangeName).build();
		expectedExchangeConfig=ExchangeConfig.builder().name(exchangeName).type(null).durable(null).autoDelete(null).delayed(null).internal(null).build();
		assertThat(exchangeConfig, equalTo(expectedExchangeConfig));
	}
	
	@Test
	public void exchangeConfigWithOnlyExchangeNameAndFewGlobalConfigurationTest() { 
		globalExchangeConfig=ExchangeConfig.builder()
				.type(ExchangeTypes.HEADERS).durable(true).autoDelete(true).delayed(true).internal(true).argument("key1", "value1")
				.build();
		
		exchangeConfig=ExchangeConfig.builder()
				.name(exchangeName)
				.build()
				.applyGlobalConfig(globalExchangeConfig);
		
		expectedExchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.HEADERS).durable(true).autoDelete(true).delayed(true).internal(true)
				.argument("key1", "value1")
				.build();

		assertThat(exchangeConfig, equalTo(expectedExchangeConfig));
	}
	
	@Test
	public void exchangeConfigByOverriddingFromGlobalConfigurationTest() { 
		globalExchangeConfig=ExchangeConfig.builder()
				.type(ExchangeTypes.HEADERS).durable(true).autoDelete(true).delayed(true).internal(true).argument("key1", "value1")
				.build();
		
		exchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.FANOUT).durable(false).autoDelete(false)
				.argument("key1", "NEW_VALUE").argument("key2", "value2")
				.build()
				.applyGlobalConfig(globalExchangeConfig);
		
		expectedExchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.FANOUT).durable(false).autoDelete(false).delayed(true).internal(true)
				.argument("key1", "NEW_VALUE").argument("key2", "value2")
				.build();

		assertThat(exchangeConfig, equalTo(expectedExchangeConfig));
	}
	
	@Test
	public void createExchangeWithDefaultGlobalConfigurationAppliedTest(){
		globalExchangeConfig=ExchangeConfig.builder().build();
		exchangeConfig=ExchangeConfig.builder().name(exchangeName).build();
		expectedExchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.TOPIC).durable(false).autoDelete(false).delayed(false).internal(false).arguments(new HashMap<>())
				.build();
		AbstractExchange exchange = exchangeConfig.buildExchange(globalExchangeConfig);
		assertExchange(exchange, expectedExchangeConfig);
	}
	
	@Test
	public void createExchangeWithDefaultGlobalConfigurationPreAppliedTest(){
		globalExchangeConfig=ExchangeConfig.builder().build();
		exchangeConfig=ExchangeConfig.builder().name(exchangeName).build().applyGlobalConfig(globalExchangeConfig);
		expectedExchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.TOPIC).durable(false).autoDelete(false).delayed(false).internal(false).arguments(new HashMap<>())
				.build();
		AbstractExchange exchange = exchangeConfig.buildExchange(globalExchangeConfig);
		assertExchange(exchange, expectedExchangeConfig);
	}
	
	@Test
	public void createExchangeWithFewGlobalConfigurationTest(){
		globalExchangeConfig=ExchangeConfig.builder()
				.type(ExchangeTypes.HEADERS).durable(true).autoDelete(true).delayed(true).internal(true).argument("key1", "value1")
				.build();
		
		exchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.FANOUT).durable(false).autoDelete(false)
				.argument("key1", "NEW_VALUE").argument("key2", "value2")
				.build()
				.applyGlobalConfig(globalExchangeConfig);
		
		expectedExchangeConfig=ExchangeConfig.builder()
				.name(exchangeName).type(ExchangeTypes.FANOUT).durable(false).autoDelete(false).delayed(true).internal(true)
				.argument("key1", "NEW_VALUE").argument("key2", "value2")
				.build();
		
		AbstractExchange exchange = exchangeConfig.buildExchange(globalExchangeConfig);
		assertExchange(exchange, expectedExchangeConfig);
	}
	
	private void assertExchange(AbstractExchange exchange, ExchangeConfig exchangeConfig){
		assertThat(exchange.getName(),equalTo(exchangeConfig.getName()));
		assertThat(exchange.getType(),equalTo(exchangeConfig.getType().getValue()));
		assertThat(exchange.isDurable(),equalTo(exchangeConfig.getDurable()));
		assertThat(exchange.isAutoDelete(),equalTo(exchangeConfig.getAutoDelete()));
		assertThat(exchange.isDelayed(),equalTo(exchangeConfig.getDelayed()));
		assertThat(exchange.isInternal(),equalTo(exchangeConfig.getInternal()));
		assertThat(exchange.getArguments(),equalTo(exchangeConfig.getArguments()));
	}
	
}
