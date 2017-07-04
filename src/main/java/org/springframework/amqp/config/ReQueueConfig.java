package org.springframework.amqp.config;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Created by anand on 04/07/17.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class ReQueueConfig extends AbstractConfig {

    private boolean enabled;

    private ExchangeConfig exchange;

    private QueueConfig queue;

    private String cron;

    private boolean autoRequeueEnabled;

    public boolean validate() {

        boolean valid=true;

        valid=validate("exchange", exchange, valid);

        valid=validate("queue", queue, valid);

        if(autoRequeueEnabled && StringUtils.isEmpty(cron)) {
            log.error("Invalid Cron : Cron must be provided for auto requeue configuration");
            valid=false;
        }

        if(valid) {
            log.info("Requeue configuration validated successfully : '{}'", this);
        }

        return valid;
    }

    private boolean validate(String key, AbstractConfig abstractConfig, boolean valid) {
        if(abstractConfig==null) {
            log.error("Invalid {} : {} must be provided for a requeue configuration", key, key);
            valid=false;
        }
        else {
            valid=abstractConfig.validate()?valid:false;
        }
        return valid;
    }
}
