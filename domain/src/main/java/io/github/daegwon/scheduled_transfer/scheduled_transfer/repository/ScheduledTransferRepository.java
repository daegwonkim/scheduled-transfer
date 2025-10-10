package io.github.daegwon.scheduled_transfer.scheduled_transfer.repository;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.TransferStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduledTransferRepository {
    Optional<ScheduledTransfer> findById(Long id);
    List<ScheduledTransfer> findByScheduledAtBeforeAndStatus(LocalDateTime now, TransferStatus status);
    void save(ScheduledTransfer scheduledTransfer);
    void updateStatusByIds(List<Long> ids, TransferStatus status);
}
