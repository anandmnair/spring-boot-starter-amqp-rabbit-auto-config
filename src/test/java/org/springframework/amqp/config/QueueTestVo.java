package org.springframework.amqp.config;

import org.springframework.amqp.core.Queue;

import lombok.Data;

@Data
public class QueueTestVo {

	private String name;

	private boolean durable;

	private boolean exclusive;

	private boolean autoDelete;

	public Queue build() {
		return new Queue(getName(), isDurable(),isExclusive(), isAutoDelete());
	}
}
