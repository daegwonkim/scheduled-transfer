package io.github.daegwon.scheduled_transfer.scheduled_transfer.service;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.TransferStatus;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.repository.ScheduledTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ScheduledTransferService {

    private final ScheduledTransferRepository scheduledTransferRepository;

    public ScheduledTransfer getScheduledTransfer(Long id) {
        return scheduledTransferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이체 건: " + id));
    }

    public List<ScheduledTransfer> getOverduePendingTransfer() {
        LocalDateTime now = LocalDateTime.now();
        return scheduledTransferRepository.findByScheduledAtBeforeAndStatus(now, TransferStatus.PENDING);
    }

    @Transactional
    public ScheduledTransfer save(ScheduledTransfer scheduledTransfer) {
        return scheduledTransferRepository.save(scheduledTransfer);
    }
}
