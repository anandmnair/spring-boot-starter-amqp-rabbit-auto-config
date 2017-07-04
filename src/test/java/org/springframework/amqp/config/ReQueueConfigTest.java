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
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .build();
        exchangeConfig = ExchangeConfig.builder().name("requeue-exchange").build();
        queueConfig = QueueConfig.builder().name("requeue").build();
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
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .autoRequeueEnabled(false)
                .build();
        assertTrue(reQueueConfig.validate());
    }

    @Test
    public void validateWhenAutoConfigurationEnabledTest() {
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .autoRequeueEnabled(false)
                .cron("* * * * *")
                .build();
        assertTrue(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenExchangeIsNullTest() {
        reQueueConfig = ReQueueConfig.builder()
                .exchange(null)
                .queue(queueConfig)
                .autoRequeueEnabled(false)
                .build();
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenExchangeIsInvalidTest() {
        exchangeConfig.setName(null);
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .build();
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenQueueIsNullTest() {
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(null)
                .autoRequeueEnabled(false)
                .build();
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenQueueIsInvalidTest() {
        queueConfig.setName(null);
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .autoRequeueEnabled(false)
                .build();
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void invalidWhenAutoDeleteEnabledAndCronIsNullTest() {
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(null)
                .autoRequeueEnabled(true)
                .cron(null)
                .build();
        Assert.assertFalse(reQueueConfig.validate());
    }

    @Test
    public void validWhenAutoDeleteDisabledAndCronIsNullTest() {
        reQueueConfig = ReQueueConfig.builder()
                .exchange(exchangeConfig)
                .queue(queueConfig)
                .autoRequeueEnabled(false)
                .cron(null)
                .build();
        assertTrue(reQueueConfig.validate());
    }
}
