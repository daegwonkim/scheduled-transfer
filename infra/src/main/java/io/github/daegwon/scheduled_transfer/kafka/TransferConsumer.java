package io.github.daegwon.scheduled_transfer.kafka;

import io.github.daegwon.scheduled_transfer.service.CoreBankingService;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.TransferStatus;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.dto.TransferMessage;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.service.ScheduledTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferConsumer {

    private final CoreBankingService coreBankingService;
    private final ScheduledTransferService scheduledTransferService;

    /**
     * Kafka에서 예약이체 메시지를 배치로 consume하여 코어뱅킹 서버로 전송
     *
     * @param records 예약이체 메시지 배치
     */
    @KafkaListener(topics = "scheduled-transfer", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransferMessages(List<ConsumerRecord<String, TransferMessage>> records) {
        log.info("배치 메시지 수신 - 총 {}건", records.size());

        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (ConsumerRecord<String, TransferMessage> record : records) {
            TransferMessage transferMessage = record.value();
            int partition = record.partition();
            long offset = record.offset();

            log.debug("메시지 처리 중 - Transfer ID: {}, Partition: {}, Offset: {}",
                    transferMessage.transferId(), partition, offset);

            try {
                processTransfer(transferMessage, successIds, failedIds);
            } catch (Exception e) {
                log.error("메시지 처리 중 예외 발생 - Transfer ID: {}, Error: {}",
                        transferMessage.transferId(), e.getMessage(), e);
                failedIds.add(transferMessage.transferId());
            }
        }

        // 배치 결과 로깅 및 ACK
        log.info("배치 처리 완료 - 성공: {}건, 실패: {}건", successIds.size(), failedIds.size());
    }

    /**
     * 개별 이체 처리 로직
     */
    @Transactional
    private void processTransfer(
            TransferMessage transferMessage,
            List<Long> successIds,
            List<Long> failedIds
    ) {
        ScheduledTransfer transfer = scheduledTransferService.getScheduledTransfer(
                transferMessage.transferId()
        );

        // 이미 처리된 이체 건인지 확인
        if (transfer.getStatus() != TransferStatus.PROCESSING) {
            log.warn("이미 처리된 이체 건 - Transfer ID: {}", transfer.getId());
            return;
        }

        try {
            // 코어뱅킹 서버로 이체 요청 및 결과 반환
            boolean success = coreBankingService.executeTransfer(
                    transferMessage.fromAccount(),
                    transferMessage.toAccount(),
                    transferMessage.amount()
            );

            if (success) {
                // 성공: PROCESSING -> COMPLETED
                transfer.setStatus(TransferStatus.COMPLETED);
                scheduledTransferService.save(transfer);
                successIds.add(transfer.getId());

                log.info("이체 성공 - Transfer ID: {}", transfer.getId());
            } else {
                // 실패: PROCESSING -> FAILED
                transfer.setStatus(TransferStatus.FAILED);
                scheduledTransferService.save(transfer);
                failedIds.add(transfer.getId());

                log.error("이체 실패 - Transfer ID: {}", transfer.getId());
            }
        } catch (Exception e) {
            log.error("이체 처리 중 예외 발생 - Transfer ID: {}, Error: {}",
                    transfer.getId(), e.getMessage(), e);

            // 예외 발생: PROCESSING -> FAILED
            transfer.setStatus(TransferStatus.FAILED);
            scheduledTransferService.save(transfer);
            failedIds.add(transfer.getId());
        }
    }
}
