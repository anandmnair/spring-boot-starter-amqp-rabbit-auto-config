package org.springframework.amqp.processor;

import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by anand on 02/07/17.
 */
public class CorrelationPostProcessorTest {

    private CorrelationPostProcessor correlationPostProcessor;

    private Message message;

    @Before
    public void setUp() {
        correlationPostProcessor=new CorrelationPostProcessor();
        message=MessageBuilder.withBody("DummyMessage".getBytes()).build();
    }

    @Test
    public void addNewCorrelationIdToHeaderIfMissingTest() {
        correlationPostProcessor.postProcessMessage(message);
        assertNotNull(message.getMessageProperties().getHeaders().get("correlation-id"));
    }

    @Test
    public void addExistingCorrelationIdToHeaderIfPresentTest() {
        message.getMessageProperties().setCorrelationIdString("ExistingCorrelationId");
        correlationPostProcessor.postProcessMessage(message);
        assertNotNull(message.getMessageProperties().getHeaders().get("correlation-id"));
        assertThat(message.getMessageProperties().getHeaders().get("correlation-id"), is(equalTo("ExistingCorrelationId")));
    }

}
