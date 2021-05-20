package com.targa.labs.quarkushop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.targa.labs.quarkushop.web.dto.AccessTokenDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestScoped
public class TokenService {

    private static final String TOKENS_REQUESTS_TIMER = "tokensRequestsTimer";
    private static final String TOKENS_REQUESTS_COUNTER = "tokensRequestsCounter";
    private static final String TOKENS_INVALIDATION_REQUESTS_TIMER = "tokensInvalidationRequestsTimer";
    private static final String TOKENS_INVALIDATION_REQUESTS_COUNTER = "tokensInvalidationRequestsCounter";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String CANNOT_GET_THE_ACCESS_TOKEN = "Cannot get the access_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Inject
    MeterRegistry registry;

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "undefined")
    Provider<String> jwtIssuerUrlProvider;

    @ConfigProperty(name = "keycloak.credentials.client-id", defaultValue = "undefined")
    Provider<String> clientIdProvider;

    @PostConstruct
    public void init() {
        // How many access_tokens have been requested
        registry.counter(TOKENS_REQUESTS_COUNTER, Tags.empty());
        registry.counter(TOKENS_INVALIDATION_REQUESTS_COUNTER, Tags.empty());
        // A measure of how long it takes to get an access_token
        registry.timer(TOKENS_REQUESTS_TIMER, Tags.empty());
        registry.timer(TOKENS_INVALIDATION_REQUESTS_TIMER, Tags.empty());
    }

    public AccessTokenDto getAccessToken(String userName, String password) {

        var timer = registry.timer(TOKENS_REQUESTS_TIMER);
        return timer.record(() -> {
            AccessTokenDto accessToken = null;
            try {
                accessToken = getAccessToken(jwtIssuerUrlProvider.get(), userName, password, clientIdProvider.get(), null);
                registry.counter(TOKENS_REQUESTS_COUNTER).increment();
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(CANNOT_GET_THE_ACCESS_TOKEN);
            }
            return accessToken;
        });
    }

    public AccessTokenDto getAccessToken(String jwtIssuerUrl, String userName, String password, String clientId, String clientSecret) throws IOException, InterruptedException {
        String tokenizer = jwtIssuerUrl + "/protocol/openid-connect/token";

        Map<String, String> data = new HashMap<>();
        data.put(USERNAME, userName);
        data.put(PASSWORD, password);
        data.put(GRANT_TYPE, PASSWORD);
        data.put(CLIENT_ID, clientId);

        if (clientSecret != null) {
            data.put(CLIENT_SECRET, clientSecret);
        }

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenizer))
                .header(CONTENT_TYPE, FORM_URLENCODED)
                .POST(ofFormData(data))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        var accessToken = "";
        var refreshToken = "";

        if (response.statusCode() == 200) {
            var mapper = new ObjectMapper();
            try {
                accessToken = mapper.readTree(response.body()).get(ACCESS_TOKEN).textValue();
                refreshToken = mapper.readTree(response.body()).get(REFRESH_TOKEN).textValue();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("UnauthorizedException");
            throw new UnauthorizedException();
        }

        return new AccessTokenDto(accessToken, refreshToken);
    }

    public Boolean invalidateToken(@Valid AccessTokenDto accessTokenDto) {
        var timer = registry.timer(TOKENS_INVALIDATION_REQUESTS_TIMER);
        return timer.record(() -> {
            Boolean result = false;
            try {
                result = invalidateToken(jwtIssuerUrlProvider.get(), clientIdProvider.get(), null, accessTokenDto);
                registry.counter(TOKENS_INVALIDATION_REQUESTS_COUNTER).increment();
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(CANNOT_GET_THE_ACCESS_TOKEN);
            }
            return result;
        });
    }

    private Boolean invalidateToken(String jwtIssuerUrl, String clientId, String clientSecret, AccessTokenDto accessTokenDto) throws IOException, InterruptedException {
        String tokenizer = jwtIssuerUrl + "/protocol/openid-connect/logout";

        Map<String, String> data = new HashMap<>();
        data.put(REFRESH_TOKEN, accessTokenDto.getRefreshToken());
        data.put(CLIENT_ID, clientId);

        if (clientSecret != null) {
            data.put(CLIENT_SECRET, clientSecret);
        }

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenizer))
                .header(CONTENT_TYPE, FORM_URLENCODED)
                .POST(ofFormData(data))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200 || response.statusCode() == 204;
    }

    public AccessTokenDto getRefreshToken(AccessTokenDto accessTokenDto) {

        var timer = registry.timer(TOKENS_REQUESTS_TIMER);
        return timer.record(() -> {
            AccessTokenDto accessToken = null;
            try {
                accessToken = getRefreshToken(jwtIssuerUrlProvider.get(), accessTokenDto, clientIdProvider.get(), null);
                registry.counter(TOKENS_REQUESTS_COUNTER).increment();
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(CANNOT_GET_THE_ACCESS_TOKEN);
            }
            return accessToken;
        });
    }

    public AccessTokenDto getRefreshToken(String jwtIssuerUrl, AccessTokenDto accessTokenDto, String clientId, String clientSecret) throws IOException, InterruptedException {
        String tokenizer = jwtIssuerUrl + "/protocol/openid-connect/token";

        Map<String, String> data = new HashMap<>();
        data.put(REFRESH_TOKEN, accessTokenDto.getRefreshToken());
        data.put(GRANT_TYPE, REFRESH_TOKEN);
        data.put(CLIENT_ID, clientId);

        if (clientSecret != null) {
            data.put(CLIENT_SECRET, clientSecret);
        }

        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        var request = HttpRequest.newBuilder()
                .POST(ofFormData(data))
                .uri(URI.create(tokenizer))
                .header(CONTENT_TYPE, FORM_URLENCODED)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        var accessToken = "";
        var refreshToken = "";

        if (response.statusCode() == 200) {
            var mapper = new ObjectMapper();
            try {
                accessToken = mapper.readTree(response.body()).get(ACCESS_TOKEN).textValue();
                refreshToken = mapper.readTree(response.body()).get(REFRESH_TOKEN).textValue();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            log.error("UnauthorizedException");
            throw new UnauthorizedException();
        }

        return new AccessTokenDto(accessToken, refreshToken);
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<String, String> data) {
        var builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
