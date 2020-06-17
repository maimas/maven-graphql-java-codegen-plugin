package com.maimas.graphql.generator;

import com.maimas.graphql.schema.processor.TemplateProcessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates complete GraphQL API schema with all the supported queries and mutations.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.NONE)
public class GQLCodeGeneratorMojo extends AbstractMojo {

    /**
     * GraphQL server(s) connection details and output location.
     */
    @Parameter(property = "servers")
    List<UserConfig> servers = new ArrayList<>();


    public void execute() {
        getLog().info("Starting to generate GraphQL API(s) for '" + servers.size() + "' servers...");
        servers.forEach(server -> {
            try {
                getLog().info("Server: " + server.toString());
                new TemplateProcessor(server).generate();
                getLog().info("GraphQL API class generated at " + server.getDir() + "\n");

            } catch (Exception e) {
                getLog().error(e);
                throw e;
            }
        });
        getLog().info("GraphQL API(s) generation completed.");
    }

}