package io.github.daegwon.scheduled_transfer.scheduled_transfer.repository;

import io.github.daegwon.scheduled_transfer.scheduled_transfer.entity.ScheduledTransfer;
import io.github.daegwon.scheduled_transfer.scheduled_transfer.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledTransferJpaRepository extends JpaRepository<ScheduledTransfer, Long> {
    List<ScheduledTransfer> findByScheduledAtBeforeAndStatus(LocalDateTime now, TransferStatus status);

    @Modifying
    @Query("UPDATE ScheduledTransfer st SET st.status = :status WHERE st.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") TransferStatus status);
}
