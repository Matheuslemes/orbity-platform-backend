package br.com.orbity.ms_media_service.adapters.in.rest;

import br.com.orbity.ms_media_service.domain.port.in.DeleteMediaCommand;
import br.com.orbity.ms_media_service.domain.port.in.GetMediaQuery;
import br.com.orbity.ms_media_service.domain.port.in.UploadMediaCommand;
import br.com.orbity.ms_media_service.dto.MediaDto;
import br.com.orbity.ms_media_service.mapping.MediaDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/media", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Media", description = "Upload e consulta de assets de mídia")
@SecurityRequirement(name = "bearerAuth")
public class MediaController {

    private final UploadMediaCommand uploadMedia;
    private final GetMediaQuery getMedia;
    private final MediaDtoMapper mapper;
    private final DeleteMediaCommand deleteMedia;

    // upload
    @Operation(
            summary = "Upload de Mídia",
            description = """
                    **HTTP**: `POST /api/v1/media`
                    **Body**: multipart/form-data com `file`
                    **Retorno**: 201 Created com Location `/api/v1/media/{id}`
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MediaDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaDto> upload(
            @Parameter(description = "Arquivo de mídia (multipart/form-data)", required = true)
            @RequestPart("file") @NotNull MultipartFile file
    ) throws Exception {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var saved = uploadMedia.upload(new UploadMediaCommand.Input(
                normalize(file.getOriginalFilename()),
                normalize(file.getContentType()),
                file.getBytes()

        ));

        var dto = mapper.toDto(saved);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity.created(location).body(dto);

    }

    // query: byid
    @Operation(
            summary = "Buscar metadados por Id",
            description = """
                    **HTTP**: `GET /api/v1/media/{id}`
                    **Retorno**: 200 OK se encontrado, 404 se não encontrado
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MediaDto.class))),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content)

            }

    )
    @GetMapping("/{id}")
    public ResponseEntity<MediaDto> byId(
            @Parameter(description = "ID do asset (UUID)", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {

        return getMedia.byId(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    // query: presigned null
    @Operation(
            summary = "Obter URL temporária de acesso",
            description = """
                    **HTTP**: `GET /api/v1/media/{id}/url`
                    **Retorno**: 200 OK com URL, 404 se não encontrado
                    """
    )
    @GetMapping("/{id}/url")
    public ResponseEntity<PresignedUrlResponse> url(
            @Parameter(description = "ID do asset (UUID)", required = true)
            @PathVariable UUID id
    ) {

        return getMedia.presignedUrl(id)
                .map(u -> ResponseEntity.ok(new PresignedUrlResponse(u)))
                .orElse(ResponseEntity.notFound().build());

    }

    @Operation(
            summary = "Deletar mídia",
            description = """
                    **HTTP**: `DELETE /api/v1/media/{id}`
                    **Retorno**:
                    - 204 No Content se deletado com sucesso
                    - 404 Not Found se não existir mídia com esse ID
                    """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deletado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do asset (UUID)", required = true)
            @PathVariable UUID id
    ) {

        boolean deleted = deleteMedia.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    public record PresignedUrlResponse(String url) {
    }

    private static String normalize(String s) {
        return s == null ? null : s.trim();
    }
}
