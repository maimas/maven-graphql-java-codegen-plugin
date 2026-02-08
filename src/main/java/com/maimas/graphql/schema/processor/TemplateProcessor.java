package com.maimas.graphql.schema.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maimas.graphql.generator.UserConfig;
import com.maimas.graphql.schema.model.SchemaModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Generated the GraphQL class with all types, queries and mutations for a particular class.
 */
public class TemplateProcessor {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(TemplateProcessor.class.getName());
    private UserConfig userCfg;

    private TemplateProcessor() {
    }

    public TemplateProcessor(final UserConfig userCfg) {
        this.userCfg = userCfg;
    }


    /**
     * Generates GraphQL API class based on a template.
     *
     * @return - generated class content.
     */
    @SneakyThrows
    public String generate() {
        LOGGER.info("Starting code generation for " + userCfg.getResultClassName() + " in package " + userCfg.getResultClassPackage());
        LOGGER.info("Using language: " + userCfg.getLanguage().getName());

        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8)) {

            String language = userCfg.getLanguage().getName();
            LOGGER.info("Retrieving template configuration for language: " + language);
            TemplateConfig templateConfig = TemplateRegistry.getTemplateConfig(language);
            String templatePath = (String) templateConfig.getProperty("templatePath");
            LOGGER.info("Using template path: " + templatePath);

            LOGGER.info("Loading template...");
            Template template = getConfig().getTemplate(templatePath);
            LOGGER.info("Template loaded successfully");

            // Add template configuration to the context
            LOGGER.info("Preparing template context...");
            HashMap<Object, Object> context = getContext();
            context.put("templateConfig", templateConfig);
            LOGGER.info("Template context prepared with " + context.size() + " entries");

            // Process the template with the context
            LOGGER.info("Processing template...");
            template.process(context, writer);
            writer.flush();
            LOGGER.info("Template processing completed");

            String generatedCode = outStream.toString(StandardCharsets.UTF_8);

            LOGGER.info("Generated code length: " + generatedCode.length() + " characters");
            LOGGER.info("Validating generated code...");

            // Validate the generated code
            if (!CodeValidator.validate(generatedCode, userCfg.getValidationErrorOutputFile(), userCfg.getIgnoredValidationRules(), userCfg.getLanguage().getName())) {
                if (userCfg.isFailOnValidationError()) {
                    String errorMsg = "Generated code validation failed. ";
                    if (userCfg.getValidationErrorOutputFile() != null) {
                        errorMsg += "Detailed errors written to: " + userCfg.getValidationErrorOutputFile();
                    } else {
                        errorMsg += "See error log for details.";
                    }
                    throw new RuntimeException(errorMsg);
                }
                java.util.logging.Logger.getLogger(TemplateProcessor.class.getName())
                        .warning("Generated code failed validation; continuing due to configuration failOnValidationError=false.");
            } else {
                LOGGER.info("Code validation successful");
            }

            // Write the generated content to a file
            String fileExtension = (String) templateConfig.getProperty("fileExtension", userCfg.getLanguage().getExtension());
            java.nio.file.Path targetDir = java.nio.file.Paths.get(userCfg.getDir());
            java.nio.file.Files.createDirectories(targetDir);
            java.nio.file.Path filePath = targetDir.resolve(userCfg.getResultClassName() + fileExtension);
            java.nio.file.Files.write(filePath, generatedCode.getBytes(StandardCharsets.UTF_8));

            return generatedCode;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(TemplateProcessor.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error generating GraphQL API class", e);
            throw e;
        }
    }

    private HashMap<Object, Object> getContext() throws JsonProcessingException {
        LOGGER.info("Fetching GraphQL schema from: " + userCfg.getUrl());
        try {
            String remoteGQLSchema = SchemaFetcher.download(userCfg.getUrl(), userCfg.getHttpHeaders(), userCfg);
            LOGGER.info("GraphQL schema fetched successfully (" + remoteGQLSchema.length() + " bytes)");

            LOGGER.info("Parsing GraphQL schema...");
            SchemaModel schemaModel;
            try {
                schemaModel = new ObjectMapper().readValue(remoteGQLSchema, SchemaModel.class);
                LOGGER.info("GraphQL schema parsed successfully");
            } catch (Exception e) {
                LOGGER.severe("Failed to parse GraphQL schema: " + e.getMessage());
                throw new RuntimeException("Error parsing GraphQL schema. The schema may be malformed or not in the expected format. " +
                    "Please check that the GraphQL endpoint is correct and accessible. Details: " + e.getMessage(), e);
            }

            if (schemaModel == null || schemaModel.getData() == null || schemaModel.getData().getSchema() == null) {
                LOGGER.severe("GraphQL schema is empty or missing required data");
                throw new RuntimeException("Error: GraphQL schema is empty or missing required data. " +
                    "Please check that the GraphQL endpoint returns a valid schema with the expected structure.");
            }

            LOGGER.info("Building template context...");
            HashMap<Object, Object> context = new HashMap<>();
            context.put("className", userCfg.getResultClassName());
            context.put("package", userCfg.getResultClassPackage());
            context.put("schema", schemaModel.getData().getSchema());

            LOGGER.info("Loading GraphQL builders content...");
            String buildersContent = getGQLBuildersContent();
            context.put("gqlBuildersContent", buildersContent);
            LOGGER.info("GraphQL builders content loaded (" + buildersContent.length() + " bytes)");

            return context;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
             throw e;
        } catch (RuntimeException e) {
            // Enhance error message with more context and troubleshooting information
            String errorMsg = "Failed to fetch or process GraphQL schema from " + userCfg.getUrl() + ". ";
            errorMsg += "Please check:\n";
            errorMsg += "1. The GraphQL endpoint URL is correct and accessible\n";
            errorMsg += "2. Authentication headers are valid (if required)\n";
            errorMsg += "3. The GraphQL server is running and responding correctly\n";
            errorMsg += "4. Network settings (timeouts, retries) are appropriate for your environment\n\n";
            errorMsg += "Original error: " + e.getMessage();

            LOGGER.severe(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    private Configuration getConfig() {
        Configuration config = new Configuration(Configuration.VERSION_2_3_23);
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setClassForTemplateLoading(this.getClass(), "/");

        return config;
    }

    @SneakyThrows
    private String getGQLBuildersContent() {
        String resource = userCfg.getLanguage().getName() + "_GraphQL_Builders.txt";
        InputStream stream = TemplateProcessor.class.getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            throw new IllegalStateException("Template resource not found: " + resource);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining("\n"));
        }
    }

}
