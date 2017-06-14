package org.springframework.amqp.feature.queue;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features="classpath:feature/queue.feature",strict=true)
public class RunQueueCucumberTest {

}