package br.com.orbity.ms_checkout_service_v1.application.policy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;


@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionalPolicy {

    private final TransactionTemplate tx;


    public <T> T inTx(SupplierEx<T> supplier) {

        return tx.execute(status -> {
            try { return supplier.get(); }
            catch (RuntimeException e){ throw e; }
            catch (Exception e){ throw new IllegalStateException(e); }

        });

    }

    public void inTx(RunnableEx r) {
        tx.executeWithoutResult(s -> {

            try { r.run(); }

            catch (RuntimeException e){ throw e; }

            catch (Exception e){ throw new IllegalStateException(e); }

        });

    }

    @FunctionalInterface public interface RunnableEx { void run() throws Exception; }

    @FunctionalInterface public interface SupplierEx<T> { T get() throws Exception; }

}