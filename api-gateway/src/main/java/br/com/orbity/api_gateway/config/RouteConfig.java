package br.com.orbity.api_gateway.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RouteConfig {

    private static final String X_FORWARDED_FROM = "X-Forwarded-From";
    private static final String FORWARDED_FROM_GATEWAY = "gateway";

    @Value("${routes.ms-catalog:http://localhost:8080}")   String catalogUri;
    @Value("${routes.ms-media:http://localhost:8082}")     String mediaUri;
    @Value("${routes.ms-inventory:http://localhost:8083}") String inventoryUri;
    @Value("${routes.ms-search:http://localhost:8084}")    String searchUri;
    @Value("${routes.ms-cart:http://localhost:8086}")      String cartUri;
    @Value("${routes.ms-checkout:http://localhost:8087}")  String checkoutUri;
    @Value("${routes.ms-orders:http://localhost:8088}")    String ordersUri;
    @Value("${routes.ms-pricing:http://localhost:8089}")   String pricingUri;
    @Value("${routes.ms-customer:http://localhost:8090}")  String customerUri;

    @Bean
    RouteLocator routes(RouteLocatorBuilder rlb,
                        ObjectProvider<RateLimiter<?>> rateLimiterProvider,
                        ObjectProvider<KeyResolver> keyResolverProvider) {

        var routes = rlb.routes();

        routes.route("catalog", p -> p.path("/api/catalog/**")
                .filters(f -> f
                        .rewritePath("/api/catalog/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                        .retry(c -> c.setRetries(2).setMethods(HttpMethod.GET))
                )
                .uri(catalogUri)
        );

        routes.route("media", p -> p.path("/api/media/**")
                .filters(f -> f
                        .rewritePath("/api/media/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                        .setResponseHeader("Cache-Control", "public,max-age=3600")
                )
                .uri(mediaUri)
        );

        routes.route("search", p -> p.path("/api/search/**")
                .filters(f -> f
                        .rewritePath("/api/search/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                        .retry(c -> c.setRetries(2).setMethods(HttpMethod.GET))
                )
                .uri(searchUri)
        );

        routes.route("cart", p -> p.path("/api/cart/**")
                .filters(f -> {
                    var spec = f
                            .rewritePath("/api/cart/(?<p>.*)", "/${p}")
                            .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY);

                    var rlBean = rateLimiterProvider.getIfAvailable();
                    var krBean = keyResolverProvider.getIfAvailable();
                    if (rlBean != null && krBean != null) {
                        spec.requestRateLimiter(rl -> rl
                                .setRateLimiter(rlBean)
                                .setKeyResolver(krBean)
                        );
                    }
                    return spec;
                })
                .uri(cartUri)
        );

        routes.route("checkout", p -> p.path("/api/checkout/**")
                .filters(f -> f
                        .rewritePath("/api/checkout/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                )
                .uri(checkoutUri)
        );

        routes.route("orders", p -> p.path("/api/orders/**")
                .filters(f -> f
                        .rewritePath("/api/orders/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                )
                .uri(ordersUri)
        );

        routes.route("customer", p -> p.path("/api/customer/**")
                .filters(f -> f
                        .rewritePath("/api/customer/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                )
                .uri(customerUri)
        );

        routes.route("pricing", p -> p.path("/api/pricing/**")
                .filters(f -> f
                        .rewritePath("/api/pricing/(?<p>.*)", "/${p}")
                        .addRequestHeader(X_FORWARDED_FROM, FORWARDED_FROM_GATEWAY)
                        .retry(c -> c.setRetries(2).setMethods(HttpMethod.GET))
                )
                .uri(pricingUri)
        );

        routes.route("bo-catalog", p -> p.path("/backoffice/catalog/**")
                .filters(f -> f.rewritePath("/backoffice/catalog/(?<p>.*)", "/${p}"))
                .uri(catalogUri)
        );

        routes.route("bo-media", p -> p.path("/backoffice/media/**")
                .filters(f -> f.rewritePath("/backoffice/media/(?<p>.*)", "/${p}"))
                .uri(mediaUri)
        );

        routes.route("bo-inventory", p -> p.path("/backoffice/inventory/**")
                .filters(f -> f.rewritePath("/backoffice/inventory/(?<p>.*)", "/${p}"))
                .uri(inventoryUri)
        );

        routes.route("bo-orders", p -> p.path("/backoffice/orders/**")
                .filters(f -> f.rewritePath("/backoffice/orders/(?<p>.*)", "/${p}"))
                .uri(ordersUri)
        );

        return routes.build();
    }
}
