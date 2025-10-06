package io.github.daegwon.scheduled_transfer.kafka;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;
import io.github.daegwon.scheduled_transfer.dto.TransferMessage;
import io.github.daegwon.scheduled_transfer.service.CoreBankingService;
import io.github.daegwon.scheduled_transfer.service.ScheduledTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferConsumer {

    private final CoreBankingService coreBankingService;
    private final ScheduledTransferService scheduledTransferService;

    /**
     * Kafka에서 예약이체 메시지를 consume하여 코어뱅킹 서버로 전송
     *
     * @param transferMessage 예약이체를 위한 정보가 담긴 DTO
     */
    @KafkaListener(topics = "scheduled-transfer", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consumeTransferMessage(
            @Payload TransferMessage transferMessage,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) int offset,
            Acknowledgment ack
    ) {
        log.info("메시지 수신 - Transfer ID: {}, Partition: {}, Offset: {}",
                transferMessage.transferId(), partition, offset);

        ScheduledTransfer transfer = scheduledTransferService.getScheduledTransfer(transferMessage.transferId());

        try {
            // 이미 처리된 이체 건인지 확인
            if (transfer.getStatus() != TransferStatus.PROCESSING) {
                log.warn("이미 처리된 이체 건 - Transfer ID: {}", transfer.getId());
                ack.acknowledge();
                return;
            }

            // 코어뱅킹 서버로 이체 요청 및 결과 반환
            boolean success = coreBankingService.executeTransfer(
                    transferMessage.fromAccount(),
                    transferMessage.toAccount(),
                    transferMessage.amount()
            );

            if (success) {
                // 성공: PROCESSING -> COMPLETED
                transfer.setStatus(TransferStatus.COMPLETED);
                log.info("이체 성공 - Transfer ID: {}", transfer.getId());
            } else {
                // 실패: PROCESSING -> FAILED
                transfer.setStatus(TransferStatus.FAILED);
                log.error("이체 실패 - Transfer ID: {}", transfer.getId());
            }
        } catch (Exception e) {
            log.error("이체 처리 중 예외 발생 - Transfer ID: {}, Error: {}", transfer.getId(), e.getMessage(), e);

            // 예외 발생: PROCESSING -> FAILED
            transfer.setStatus(TransferStatus.FAILED);
        }

        // Kafka offset 커밋
        ack.acknowledge();
    }
}
