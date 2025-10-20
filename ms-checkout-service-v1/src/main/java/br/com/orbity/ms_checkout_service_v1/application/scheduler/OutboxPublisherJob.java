package br.com.orbity.ms_checkout_service_v1.application.scheduler;

import br.com.orbity.ms_checkout_service_v1.domain.port.out.OutboxPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherJob {

    private final OutboxPortOut outbox;

    @Scheduled(fixedDelayString = "${catalog.outbox.publisher.delay-ms:2000}")
    public void run() {

        try {

            outbox.publishPending();
        } catch (Exception e) {

            log.error("outbox publish failed", e);

        }

    }

}
