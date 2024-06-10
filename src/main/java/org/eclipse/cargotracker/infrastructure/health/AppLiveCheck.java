package org.eclipse.cargotracker.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class AppLiveCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        //DO ANY VALIDATION and then RETURN UP OR DOWN
        return HealthCheckResponse.up("myCheck");
    }
}
