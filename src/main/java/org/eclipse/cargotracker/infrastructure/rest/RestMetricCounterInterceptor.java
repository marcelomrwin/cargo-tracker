package org.eclipse.cargotracker.infrastructure.rest;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;

@Interceptor
@MetricCounterInterceptor
@Priority(Interceptor.Priority.APPLICATION)
public class RestMetricCounterInterceptor {
    @Inject
    private Logger logger;

    @Inject
    private MeterRegistry registry;

    @Resource
    ManagedExecutorService executor;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Throwable {
        Timer timer = registry.timer(context.getMethod().getName()+"_timer");
        executor.submit(() -> {
            Counter.builder(context.getMethod().getName()).description(context.getMethod().getAnnotation(Operation.class).description()).register(registry).increment();
        });
        return timer.recordCallable(context::proceed);
    }
}
