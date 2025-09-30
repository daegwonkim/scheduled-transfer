package io.github.daegwon.scheduled_transfer.domain.bank;


import io.github.daegwon.scheduled_transfer.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "banks")
public class Bank extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "maintenance_start_time", nullable = false)
    private LocalTime maintenanceStartTime;

    @Column(name = "maintenance_end_time", nullable = false)
    private LocalTime maintenanceEndTime;
}
