package br.com.orbity.ms_checkout_service_v1.adapters.in.rest;

import br.com.orbity.ms_checkout_service_v1.domain.port.in.GetCheckoutStatusQuery;
import br.com.orbity.ms_checkout_service_v1.domain.port.in.StartCheckoutCommand;
import br.com.orbity.ms_checkout_service_v1.dto.CheckoutRequestDto;
import br.com.orbity.ms_checkout_service_v1.dto.CheckoutStatusDto;
import br.com.orbity.ms_checkout_service_v1.mapping.CheckoutDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Checkout", description = "Fluxo de checkout (início e consulta de status)")
@SecurityRequirement(name = "bearerAuth")
public class CheckoutController {

    private final StartCheckoutCommand startCheckout;
    private final GetCheckoutStatusQuery getStatus;
    private final CheckoutDtoMapper mapper;

    @Operation(
            summary = "Iniciar checkout (idempotente por header x-idempotency-key)",
            description = """
            **HTTP**: `POST /api/v1/checkout`  
            **Header opcional**: `x-idempotency-key` (recomendada para evitar duplicidades)  
            **Body**: JSON de `CheckoutRequestDto`  
            
            **Comportamento**  
            - Se `x-idempotency-key` for reutilizada com o mesmo payload, a operação é tratada como idempotente.  
            - Retorna `201 Created` com `Location: /api/v1/checkout/{id}/status` e o corpo `CheckoutStatusDto`.
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Checkout iniciado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CheckoutStatusDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflito de idempotência", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CheckoutStatusDto> start(
            @Parameter(description = "Chave de idempotência (UUID ou string única). Ex.: 0c6a0f8e-cc31-4f2a-9f0e-2b1b6e0a1a3c")
            @RequestHeader(value = "x-idempotency-key", required = false) String idemKey,
            @Valid @RequestBody CheckoutRequestDto body
    ) {
        var draft = mapper.toDomain(body);
        var started = startCheckout.start(draft, idemKey);
        var out = mapper.toStatusDto(started);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}/status")
                .buildAndExpand(out.checkoutId())
                .toUri();

        return ResponseEntity.created(location).body(out);
    }

    @Operation(
            summary = "Consultar status do checkout",
            description = """
            **HTTP**: `GET /api/v1/checkout/{id}/status`  
            **Path**: `id` (UUID do checkout)  
            **Retorno**: `200 OK` com `CheckoutStatusDto` ou `404 Not Found`.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CheckoutStatusDto.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content)
            }
    )
    @GetMapping("/{id}/status")
    public ResponseEntity<CheckoutStatusDto> status(
            @Parameter(description = "ID do checkout (UUID)", required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {
        return getStatus.byId(id)
                .map(mapper::toStatusDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
