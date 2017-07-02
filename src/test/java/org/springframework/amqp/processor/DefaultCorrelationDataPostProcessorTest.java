package org.springframework.amqp.processor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by anand on 02/07/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultCorrelationDataPostProcessorTest {

    @Autowired
    private DefaultCorrelationDataPostProcessor defaultCorrelationDataPostProcessor;

    private Message message;

    @Before
    public void setUp() {
        message=MessageBuilder.withBody("DummyMessage".getBytes()).build();
    }

    @Test
    public void postProcessWithNoCorrelationDataTest() {
        CorrelationData correlationData = defaultCorrelationDataPostProcessor.postProcess(message, null);
        assertNotNull(message.getMessageProperties().getHeaders().get("correlation-id"));
        assertNotNull(correlationData);
        assertNotNull(correlationData.getId());
        assertThat(message.getMessageProperties().getHeaders().get("correlation-id"),
                is(correlationData.getId()));
    }

    @Test
    public void postProcessWithCorrelationDataTest() {
        CorrelationData inputCorrelationData = new CorrelationData("my-correlation-id");
        CorrelationData correlationData = defaultCorrelationDataPostProcessor.postProcess(message, inputCorrelationData);
        assertNotNull(message.getMessageProperties().getHeaders().get("correlation-id"));
        assertNotNull(correlationData);
        assertNotNull(correlationData.getId());
        assertThat(message.getMessageProperties().getHeaders().get("correlation-id"),
                is(inputCorrelationData.getId()));
        assertThat(correlationData.getId(),
                is(inputCorrelationData.getId()));
    }



}
