package com.maimas.graphql.schema.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for templates.
 * This allows for language-specific template configurations.
 */
public class TemplateConfig {
    private final String language;
    private final String version;
    private final Map<String, Object> properties;

    /**
     * Creates a new template configuration.
     *
     * @param language the programming language
     * @param version the template version
     */
    public TemplateConfig(String language, String version) {
        this.language = language;
        this.version = version;
        this.properties = new HashMap<>();
    }

    /**
     * Gets the programming language.
     *
     * @return the programming language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Gets the template version.
     *
     * @return the template version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets a property in the configuration.
     *
     * @param key the property key
     * @param value the property value
     * @return this configuration
     */
    public TemplateConfig setProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    /**
     * Gets a property from the configuration.
     *
     * @param key the property key
     * @return the property value
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Gets a property from the configuration with a default value.
     *
     * @param key the property key
     * @param defaultValue the default value
     * @return the property value or the default value if the property is not found
     */
    public Object getProperty(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    /**
     * Gets all properties from the configuration.
     *
     * @return the properties
     */
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
}