package com.maimas.processor;

import com.maimas.graphql.generator.UserConfig;
import com.maimas.graphql.schema.processor.TemplateProcessor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateProcessorSnapshotTest {

    private HttpServer server;
    private int port;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        // Serve fixed schema JSON from test resources
        server.createContext("/graphql", new ResourceResponder("/RemoteServiceGraphlqSchema.json"));
        server.start();
    }

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void generated_output_matches_snapshot() throws Exception {
        // Given
        UserConfig cfg = new UserConfig();
        cfg.setLanguage(UserConfig.Selector.Java);
        cfg.setUrl("http://localhost:" + port + "/graphql");
        cfg.setDir("./src/test/java/com/maimas/generated");
        cfg.setResultClassPackage("com.maimas.generated");
        cfg.setResultClassName("GeneratedGraphqlAPI");
        cfg.setFailOnValidationError(true);
        cfg.setConnectTimeoutMs(2000);
        cfg.setSocketTimeoutMs(2000);

        TemplateProcessor tp = new TemplateProcessor(cfg);

        // When
        String generated = tp.generate();

        // Then compare to the existing snapshot file
        Path snapshotPath = Path.of("src", "test", "java", "com", "maimas", "generated", "GeneratedGraphqlAPI.java");
        String expected = Files.readString(snapshotPath, StandardCharsets.UTF_8);
        // Normalize line endings for comparison
        String normGen = generated.replace("\r\n", "\n");
        String normExp = expected.replace("\r\n", "\n");
        Assertions.assertEquals(normExp, normGen);
    }

    static class ResourceResponder implements HttpHandler {
        private final String resourcePath;
        ResourceResponder(String resourcePath) { this.resourcePath = resourcePath; }
        @Override public void handle(HttpExchange exchange) throws IOException {
            byte[] payload = new String(
                    TemplateProcessorSnapshotTest.class.getResourceAsStream(resourcePath)
                            .readAllBytes(), StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        }
    }
}
