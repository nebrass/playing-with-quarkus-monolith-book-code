package com.targa.labs.quarkushop.security.barbarus;

import com.targa.labs.quarkushop.security.TokenService;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Slf4j
@ApplicationScoped
public class EventsListener {
    private final BarbarusResource barbarusEndpoint;
    private final TokenService tokenService;
    private final SseEmitter sseEmitter;

    public EventsListener(BarbarusResource barbarusEndpoint,
                          TokenService tokenService,
                          SseEmitter sseEmitter) {
        this.barbarusEndpoint = barbarusEndpoint;
        this.tokenService = tokenService;
        this.sseEmitter = sseEmitter;
    }

    public void onApplicationEvent(@Observes LoginRequestEvent event) {
        BarbarusLoginDto loginDto =
                event.getContent();

        handleSseResponse(loginDto);

        log.info("Received message as Authentication Request class: {}", loginDto);
    }

    private void handleSseResponse(BarbarusLoginDto loginDto) {
        if (sseEmitter != null) {
            String accessToken = tokenService.getAccessToken(loginDto.getUsername(), loginDto.getPassword());

            sseEmitter.emitToken(loginDto.getViewId(), accessToken);
        }
    }
}

