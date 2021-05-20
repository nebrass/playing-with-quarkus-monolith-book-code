package com.targa.labs.quarkushop.security.barbarus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.targa.labs.quarkushop.web.dto.AccessTokenDto;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
@ServerEndpoint(value = "/websocket/{viewId}")
public class SseEmitter {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("viewId") String viewId) {
        sessions.put(viewId, session);
        log.trace("ViewId " + viewId + " joined");
    }

    @OnClose
    public void onClose(Session session, @PathParam("viewId") String viewId) {
        sessions.remove(viewId);
        log.trace("ViewId " + viewId + " closed");
    }

    @OnError
    public void onError(Session session, @PathParam("viewId") String viewId, Throwable throwable) {
        sessions.remove(viewId);
        log.warn("ViewId " + viewId + " closed with error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("viewId") String viewId) {
        log.trace(">> " + viewId + ": " + message);
    }

    public void emitToken(String viewId, AccessTokenDto accessToken) {
        var session = sessions.get(viewId);
        if (session != null) {
            session.getAsyncRemote().sendText(toJson(accessToken), result -> {
                if (result.getException() != null) {
                    log.warn("Unable to send message: " + result.getException());
                }
            });
        } else {
            throw new IllegalStateException("Session for ViewId[" + viewId + "] was not found !");
        }
    }

    private String toJson(AccessTokenDto accessToken) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(accessToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
