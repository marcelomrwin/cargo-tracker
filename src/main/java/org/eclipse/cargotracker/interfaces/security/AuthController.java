package org.eclipse.cargotracker.interfaces.security;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;

import java.io.Serializable;
import java.util.Optional;

@Named
@SessionScoped
public class AuthController implements Serializable {
    @Inject
    SecurityContext securityContext;

    public boolean isAuthenticated() {
        return Optional.ofNullable(securityContext.getCallerPrincipal()).
                isPresent();
    }

    public String getCurrentIdentityName() {
        return securityContext.getCallerPrincipal().getName();
    }

//    public boolean hasSpeakerRole() {
//        return securityContext.isCallerInRole("speaker");
//    }
//
//    public boolean hasAttendeeRole() {
//        return securityContext.isCallerInRole("attendee");
//    }
//
//    public boolean hasAdminRole() {
//        return securityContext.isCallerInRole("admin");
//    }
}
