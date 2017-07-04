package org.springframework.amqp.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by anand on 04/07/17.
 */
public class ReQueueConfigTest {

    private ReQueueConfig reQueueConfig;

    private ExchangeConfig exchangeConfig;

    private QueueConfig queueConfig;

    @Before
    public void setUp() {
    	exchangeConfig=ExchangeConfig.builder().name("requeue-exchange").build();
    	queueConfig=QueueConfig.builder().name("requeue").build();
    }

    @Test
    public void validateWhenAutoConfigurationDisabledTest() {
    	reQueueConfig = ReQueueConfig.builder()
    			.exchange(exchangeConfig)
    			.queue(queueConfig)
    			.autoRequeueEnabled(false)
    			.build();
        Assert.assertTrue(reQueueConfig.validate());
    }

    @Test
    public void validateWhenAutoConfigurationEnabledTest() {
    	reQueueConfig = ReQueueConfig.builder()
    			.exchange(exchangeConfig)
    			.queue(queueConfig)
    			.autoRequeueEnabled(false)
    			.cron("* * * * *")
    			.build();
        Assert.assertTrue(reQueueConfig.validate());
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
        Assert.assertTrue(reQueueConfig.validate());
    }
}
