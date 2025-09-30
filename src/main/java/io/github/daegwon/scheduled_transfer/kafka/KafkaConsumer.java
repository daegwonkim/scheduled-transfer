package io.github.daegwon.scheduled_transfer.kafka;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "scheduled_transfer", groupId = "group_1")
    public void consume(ScheduledTransfer data) {
        System.out.println("Received data: " + data.getFromAccountNumber() + " > " + data.getToAccountNumber());
    }
}
