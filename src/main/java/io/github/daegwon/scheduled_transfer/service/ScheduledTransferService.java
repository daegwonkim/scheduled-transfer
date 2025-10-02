package io.github.daegwon.scheduled_transfer.service;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.repository.ScheduledTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTransferService {

    private final ScheduledTransferRepository scheduledTransferRepository;

    public List<ScheduledTransfer> getOverdueScheduledTransfers() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return scheduledTransferRepository.findByScheduledAtBeforeAndStatus(currentDateTime, TransferStatus.PENDING);
    }
}
