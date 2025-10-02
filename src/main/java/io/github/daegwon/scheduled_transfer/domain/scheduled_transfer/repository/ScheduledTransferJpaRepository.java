package io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.repository;

import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.domain.scheduled_transfer.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledTransferJpaRepository extends JpaRepository<ScheduledTransfer, Long> {
    List<ScheduledTransfer> findByScheduledAtBeforeAndStatus(LocalDateTime now, TransferStatus status);
}
