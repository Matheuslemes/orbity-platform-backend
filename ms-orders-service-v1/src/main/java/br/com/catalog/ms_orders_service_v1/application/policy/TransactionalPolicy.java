package br.com.catalog.ms_orders_service_v1.application.policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public class TransactionalPolicy {

    private final TransactionTemplate tx;


    public void runInTx(Runnable action) {

        tx.executeWithoutResult(status -> action.run());

    }

    public <T> T callInTx(Supplier<T> action) {

        return tx.execute(status -> action.get());

    }
}
