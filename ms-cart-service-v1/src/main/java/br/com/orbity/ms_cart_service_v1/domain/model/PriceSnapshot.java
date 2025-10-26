package br.com.orbity.ms_cart_service_v1.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(of = {"sku", "capturedAtEpochMs"})
public class PriceSnapshot {

    private static final String DEFAULT_CURRENCY = "BRL";
    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    private String sku;

    private BigDecimal unitPrice;

    private String currency;

    private Long capturedAtEpochMs;

    public static PriceSnapshot of(String sku, BigDecimal unitPrice, String currency, Long capturedAtEpochMs) {
        return PriceSnapshot.builder()
                .sku(sku)
                .unitPrice(roundNonNegative(unitPrice))
                .currency(normalizeCurrency(currency))
                .capturedAtEpochMs(defaultIfNull(capturedAtEpochMs, System.currentTimeMillis()))
                .build();
    }

    public PriceSnapshot(String sku, BigDecimal price, String currency, Instant at) {
        this(sku, price, currency, at == null ? null : at.toEpochMilli());
    }

    public static PriceSnapshot now(String sku, BigDecimal unitPrice, String currency) {
        return of(sku, unitPrice, currency, System.currentTimeMillis());
    }

    public PriceSnapshot normalize() {
        this.currency = normalizeCurrency(this.currency);
        this.unitPrice = roundNonNegative(this.unitPrice);
        if (this.capturedAtEpochMs == null) {
            this.capturedAtEpochMs = System.currentTimeMillis();
        }
        return this;
    }

    public boolean isNewerOrSameThan(PriceSnapshot other) {
        long thisTs = defaultIfNull(this.capturedAtEpochMs, 0L);
        long otherTs = other == null ? -1L : defaultIfNull(other.capturedAtEpochMs, 0L);
        return thisTs >= otherTs;
    }

    private static BigDecimal roundNonNegative(BigDecimal value) {
        if (value == null) return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        if (value.signum() < 0) value = BigDecimal.ZERO;
        return value.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) return DEFAULT_CURRENCY;
        return currency.trim().toUpperCase(Locale.ROOT);
    }

    private static <T> T defaultIfNull(T value, T fallback) {
        return Objects.isNull(value) ? fallback : value;
    }

}
