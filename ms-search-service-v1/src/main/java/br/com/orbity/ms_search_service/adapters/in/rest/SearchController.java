package br.com.orbity.ms_search_service.adapters.in.rest;

import br.com.orbity.ms_search_service.domain.port.in.ReindexPortIn;
import br.com.orbity.ms_search_service.domain.port.out.SearchRepositoryPortOut;
import br.com.orbity.ms_search_service.dto.ProductHitDto;
import br.com.orbity.ms_search_service.dto.SearchResultDto;
import br.com.orbity.ms_search_service.mapping.SearchDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/search/products", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Search", description = "Consulte e indexação de produtos no mecanismo de busca")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {

    private final SearchRepositoryPortOut repository;
    private final ReindexPortIn reindex;
    private final SearchDtoMapper mapper;

    @Operation(
            summary = "Buscar produtos (full-text)",
            description = """
                    **HTTP**: `GET /api/v1/search/products?q=...&page=0&size=20`  
                    Retorna um envelope com consulta, paginação e lista de hits.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SearchResultDto.class))),
                    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<SearchResultDto> search(
            @Parameter(description = "Consulta full-text", required = true)
            @RequestParam("q") @NotBlank String q,
            @Parameter(description = "Página (0..N)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamanho da página (>=1)") @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        log.info("[SearchController] - [search] IN -> q='{}' page={} size={}", q, page, size);

        final var docs = repository.search(q, page, size);
        final var dto  = mapper.toResult(q, page, size, docs);

        log.info("[SearchController] - [search] OUT -> hits={}", dto.hits().size());

        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Buscar documento indexado por ID",
            description = """
                    **HTTP**: `GET /api/v1/search/products/{id}`  
                    Retorna o documento indexado se existir.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductHitDto.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductHitDto> byId(@PathVariable UUID id) {

        log.info("[SearchController] - [byId] IN -> id={}", id);

        return repository.findById(id)
                .map(mapper::toHit)
                .map(dto -> {
                    log.info("[SearchController] - [byId] OUT -> found id={}", id);

                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.info("[SearchController] - [byId] OUT -> 404 id={}", id);

                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(
            summary = "Reindexar todos os produtos",
            description = """
                    **HTTP**: `POST /api/v1/search/products/_reindex`  
                    Dispara reindexação (processo assíncrono/leve).
                    """,
            responses = {
                    @ApiResponse(responseCode = "202", description = "Aceito", content = @Content)
            }
    )
    @PostMapping("/_reindex")
    public ResponseEntity<Void> reindexAll() {

        log.info("[SearchController] - [reindexAll] IN");
        reindex.reindexAll();
        log.info("[SearchController] - [reindexAll] OUT -> accepted");

        return ResponseEntity.accepted().build();
    }

}
