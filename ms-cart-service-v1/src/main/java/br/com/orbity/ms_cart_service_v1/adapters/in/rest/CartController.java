package br.com.orbity.ms_cart_service_v1.adapters.in.rest;

import br.com.orbity.ms_cart_service_v1.application.usecase.*;
import br.com.orbity.ms_cart_service_v1.domain.model.Cart;
import br.com.orbity.ms_cart_service_v1.domain.port.in.*;
import br.com.orbity.ms_cart_service_v1.dto.CartDto;
import br.com.orbity.ms_cart_service_v1.mapping.CartDtoMapper;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/carts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cart", description = "Endpoints de gerenciamento de carrinho")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final AddItemUseCase addItem;
    private final UpdateItemUseCase updateItem;
    private final RemoveItemUseCase removeItem;
    private final ClearCartUseCase clearCart;
    private final MergeCartUseCase mergeCart;
    private final GetCartUseCase getCart;
    private final CartDtoMapper mapper;

    @Operation(
            summary = "Buscar carrinho por ID",
            description = """
            **HTTP**: `GET /api/v1/carts/{cartId}`  
            **Path**: `cartId` (identificador do carrinho)  
            **Retorno**: 200 OK com o carrinho existente ou vazio (se não encontrado)  
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CartDto.class)))
            }
    )
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> get(
            @Parameter(description = "ID do carrinho", required = true, example = "guest-123" )
            @PathVariable String cartId
    ) {
        Cart cart = getCart.handle(new GetCartQuery(cartId));
        return ResponseEntity.ok(mapper.toDto(cart));
    }

    @Operation(
            summary = "Adicionar item ao carrinho",
            description = """
            **HTTP**: `POST /api/v1/carts/{cartId}/items`  
            **Path**: `cartId`  
            **Body**: `ItemBody { sku, quantity }`  
            **Retorno**: 200 OK com estado atualizado do carrinho  
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CartDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
            }
    )
    @PostMapping(value = "/{cartId}/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDto> add(
            @Parameter(description = "ID do carrinho", required = true, example = "guest-123")
            @PathVariable String cartId,
            @Valid @RequestBody ItemBody body
    ) {
        var cart = addItem.handle(new AddItemCommand(cartId, body.sku(), body.quantity()));
        return ResponseEntity.ok(mapper.toDto(cart));
    }

    @Operation(
            summary = "Atualizar quantidade de um item",
            description = """
            **HTTP**: `PUT /api/v1/carts/{cartId}/items/{sku}`  
            **Path**: `cartId`, `sku`  
            **Body**: `QtyBody { quantity }` (se `quantity <= 0`, o item é removido)  
            **Retorno**: 204 No Content  
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "Atualizado"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Carrinho/Item não encontrado", content = @Content)
            }
    )
    @PutMapping(value = "/{cartId}/items/{sku}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(
            @Parameter(description = "ID do carrinho", required = true, example = "guest-123")
            @PathVariable String cartId,
            @Parameter(description = "SKU do item", required = true, example = "SKU-ABC-123")
            @PathVariable String sku,
            @Valid @RequestBody QtyBody body
    ) {
        updateItem.handle(new UpdateItemCommand(cartId, sku, body.quantity()));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remover item do carrinho",
            description = """
            **HTTP**: `DELETE /api/v1/carts/{cartId}/items/{sku}`  
            **Path**: `cartId`, `sku`  
            **Retorno**: 204 No Content  
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "Removido"),
                    @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content)
            }
    )
    @DeleteMapping("/{cartId}/items/{sku}")
    public ResponseEntity<Void> remove(
            @Parameter(description = "ID do carrinho", required = true, example = "guest-123")
            @PathVariable String cartId,
            @Parameter(description = "SKU do item", required = true, example = "SKU-ABC-123")
            @PathVariable String sku
    ) {
        removeItem.handle(new RemoveItemCommand(cartId, sku));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Limpar carrinho",
            description = """
            **HTTP**: `DELETE /api/v1/carts/{cartId}`  
            **Path**: `cartId`  
            **Retorno**: 204 No Content  
            """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "Limpado")
            }
    )
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clear(
            @Parameter(description = "ID do carrinho", required = true, example = "guest-123")
            @PathVariable String cartId
    ) {
        clearCart.handle(new ClearCartCommand(cartId));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Mesclar carrinho anônimo no carrinho do usuário",
            description = """
            **HTTP**: `POST /api/v1/carts/merge`  
            **Body**: `MergeBody { anonymousCartId, userCartId }`  
            **Retorno**: 200 OK com o carrinho resultante  
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CartDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
            }
    )
    @PostMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDto> merge(@Valid @RequestBody MergeBody body) {
        var result = mergeCart.handle(new MergeCartCommand(body.anonymousCartId(), body.userCartId()));
        return ResponseEntity.ok(mapper.toDto(result));
    }

    // ====== Request bodies ======

    @Schema(name = "ItemBody", description = "Item a ser adicionado ao carrinho")
    public record ItemBody(
            @NotBlank(message = "sku is required")
            @Schema(description = "SKU do item", example = "SKU-ABC-123")
            String sku,
            @Min(value = 1, message = "quantity must be >= 1")
            @Schema(description = "Quantidade a adicionar", example = "2")
            int quantity
    ) {}

    @Schema(name = "QtyBody", description = "Quantidade do item")
    public record QtyBody(
            @Schema(description = "Quantidade absoluta (<= 0 remove o item)", example = "3")
            int quantity
    ) {}

    @Schema(name = "MergeBody", description = "Payload para mesclar carrinhos")
    public record MergeBody(
            @NotBlank(message = "anonymousCartId is required")
            @Schema(description = "ID do carrinho anônimo (guest)", example = "guest-123")
            String anonymousCartId,
            @NotBlank(message = "userCartId is required")
            @Schema(description = "ID do carrinho do usuário autenticado", example = "user-456")
            String userCartId
    ) {}
}
