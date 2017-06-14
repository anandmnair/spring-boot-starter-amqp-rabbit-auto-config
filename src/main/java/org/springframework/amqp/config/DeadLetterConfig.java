package org.springframework.amqp.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Slf4j
public class DeadLetterConfig extends AbstractConfig {
	
	private static final String DEFAULT_DEAD_LETTER_QUEUE_POSTFIX = ".DLQ";

	private ExchangeConfig deadLetterExchange;
	
	private String queuePostfix = DEFAULT_DEAD_LETTER_QUEUE_POSTFIX;

	public String createDeadLetterQueueName(String queueName) {
		return new StringBuilder()
				.append(queueName)
				.append(getDefaultConfig("DeadLetterConfig", "queuePostfix", queuePostfix, null, DEFAULT_DEAD_LETTER_QUEUE_POSTFIX)).toString();
	}	
	
	@Override
	public boolean validate() {
		log.info("Validating DeadLetterConfig...");
		if (deadLetterExchange!=null && deadLetterExchange.validate()) {
			log.info("DeadLetterConfig configuration validated successfully for deadLetterExchange '{}'", deadLetterExchange);
			return true;
		}
		log.error("Invalid DeadLetterConfig Configuration : Valid DeadLetterExchange must be provided for DeadLetterConfig");
		return false;
	}

}