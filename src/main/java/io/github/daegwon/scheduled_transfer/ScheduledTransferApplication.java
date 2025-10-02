package io.github.daegwon.scheduled_transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ScheduledTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduledTransferApplication.class, args);
    }
}
