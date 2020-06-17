package com.maimas.graphql.schema.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maimas.graphql.generator.UserConfig;
import com.maimas.graphql.schema.model.SchemaModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Generated the GraphQL class with all types, queries and mutations for a particular class.
 */
public class TemplateProcessor {
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
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(outStream);
        Template template = getConfig().getTemplate(userCfg.getLanguage().getName() + "_GQL_schema_template.ftl");

        template.process(getContext(), writer);

        File file = new File(userCfg.getDir(), userCfg.getResultClassName() + userCfg.getLanguage().getExtension());
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileUtils.fileWrite(file, outStream.toString());

        return outStream.toString();
    }

    private HashMap<Object, Object> getContext() throws JsonProcessingException {
        String remoteGQLSchema = SchemaFetcher.download(userCfg.getUrl(), userCfg.getHttpHeaders());
        SchemaModel schemaModel = new ObjectMapper().readValue(remoteGQLSchema, SchemaModel.class);

        HashMap<Object, Object> context = new HashMap<>();
        context.put("className", userCfg.getResultClassName());
        context.put("package", userCfg.getResultClassPackage());
        context.put("schema", schemaModel.getData().getSchema());
        context.put("gqlBuildersContent", getGQLBuildersContent());

        return context;
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

        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

}
