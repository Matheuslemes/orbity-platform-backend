package br.com.catalog.ms_orders_service_v1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory cf) {

        RedisCacheConfiguration defaultCfg = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(10)); // ttl padrão

        // caches específicos com TTLs ajustados
        Map<String, RedisCacheConfiguration> caches = new HashMap<>();

        // detalhe de pedido por ID costuma mudar pouco após criado (dependendo do fluxo de status)
        // ttl um pouco maior para aliviar leituras repetidas
        caches.put("order-by-id", defaultCfg.entryTtl(Duration.ofMinutes(20)));

        // lista “Meus Pedidos” costuma ser consultada com frequência,
        // mas precisa refletir mudanças com mais rapidez
        caches.put("orders-by-customer", defaultCfg.entryTtl(Duration.ofMinutes(5)));

        // consulta por status (ex.: últimas atualizações) – ttl curto
        caches.put("orders-by-status", defaultCfg.entryTtl(Duration.ofMinutes(3)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaultCfg)
                .withInitialCacheConfigurations(caches)
                .build();
    }
}