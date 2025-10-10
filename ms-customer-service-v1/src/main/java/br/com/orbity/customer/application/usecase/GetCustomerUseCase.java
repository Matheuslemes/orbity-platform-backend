package br.com.orbity.customer.application.usecase;

import br.com.orbity.customer.domain.model.Customer;
import br.com.orbity.customer.domain.port.in.GetCustomerQuery;
import br.com.orbity.customer.domain.port.out.CustomerRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCustomerUseCase implements GetCustomerQuery {

    private final CustomerRepositoryPortOut repository;

    @Override
    public Optional<Customer> byId(UUID id) {

        log.info("[GetCustomerUseCase] - [byId] IN -> id={}", id);
        requireNonNull(id, "id");

        var out = repository.findById(id);
        if (out.isPresent()) {

            log.info("[GetCustomerUseCase] - [byId] OUT -> id={} found", id);

        } else {

            log.info("[GetCustomerUseCase] - [byId] OUT -> id={} not-found", id);

        }

        return out;

    }

    @Override
    public Optional<Customer> byEmail(String email) {

        log.info("[GetCustomerUseCase] - [byEmail] IN -> email={}", email);
        var normalized = normalizeEmail(email);
        requireNonBlank(normalized, "email");

        var out = repository.findByEmail(normalized);

        if (out.isPresent()) {

            log.info("[GetCustomerUseCase] - [byEmail] OUT -> email={} found", normalized);

        } else {

            log.info("[GetCustomerUseCase] - [byEmail] OUT -> email={} not-found", normalized);

        }

        return out;

    }

    @Override
    public Optional<Customer> me(String subOrEmail) {

        log.info("[GetCustomerUseCase] - [me] IN -> subOrEmail={}", subOrEmail);
        var in = trimToNull(subOrEmail);
        requireNonBlank(in, "subOrEmail");

        Optional<Customer> out;
        if (looksLikeEmail(in)) {

            var email = normalizeEmail(in);
            out = repository.findByEmail(email);

            if (out.isEmpty()) {

                out = repository.findBySub(in);

            }
        } else {

            out = repository.findBySub(in);
            if (out.isEmpty()) {

                out = repository.findByEmail(normalizeEmail(in));

            }

        }

        log.info("[GetCustomerUseCase] - [me] OUT -> found={}", out.isPresent());
        return out;
    }

    @Override
    public List<Customer> list(int page, int size) {
        // saneamento + limites
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100); // trava em 100 p/ não estourar consulta
        log.info("[GetCustomerUseCase] - [list] IN -> page={} size={}", safePage, safeSize);

        List<Customer> out = repository.list(safePage, safeSize);
        log.info("[GetCustomerUseCase] - [list] OUT -> rows={}", out.size());
        return out;
    }

    // helpers
    private static void requireNonNull(Object v, String field) {

        if (v == null) throw new IllegalArgumentException(field + " must not be null");

    }

    private static void requireNonBlank(String v, String field) {

        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " must not be blank");

    }

    private static String normalizeEmail(String email) {

        var t = trimToNull(email);
        return (t == null) ? null : t.toLowerCase();

    }

    private static boolean looksLikeEmail(String s) {

        var t = trimToNull(s);
        return t != null && t.contains("@");

    }

    private static String trimToNull(String s) {

        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;

    }
}
