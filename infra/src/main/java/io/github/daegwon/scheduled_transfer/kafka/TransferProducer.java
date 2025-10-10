package io.github.daegwon.scheduled_transfer.kafka;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.dto.TransferMessage;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Kafka에 예약이체 메시지를 발행
     *
     * @param transfer 발행할 예약이체 건
     */
    public void sendTransferMessage(ScheduledTransfer transfer) {
        // 엔티티 -> DTO
        TransferMessage transferMessage = TransferMessage.fromEntity(transfer);

        kafkaTemplate.send("scheduled-transfer", transferMessage.fromAccount(), transferMessage);
    }
}
