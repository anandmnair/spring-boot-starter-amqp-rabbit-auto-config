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

        if(getExchange()==null) {
            log.error("Invalid Exchange : Exchange must be provided for a requeue configuration");
            valid=false;
        }
        else {
            valid=getExchange().validate()?valid:false;
        }

        if(getQueue()==null) {
            log.error("Invalid Queue : Queue must be provided for a requeue configuration");
            valid=false;
        }
        else {
            valid=getQueue().validate()?valid:false;
        }

        if(autoRequeueEnabled && StringUtils.isEmpty(cron)) {
            log.error("Invalid Cron : Cron must be provided for auto requeue configuration");
            valid=false;
        }

        if(valid) {
            log.info("Requeue configuration validated successfully : '{}'", this);
        }

        return valid;
    }
}
