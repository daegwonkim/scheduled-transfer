package io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.repository;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledTransferRepository {
    List<ScheduledTransfer> findByScheduledAtBeforeAndStatus(LocalDateTime currentTime, TransferStatus status);
}
