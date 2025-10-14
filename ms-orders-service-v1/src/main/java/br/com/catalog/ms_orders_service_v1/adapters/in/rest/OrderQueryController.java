package br.com.catalog.ms_orders_service_v1.adapters.in.rest;

import br.com.catalog.ms_orders_service_v1.domain.port.in.GetOrderQuery;
import br.com.catalog.ms_orders_service_v1.domain.port.in.ListOrdersByCustomerQuery;
import br.com.catalog.ms_orders_service_v1.dto.OrderDto;
import br.com.catalog.ms_orders_service_v1.mapping.OrderDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Orders", description = "Consultas de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class OrderQueryController {

    private final GetOrderQuery getOrder;
    private final ListOrdersByCustomerQuery listOrders;
    private final OrderDtoMapper mapper;

    @Operation(
            summary = "Buscar pedido por ID",
            description = """
                    **HTTP**: `GET /api/v1/orders/{id}`
                    **Retorno**: 200 OK se encontrado, 404 se não encontrado
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SpringDataJaxb.OrderDto.class))),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> byId(
            @Parameter(description = "ID do pedido (UUID)", required = true)
            @PathVariable @NotNull UUID id
    ) {

        log.info("[OrderQueryController] - [byId] IN -> id={}", id);

        var resp = getOrder.byId(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.info("[OrderQueryController] - [byId] OUT -> 404 id={}", id);

                    return ResponseEntity.notFound().build();

                });


        if (resp.getStatusCode().is2xxSuccessful()) {

            log.info("[OrderQueryController] - [byId] OUT -> 200 id={}", id);

        }

        return resp;

    }

    @Operation(
            summary = "Listar pedidos por cliente (paginado simples)",
            description = """
                    **HTTP**: `GET /api/v1/orders/customers/{customerId}`
                    **Query**: `page` (default=0, mínimo=0), `size` (default=20, 1..100)
                    **Retorno**: 200 OK com `List<OrderDto>`
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = OrderDto.class))),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
            }
    )
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<OrderDto>> byCustomer(
            @Parameter(description = "ID do cliente (UUID)", required = true)
            @PathVariable @NotNull UUID customerId,
            @Parameter(description = "Página (0..N)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamanho da página (1..100)")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {

        log.info("[OrderQueryController] - [byCustomer] IN -> customerId={} page={} size={}", customerId, page, size);

        var result = listOrders.listByCustomer(customerId, page, size)
                .stream()
                .map(mapper::toDto)
                .toList();

        log.info("[OrderQueryController] - [byCustomer] OUT -> count={}", result.size());

        return ResponseEntity.ok(result);

    }

}