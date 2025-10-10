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

    @KafkaListener(topics = "scheduled-transfer", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransferMessages(List<ConsumerRecord<String, TransferMessage>> records) {
        log.info("배치 메시지 수신 - 총 {}건", records.size());

        // 결과를 상태별로 분류
        List<Long> completedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (ConsumerRecord<String, TransferMessage> record : records) {
            TransferMessage transferMessage = record.value();

            try {
                processTransfer(transferMessage, completedIds, failedIds);
            } catch (Exception e) {
                log.error("메시지 처리 중 예외 발생 - Transfer ID: {}, Error: {}",
                        transferMessage.transferId(), e.getMessage(), e);
                failedIds.add(transferMessage.transferId());
            }
        }

        if (!completedIds.isEmpty()) {
            scheduledTransferService.updateStatusByIds(completedIds, TransferStatus.COMPLETED);
        }
        if (!failedIds.isEmpty()) {
            scheduledTransferService.updateStatusByIds(failedIds, TransferStatus.FAILED);
        }

        log.info("배치 처리 완료 - 성공: {}건, 실패: {}건", completedIds.size(), failedIds.size());
    }

    /**
     * 개별 이체 처리 로직
     */
    private void processTransfer(
            TransferMessage transferMessage,
            List<Long> completedIds,
            List<Long> failedIds
    ) {
        Long transferId = transferMessage.transferId();

        // 이체 건 조회 (상태 확인용)
        ScheduledTransfer transfer = scheduledTransferService.getScheduledTransfer(transferId);

        // 이미 처리된 이체 건인지 확인
        if (transfer.getStatus() != TransferStatus.PROCESSING) {
            log.warn("이미 처리된 이체 건 - Transfer ID: {}", transferId);
            return;
        }

        try {
            // 코어뱅킹 서버로 이체 요청
            boolean success = coreBankingService.executeTransfer(
                    transferMessage.fromAccount(),
                    transferMessage.toAccount(),
                    transferMessage.amount()
            );

            if (success) {
                completedIds.add(transferId);
                log.info("이체 성공 - Transfer ID: {}", transferId);
            } else {
                failedIds.add(transferId);
                log.error("이체 실패 - Transfer ID: {}", transferId);
            }
        } catch (Exception e) {
            log.error("이체 처리 중 예외 발생 - Transfer ID: {}, Error: {}",
                    transferId, e.getMessage(), e);
            failedIds.add(transferId);
        }
    }
}
