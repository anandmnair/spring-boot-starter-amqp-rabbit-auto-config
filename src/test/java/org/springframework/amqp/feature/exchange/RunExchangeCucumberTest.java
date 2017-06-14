package org.springframework.amqp.feature.exchange;

import org.junit.Test;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features="classpath:feature/exchange.feature",strict=true)
public class RunExchangeCucumberTest {

	@Test
	public void test() {
		//dummy test
	}
}