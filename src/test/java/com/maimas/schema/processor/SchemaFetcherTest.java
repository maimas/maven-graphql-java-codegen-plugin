package com.maimas.schema.processor;

import com.maimas.graphql.generator.UserConfig;
import com.maimas.graphql.schema.processor.SchemaFetcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SchemaFetcherTest {

    private HttpServer server;
    private int port;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        server.start();
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void download_success_200_returnsBody() throws Exception {
        // Given
        final String body = "{\"data\":{}}";
        server.createContext("/graphql", new FixedResponseHandler(200, body));

        UserConfig cfg = new UserConfig();
        cfg.setConnectTimeoutMs(2000);
        cfg.setSocketTimeoutMs(2000);
        cfg.setMaxRetries(0);
        cfg.setRetryBackoffMs(0);

        // When
        String result = SchemaFetcher.download("http://localhost:" + port + "/graphql", new HashMap<>(), cfg);

        // Then
        Assertions.assertEquals(body, result);
    }

    @Test
    void download_200_with_errors_throws() throws Exception {
        // Given
        final String body = "{\"errors\":[{\"message\":\"Bad query\",\"path\":[\"__schema\"]}]}";
        server.createContext("/graphql", new FixedResponseHandler(200, body));

        UserConfig cfg = new UserConfig();
        cfg.setConnectTimeoutMs(2000);
        cfg.setSocketTimeoutMs(2000);
        cfg.setMaxRetries(0);
        cfg.setRetryBackoffMs(0);

        // When/Then
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                SchemaFetcher.download("http://localhost:" + port + "/graphql", new HashMap<>(), cfg)
        );
        Assertions.assertTrue(ex.getMessage().contains("GraphQL responded with errors"));
    }

    @Test
    void download_retries_on_5xx_then_succeeds() throws Exception {
        // Given: first 500, then 200
        server.createContext("/graphql", new SequenceHandler(
                new FixedResponseHandler(500, "oops"),
                new FixedResponseHandler(200, "{\"data\":{}}")
        ));

        UserConfig cfg = new UserConfig();
        cfg.setConnectTimeoutMs(2000);
        cfg.setSocketTimeoutMs(2000);
        cfg.setMaxRetries(2);
        cfg.setRetryBackoffMs(1);

        // When
        String result = SchemaFetcher.download("http://localhost:" + port + "/graphql", new HashMap<>(), cfg);

        // Then
        Assertions.assertEquals("{\"data\":{}}", result);
    }

    // Helpers
    static class FixedResponseHandler implements HttpHandler {
        private final int status;
        private final String body;
        FixedResponseHandler(int status, String body) {
            this.status = status;
            this.body = body == null ? "" : body;
        }
        @Override public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    static class SequenceHandler implements HttpHandler {
        private final HttpHandler[] handlers;
        private int idx = 0;
        SequenceHandler(HttpHandler... handlers) { this.handlers = handlers; }
        @Override public void handle(HttpExchange exchange) throws IOException {
            int i = Math.min(idx, handlers.length - 1);
            handlers[i].handle(exchange);
            idx++;
        }
    }
}
