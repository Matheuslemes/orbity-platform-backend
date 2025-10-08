package br.com.orbity.customer.application.policy;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Component
public class TransactionalPolicy {

    @Transactional
    public <T> T runInTx(Callable<T> work) {

        try {

            return work.call();
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void runInTx(Runnable work) {

        work.run();
    }
}
