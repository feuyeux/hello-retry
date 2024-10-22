# Hello Retry

## Implement exponential backoff retry using resilience4j
```xml
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-retry</artifactId>
  <version>2.2.0</version>
</dependency>
```


```java
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
```

```java
@Test
public void testResilience4j() {
    HelloRetry.retryWithResilience4j(new ACallable("Resilience4j"));
}
```

## Implement exponential backoff retry using guava-retrying

```xml
<dependency>
  <groupId>com.github.rholder</groupId>
  <artifactId>guava-retrying</artifactId>
  <version>2.0.0</version>
</dependency>
```

```java
@Test
public void testGuavaRetryer() {
    HelloRetry.retryWithGuavaRetryer(new ACallable("GuavaRetryer"));
}
```

```java
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
```
