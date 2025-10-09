package io.github.daegwon.scheduled_transfer.scheduled_transfer.dto;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 코어뱅킹 서버로 이체 요청을 하기 위한 DTO
 *
 * @param transferId 이체 ID(식별자)
 * @param fromAccount 출금계좌
 * @param toAccount 입금계좌
 * @param amount 이체 금액
 * @param scheduledAt 예약일시
 */
@Builder
public record TransferMessage(
        Long transferId,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        LocalDateTime scheduledAt
) implements Serializable {
    public static TransferMessage fromEntity(ScheduledTransfer transfer) {
        return TransferMessage.builder()
                .transferId(transfer.getId())
                .fromAccount(transfer.getFromAccount())
                .toAccount(transfer.getToAccount())
                .amount(transfer.getAmount())
                .scheduledAt(transfer.getScheduledAt())
                .build();
    }
}
