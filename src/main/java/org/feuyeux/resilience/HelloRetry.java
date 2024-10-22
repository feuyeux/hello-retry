package org.feuyeux.resilience;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class HelloRetry {
    public static void retryWithResilience4j(Callable<Boolean> callable) {
        IntervalFunction intervalFunction = IntervalFunction.ofExponentialBackoff(1000, 2);
        RetryConfig config =
                RetryConfig.custom()
                        .retryOnResult(Predicates.alwaysTrue())
                        .maxAttempts(3)
                        .intervalFunction(intervalFunction)
                        .build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("flightSearchService", config);
        Supplier<Boolean> supplier =
                Retry.decorateSupplier(
                        retry,
                        () -> {
                            try {
                                return callable.call();
                            } catch (Exception e) {
                                log.error("Exception:{}", e.getMessage());
                                return false;
                            }
                        });
        Boolean result = supplier.get();
        log.info("Resilience4j result:{}", result);
    }

    public static void retryWithGuavaRetryer(Callable<Boolean> callable) {
        WaitStrategy waitStrategy = WaitStrategies.exponentialWait(2, 1000, TimeUnit.MINUTES);
        StopStrategy stopStrategy = StopStrategies.stopAfterAttempt(3);
        Retryer<Boolean> retryer =
                RetryerBuilder.<Boolean>newBuilder()
                        .retryIfResult(Predicates.alwaysTrue())
                        .retryIfRuntimeException()
                        .withWaitStrategy(waitStrategy)
                        .withStopStrategy(stopStrategy)
                        .build();
        try {
            Boolean result = retryer.call(callable);
            log.info("GuavaRetryer result:{}", result);
        } catch (RetryException e) {
            log.error("RetryException");
        } catch (ExecutionException e) {
            log.error("ExecutionException");
        }
    }
}
