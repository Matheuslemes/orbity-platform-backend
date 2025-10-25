package br.com.orbity.ms_cart_service_v1.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceSnapshot {

    private String sku;

    private BigDecimal unitPrice;

    private String currency;

    private Long capturedAtEpochMs;

}