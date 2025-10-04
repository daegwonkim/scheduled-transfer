package io.github.daegwon.scheduled_transfer.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequest(
        String fromAccount,
        String toAccount,
        BigDecimal amount
) {
}
