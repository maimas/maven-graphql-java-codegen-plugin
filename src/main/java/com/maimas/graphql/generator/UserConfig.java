package com.maimas.graphql.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.util.HashMap;

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

    @SneakyThrows
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }


    /**
     * HttpHeaders used to make the request ot remote service in order to get the schema JSON.
     */
    public HashMap<String, String> getHttpHeaders() {
        HashMap<String, String> httpHeaders = new HashMap<>();
        if (headers != null && headers.length > 0) {
            for (String header : headers) {
                String key = header.split(":")[0].trim();
                String value = header.split(":")[1].trim();
                httpHeaders.put(key, value);
            }
        }

        return httpHeaders;
    }


    public enum Selector {
        Java("Java", ".java"),
        Typescript("Typescrypt", ".ts");

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
    }
}
