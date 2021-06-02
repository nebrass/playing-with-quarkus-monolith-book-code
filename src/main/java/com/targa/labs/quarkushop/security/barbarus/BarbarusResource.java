package com.targa.labs.quarkushop.security.barbarus;

import com.targa.labs.quarkushop.security.TokenService;
import com.targa.labs.quarkushop.web.dto.AccessTokenDto;
import com.targa.labs.quarkushop.web.dto.BarbarusLoginDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/barbarus")
@Tag(name = "barbarus", description = "OWASP Barbarus implementation methods")
@RequiredArgsConstructor
public class BarbarusResource {

    @ConfigProperty(name = "barbarus.url")
    String barbarusLoginUrl;

    private final SseEmitter sseEmitter;
    private final TokenService tokenService;

    @GET
    @Path("/sse")
    public BarbarusLoginDto sseEmitter() {
        return new BarbarusLoginDto(null, null, UUID.randomUUID().toString(), barbarusLoginUrl);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public AccessTokenDto login(@Valid BarbarusLoginDto loginDto) {
        AccessTokenDto accessToken = tokenService.getAccessToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        );

        sseEmitter.emitToken(loginDto.getViewId(), accessToken);

        return accessToken;
    }

    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@Valid AccessTokenDto accessTokenDto) {
        boolean result = this.tokenService.invalidateToken(accessTokenDto);

        if (result) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }
}
