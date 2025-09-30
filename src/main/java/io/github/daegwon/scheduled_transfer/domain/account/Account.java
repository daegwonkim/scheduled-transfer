package io.github.daegwon.scheduled_transfer.domain.account;


import io.github.daegwon.scheduled_transfer.domain.BaseEntity;
import io.github.daegwon.scheduled_transfer.domain.bank.Bank;
import io.github.daegwon.scheduled_transfer.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;
}
