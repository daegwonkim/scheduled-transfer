package io.github.daegwon.scheduled_transfer.domain.scheduled_transfer;


import io.github.daegwon.scheduled_transfer.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "scheduled_transfers")
public class ScheduledTransfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_account_number", nullable = false)
    private String fromAccountNumber;

    @Column(name = "to_account_number", nullable = false)
    private String toAccountNumber;

    @Column(name = "to_bank_code", nullable = false)
    private String toBankCode;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;
}
