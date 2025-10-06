package br.com.orbity.ms_search_service.adapters.out.persistence;

import br.com.orbity.ms_search_service.domain.model.ProductIndex;
import br.com.orbity.ms_search_service.domain.port.out.SearchRepositoryPortOut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpType;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.UpdateAliasesRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenSearchRepositoryAdapter implements SearchRepositoryPortOut {

    private final OpenSearchClient client;

    @Value("${search.opensearch.index:products}")
    private String index;

    @Override
    public void index(ProductIndex doc) {

        if (doc == null || doc.id() == null) {
            log.warn("[OpenSearchRepo] - [index] IN -> doc nulo/sem id (ignorando)");
            return;
        }

        if (isBlank(index)) {
            throw new IllegalStateException("OpenSearch index não configurado (property search.opensearch.index).");
        }

        try {

            log.info("[OpenSearchRepo] - [index] IN -> id={} sku={}", doc.id(), doc.sku());

            IndexRequest<ProductIndex> req = IndexRequest.of(b -> b
                    .index(index)
                    .id(doc.id().toString())
                    .document(doc)
            );
            client.index(req);

            log.info("[OpenSearchRepo] - [index] OUT -> id={}", doc.id());

        } catch (Exception e) {
            log.error("[OpenSearchRepo] - [index] FAIL id={} err={}", doc.id(), e.getMessage(), e);
            throw new IllegalStateException("Index failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void bulkIndex(List<ProductIndex> docs) {

        if (docs == null || docs.isEmpty()) {
            log.info("[OpenSearchRepo] - [bulkIndex] IN -> lista vazia (ignorado)");
            return;
        }

        try {

            log.info("[OpenSearchRepo] - [bulkIndex] IN -> size={}", docs.size());

            BulkRequest.Builder br = new BulkRequest.Builder();
            for (ProductIndex d : docs) {
                if (d == null || d.id() == null) continue;
                br.operations(op -> op.index(idx -> idx
                        .index(index)
                        .id(d.id().toString())
                        .document(d)
                ));
            }

            BulkResponse resp = client.bulk(br.build());

            if (resp.errors()) {

                long fails = resp.items().stream()
                        .filter(i -> i.error() != null)
                        .count();
                log.warn("[OpenSearchRepo] - [bulkIndex] OUT -> completed with errors: {} failures", fails);

            } else {
                log.info("[OpenSearchRepo] - [bulkIndex] OUT -> OK");
            }
        } catch (Exception e) {
            log.error("[OpenSearchRepo] - [bulkIndex] FAIL err={}", e.getMessage(), e);
            throw new IllegalStateException("Bulk index failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ProductIndex> findById(UUID id) {

        if (id == null) {
            log.warn("[OpenSearchRepo] - [findById] IN -> id nulo");
            return Optional.empty();
        }

        if (isBlank(index)) {
            throw new IllegalStateException("OpenSearch index não configurado (property search.opensearch.index).");
        }

        try {

            log.info("[OpenSearchRepo] - [findById] IN -> id={}", id);

            GetResponse<ProductIndex> res =
                    client.get(g -> g.index(index).id(id.toString()), ProductIndex.class);

            if (res.found() && res.source() != null) {
                log.info("[OpenSearchRepo] - [findById] OUT -> found id={}", id);

                return Optional.of(res.source());
            }

            log.info("[OpenSearchRepo] - [findById] OUT -> not found id={}", id);

            return Optional.empty();

        } catch (Exception e) {
            log.error("[OpenSearchRepo] - [findById] FAIL id={} err={}", id, e.getMessage(), e);
            throw new IllegalStateException("Get failed: " + e.getMessage(), e);
        }
    }


    @Override
    public List<ProductIndex> search(String query, int page, int size) {

        if (isBlank(index)) {

            throw new IllegalStateException("OpenSearch index não configurado (property search.opensearch.index).");
        }

        try {

            int normalizedSize = Math.max(size, 1);
            int normalizedPage = Math.max(page, 0);
            int from = normalizedPage * normalizedSize;

            log.info("[OpenSearchRepo] - [search] IN -> q='{}' page={} size={} from={}",
                    safeLog(query), normalizedPage, normalizedSize, from);

            SearchResponse<ProductIndex> res = client.search(s -> s
                            .index(index)
                            .from(from)
                            .size(normalizedSize)
                            .query(qb -> {

                                if (isBlank(query)) {
                                    return qb.matchAll(ma -> ma);
                                }

                                return qb.multiMatch(mm -> mm
                                        .query(query)
                                        .fields("name^2", "description", "sku"));
                            }),
                    ProductIndex.class);

            List<ProductIndex> out = new ArrayList<>();
            for (Hit<ProductIndex> h : res.hits().hits()) {
                if (h.source() != null) out.add(h.source());
            }

            log.info("[OpenSearchRepo] - [search] OUT -> hits={}", out.size());

            return out;

        } catch (Exception e) {
            log.error("[OpenSearchRepo] - [search] FAIL q='{}' err={}", safeLog(query), e.getMessage(), e);
            throw new IllegalStateException("Search failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void reindexAll() {

        final String alias = index; // trata index como alias logico
        final String newIndex = alias + "-" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                .format(LocalDateTime.now());

        log.info("[OpenSearchRepo] - [reindexAll] IN -> alias={} newIndex={}", alias, newIndex);

        try {
            // 1 - descobrir indices atuais do alias
            var aliasResp = client.indices().getAlias(g -> g.name(alias));
            var currentIndices = new ArrayList<String>(aliasResp.result().keySet());

            if (currentIndices.isEmpty()) {

                log.warn("[OpenSearchRepo] - [reindexAll] alias '{}' sem índices. Reindex será um bootstrap.", alias);
            } else {

                log.info("[OpenSearchRepo] - [reindexAll] alias={} -> sources={}", alias, currentIndices);
            }

            // 2 - cria indice novo (settings + mappings básicos)
            client.indices().create(c -> c
                    .index(newIndex)
                    .settings(s -> s
                            .numberOfShards("1")
                            .numberOfReplicas("1"))
                    .mappings(m -> m
                            .properties("id", p -> p.keyword(k -> k))
                            .properties("sku", p -> p.keyword(k -> k))
                            .properties("name", p -> p.text(t -> t.analyzer("standard")))
                            .properties("description", p -> p.text(t -> t.analyzer("standard")))
                            .properties("price", p -> p.double_(d -> d))
                            .properties("available", p -> p.boolean_(b -> b))
                    )
            );
            log.info("[OpenSearchRepo] - [reindexAll] created index={}", newIndex);

            // 3 - reindex (se houver fonte); espere terminar
            if (!currentIndices.isEmpty()) {

                var reindexResp = client.reindex(r -> r
                        .source(s -> s.index(currentIndices))
                        .dest(d -> d.index(newIndex).opType(OpType.Index))
                        .refresh(true)
                        .waitForCompletion(true)
                );
                log.info("[OpenSearchRepo] - [reindexAll] reindex done -> created={} updated={} failures={}",
                        reindexResp.created(), reindexResp.updated(),
                        (reindexResp.failures() == null ? 0 : reindexResp.failures().size()));

                if (reindexResp.failures() != null && !reindexResp.failures().isEmpty()) {

                    reindexResp.failures().forEach(f ->
                            log.warn("[OpenSearchRepo] - [reindexall] failure index={} cause={}",
                                    f.index(), (f.cause() != null ? f.cause().reason() : "n/a")));

                }
            }

            // 4 - swap do alias de forma atômica
            var actionsBuilder = new UpdateAliasesRequest.Builder();
            for (String oldIdx : currentIndices) {

                actionsBuilder.actions(a -> a.remove(r -> r.index(oldIdx).alias(alias)));
            }

            actionsBuilder.actions(a -> a.add(add -> add.index(newIndex).alias(alias)));
            client.indices().updateAliases(actionsBuilder.build());
            log.info("[OpenSearchRepo] - [reindexAll] alias swapped -> {} => {}", alias, newIndex);

            // 5 - apaga indices antigos
            for (String oldIdx : currentIndices) {

                if (!oldIdx.equals(newIndex)) {

                    try {

                        client.indices().delete(d -> d.index(oldIdx));
                        log.info("[OpenSearchRepo] - [reindexAll] deleted old index={}", oldIdx);
                    } catch (Exception delEx) {
                        log.warn("[OpenSearchRepo] - [reindexAll] could not delete old index={} err={}",
                                oldIdx, delEx.getMessage());
                    }
                }
            }

            // 5 - refresh final do alias
            client.indices().refresh(r -> r.index(alias));
            log.info("[OpenSearchRepo] - [reindexAll] OUT -> OK (alias={} now points to {})", alias, newIndex);

        } catch (OpenSearchException e) {

            log.error("[OpenSearchRepo] - [reindexAll] FAIL status={} err={}", e.status(), e.getMessage(), e);
            throw new IllegalStateException("Reindex failed: " + e.getMessage(), e);
        } catch (Exception e) {

            log.error("[OpenSearchRepo] - [reindexAll] FAIL err={}", e.getMessage(), e);
            throw new IllegalStateException("Reindex failed: " + e.getMessage(), e);
        }

    }

    private static boolean isBlank(String s) {

        return s == null || s.isBlank();
    }

    private static String safeLog(String s) {

        return s == null ? "<null>" : s;
    }
}
