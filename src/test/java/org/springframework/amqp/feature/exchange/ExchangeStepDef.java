package org.springframework.amqp.feature.exchange;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.ExchangeConfigTestVo;
import org.springframework.amqp.config.ValidationResult;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.CustomExchange;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ExchangeStepDef {

	private List<ExchangeConfig> exchangeConfigs;

	private ExchangeConfig globalExchangeConfiguration;

	private Map<String, AbstractExchange> actualExchanges;
	
	private Map<String, ValidationResult> actualExchangeValidationResult;

	@Before
	public void setup() {
		exchangeConfigs = new ArrayList<>();
		actualExchanges = new HashMap<>();
		actualExchangeValidationResult = new HashMap<>();
	}

	@Given("^These are the exchange configurations available$")
	public void these_are_the_exchange_configurations_available(List<ExchangeConfigTestVo> exchangeConfigTestVos)
			throws Throwable {
		exchangeConfigTestVos.forEach(e -> this.exchangeConfigs.add(e.build()));
	}

	@Given("^There is no global exchange configuration available$")
	public void there_is_no_global_exchange_configuration_available() throws Throwable {
		globalExchangeConfiguration = ExchangeConfig.builder().build();
	}
	
	@Given("^Below is the global exchange configuration available$")
	public void belowIsTheGlobalExchangeConfigurationAvailable(List<ExchangeConfigTestVo> exchangeConfigTestVos) throws Throwable {
		globalExchangeConfiguration=exchangeConfigTestVos.get(0).build();
	}

	@When("^I build the exchanges$")
	public void i_build_the_exchanges() throws Throwable {
		for (ExchangeConfig exchangeConfig : this.exchangeConfigs) {
			AbstractExchange exchange = exchangeConfig.buildExchange(globalExchangeConfiguration);
			actualExchanges.put(exchange.getName(), exchange);
		}
	}

	@When("^I validate the exchanges$")
	public void i_validate_the_exchanges() throws Throwable {
		for (ExchangeConfig exchangeConfig : this.exchangeConfigs) {
			boolean valid = exchangeConfig.validate();
			actualExchangeValidationResult.put(exchangeConfig.getName(), new ValidationResult(exchangeConfig.getName(), valid));
		}
	}

	@Then("^These are the exchanges created in the system$")
	public void these_are_the_exchanges_created_in_the_system(List<CustomExchange> expectedExchangeTestVos)
			throws Throwable {
		for (CustomExchange expectedExchange : expectedExchangeTestVos) {
			assertAbstractExchange(actualExchanges.get(expectedExchange.getName()), expectedExchange);
		}
	}

	@Then("^These are the validation result for the exchanges$")
	public void these_are_the_validation_result_for_the_exchanges(List<ValidationResult> validationResults) throws Throwable {
		for(ValidationResult validationResult : validationResults) {
			assertThat(actualExchangeValidationResult.get(validationResult.getName()), equalTo(validationResult));
		}
	}



	private void assertAbstractExchange(AbstractExchange actualExchange, AbstractExchange expectedExchange) {
		assertThat(actualExchange.getName(), equalTo(expectedExchange.getName()));
		assertThat(actualExchange.getType(), equalTo(expectedExchange.getType()));
		assertThat(actualExchange.isDurable(), equalTo(expectedExchange.isDurable()));
		assertThat(actualExchange.isAutoDelete(), equalTo(expectedExchange.isAutoDelete()));
		assertThat(actualExchange.isDelayed(), equalTo(expectedExchange.isDelayed()));
		assertThat(actualExchange.isInternal(), equalTo(expectedExchange.isInternal()));
	}
}
