package com.maimas.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maimas.graphql.generator.UserConfig;
import com.maimas.graphql.schema.processor.TemplateProcessor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * End-to-end integration tests that start an embedded HTTP server which serves a GraphQL introspection JSON,
 * run the TemplateProcessor, and verify the generated code and side effects.
 */
public class GenerationIntegrationTest {

    private HttpServer server;
    private int port;

    private static final Path OUT_DIR = Path.of("target", "test-generated");

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();
        server.start();
    }

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @BeforeAll
    static void cleanOutDir() throws IOException {
        if (Files.exists(OUT_DIR)) {
            // clean any previous content
            try (var s = Files.walk(OUT_DIR)) {
                s.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                });
            }
        }
        Files.createDirectories(OUT_DIR);
    }

    @Test
    void generate_happy_path_creates_file_and_valid_code() {
        // Serve the real introspection JSON from test resources
        server.createContext("/graphql", new ResourceResponder("/RemoteServiceGraphlqSchema.json"));

        UserConfig cfg = new UserConfig();
        cfg.setUrl("http://localhost:" + port + "/graphql");
        cfg.setResultClassName("UserServiceGraphQLProvider");
        cfg.setResultClassPackage("com.maimas.generated");
        cfg.setDir(OUT_DIR.toString());
        cfg.setFailOnValidationError(true);
        cfg.setConnectTimeoutMs(2000);
        cfg.setSocketTimeoutMs(2000);
        cfg.setMaxRetries(0);
        cfg.setRetryBackoffMs(0);

        TemplateProcessor tp = new TemplateProcessor(cfg);
        String generated = tp.generate();

        // Basic assertions over returned content
        Assertions.assertNotNull(generated);
        Assertions.assertTrue(generated.length() > 100, "Generated content too short");
        Assertions.assertTrue(generated.contains("package com.maimas.generated;"));
        Assertions.assertTrue(generated.contains("public class UserServiceGraphQLProvider"));

        // Assert file created on disk
        Path genFile = OUT_DIR.resolve("UserServiceGraphQLProvider.java");
        Assertions.assertTrue(Files.exists(genFile), "Expected generated file to exist: " + genFile);

        // Quick sanity: read few first bytes
        String head = readHead(genFile, 512);
        Assertions.assertTrue(head.contains("package com.maimas.generated;"));
    }

    @Test
    void generate_with_ignored_warning_rule_succeeds_and_logs_warning() {
        server.createContext("/graphql", new ResourceResponder("/RemoteServiceGraphlqSchema.json"));

        UserConfig cfg = new UserConfig();
        cfg.setUrl("http://localhost:" + port + "/graphql");
        cfg.setResultClassName("GenWithIgnore");
        cfg.setResultClassPackage("com.maimas.generated");
        cfg.setDir(OUT_DIR.toString());
        cfg.setIgnoredValidationRules(new String[]{"PARENTHESES_BALANCED"});
        cfg.setFailOnValidationError(true);

        TemplateProcessor tp = new TemplateProcessor(cfg);
        String generated = tp.generate();
        Assertions.assertNotNull(generated);
        Assertions.assertTrue(generated.contains("public class GenWithIgnore"));
        Assertions.assertTrue(Files.exists(OUT_DIR.resolve("GenWithIgnore.java")));
    }

    // Helpers
    static class ResourceResponder implements HttpHandler {
        private final String resourcePath;
        ResourceResponder(String resourcePath) { this.resourcePath = resourcePath; }
        @Override public void handle(HttpExchange exchange) throws IOException {
            byte[] content = readResource(resourcePath);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
        private byte[] readResource(String path) throws IOException {
            try (InputStream is = GenerationIntegrationTest.class.getResourceAsStream(path)) {
                if (is == null) throw new FileNotFoundException("Resource not found: " + path);
                return is.readAllBytes();
            }
        }
    }

    private static String readHead(Path file, int max) {
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            String line; int count = 0;
            while ((line = br.readLine()) != null && sb.length() < max && count++ < 50) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
