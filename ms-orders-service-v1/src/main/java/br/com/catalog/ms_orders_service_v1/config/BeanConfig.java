package br.com.catalog.ms_orders_service_v1.config;

import br.com.catalog.ms_orders_service_v1.application.policy.TransactionalPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager ptm) {

        return new TransactionTemplate(ptm);

    }

    @Bean
    public TransactionalPolicy transactionalPolicy(TransactionTemplate txTemplate) {

        return new TransactionalPolicy(txTemplate);

    }
}
