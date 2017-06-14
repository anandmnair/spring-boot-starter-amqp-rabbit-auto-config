package org.springframework.amqp;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication
public class SpringBootStarterAmqpRabbitAutoConfigApplicationTests {

	@Autowired
	private ConfigurableApplicationContext applicationContext;
	
	@Test
	public void contextLoads() {
		assertTrue(applicationContext.containsBean("exchange-one"));
		assertTrue(applicationContext.containsBean("exchange-two"));
		assertTrue(applicationContext.containsBean("queue-one"));
		assertTrue(applicationContext.containsBean("queue-two"));
		assertTrue(applicationContext.containsBean("binding-one"));
		assertTrue(applicationContext.containsBean("binding-two"));
		assertTrue(applicationContext.containsBean("global-dead-letter-exchange.dlx"));
		assertTrue(applicationContext.containsBean("queue-one.dlq"));
		assertTrue(applicationContext.containsBean("queue-two.dlq"));
	}
	
}
