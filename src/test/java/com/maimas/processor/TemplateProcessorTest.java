package com.maimas.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maimas.generated.GeneratedGraphqlAPI;
import com.maimas.graphql.generator.UserConfig;
import com.maimas.graphql.schema.processor.TemplateProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static com.maimas.generated.GeneratedGraphqlAPI.GQLQuery;
import static com.maimas.generated.GeneratedGraphqlAPI.Types.User;


public class TemplateProcessorTest {

    /**
     * Fetch Remote service GrahpQL API JSON schema and generate code types, queries, mutations, etc.
     */
    @Disabled
    @Test
    public void test_schema_generation() {
        UserConfig userConfig = new UserConfig();
        userConfig.setUrl("http://localhost:8080/graphql");
        userConfig.setDir("./src/test/java/com/maimas/generated");
        userConfig.setResultClassPackage("com.maimas.generated");

        TemplateProcessor template = new TemplateProcessor(userConfig);

        String generatedClassContent = template.generate();
        System.out.println(generatedClassContent);

        File generatedCodeFile = new File(userConfig.getDir());
        Assertions.assertTrue(generatedCodeFile.exists());
    }


    /**
     * Example of how to build a graphql mutation query programmatically using generated API.
     * <p>
     * Also, pay attention that <@code>GQLQuery</@code> object provides a return type reference that
     * can be used for HTTP response deserialization.
     * Example:
     * User user = new ObjectMapper().readValue(httpResponse.getBody(), gqlQuery.getReturnType());
     */
    @Test
    public void testGeneratedMutationFunction_with_optional_arg_null() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword(
                input -> input
                        .id("1234124")
                        .rawPassword(null),
                output -> output
                        .id()
                        .firstName()
                        .lastName()
                        .status());

        String expected = "{\n" +
                "  \"operationName\" : null,\n" +
                "  \"query\" : \"mutation($id: String! ){ resetPassword( id: $id ){ id firstName lastName status } }\",\n" +
                "  \"variables\" : {\n" +
                "    \"id\" : \"1234124\"\n" +
                "  }\n" +
                "}";

        Assertions.assertEquals(expected, gqlQuery.toString());
        Assertions.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }


    @Test
    public void testGeneratedMutationFunction_with_optional_arg() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword(input -> input
                        .id("1234124")
                        .rawPassword(Optional.of("123123")),
                output -> output
                        .id()
                        .firstName()
                        .lastName()
                        .status());

        String expected = "{\n" +
                "  \"operationName\" : null,\n" +
                "  \"query\" : \"mutation($id: String!, $rawPassword: String ){ resetPassword( id: $id, rawPassword: $rawPassword ){ id firstName lastName status } }\",\n" +
                "  \"variables\" : {\n" +
                "    \"id\" : \"1234124\",\n" +
                "    \"rawPassword\" : \"123123\"\n" +
                "  }\n" +
                "}";

        Assertions.assertEquals(expected, gqlQuery.toString());
        Assertions.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    @Test
    public void testGeneratedMutationFunction_with_optional_ofNullable() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword(input -> input
                        .id("1234124")
                        .rawPassword(Optional.ofNullable(null)),
                output -> output
                        .id()
                        .firstName()
                        .lastName()
                        .status());

        String expected = "{\n" +
                "  \"operationName\" : null,\n" +
                "  \"query\" : \"mutation($id: String! ){ resetPassword( id: $id ){ id firstName lastName status } }\",\n" +
                "  \"variables\" : {\n" +
                "    \"id\" : \"1234124\"\n" +
                "  }\n" +
                "}";

        Assertions.assertEquals(expected, gqlQuery.toString());
        Assertions.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    @Test
    public void testGeneratedMutationFunction_with_optional() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword(
                input -> input
                        .id("1234124")
                        .rawPassword(Optional.of("123123")),
                output -> output
                        .id()
                        .firstName()
                        .lastName()
                        .status());

        String expected = "{\n" +
                "  \"operationName\" : null,\n" +
                "  \"query\" : \"mutation($id: String!, $rawPassword: String ){ resetPassword( id: $id, rawPassword: $rawPassword ){ id firstName lastName status } }\",\n" +
                "  \"variables\" : {\n" +
                "    \"id\" : \"1234124\",\n" +
                "    \"rawPassword\" : \"123123\"\n" +
                "  }\n" +
                "}";

        Assertions.assertEquals(expected, gqlQuery.toString());
        Assertions.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    @Test
    public void testQueryBuilder() {
        GQLQuery gqlQuery = new GeneratedGraphqlAPI.Query().findById(
                input -> input
                        .id("1234124"),
                output -> output
                        .id()
                        .firstName()
                        .lastName()
                        .status());

        String expected = "{\n" +
                "  \"operationName\" : null,\n" +
                "  \"query\" : \"query($id: String! ){ findById( id: $id ){ id firstName lastName status } }\",\n" +
                "  \"variables\" : {\n" +
                "    \"id\" : \"1234124\"\n" +
                "  }\n" +
                "}";

        Assertions.assertEquals(expected, gqlQuery.toString());
        Assertions.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

}
