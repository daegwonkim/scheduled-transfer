package io.github.daegwon.scheduled_transfer.kafka;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;
import io.github.daegwon.scheduled_transfer.dto.TransferMessage;
import io.github.daegwon.scheduled_transfer.service.CoreBankingService;
import io.github.daegwon.scheduled_transfer.service.ScheduledTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

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
    public void consumeTransferMessage(@Payload TransferMessage transferMessage) {
        log.info("메시지 수신 - Transfer ID: {}", transferMessage.transferId());

        ScheduledTransfer transfer = scheduledTransferService.getScheduledTransfer(transferMessage.transferId());

        try {
            // 이미 처리된 이체 건인지 확인
            if (transfer.getStatus() != TransferStatus.PENDING) {
                log.error("이미 처리된 이체 건 - Transfer ID: {}", transfer.getId());
                return;
            }

            // 코어뱅킹 서버로 이체 요청 및 결과 반환
            boolean success = coreBankingService.executeTransfer(
                    transferMessage.fromAccount(),
                    transferMessage.toAccount(),
                    transferMessage.amount()
            );

            if (success) {
                // 성공: PENDING -> COMPLETED
                transfer.setStatus(TransferStatus.COMPLETED);
                log.info("이체 성공 - Transfer ID: {}", transfer.getId());
            } else {
                // 실패: PENDING -> FAILED
                transfer.setStatus(TransferStatus.FAILED);
                log.error("이체 실패 - Transfer ID: {}", transfer.getId());
            }
        } catch (Exception e) {
            log.error("이체 처리 중 예외 발생 - Transfer ID: {}, Error: {}", transfer.getId(), e.getMessage(), e);

            // 예외 발생: PENDING -> FAILED
            transfer.setStatus(TransferStatus.FAILED);
        }
    }
}
