package br.com.orbity.ms_checkout_service_v1.adapters.in.rest;

import br.com.orbity.ms_checkout_service_v1.domain.port.in.GetCheckoutStatusQuery;
import br.com.orbity.ms_checkout_service_v1.domain.port.in.StartCheckoutCommand;
import br.com.orbity.ms_checkout_service_v1.dto.CheckoutRequestDto;
import br.com.orbity.ms_checkout_service_v1.dto.CheckoutStatusDto;
import br.com.orbity.ms_checkout_service_v1.mapping.CheckoutDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Checkout")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/checkout")
public class CheckoutController {

    private final StartCheckoutCommand startCheckout;
    private final GetCheckoutStatusQuery getStatus;
    private final CheckoutDtoMapper mapper;

    @Operation(summary = "Iniciar checkout (idempotente por header x-idempotency-key)")
    @PostMapping
    public ResponseEntity<CheckoutStatusDto> start(
            @RequestHeader(value = "x-idempotency-key", required = false) String idemKey,
            @Valid @RequestBody CheckoutRequestDto body
    ) {

        var draft = mapper.toDomain(body);
        var started = startCheckout.start(draft, idemKey);

        return ResponseEntity.ok(mapper.toStatusDto(started));

    }

    @Operation(summary = "Consultar status do checkout")
    @GetMapping("/{id}/status")
    public ResponseEntity<CheckoutStatusDto> status(@PathVariable UUID id) {

        return getStatus.byId(id)
                .map(mapper::toStatusDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
}