package io.github.daegwon.scheduled_transfer.scheduler;

import io.github.daegwon.scheduled_transfer.kafka.TransferProducer;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.TransferStatus;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.service.ScheduledTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTransferScheduler {

    private final ScheduledTransferService scheduledTransferService;
    private final TransferProducer producer;

    /**
     * 5분마다 송금가능일시가 도래한 예약이체 건들을 조회하여 Kafka로 발행
     */
    @Scheduled(cron = "${scheduling.cron}")
    @Transactional
    public void produceOverdueScheduledTransfer() {
        log.info("=== 예약이체 스케줄러 시작 ===");

        // PENDING 상태이면서 송금시간이 도래한 건들 조회
        List<ScheduledTransfer> transfers = scheduledTransferService.getOverduePendingTransfer();

        log.info("처리 대상 예약이체 건수: {}", transfers.size());

        List<Long> transferIds = transfers.stream()
                .map(ScheduledTransfer::getId)
                .toList();

        scheduledTransferService.updateStatusByIds(transferIds, TransferStatus.PROCESSING);

        int successCount = 0;
        int failCount = 0;

        for (ScheduledTransfer transfer : transfers) {
            try {
                // Kafka로 발행
                producer.sendTransferMessage(transfer);
                successCount++;

                log.info("Kafka 발행 성공 - Transfer ID: {}", transfer.getId());
            } catch (Exception e) {
                // 예외 발생시 상태 복구: PROCESSING -> PENDING
                transfer.setStatus(TransferStatus.PENDING);
                scheduledTransferService.save(transfer);

                failCount++;

                log.error("Kafka 발행 실패 - Transfer ID: {}, Error: {}",
                        transfer.getId(), e.getMessage(), e);
            }
        }

        log.info("=== 예약이체 스케줄러 종료 === 성공: {}, 실패: {}", successCount, failCount);
    }
}
