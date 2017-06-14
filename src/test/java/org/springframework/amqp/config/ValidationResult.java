package org.springframework.amqp.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidationResult {
	private String name;
	private boolean valid;
}
