package br.com.orbity.customer.adapters.in.rest;

import br.com.orbity.customer.domain.port.in.*;
import br.com.orbity.customer.dto.AddressDto;
import br.com.orbity.customer.dto.ConsentDto;
import br.com.orbity.customer.dto.CustomerDto;
import br.com.orbity.customer.mapping.CustomerDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Customers", description = "Endpoints de clientes (perfil, endereços, consentimentos)")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final UpsertCustomerCommand upsertCustomer;
    private final AddAddressCommand addAddress;
    private final UpdateAddressCommand updateAddress;
    private final RemoveAddressCommand removeAddress;
    private final UpdateConsentCommand updateConsent;
    private final GetCustomerQuery getCustomer;
    private final CustomerDtoMapper mapper;


    @Operation(
            summary = "Recuperar perfil do usuário autenticado (/me)",
            description = """
                    **HTTP**: `GET /api/v1/customers/me`
                    **Auth**: JWT (claims `sub`, `email`, `given_name`, `family_name`, `phone_number`)
                    **Retorno**: 200 OK com `CustomerDto` (upsert automático se não existir)
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDto.class))),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
            }
    )
    @GetMapping("/me")
    public ResponseEntity<CustomerDto> me(Authentication auth) {

        Jwt jwt = (auth != null && auth.getPrincipal() instanceof Jwt j) ? j : null;
        String sub = claim(jwt, "sub");
        String email = claim(jwt, "email");
        String first = claim(jwt, "given_name");
        String last = claim(jwt, "family_name");
        String phone = claim(jwt, "phone_number");

        log.info("[CustomerController] - [me] IN -> sub={} email={}", sub, email);

        var saved = upsertCustomer.upsert(sub, email, first, last, phone);
        var dto = mapper.toDto(saved);
        return ResponseEntity.ok(dto);

    }

    @Operation(
            summary = "Criar/atualizar cliente por sub/email (upsert)",
            description = """
                    **HTTP**: `POST /api/v1/customers`
                    **Body**: JSON com `sub`, `email`, `firstName`, `lastName`, `phone`
                    **Retorno**: 201 Created com Location `/api/v1/customers/{id}`
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "Criado/Atualizado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CustomerDto.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content)
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> upsert(
            @Valid @RequestBody UpsertRequest body) {

        log.info("[CustomerController] - [upsert] IN -> sub={} email={}", body.sub(), body.email());

        var saved = upsertCustomer.upsert(
                trim(body.sub()),
                normalizeEmail(body.email()),
                trim(body.firstName()),
                trim(body.lastName()),
                trim(body.phone())
        );

        var out = mapper.toDto(saved);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(out.id())
                .toUri();

        return ResponseEntity.created(location).body(out);

    }

    @Operation(
            summary = "Adicionar endereço ao cliente",
            description = """
                    **HTTP**: `POST /api/v1/customers/{id}/addresses`
                    **Body**: AddressDto
                    **Retorno**: 200 OK com CustomerDto atualizado
                    """
    )
    @PostMapping(value = "/{id}/addresses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> addAddress(
            @Parameter(description = "ID do cliente (UUID)", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody AddressDto dto
    ) {

        log.info("[CustomerController] - [addAddress] IN -> customerId={}", id);

        addAddress.add(
                id,
                trim(dto.label()),
                trim(dto.street()),
                trim(dto.number()),
                trim(dto.complement()),
                trim(dto.district()),
                trim(dto.city()),
                trim(dto.state()),
                trim(dto.country()),
                trim(dto.zip()),
                dto.main()
        );

        var customer = getCustomer.byId(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        return ResponseEntity.ok(mapper.toDto(customer));

    }

    @Operation(
            summary = "Atualizar endereço de um cliente",
            description = """
                    **HTTP**: `PUT /api/v1/customers/{id}/addresses/{addressId}`
                    **Body**: AddressDto
                    **Retorno**: 200 OK com CustomerDto atualizado
                    """
    )
    @PutMapping(value = "/{id}/addresses/{addressId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> updateAddress(
            @PathVariable UUID id,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressDto dto
    ) {

        log.info("[CustomerController] - [updateAddress] IN -> customerId={} addressId={}", id, addressId);

        updateAddress.update(
                id, addressId,
                trim(dto.label()),
                trim(dto.street()),
                trim(dto.number()),
                trim(dto.complement()),
                trim(dto.district()),
                trim(dto.city()),
                trim(dto.state()),
                trim(dto.country()),
                trim(dto.zip()),
                dto.main()
        );

        var customer = getCustomer.byId(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        return ResponseEntity.ok(mapper.toDto(customer));

    }

    @Operation(
            summary = "Remover endereço de um cliente",
            description = """
                    **HTTP**: `DELETE /api/v1/customers/{id}/addresses/{addressId}`
                    **Retorno**: 204 No Content
                    """
    )
    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Void> removeAddress(
            @Parameter(description = "ID do cliente (UUID)", required = true) @PathVariable UUID id,
            @Parameter(description = "ID do endereço (UUID)", required = true) @PathVariable UUID addressId
    ) {

        log.info("[CustomerController] - [removeAddress] IN -> customerId={} addressId={}", id, addressId);
        removeAddress.remove(id, addressId);

        return ResponseEntity.noContent().build();

    }

    @Operation(
            summary = "Atualizar consentimentos do cliente",
            description = """
                    **HTTP**: `PUT /api/v1/customers/{id}/consent`
                    **Body**: ConsentDto
                    **Retorno**: 200 OK com CustomerDto atualizado
                    """
    )
    @PutMapping(value = "/{id}/consent", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> updateConsent(
            @PathVariable UUID id,
            @Valid @RequestBody ConsentDto dto
    ) {

        log.info("[CustomerController] - [updateConsent] IN -> customerId={}", id);

        updateConsent.update(
                id,
                dto.marketingOption(),
                dto.termsAccepted(),
                dto.dataProcessing()
        );

        var customer = getCustomer.byId(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        return ResponseEntity.ok(mapper.toDto(customer));

    }

    @Operation(
            summary = "Buscar cliente por e-mail",
            description = """
                **HTTP**: `GET /api/v1/customers/by-email?email={email}`
                **Retorno**: 200 OK se encontrado, 404 se não encontrado
                """
    )
    @GetMapping("/by-email")
    public ResponseEntity<CustomerDto> byEmail(
            @Parameter(description = "E-mail do cliente", required = true)
            @RequestParam @NotBlank @Email String email
    ) {

        log.info("[CustomerController] - [byEmail] IN -> email={}", email);

        return getCustomer.byEmail(email.trim().toLowerCase())
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Operation(
            summary = "Resolver cliente por sub OU e-mail (fallback do /me)",
            description = """
                **HTTP**: `GET /api/v1/customers/resolve?value={subOuEmail}`
                **Regra**: tenta `sub` primeiro; se não achar, tenta por `email`
                **Retorno**: 200 OK se encontrado, 404 se não encontrado
                """
    )
    @GetMapping("/resolve")
    public ResponseEntity<CustomerDto> resolveBySubOrEmail(
            @Parameter(description = "sub (subject do IdP) OU e-mail", required = true)
            @RequestParam("value") @NotBlank String subOrEmail
    ) {

        log.info("[CustomerController] - [resolve] IN -> value={}", subOrEmail);

        return getCustomer.me(subOrEmail.trim())
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @Operation(
            summary = "Listar clientes (paginado simples)",
            description = """
                    **HTTP**: `GET /api/v1/customers`
                    **Query**: `page` (default=0), `size` (default=20)
                    **Retorno**: 200 OK com `List<CustomerDto>`
                    """
    )
    @GetMapping
    public ResponseEntity<List<CustomerDto>> list(
            @Parameter(description = "Página (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size
    ) {

        log.info("[CustomerController] - [list] IN -> page={} size={}", page, size);

        var customers = getCustomer.list(page, size);
        var result = customers.stream().map(mapper::toDto).toList();

        return ResponseEntity.ok(result);

    }

    @Operation(
            summary = "Buscar cliente por ID",
            description = """
                    **HTTP**: `GET /api/v1/customers/{id}`
                    **Retorno**: 200 OK se encontrado, 404 se não encontrado
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> byId(
            @Parameter(description = "ID do cliente (UUID)", required = true) @PathVariable UUID id
    ) {

        log.info("[CustomerController] - [byId] IN -> id={}", id);
        return getCustomer.byId(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    // dto request
    public record UpsertRequest(

            @NotBlank String sub,
            @NotBlank @Email String email,
            @NotBlank @Size(max = 80) String firstName,
            @NotBlank @Size(max = 80) String lastName,
            @Size(max = 40) String phone

    ) { }

    // helpers
    private static String claim(Jwt jwt, String name) {

        if (jwt == null) return null;
        Object v = jwt.getClaims().get(name);
        return v == null ? null : v.toString();

    }

    private static String normalizeEmail(String email) {

        if (email == null) return null;
        String e = email.trim().toLowerCase();
        return e.isEmpty() ? null : e;

    }

    private static String trim(String s) {

        return s == null ? null : s.trim();

    }

}