package br.com.catalog.ms_orders_service_v1.application.policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class TransactionalPolicy {

    private final PlatformTransactionManager txManager;

    public void runInTx(Runnable r) {

        var tt = new TransactionTemplate(txManager);
        tt.executeWithoutResult(status -> r.run());

    }

    public <T> T callInTx(Supplier<T> s) {

        var tt = new TransactionTemplate(txManager);
        return tt.execute(status -> s.get());

    }
}
