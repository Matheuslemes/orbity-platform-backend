package br.com.orbity.customer.adapters.in.messaging.consumer;

import br.com.orbity.customer.domain.port.in.UpsertCustomerCommand;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdpEventsConsumer {

    private final ObjectMapper om;
    private final UpsertCustomerCommand upsertCustomer;

    @KafkaListener(
            id = "customer-idp-events",
            topics = "${orbity.kafka.consumer.topics.idp-events.name:idp.events.v1}",
            groupId = "${spring.kafka.consumer.group-id:ms-customer}",
            concurrency = "${orbity.kafka.consumer.topics.idp-events.concurrency:1}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onIdpEvent(ConsumerRecord<String, String> rec, Acknowledgment ack) {

        try {

            final String payload = rec.value();
            if (payload == null || payload.isBlank()) {

                log.warn("[KafkaIdpEventsConsumer] BLANK payload topic={} off={} key={}",
                        rec.topic(), rec.offset(), rec.key());
                ack.acknowledge();
                return;

            }

            JsonNode node = om.readTree(payload);

            final String type = trim(node.path("type").asText());
            final String sub = trim(node.path("sub").asText());
            final String email = trim(node.path("email").asText());
            final String firstName = trim(node.path("firstName").asText(node.path("given_name").asText()));
            final String lastName = trim(node.path("lastName").asText(node.path("family_name").asText()));
            final String phone = normalizePhone(trim(node.path("phone").asText(node.path("phone_number").asText())));

            log.info("[KafkaIdpEventsConsumer] RX type={} sub={} email={} topic={} off={}",
                    type, sub, email, rec.topic(), rec.offset());

            if (isBlank(sub)) {

                log.warn("[KafkaIdpEventsConsumer] INVALID sub (required) topic={} off={}", rec.topic(), rec.offset());
                ack.acknowledge();
                return;

            }

            if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                log.warn("[KafkaIdpEventsConsumer] INVALID email={} topic={} off={}", email, rec.topic(), rec.offset());
                ack.acknowledge();
                return;

            }

            if ("user_created".equalsIgnoreCase(type) || "user_updated".equalsIgnoreCase(type)) {

                upsertCustomer.upsert(sub, email, safeLen(firstName, 120), safeLen(lastName, 120), phone);
            } else {

                log.info("[KafkaIdpEventsConsumer] ignoring type={} (no-op)", type);
            }

            ack.acknowledge();

        } catch (Exception e) {

            log.error("[KafkaIdpEventsConsumer] FAIL topic={} off={} err={}",
                    rec.topic(), rec.offset(), e.getMessage(), e);
        }
    }

    //helpers
    private static String trim(String s) {

        return s == null ? null : s.trim();

    }

    private static boolean isBlank(String s) {

        return s == null || s.isBlank();

    }

    private static String safeLen(String s, int max) {

        return s == null ? null : (s.length() <= max ? s : s.substring(0, max));

    }

    private static String normalizePhone(String s) {

        if (s == null) return null;
        String digits = s.replaceAll("\\D+", "");
        return digits.isEmpty() ? null : digits;

    }

}