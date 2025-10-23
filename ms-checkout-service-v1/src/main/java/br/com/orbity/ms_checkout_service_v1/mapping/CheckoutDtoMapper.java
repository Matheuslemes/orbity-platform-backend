package br.com.orbity.ms_checkout_service_v1.mapping;

import br.com.orbity.ms_checkout_service_v1.domain.model.*;
import br.com.orbity.ms_checkout_service_v1.dto.CheckoutRequestDto;
import br.com.orbity.ms_checkout_service_v1.dto.CheckoutStatusDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class CheckoutDtoMapper {

    public Checkout toDomain(CheckoutRequestDto d){

        List<CheckoutItem> items = d.items().stream()
                .map(i -> new CheckoutItem(i.productId(), i.sku(), i.name(), i.quantity(), i.unitPrice(), i.lineTotal()))
                .toList();

        var addr = new Address(

                d.address().street(), d.address().number(), d.address().complement(),
                d.address().district(), d.address().city(), d.address().state(),
                d.address().country(), d.address().zip()

        );

        var pay = new PaymentInfo(d.payment().method(), d.payment().token(), d.payment().currency());

        BigDecimal total = items.stream().map(CheckoutItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Checkout(

                d.checkoutId(),
                d.customerId(),
                items,
                addr,
                pay,
                total,
                CheckoutStatus.STARTED,
                new SagaState("STARTED", null, OffsetDateTime.now()),
                OffsetDateTime.now(),
                OffsetDateTime.now()

        );

    }

    public CheckoutStatusDto toStatusDto(Checkout c){

        return new CheckoutStatusDto(c.id(), c.customerId(), c.status(), c.totalAmount());

    }
}
