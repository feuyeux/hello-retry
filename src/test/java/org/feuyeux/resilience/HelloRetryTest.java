package org.feuyeux.resilience;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
@Slf4j
public class HelloRetryTest {

  @Test
  public void testGuavaRetryer() {
    HelloRetry.retryWithGuavaRetryer(new ACallable("GuavaRetryer"));
  }

  @Test
  public void testResilience4j() {
    HelloRetry.retryWithResilience4j(new ACallable("Resilience4j"));
  }
}
