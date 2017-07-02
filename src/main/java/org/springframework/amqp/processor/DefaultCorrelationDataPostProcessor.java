package org.springframework.amqp.processor;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.CorrelationDataPostProcessor;
import org.springframework.amqp.rabbit.support.CorrelationData;

public class DefaultCorrelationDataPostProcessor implements CorrelationDataPostProcessor {

	private CorrelationPostProcessor correlationPostProcessor;

	public DefaultCorrelationDataPostProcessor(CorrelationPostProcessor correlationPostProcessor) {
		this.correlationPostProcessor=correlationPostProcessor;
	}

	@Override
	public CorrelationData postProcess(final Message message, CorrelationData correlationData) {
		CorrelationData resultCorrelationData = correlationData==null?new CorrelationData():correlationData;
		MessageProperties messageProperties = message.getMessageProperties();
		if(correlationData!=null && correlationData.getId()!=null) {
			messageProperties.setCorrelationIdString(correlationData.getId());
		}
		correlationPostProcessor.postProcessMessage(message);
		resultCorrelationData.setId(messageProperties.getCorrelationIdString());
		return resultCorrelationData;
	}

}
