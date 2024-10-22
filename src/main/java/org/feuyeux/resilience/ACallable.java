package org.feuyeux.resilience;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class ACallable implements Callable<Boolean> {
    private final String caller;

    public ACallable(String caller) {
        this.caller = caller;
    }

    @Override
    public Boolean call() throws Exception {
        log.info("{} calling...", caller);
        return false;
    }
}
