package br.com.orbity.ms_inventory_service.application.process;

import br.com.orbity.ms_inventory_service.domain.event.StockReleased;
import br.com.orbity.ms_inventory_service.domain.event.StockReserved;
import br.com.orbity.ms_inventory_service.domain.port.out.StockEventPublisherPortOut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ReservationSaga {

    private final StockEventPublisherPortOut publisher;

    public ReservationSaga(StockEventPublisherPortOut publisher) {
        this.publisher = publisher;
    }

    public void on(StockReserved ev) {

        validate(ev);
        publisher.publish(ev);

    }

    public void on(StockReleased ev) {

        validate(ev);
        publisher.publish(ev);

    }

    //helpers
    private static void validate(StockReserved ev) {

        Objects.requireNonNull(ev, "event is required");

        if (ev.quantity() <= 0) {
            throw new IllegalArgumentException("reserved quantity must be > 0");
        }

        if (ev.aggregateId() == null || isBlank(ev.sku())) {
            throw new IllegalArgumentException("aggregateId and sku are required");
        }
    }

    private static void validate(StockReleased ev) {

        Objects.requireNonNull(ev, "event is required");

        if (ev.quantity() <= 0) {
            throw new IllegalArgumentException("released quantity must be > 0");
        }

        if (ev.aggregateId() == null || isBlank(ev.sku())) {
            throw new IllegalArgumentException("aggregateId and sku are required");
        }

    }

    private static boolean isBlank(String s) {

        return s == null || s.isBlank();

    }

}