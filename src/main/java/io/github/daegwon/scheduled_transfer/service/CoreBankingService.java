package io.github.daegwon.scheduled_transfer.service;

import io.github.daegwon.scheduled_transfer.dto.TransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
        try {
            log.info("코어뱅킹 서버 이체 요청 - From: {}, To: {}, Amount: {}", fromAccount, toAccount, amount);

            TransferRequest requestBody = TransferRequest.builder()
                    .fromAccount(fromAccount)
                    .toAccount(toAccount)
                    .amount(amount)
                    .build();

            RestClient restClient = RestClient.create();
            ResponseEntity<Void> response = restClient.post()
                    .uri(CORE_BANKING_SERVER_URL + "/api/v1/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("코어뱅킹 서버 이체 성공 - From: {}, To: {}", fromAccount, toAccount);
                return true;
            } else {
                log.error("코어뱅킹 서버 이체 실패 - Status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("코어뱅킹 서버 호출 실패 - Error: {}", e.getMessage(), e);
            return false;
        }
    }
}
