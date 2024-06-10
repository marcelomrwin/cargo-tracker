package org.eclipse.cargotracker.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.ClaimsDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.PromptType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

//@OpenIdAuthenticationMechanismDefinition(
//        clientId = "${oidConfig.clientID}",
//        clientSecret = "${oidConfig.clientSecret}",
//        redirectURI = "${baseURL}/index.xhtml",
//        scope = {"openid", "email", "profile", "microprofile-jwt"},
//        prompt = PromptType.LOGIN,
//        providerURI = "${oidConfig.providerUri}",
//        jwksReadTimeout = 10_000,
//        jwksConnectTimeout = 10_000,
//        claimsDefinition = @ClaimsDefinition(callerGroupsClaim = "${oidConfig.callerGroupsClaim}"),
//        extraParameters = "audience=cargotracker",
//        logout = @LogoutDefinition(redirectURI = "${baseURL}/index.xhtml")
//)
//@Named("oidConfig")
//@ApplicationScoped
public class OpenIDSecurityConfiguration {
    private static final String ROLES_CLAIM = "/groups";

    @Inject
    @ConfigProperty(name = "openid.clientId")
    private String clientID;
    @Inject
    @ConfigProperty(name = "openid.clientSecret")
    private String clientSecret;
    @Inject
    @ConfigProperty(name = "openid.providerUri")
    private String providerUri;

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getProviderUri() {
        return providerUri;
    }

    public String getCallerGroupsClaim() {
        return  ROLES_CLAIM;
    }
}
