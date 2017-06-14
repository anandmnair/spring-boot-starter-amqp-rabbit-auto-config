package org.springframework.amqp.feature.queue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.config.DeadLetterConfig;
import org.springframework.amqp.config.ExchangeConfig;
import org.springframework.amqp.config.ExchangeTypes;
import org.springframework.amqp.config.QueueConfig;
import org.springframework.amqp.config.QueueTestVo;
import org.springframework.amqp.config.ValidationResult;
import org.springframework.amqp.core.Queue;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class QueueStepDef {

	private List<QueueConfig> queueConfigs;

	private QueueConfig globalQueueConfiguration;
	
	private DeadLetterConfig deadLetterConfig;

	private Map<String, Queue> actualQueues;
	
	private Map<String, ValidationResult> actualQueueValidationResult;

	@Before
	public void setup() {
		queueConfigs = new ArrayList<>();
		actualQueues = new HashMap<>();
		actualQueueValidationResult = new HashMap<>();
	}

	@Given("^These are the queue configurations available$")
	public void these_are_the_queue_configurations_available(List<QueueConfig> queueConfigs)
			throws Throwable {
		 this.queueConfigs.addAll(queueConfigs);
	}

	@Given("^There is no global queue configuration available$")
	public void there_is_no_global_queue_configuration_available() throws Throwable {
		globalQueueConfiguration = QueueConfig.builder().build();
	}
	
	@Given("^Below is the global queue configuration available$")
	public void belowIsTheGlobalQueueConfigurationAvailable(List<QueueConfig> queueConfigs) throws Throwable {
		globalQueueConfiguration=queueConfigs.get(0);
	}
	
	@Given("^The dead letter exchange is \"([^\"]*)\" and dead letter queue postfix is \"([^\"]*)\"$")
	public void the_dead_letter_exchange_is_and_dead_letter_queue_postfix_is(String deadLetterExchangeName, String deadLetterQueuePostfix) throws Throwable {
		deadLetterConfig=DeadLetterConfig.builder()
				.deadLetterExchange(ExchangeConfig.builder().name(deadLetterExchangeName).type(ExchangeTypes.TOPIC).build())
				.build();
	}

	@When("^I build the queues$")
	public void i_build_the_queues() throws Throwable {
		for (QueueConfig queueConfig : this.queueConfigs) {
			Queue queue = queueConfig.buildQueue(globalQueueConfiguration, deadLetterConfig);
			actualQueues.put(queue.getName(), queue);
		}
	}

	@When("^I validate the queues$")
	public void i_validate_the_queues() throws Throwable {
		for (QueueConfig queueConfig : this.queueConfigs) {
			boolean valid = queueConfig.validate();
			actualQueueValidationResult.put(queueConfig.getName(), new ValidationResult(queueConfig.getName(), valid));
		}
	}

	@Then("^These are the queues created in the system$")
	public void these_are_the_queues_created_in_the_system(List<QueueTestVo> expectedQueueTestVos)
			throws Throwable {
		for (QueueTestVo expectedQueueTestVo : expectedQueueTestVos) {
			assertQueue(actualQueues.get(expectedQueueTestVo.getName()), expectedQueueTestVo.build());
		}
	}

	@Then("^These are the validation result for the queues$")
	public void these_are_the_validation_result_for_the_queues(List<ValidationResult> validationResults) throws Throwable {
		for(ValidationResult validationResult : validationResults) {
			assertThat(actualQueueValidationResult.get(validationResult.getName()), equalTo(validationResult));
		}
	}

	private void assertQueue(Queue actualQueue, Queue expectedQueue) {
		assertThat(actualQueue.getName(), equalTo(expectedQueue.getName()));
		assertThat(actualQueue.isDurable(), equalTo(expectedQueue.isDurable()));
		assertThat(actualQueue.isAutoDelete(), equalTo(expectedQueue.isAutoDelete()));
		assertThat(actualQueue.isExclusive(), equalTo(expectedQueue.isExclusive()));
	}
}
