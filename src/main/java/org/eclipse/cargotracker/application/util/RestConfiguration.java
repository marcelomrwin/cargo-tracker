package org.eclipse.cargotracker.application.util;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * Jakarta REST configuration.
 */
@ApplicationPath("rest")
public class RestConfiguration extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("jersey.config.beanValidation.enableOutputValidationErrorEntity.server", true);
        return properties;
    }
}
