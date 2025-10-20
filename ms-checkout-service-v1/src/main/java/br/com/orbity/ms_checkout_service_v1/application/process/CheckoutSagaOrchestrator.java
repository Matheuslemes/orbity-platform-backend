package br.com.orbity.ms_checkout_service_v1.application.process;

import br.com.orbity.ms_checkout_service_v1.domain.event.InventoryReservationConfirmedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.event.InventoryReservationDeniedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.event.PaymentAuthorizedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.event.PaymentDeniedEvent;
import br.com.orbity.ms_checkout_service_v1.domain.model.CheckoutStatus;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.InventoryReservationPortOut;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.PaymentGatewayPortOut;
import br.com.orbity.ms_checkout_service_v1.domain.port.out.SagaStateRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckoutSagaOrchestrator {

    private final PaymentGatewayPortOut payment;
    private final InventoryReservationPortOut inventory;
    private final SagaStateRepositoryPortOut sagaRepo;

    public void handlePaymentAuthorized(PaymentAuthorizedEvent evt){

        sagaRepo.byId(evt.checkoutId()).ifPresent(ch -> {
            boolean reserved = inventory.reserve(ch);
            ch.setStatus(reserved ? CheckoutStatus.RESERVED : CheckoutStatus.FAILED);
            sagaRepo.upsert(ch);
        });

    }

    public void handlePaymentDenied(PaymentDeniedEvent evt){

        sagaRepo.byId(evt.checkoutId()).ifPresent(ch -> {
            ch.setStatus(CheckoutStatus.FAILED);
            sagaRepo.upsert(ch);
        });

    }

    public void handleInventoryReserved(InventoryReservationConfirmedEvent evt){

        sagaRepo.byId(evt.checkoutId()).ifPresent(ch -> {
            ch.setStatus(CheckoutStatus.COMPLETED);
            sagaRepo.upsert(ch);
        });

    }

    public void handleInventoryDenied(InventoryReservationDeniedEvent evt){

        sagaRepo.byId(evt.checkoutId()).ifPresent(ch -> {
            payment.refund(ch); // compensação
            ch.setStatus(CheckoutStatus.FAILED);
            sagaRepo.upsert(ch);
        });

    }

}