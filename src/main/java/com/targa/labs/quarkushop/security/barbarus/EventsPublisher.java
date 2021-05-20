package com.targa.labs.quarkushop.security.barbarus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;

@ApplicationScoped
public class EventsPublisher {
    Event<LoginRequestEvent> loginRequestPublisher;

    public EventsPublisher(Event<LoginRequestEvent> loginRequestPublisher) {
        this.loginRequestPublisher = loginRequestPublisher;
    }

    public void publishEvent(final BarbarusLoginDto loginRequest) {
        LoginRequestEvent loginRequestEvent = new LoginRequestEvent(loginRequest);
        loginRequestPublisher.fire(loginRequestEvent);
    }
}
