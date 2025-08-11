package com.maimas.graphql.generator;

import com.maimas.graphql.schema.processor.TemplateProcessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (servers == null || servers.isEmpty()) {
            throw new MojoFailureException("No servers configured. Please provide at least one <servers> entry in the plugin configuration.");
        }
        getLog().info("Starting to generate GraphQL API(s) for '" + servers.size() + "' servers...");
        for (int i = 0; i < servers.size(); i++) {
            UserConfig server = servers.get(i);
            try {
                getLog().info("Server[" + i + "]: " + server.toString());
                // Validate configuration early
                try {
                    server.validate();
                } catch (IllegalArgumentException ex) {
                    throw new MojoFailureException("Invalid configuration for server index " + i + ": " + ex.getMessage(), ex);
                }
                new TemplateProcessor(server).generate();
                getLog().info("GraphQL API class generated at " + server.getDir() + "\n");
            } catch (Exception e) {
                String msg = "Failed to generate for server index " + i + ": " + e.getMessage();
                getLog().error(msg, e);
                if (e instanceof MojoFailureException) {
                    throw (MojoFailureException) e;
                }
                throw new MojoExecutionException(msg, e);
            }
        }
        getLog().info("GraphQL API(s) generation completed.");
    }

}