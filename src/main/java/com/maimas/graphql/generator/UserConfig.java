package com.maimas.graphql.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

/**
 * User configuration wrapper.
 */
public class UserConfig {
    /**
     * Remote service graphql endpoint.
     */
    private String url;

    /**
     * An array of http headers.
     * Ex:
     * key1:value1
     * key2:value2
     */
    private String[] headers;

    /**
     * Language of the schema to ge generated
     */
    private Selector language = Selector.Java;

    /**
     * Generated class name.
     */
    private String resultClassName = "GeneratedGraphqlAPI";

    /**
     * Generated class package name.
     */
    private String resultClassPackage = "com.maimas.graphql.generated";

    /**
     * Directory where to store the generated class file.
     */
    private String dir = "./generated";

    // Validation behavior: by default, fail build if generated code is invalid
    private boolean failOnValidationError = true;

    /**
     * Optional file path to write validation errors to.
     * If provided, validation errors will be written to this file.
     */
    private String validationErrorOutputFile;

    // --- Network and retry configuration ---
    /** Connect timeout in milliseconds (default 5000). */
    private Integer connectTimeoutMs = 5000;
    /** Socket (read) timeout in milliseconds (default 5000). */
    private Integer socketTimeoutMs = 5000;
    /** Maximum number of retries for transient errors (default 2). */
    private Integer maxRetries = 2;
    /** Backoff base in milliseconds between retries (default 500). */
    private Integer retryBackoffMs = 500;

    public UserConfig() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Selector getLanguage() {
        return language;
    }

    public void setLanguage(Selector language) {
        this.language = language;
    }

    public String getResultClassName() {
        return resultClassName;
    }

    public void setResultClassName(String resultClassName) {
        this.resultClassName = resultClassName;
    }

    public String getResultClassPackage() {
        return resultClassPackage;
    }

    public void setResultClassPackage(String resultClassPackage) {
        this.resultClassPackage = resultClassPackage;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isFailOnValidationError() {
        return failOnValidationError;
    }

    public void setFailOnValidationError(boolean failOnValidationError) {
        this.failOnValidationError = failOnValidationError;
    }

    public String getValidationErrorOutputFile() {
        return validationErrorOutputFile;
    }

    public void setValidationErrorOutputFile(String validationErrorOutputFile) {
        this.validationErrorOutputFile = validationErrorOutputFile;
    }

    public Integer getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(Integer connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public Integer getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public void setSocketTimeoutMs(Integer socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Integer getRetryBackoffMs() {
        return retryBackoffMs;
    }

    public void setRetryBackoffMs(Integer retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

    @SneakyThrows
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public String[] getHeaders() {
        return headers == null ? null : headers.clone();
    }

    public void setHeaders(String[] headers) {
        this.headers = headers == null ? null : headers.clone();
    }

    /**
     * Validate configuration fields and normalize directory path.
     * Throws IllegalArgumentException with a clear message if invalid.
     */
    public void validate() {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration error: 'url' must be provided and non-empty.");
        }
        // Basic URL scheme check (not a full validator to remain minimal)
        String u = url.trim().toLowerCase();
        if (!(u.startsWith("http://") || u.startsWith("https://"))) {
            throw new IllegalArgumentException("Configuration error: 'url' should start with http:// or https://");
        }

        if (resultClassName == null || !isValidJavaIdentifier(resultClassName)) {
            throw new IllegalArgumentException("Configuration error: 'resultClassName' must be a valid Java identifier (e.g., MyApi).");
        }
        if (resultClassPackage == null || !isValidPackageName(resultClassPackage)) {
            throw new IllegalArgumentException("Configuration error: 'resultClassPackage' must be a valid Java package (e.g., com.example.api).");
        }
        if (dir == null || dir.trim().isEmpty()) {
            dir = "./generated";
        }
        dir = normalizeDir(dir);

        // Sanity checks for timeouts/retries
        if (connectTimeoutMs != null && connectTimeoutMs < 0) {
            throw new IllegalArgumentException("Configuration error: 'connectTimeoutMs' must be >= 0");
        }
        if (socketTimeoutMs != null && socketTimeoutMs < 0) {
            throw new IllegalArgumentException("Configuration error: 'socketTimeoutMs' must be >= 0");
        }
        if (maxRetries != null && maxRetries < 0) {
            throw new IllegalArgumentException("Configuration error: 'maxRetries' must be >= 0");
        }
        if (retryBackoffMs != null && retryBackoffMs < 0) {
            throw new IllegalArgumentException("Configuration error: 'retryBackoffMs' must be >= 0");
        }

        // Validate validationErrorOutputFile if provided
        if (validationErrorOutputFile != null && !validationErrorOutputFile.trim().isEmpty()) {
            File errorFile = new File(validationErrorOutputFile);
            File parentDir = errorFile.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new IllegalArgumentException("Configuration error: Cannot create directory for 'validationErrorOutputFile': " + parentDir);
            }
        }
    }

    private static boolean isValidJavaIdentifier(String name) {
        if (name == null || name.isEmpty()) return false;
        if (!Character.isJavaIdentifierStart(name.charAt(0))) return false;
        for (int i = 1; i < name.length(); i++) {
            if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
        }
        return true;
    }

    private static boolean isValidPackageName(String pkg) {
        String[] parts = pkg.split("\\.");
        if (parts.length == 0) return false;
        for (String p : parts) {
            if (p.isEmpty() || !isValidJavaIdentifier(p)) return false;
        }
        return true;
    }

    private static String normalizeDir(String d) {
        String s = d.trim();
        // Replace separators to system default and remove trailing separators
        s = s.replace('/', File.separatorChar).replace('\\', File.separatorChar).trim();
        while (s.endsWith(File.separator) && s.length() > 1) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * HttpHeaders used to make the request ot remote service in order to get the schema JSON.
     */
    public HashMap<String, String> getHttpHeaders() {
        HashMap<String, String> httpHeaders = new HashMap<>();
        if (headers != null && headers.length > 0) {
            for (String header : headers) {
                if (header == null) {
                    continue;
                }
                int idx = header.indexOf(':');
                if (idx < 0) {
                    java.util.logging.Logger.getLogger(UserConfig.class.getName())
                            .warning("Skipping malformed header (missing ':'): " + header);
                    continue;
                }
                String key = header.substring(0, idx).trim();
                String value = header.substring(idx + 1).trim(); // may be empty
                if (key.isEmpty()) {
                    java.util.logging.Logger.getLogger(UserConfig.class.getName())
                            .warning("Skipping header with empty name.");
                    continue;
                }
                httpHeaders.put(key, value);
            }
        }

        return httpHeaders;
    }


    public enum Selector {
        Java("Java", ".java"),
        Typescript("TypeScript", ".ts");

        private final String name;
        private final String extension;

        private Selector(final String name, final String extension) {
            this.name = name;
            this.extension = extension;
        }

        @JsonProperty
        public String getName() {
            return name;
        }

        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
