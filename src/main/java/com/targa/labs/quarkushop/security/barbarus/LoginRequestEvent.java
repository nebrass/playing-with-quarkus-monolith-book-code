package com.targa.labs.quarkushop.security.barbarus;

public class LoginRequestEvent {
    private BarbarusLoginDto content;

    public LoginRequestEvent(BarbarusLoginDto content) {
        this.content = content;
    }

    public BarbarusLoginDto getContent() {
        return content;
    }
}
