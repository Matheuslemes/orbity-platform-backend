package br.com.catalog_plataform.ms_catalog_service_v1.adapters.in.rest;

import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.CreateProductCommand;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.GetProductQuery;
import br.com.catalog_plataform.ms_catalog_service_v1.domain.port.in.UpdateProductCommand;
import br.com.catalog_plataform.ms_catalog_service_v1.dto.ProductDto;
import br.com.catalog_plataform.ms_catalog_service_v1.mapping.ProductDtoMapper;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/catalog/products", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Products", description = "Endpoints de catálogo de produtos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final CreateProductCommand createProduct;
    private final UpdateProductCommand updateProduct;
    private final GetProductQuery getProduct;
    private final ProductDtoMapper mapper;

    @Operation(
            summary = "Criar produto",
            description = """
            **HTTP**: `POST /api/v1/catalog/products`
            **Body**: JSON de ProductDto
            **Retorno**: 201 Created com Location `/api/v1/catalog/products/{id}`
            """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Conflito", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        var saved = createProduct.create(mapper.toDomain(dto));
        var out = mapper.toDto(saved);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(out.id())
                .toUri();

        return ResponseEntity.created(location).body(out);

    }

    @Operation(
            summary = "Atualizar produto por ID",
            description = """
            **HTTP**: `PUT /api/v1/catalog/products/{id}`
            **Path**: `id` (identificador do produto - UUID)
            **Body**: JSON de ProductDto
            **Retorno**: 200 OK com o recurso atualizado
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Atualizado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content)
            }
    )
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> update(
            @Parameter(description = "ID do produto (UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @Valid @RequestBody ProductDto dto
    ) {
        var saved = updateProduct.update(id, mapper.toDomain(dto));

        return ResponseEntity.ok(mapper.toDto(saved));

    }

    @Operation(
            summary = "Listar produtos (paginado simples)",
            description = """
            **HTTP**: `GET /api/v1/catalog/products`
            **Query**: `page` (default=0), `size` (default=20)
            **Retorno**: 200 OK com `List<ProductDto>`
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductDto>> list(
            @Parameter(description = "Página (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size
    ) {
        var result = getProduct.list(page, size).stream().map(mapper::toDto).toList();

        return ResponseEntity.ok(result);

    }

    @Operation(
            summary = "Buscar produto por ID",
            description = """
            **HTTP**: `GET /api/v1/catalog/products/{id}`
            **Path**: `id` (UUID do produto)
            **Retorno**: 200 OK se encontrado, 404 se não encontrado
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDto.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> byId(
            @Parameter(description = "ID do produto (UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {

        return getProduct.byId(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

}
