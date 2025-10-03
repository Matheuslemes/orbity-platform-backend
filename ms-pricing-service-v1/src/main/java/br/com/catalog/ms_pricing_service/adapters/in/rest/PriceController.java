package br.com.catalog.ms_pricing_service.adapters.in.rest;

import br.com.catalog.ms_pricing_service.application.usecase.CreateOrReplacePriceUseCase;
import br.com.catalog.ms_pricing_service.application.usecase.GetActivePriceUseCase;
import br.com.catalog.ms_pricing_service.domain.port.in.CreateOrReplacePriceCommand;
import br.com.catalog.ms_pricing_service.domain.port.in.GetActivePriceQuery;
import br.com.catalog.ms_pricing_service.dto.PriceDto;
import br.com.catalog.ms_pricing_service.mapping.PriceDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/pricing/prices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Prices", description = "Endpoints de gestão de preços")
@SecurityRequirement(name = "bearerAuth")
public class PriceController {

    private final CreateOrReplacePriceUseCase createOrReplace;
    private final GetActivePriceUseCase getActive;
    private final PriceDtoMapper mapper;

    @Operation(
            summary = "Criar/Substituir preço por SKU",
            description = """
            **HTTP**: `POST /api/v1/pricing/prices`  
            **Body**: `{ "sku": "SKU-123", "currency": "BRL", "amount": 199.90, "reason": "Tabela 2025" }`  
            **Retorno**: `201 Created` com Location `/api/v1/pricing/prices/{sku}/active` e corpo `PriceDto`.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado/Substituído",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PriceDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflito", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceDto> createOrReplace(@Valid @RequestBody CreateOrReplacePriceCommand cmd) {

        createOrReplace.handle(cmd);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{sku}/active")
                .buildAndExpand(cmd.sku())
                .toUri();

        var price = getActive.handle(new GetActivePriceQuery(cmd.sku()));
        var out = mapper.toDto(price);

        return ResponseEntity.created(location).body(out);

    }

    @Operation(
            summary = "Atualizar/Substituir preço por SKU (idempotente por SKU)",
            description = """
            **HTTP**: `PUT /api/v1/pricing/prices/{sku}`  
            **Path**: `sku` (identificador lógico do produto)  
            **Body**: `{ "currency": "BRL", "amount": 209.90, "reason": "Campanha setembro" }`  
            **Retorno**: `200 OK` com `PriceDto` do preço ativo atualizado.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualizado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PriceDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
            }
    )
    @PutMapping(value = "/{sku}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PriceDto> update(
            @Parameter(description = "SKU do produto", required = true, example = "SKU-123")
            @PathVariable String sku,
            @Valid @RequestBody UpdatePricePayload payload
    ) {

        var cmd = new CreateOrReplacePriceCommand(
                sku,
                payload.currency(),
                payload.amount(),
                payload.reason()
        );

        createOrReplace.handle(cmd);

        var price = getActive.handle(new GetActivePriceQuery(sku));
        return ResponseEntity.ok(mapper.toDto(price));

    }

    @Operation(
            summary = "Obter preço ativo por SKU",
            description = """
            **HTTP**: `GET /api/v1/pricing/prices/{sku}/active`  
            **Retorno**: `200 OK` com `PriceDto` do preço vigente.  
            **Cache**: cache-aside com TTL configurável.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PriceDto.class))),
                    @ApiResponse(responseCode = "404", description = "SKU não encontrado", content = @Content)
            }
    )
    @GetMapping("/{sku}/active")
    @Cacheable(cacheNames = "price-active", key = "#sku")
    public ResponseEntity<PriceDto> getActive(
            @Parameter(description = "SKU do produto", required = true, example = "SKU-123")
            @PathVariable String sku
    ) {

        var price = getActive.handle(new GetActivePriceQuery(sku));
        return ResponseEntity.ok(mapper.toDto(price));

    }


    public record UpdatePricePayload(
            @Schema(example = "BRL") String currency,
            @Schema(example = "209.90") java.math.BigDecimal amount,
            @Schema(example = "Campanha setembro") String reason
    ) { }
}
