package io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.repository;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduledTransferRepositoryImpl implements ScheduledTransferRepository {

    private final ScheduledTransferJpaRepository scheduledTransferJpaRepository;

    @Override
    public List<ScheduledTransfer> findByScheduledAtBeforeAndStatus(LocalDateTime currentTime, TransferStatus status) {
        return scheduledTransferJpaRepository.findByScheduledAtBeforeAndStatus(currentTime, status);
    }
}
