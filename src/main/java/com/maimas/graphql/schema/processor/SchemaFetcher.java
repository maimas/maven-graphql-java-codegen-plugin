package com.maimas.graphql.schema.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maimas.graphql.generator.UserConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

public class SchemaFetcher {

    public static String download(final String gqlUrl, HashMap<String, String> httpHeaders, UserConfig cfg) throws IOException {
        int maxRetries = cfg.getMaxRetries() != null ? cfg.getMaxRetries() : 0;
        int backoff = cfg.getRetryBackoffMs() != null ? cfg.getRetryBackoffMs() : 0;
        int connectTimeout = cfg.getConnectTimeoutMs() != null ? cfg.getConnectTimeoutMs() : 0;
        int socketTimeout = cfg.getSocketTimeoutMs() != null ? cfg.getSocketTimeoutMs() : 0;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        try {
            IOException lastIo = null;
            int attempt = 0;
            while (true) {
                attempt++;
                HttpPost post = new HttpPost(gqlUrl);

                BasicHttpEntity entity = new BasicHttpEntity();
                entity.setContent(getSchemaQuery());

                post.setHeader("Content-Type", "application/json;charset=UTF-8");
                httpHeaders.forEach(post::setHeader);
                post.setEntity(entity);

                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    int status = response.getStatusLine().getStatusCode();
                    HttpEntity respEntity = response.getEntity();
                    String responseBody = respEntity != null ? EntityUtils.toString(respEntity, StandardCharsets.UTF_8) : "";

                    if (status == HttpStatus.SC_OK) {
                        // GraphQL may return 200 with an "errors" array. Detect and surface helpful diagnostics.
                        try {
                            if (responseBody != null && !responseBody.isEmpty()) {
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode root = mapper.readTree(responseBody);
                                if (root != null && root.has("errors") && root.get("errors").isArray() && root.get("errors").size() > 0) {
                                    JsonNode errors = root.get("errors");
                                    String messages = "";
                                    for (int i = 0; i < Math.min(5, errors.size()); i++) {
                                        JsonNode err = errors.get(i);
                                        String msg = err.has("message") ? err.get("message").asText() : err.toString();
                                        String path = err.has("path") ? err.get("path").toString() : "[]";
                                        messages += String.format("#%d message=%s path=%s; ", i + 1, msg, path);
                                    }
                                    String headerKeys = httpHeaders != null ? httpHeaders.keySet().stream().collect(Collectors.joining(", ")) : "";
                                    throw new RuntimeException("ERROR - GraphQL responded with errors. Status: 200, Errors: " + messages + ", Headers(keys): [" + headerKeys + "]");
                                }
                            }
                        } catch (Exception ignore) {
                            // Ignore JSON parsing issues and fallback to a lightweight check below.
                        }
                        // Fallback: simple heuristic to detect GraphQL errors even if JSON parsing failed
                        if (responseBody != null && responseBody.contains("\"errors\"")) {
                            String headerKeys = httpHeaders != null ? httpHeaders.keySet().stream().collect(Collectors.joining(", ")) : "";
                            throw new RuntimeException("ERROR - GraphQL responded with errors. Status: 200, Body snippet: " + truncate(responseBody) + ", Headers(keys): [" + headerKeys + "]");
                        }
                        return responseBody;
                    }

                    // Retry on 5xx
                    if (status >= 500 && status < 600 && attempt <= maxRetries) {
                        sleepBackoff(backoff, attempt);
                        continue;
                    }

                    String headerKeys = httpHeaders != null ? httpHeaders.keySet().stream().collect(Collectors.joining(", ")) : "";
                    throw new RuntimeException("ERROR - Generation failed from provided URL '" + gqlUrl + "'. Status: " + status + ", Body: " + truncate(responseBody) + ", Headers(keys): [" + headerKeys + "]");
                } catch (IOException io) {
                    lastIo = io;
                    if (attempt <= maxRetries) {
                        sleepBackoff(backoff, attempt);
                        continue;
                    }
                    throw io;
                }
            }
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // Ignore close exception
            }
        }
    }

    private static void sleepBackoff(int backoff, int attempt) {
        if (backoff <= 0) return;
        try {
            Thread.sleep((long) backoff * attempt);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static String truncate(String s) {
        if (s == null) return "";
        return s.length() > 512 ? s.substring(0, 512) + "..." : s;
    }

    private static InputStream getSchemaQuery() {
        return SchemaFetcher.class.getClassLoader().getResourceAsStream("GraphQL_IntrospectionQuery.json");
    }
}
