package br.com.orbity.ms_checkout_service_v1.domain.model;

import java.time.OffsetDateTime;

public class SagaState {

    private String step;

    private String compensationStep;

    private OffsetDateTime updatedAt;

    public SagaState(String step, String compensationStep, OffsetDateTime updatedAt) {

        this.step = step;
        this.compensationStep = compensationStep;
        this.updatedAt = updatedAt;

    }

    public String getStep() { return step; }

    public String getCompensationStep() { return compensationStep; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setStep(String step) { this.step = step; }

    public void setCompensationStep(String c) { this.compensationStep = c; }

    public void touch() { this.updatedAt = OffsetDateTime.now(); }

}
