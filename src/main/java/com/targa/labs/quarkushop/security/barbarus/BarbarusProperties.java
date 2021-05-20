package com.targa.labs.quarkushop.security.barbarus;

import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.validation.constraints.NotNull;

@Data
public class BarbarusProperties {
    @NotNull
    @ConfigProperty(name = "barbarus.paramters.view-id")
    private String viewId;
    @NotNull
    @ConfigProperty(name = "barbarus.paramters.jwt-token")
    private String jwtToken;
    @ConfigProperty(name = "barbarus.paramters.token-validity-in-millis", defaultValue = "86400L")
    private Long tokenValidityInMillis;
    @NotNull
    @ConfigProperty(name = "barbarus.paramters.jwt-secret-key")
    private String jwtSecretKey;
    @ConfigProperty(name = "barbarus.paramters.authorities-key")
    private String authoritiesKey = "auth";
}
