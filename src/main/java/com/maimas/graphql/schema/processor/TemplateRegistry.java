package com.maimas.graphql.schema.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for managing multiple template versions.
 * This allows for easier management of templates and support for different template versions.
 */
public class TemplateRegistry {
    private static final Map<String, String> templatePaths = new HashMap<>();
    private static final Map<String, String> templateVersions = new HashMap<>();
    private static final Map<String, TemplateConfig> templateConfigs = new HashMap<>();

    static {
        // Register default templates
        TemplateConfig javaConfig = new TemplateConfig("Java", "1.0")
                .setProperty("fileExtension", ".java")
                .setProperty("templatePath", "java/Java_GQL_schema_template.ftl");
        registerTemplate(javaConfig);

        // Additional languages can be registered here
    }

    /**
     * Registers a template in the registry.
     *
     * @param config the template configuration
     */
    public static void registerTemplate(TemplateConfig config) {
        String language = config.getLanguage();
        String version = config.getVersion();
        String path = (String) config.getProperty("templatePath");

        String key = getKey(language, version);
        templatePaths.put(key, path);
        templateVersions.put(language, version);
        templateConfigs.put(key, config);
    }

    /**
     * Registers a template in the registry.
     *
     * @param language the programming language
     * @param version the template version
     * @param path the path to the template
     */
    public static void registerTemplate(String language, String version, String path) {
        TemplateConfig config = new TemplateConfig(language, version)
                .setProperty("templatePath", path);
        registerTemplate(config);
    }

    /**
     * Gets the path to the template for the specified language and version.
     *
     * @param language the programming language
     * @param version the template version (optional, uses latest version if not specified)
     * @return the path to the template
     * @throws IllegalArgumentException if the template is not found
     */
    public static String getTemplatePath(String language, String version) {
        String key = getKey(language, version);
        if (!templatePaths.containsKey(key)) {
            throw new IllegalArgumentException("Template not found for language: " + language + ", version: " + version);
        }
        return templatePaths.get(key);
    }

    /**
     * Gets the path to the latest version of the template for the specified language.
     *
     * @param language the programming language
     * @return the path to the template
     * @throws IllegalArgumentException if the template is not found
     */
    public static String getTemplatePath(String language) {
        String version = templateVersions.get(language);
        if (version == null) {
            throw new IllegalArgumentException("Template not found for language: " + language);
        }
        return getTemplatePath(language, version);
    }

    /**
     * Gets the template configuration for the specified language and version.
     *
     * @param language the programming language
     * @param version the template version
     * @return the template configuration
     * @throws IllegalArgumentException if the template is not found
     */
    public static TemplateConfig getTemplateConfig(String language, String version) {
        String key = getKey(language, version);
        if (!templateConfigs.containsKey(key)) {
            throw new IllegalArgumentException("Template configuration not found for language: " + language + ", version: " + version);
        }
        return templateConfigs.get(key);
    }

    /**
     * Gets the template configuration for the latest version of the specified language.
     *
     * @param language the programming language
     * @return the template configuration
     * @throws IllegalArgumentException if the template is not found
     */
    public static TemplateConfig getTemplateConfig(String language) {
        String version = templateVersions.get(language);
        if (version == null) {
            throw new IllegalArgumentException("Template configuration not found for language: " + language);
        }
        return getTemplateConfig(language, version);
    }

    /**
     * Gets the key for the template registry.
     *
     * @param language the programming language
     * @param version the template version
     * @return the key
     */
    private static String getKey(String language, String version) {
        return language + "_" + version;
    }
}
