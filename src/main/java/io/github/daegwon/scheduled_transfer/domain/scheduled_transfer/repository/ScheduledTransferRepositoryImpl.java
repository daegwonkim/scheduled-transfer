package io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.repository;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ScheduledTransferRepositoryImpl implements ScheduledTransferRepository {

    private final ScheduledTransferJpaRepository scheduledTransferJpaRepository;

    @Override
    public Optional<ScheduledTransfer> findById(Long id) {
        return scheduledTransferJpaRepository.findById(id);
    }

    @Override
    public List<ScheduledTransfer> findByScheduledAtBeforeAndStatus(LocalDateTime now, TransferStatus status) {
        return scheduledTransferJpaRepository.findByScheduledAtBeforeAndStatus(now, status);
    }

    @Override
    public ScheduledTransfer save(ScheduledTransfer scheduledTransfer) {
        return scheduledTransferJpaRepository.save(scheduledTransfer);
    }
}
