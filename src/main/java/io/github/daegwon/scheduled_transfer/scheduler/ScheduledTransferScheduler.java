package io.github.daegwon.scheduled_transfer.scheduler;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.kafka.KafkaProducer;
import io.github.daegwon.scheduled_transfer.service.ScheduledTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTransferScheduler {

    private final KafkaProducer kafkaProducer;
    private final ScheduledTransferService scheduledTransferService;

    @Scheduled(cron = "${schedule.cron}")
    public void produceOverdueScheduledTransfer() {
        List<ScheduledTransfer> overdueScheduledTransfers = scheduledTransferService.getOverdueScheduledTransfers();

        for (ScheduledTransfer scheduledTransfer : overdueScheduledTransfers) {
            kafkaProducer.produce(
                    "scheduled_transfer",
                    scheduledTransfer.getFromAccountNumber(),
                    scheduledTransfer
            );
        }
    }
}
