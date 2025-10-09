package io.github.daegwon.scheduled_transfer.scheduled_transfer.dto;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;

public record ScheduledTransferEvent(
        ScheduledTransfer transfer
) {
}
