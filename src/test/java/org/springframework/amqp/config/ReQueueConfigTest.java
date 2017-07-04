package org.springframework.amqp.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by anand on 04/07/17.
 */
public class ReQueueConfigTest {

    private ReQueueConfig reQueueConfig;

    private ExchangeConfig exchangeConfig;

    private QueueConfig queueConfig;

    @Before
    public void setUp() {
        exchangeConfig = ExchangeConfig.builder().name("requeue-exchange").build();
        queueConfig = QueueConfig.builder().name("requeue").build();
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .routingKey("requeue.key")
                .build();
    }

    @Test
    public void reQueueConfigEqualsTest() {
        reQueueConfig = new ReQueueConfig();
        reQueueConfig.setGlobalConfigApplied(false);
        ReQueueConfig expectedReQueueConfig = new ReQueueConfig();
        expectedReQueueConfig.setGlobalConfigApplied(true);
        assertTrue(reQueueConfig.equals(expectedReQueueConfig));
    }

    @Test
    public void validateWhenAutoConfigurationDisabledTest() {
        reQueueConfig.setAutoRequeueEnabled(false);
        assertTrue(reQueueConfig.validate());
    }

    @Test
    public void validateWhenAutoConfigurationEnabledTest() {
        reQueueConfig.setAutoRequeueEnabled(true);
        reQueueConfig.setCron("* * * * *");
        assertTrue(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenExchangeIsNullTest() {
        reQueueConfig.setExchange(null);
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenExchangeIsInvalidTest() {
        exchangeConfig.setName(null);
        reQueueConfig.setExchange(exchangeConfig);
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenQueueIsNullTest() {
        reQueueConfig.setQueue(null);
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenQueueIsInvalidTest() {
        queueConfig.setName(null);
        reQueueConfig.setQueue(queueConfig);
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenRoutingKeyIsNullTest() {
        reQueueConfig.setRoutingKey(null);
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenAutoDeleteEnabledAndCronIsNullTest() {
        reQueueConfig.setAutoRequeueEnabled(true);
        reQueueConfig.setCron(null);
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void validWhenAutoDeleteDisabledAndCronIsNullTest() {
        reQueueConfig.setAutoRequeueEnabled(false);
        reQueueConfig.setCron(null);
        assertTrue(reQueueConfig.validate());
    }
}
