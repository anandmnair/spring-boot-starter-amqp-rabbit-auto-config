package org.springframework.amqp.processor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by anand on 02/07/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InfoHeaderMessagePostProcessorTest {

    @Autowired
    private InfoHeaderMessagePostProcessor infoHeaderMessagePostProcessor;

    private Message message;

    @Autowired
    private Environment environment;

    @Before
    public void setUp() {
        infoHeaderMessagePostProcessor.getHeaders().put("info-key", "info-value");
        infoHeaderMessagePostProcessor.setEnvironment(environment);
        message=MessageBuilder.withBody("DummyMessage".getBytes()).build();
    }

    @Test
    public void addInfoHeadersToMessage() {
        infoHeaderMessagePostProcessor.postProcessMessage(message);
        assertThat(message.getMessageProperties().getHeaders().get("info-key"),
                is(equalTo("info-value")));
        assertThat(message.getMessageProperties().getHeaders().get("spring-application-name"),
                is(equalTo(environment.getProperty("spring.application.name", String.class))));
    }

    @Test
    public void addOnlyApplicationNameHeaderToMessage() {
        infoHeaderMessagePostProcessor.getHeaders().clear();
        infoHeaderMessagePostProcessor.postProcessMessage(message);
        assertThat(message.getMessageProperties().getHeaders().get("spring-application-name"),
                is(equalTo(environment.getProperty("spring.application.name", String.class))));
    }

}
