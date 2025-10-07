package br.com.orbity.customer.domain.model;

import java.util.UUID;

public record CustomerId (UUID value) {
    public static CustomerId of(UUID id) { return new CustomerId(id); }
}
