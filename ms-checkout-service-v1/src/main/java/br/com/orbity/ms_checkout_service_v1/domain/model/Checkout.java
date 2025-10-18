package br.com.orbity.ms_checkout_service_v1.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Checkout {

    private final UUID id;

    private final UUID customerId;

    private final List<CheckoutItem> items;

    private final Address shippingAddress;

    private final PaymentInfo paymentInfo;

    private BigDecimal totalAmount;

    private CheckoutStatus status;

    private SagaState saga;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;


    public Checkout(UUID id, UUID customerId, List<CheckoutItem> items,
                    Address shippingAddress, PaymentInfo paymentInfo,
                    BigDecimal totalAmount, CheckoutStatus status,
                    SagaState saga, OffsetDateTime createdAt, OffsetDateTime updatedAt) {

        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.paymentInfo = paymentInfo;
        this.totalAmount = totalAmount;
        this.status = status;
        this.saga = saga;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public UUID id() { return id; }

    public UUID customerId() { return customerId; }

    public List<CheckoutItem> items() { return items; }

    public Address shippingAddress() { return shippingAddress; }

    public PaymentInfo paymentInfo() { return paymentInfo; }

    public BigDecimal totalAmount() { return totalAmount; }

    public CheckoutStatus status() { return status; }

    public SagaState saga() { return saga; }

    public OffsetDateTime createdAt() { return createdAt; }

    public OffsetDateTime updatedAt() { return updatedAt; }

    public void setStatus(CheckoutStatus s){ this.status = s; touch(); }
    public void setSaga(SagaState s){ this.saga = s; touch(); }
    public void touch(){ this.updatedAt = OffsetDateTime.now(); }

}
