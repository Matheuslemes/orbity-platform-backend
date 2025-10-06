package br.com.orbity.ms_inventory_service.adapters.in.rest;

import br.com.orbity.ms_inventory_service.domain.port.in.AdjustStockCommand;
import br.com.orbity.ms_inventory_service.domain.port.in.CreateStockCommand;
import br.com.orbity.ms_inventory_service.domain.port.in.GetStockQuery;
import br.com.orbity.ms_inventory_service.domain.port.in.UpdateStockCommand;
import br.com.orbity.ms_inventory_service.dto.StockDto;
import br.com.orbity.ms_inventory_service.mapping.StockDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/inventory/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Stock", description = "Operações de estoque (criação, atualização, ajuste, reservas)")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final CreateStockCommand createStock;
    private final UpdateStockCommand updateStock;
    private final AdjustStockCommand adjustStock;
    private final GetStockQuery getStock;
    private final StockDtoMapper mapper;


    @Operation(
            summary = "Criar estoque para um SKU",
            description = """
                    **HTTP**: `POST /api/v1/inventory/stocks`
                    **Body**: JSON com `sku` e `initialQty`
                    **Retorno**: 201 Created com Location `/api/v1/inventory/stocks/{id}`
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StockDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflito", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockDto> create(@Valid @RequestBody CreateStockRequest body) {
        var created = createStock.create(normalizeSku(body.sku()), body.initialQty());
        var dto = mapper.toDto(created);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity.created(location).body(dto);

    }

    @Operation(
            summary = "Debitar (decrementar) quantidade disponível",
            description = """
                    **HTTP**: `POST /api/v1/inventory/stocks/{id}/decrement`
                    **Body**: `{ "qty": <long> }`
                    **Efeito**: reduz `availableQty` (uso típico: baixa de estoque)
                    **Retorno**: 202 Accepted
                    """
    )
    @PostMapping(value = "/{id}/decrement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> decrement(
            @Parameter(description = "ID do agregado de estoque (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody QtyRequest body
    ) {

        updateStock.decrement(id, body.qty());
        return ResponseEntity.accepted().build();

    }

    @Operation(
            summary = "Reservar quantidade",
            description = """
                    **HTTP**: `POST /api/v1/inventory/stocks/{id}/reserve`
                    **Body**: `{ "qty": <long> }`
                    **Efeito**: incrementa `reservedQty` (sem alterar total disponível imediato)
                    **Retorno**: 202 Accepted
                    """
    )
    @PostMapping(value = "/{id}/reserve", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reserve(
            @Parameter(description = "ID do agregado de estoque (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody QtyRequest body
    ) {

        updateStock.reserve(id, body.qty());
        return ResponseEntity.accepted().build();

    }

    @Operation(
            summary = "Liberar reserva",
            description = """
                    **HTTP**: `POST /api/v1/inventory/stocks/{id}/release`
                    **Body**: `{ "qty": <long> }`
                    **Efeito**: decrementa `reservedQty`
                    **Retorno**: 202 Accepted
                    """
    )
    @PostMapping(value = "/{id}/release", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> release(
            @Parameter(description = "ID do agregado de estoque (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody QtyRequest body
    ) {

        updateStock.release(id, body.qty());
        return ResponseEntity.accepted().build();

    }

    @Operation(
            summary = "Ajustar quantidade disponível (set absoluto)",
            description = """
                    **HTTP**: `PUT /api/v1/inventory/stocks/{id}/adjust`
                    **Body**: `{ "newAvailableQty": <long> }`
                    **Efeito**: seta `availableQty` para o valor informado
                    **Retorno**: 202 Accepted
                    """
    )
    @PutMapping(value = "/{id}/adjust", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> adjust(
            @Parameter(description = "ID do agregado de estoque (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AdjustRequest body
    ) {

        adjustStock.adjust(id, body.newAvailableQty());
        return ResponseEntity.accepted().build();

    }

    @Operation(
            summary = "Listar estoques (paginado simples)",
            description = """
                    **HTTP**: `GET /api/v1/inventory/stocks`
                    **Query**: `page` (default=0), `size` (default=20)
                    **Retorno**: 200 OK com `List<StockDto>`
                    """
    )
    @GetMapping
    public ResponseEntity<List<StockDto>> list(
            @Parameter(description = "Página (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size
    ) {

        var result = getStock.list(page, size).stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(result);

    }

    @Operation(
            summary = "Buscar estoque por ID",
            description = """
                    **HTTP**: `GET /api/v1/inventory/stocks/{id}`
                    **Retorno**: 200 OK se encontrado, 404 se não encontrado
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<StockDto> byId(
            @Parameter(description = "ID do agregado de estoque (UUID)", required = true)
            @PathVariable UUID id
    ) {

        return getStock.byId(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    public record CreateStockRequest(@NotBlank String sku, @Min(0) long initialQty) {}
    public record QtyRequest(@NotNull @Min(1) Long qty) {}
    public record AdjustRequest(@NotNull @Min(0) Long newAvailableQty) {}


    private static String normalizeSku(String sku) {
        return sku == null ? null : sku.trim();
    }

}