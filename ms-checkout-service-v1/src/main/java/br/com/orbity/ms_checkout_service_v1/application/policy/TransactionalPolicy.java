package br.com.orbity.ms_checkout_service_v1.application.policy;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionalPolicy {
    private final TransactionTemplate tx;

    public TransactionalPolicy(PlatformTransactionManager txm) {

        this.tx = new TransactionTemplate(txm);

    }

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