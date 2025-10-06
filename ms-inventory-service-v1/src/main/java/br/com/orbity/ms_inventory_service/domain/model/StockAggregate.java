package br.com.orbity.ms_inventory_service.domain.model;

import br.com.orbity.ms_inventory_service.domain.event.StockAdjusted;
import br.com.orbity.ms_inventory_service.domain.event.StockDecremented;
import br.com.orbity.ms_inventory_service.domain.event.StockReleased;
import br.com.orbity.ms_inventory_service.domain.event.StockReserved;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class StockAggregate {

    private UUID id;
    private String sku;
    private long availableQty;
    private long reservedQty;
    private long version;

    private final List<Object> uncommittedEvents = new ArrayList<>();

    private StockAggregate() { }


    public static StockAggregate createNew(UUID id, String sku, long initialQty) {

        requireNonNull(id, "id");
        requireNonBlank(sku, "sku");
        requireNonNegative(initialQty, "initialQty");

        StockAggregate a = new StockAggregate();
        a.id = id;
        a.sku = sku.trim();
        a.availableQty = initialQty;
        a.reservedQty = 0L;
        a.version = 0L;
        return a;

    }

    public static StockAggregate rehydrate(UUID id, String sku, long version, long availableQty, long reservedQty) {

        requireNonNull(id, "id");
        requireNonBlank(sku, "sku");
        requireNonNegative(version, "version");
        requireNonNegative(availableQty, "availableQty");
        requireNonNegative(reservedQty, "reservedQty");

        StockAggregate a = new StockAggregate();
        a.id = id;
        a.sku = sku.trim();
        a.version = version;
        a.availableQty = availableQty;
        a.reservedQty = reservedQty;
        return a;

    }

    public static StockAggregate replayFromEvents(Iterable<Object> pastEvents) {
        StockAggregate a = new StockAggregate();
        for (Object evt : pastEvents) {
            a.apply(evt);
        }
        return a;
    }


    public void adjust(long newAvailableQty) {

        requireNonNegative(newAvailableQty, "newAvailableQty");

        if (newAvailableQty < 0) {
            throw new IllegalStateException("Invalid adjustment");
        }

        raise(new StockAdjusted(id, sku, newAvailableQty, Instant.now()));
    }

    public void decrement(long qty) {

        requirePositive(qty, "qty");

        if (availableQty < qty) {
            throw new IllegalStateException("Insufficient available stock to decrement");
        }

        raise(new StockDecremented(id, sku, qty, Instant.now()));

    }

    public void reserve(long qty) {

        requirePositive(qty, "qty");

        if (availableQty < qty) {
            throw new IllegalStateException("Not enough available to reserve");
        }

        raise(new StockReserved(id, sku, qty, Instant.now()));

    }

    public void release(long qty) {

        requirePositive(qty, "qty");

        if (reservedQty < qty) {
            throw new IllegalStateException("Cannot release more than reserved");
        }

        raise(new StockReleased(id, sku, qty, Instant.now()));

    }

    private void raise(Object event) {
        apply(event);
        uncommittedEvents.add(event);
        version++;
    }


    public void apply(Object event) {
        if (event instanceof StockAdjusted e) {
            whenAdjusted(e);
        } else if (event instanceof StockDecremented e) {
            whenDecremented(e);
        } else if (event instanceof StockReserved e) {
            whenReserved(e);
        } else if (event instanceof StockReleased e) {
            whenReleased(e);
        } else {
            throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
        }
    }

    private void whenAdjusted(StockAdjusted e) {

        this.id = (this.id == null) ? e.aggregateId() : this.id;
        this.sku = (this.sku == null) ? e.sku() : this.sku;
        this.availableQty = e.newAvailableQty();

    }

    private void whenDecremented(StockDecremented e) {

        this.id = (this.id == null) ? e.aggregateId() : this.id;
        this.sku = (this.sku == null) ? e.sku() : this.sku;
        this.availableQty -= e.delta();

    }

    private void whenReserved(StockReserved e) {

        this.id = (this.id == null) ? e.aggregateId() : this.id;
        this.sku = (this.sku == null) ? e.sku() : this.sku;
        this.availableQty -= e.quantity();
        this.reservedQty += e.quantity();

    }

    private void whenReleased(StockReleased e) {

        this.id = (this.id == null) ? e.aggregateId() : this.id;
        this.sku = (this.sku == null) ? e.sku() : this.sku;
        this.reservedQty -= e.quantity();
        this.availableQty += e.quantity();

    }


    public List<Object> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }

    public void clearUncommittedEvents() {
        uncommittedEvents.clear();
    }

    public boolean canSnapshot(long threshold) {
        return version >= threshold;
    }


    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public long getAvailableQty() {
        return availableQty;
    }

    public long getReservedQty() {
        return reservedQty;
    }

    public long getVersion() {
        return version;
    }

    //Helpers
    private static void requireNonNull(Object o, String name) {
        Objects.requireNonNull(o, name + " is required");
    }

    private static void requireNonBlank(String s, String name) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
    }

    private static void requireNonNegative(long v, String name) {
        if (v < 0) throw new IllegalArgumentException(name + " must be >= 0");
    }

    private static void requirePositive(long v, String name) {
        if (v <= 0) throw new IllegalArgumentException(name + " must be > 0");
    }
}
