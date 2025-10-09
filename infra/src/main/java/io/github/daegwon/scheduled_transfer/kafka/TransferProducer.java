package io.github.daegwon.scheduled_transfer.kafka;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.dto.ScheduledTransferEvent;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.dto.TransferMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransferProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Kafka에 예약이체 메시지를 발행
     *
     * @param event 발행할 예약이체 건
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendTransferMessage(ScheduledTransferEvent event) {
        // 엔티티 -> DTO
        TransferMessage transferMessage = TransferMessage.fromEntity(event.transfer());

        kafkaTemplate.send("scheduled-transfer", transferMessage.fromAccount(), transferMessage);
    }
}
