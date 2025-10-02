package io.github.daegwon.scheduled_transfer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class CoreBankingService {

    @Value("${core-banking-server.url}")
    private String CORE_BANKING_SERVER_URL;

    /**
     * 코어뱅킹 서버로 이체 실행 요청
     *
     * @param fromAccount 출금계좌
     * @param toAccount 입금계좌
     * @param amount 이체 금액
     * @return 요청 결과
     */
    public boolean executeTransfer(String fromAccount, String toAccount, BigDecimal amount) {
        return false;
    }
}
