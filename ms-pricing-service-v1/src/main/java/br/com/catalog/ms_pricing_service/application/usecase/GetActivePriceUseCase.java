package br.com.catalog.ms_pricing_service.application.usecase;

import br.com.catalog.ms_pricing_service.domain.model.Price;
import br.com.catalog.ms_pricing_service.domain.port.in.GetActivePriceQuery;
import br.com.catalog.ms_pricing_service.domain.port.out.PriceRepositoryPortOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class GetActivePriceUseCase {

    private final PriceRepositoryPortOut priceRepo;

    public GetActivePriceUseCase(PriceRepositoryPortOut priceRepo) {
        this.priceRepo = priceRepo;
    }

    public Price handle(GetActivePriceQuery q) {

        log.info("[GetActivePriceUseCase] - [handle] IN -> {}", safeQuery(q));

        if (q == null) {
            log.error("[GetActivePriceUseCase] - [handle] Query is null");
            throw new IllegalArgumentException("Query is null");
        }

        if (isBlank(q.sku())) {
            log.error("[GetActivePriceUseCase] - [handle] SKU is blank");
            throw new IllegalArgumentException("SKU obrigatório");
        }

        return priceRepo.findBySku(q.sku())
                .map(price -> {
                    log.info("[GetActivePriceUseCase] - [handle] OK sku={} priceId={}", q.sku(), price.id());
                    log.info("[GetActivePriceUseCase] - [handle] OUT <- {}", price);
                    return price;
                })
                .orElseThrow(() -> {
                        log.warn("[GetActivePriceUseCase] - [handle] NOT_FOUND sku={}", q.sku());
                        return new NoSuchElementException("SKU não encontrado: " + q.sku());
                });
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String safeQuery(GetActivePriceQuery q) {

        if (q == null) return "null";
        return "{sku=" + q.sku() + "}";
    }
}
