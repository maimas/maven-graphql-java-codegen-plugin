package com.maimas.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.maimas.generated.GeneratedGraphqlAPI;
import com.maimas.graphql.schema.processor.TemplateProcessor;
import com.maimas.graphql.generator.UserConfig;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Optional;

import static com.maimas.generated.GeneratedGraphqlAPI.*;
import static com.maimas.generated.GeneratedGraphqlAPI.FunctionType.Query;
import static com.maimas.generated.GeneratedGraphqlAPI.Types.User;


public class TemplateProcessorTest {

    /**
     * Fetch Remote service GrahpQL API JSON schema and generate code types, queries, mutations, etc.
     */
    @Ignore
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
        Assert.assertTrue(generatedCodeFile.exists());
    }


    /**
     * Example of how to build custom graphql query - programmatically by using generated API utils.
     * <p>
     * Also, pay attention that <@code>GQLQuery</@code> object provides a return type reference that
     * can be used for HTTP response deserialization.
     * Example:
     * User user = new ObjectMapper().readValue(httpResponse.getBody(), gqlQuery.getReturnType());
     */
    @Test()
    public void testFunctionBuilder() {
        Function function = new Function(Query, "findById")
                .arguments(
                        Argument.of("id", "1234124"))
                .resultFragment(
                        FragmentField.of("id"),
                        FragmentField.of("firstName"),
                        FragmentField.of("lastName"),
                        FragmentField.of("status"))
                .returnType(new TypeReference<User>() {
                });

        GQLQuery gqlQuery = GQLQuery.from(function);

        String expectedQuery = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"query($id: String ){ findById( id: $id ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expectedQuery, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }


    /**
     * Example of how to build a graphql query programmatically using generated API utils and generated field constants.
     * Note: Field constants are available for all generated types under the 'Type.<YourType>.Fields' class.
     * <p>
     * Also, pay attention that <@code>GQLQuery</@code> object provides a return type reference that
     * can be used for HTTP response deserialization.
     * Example:
     * User user = new ObjectMapper().readValue(httpResponse.getBody(), gqlQuery.getReturnType());
     */
    @Test
    public void testGeneratedQueryFunctionBuilder() {
        GeneratedGraphqlAPI.Query query = new GeneratedGraphqlAPI.Query();

        GQLQuery gqlQuery = query.findById("1234124",
                FragmentField.of(Types.User.Fields.id),
                FragmentField.of(User.Fields.firstName),
                FragmentField.of(User.Fields.lastName),
                FragmentField.of(User.Fields.status));

        String expected = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"query($id: String ){ findById( id: $id ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expected, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    /**
     * Example of how to build a graphql mutation query programmatically using generated API utils and generated field constants.
     * Note: Field constants are available for all generated types under the 'Type.<YourType>.Fields' class.
     * <p>
     * Also, pay attention that <@code>GQLQuery</@code> object provides a return type reference that
     * can be used for HTTP response deserialization.
     * Example:
     * User user = new ObjectMapper().readValue(httpResponse.getBody(), gqlQuery.getReturnType());
     */
    @Test
    public void testGeneratedMutationFunction_with_optional_arg_null() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword("1234124", null,
                FragmentField.of(Types.User.Fields.id),
                FragmentField.of(User.Fields.firstName),
                FragmentField.of(User.Fields.lastName),
                FragmentField.of(User.Fields.status));

        String expected = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"mutation($id: String ){ resetPassword( id: $id ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expected, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<Boolean>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }


    @Test
    public void testGeneratedMutationFunction_with_optional_arg() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword("1234124", Optional.of("123123"),
                FragmentField.of(Types.User.Fields.id),
                FragmentField.of(User.Fields.firstName),
                FragmentField.of(User.Fields.lastName),
                FragmentField.of(User.Fields.status));

        String expected = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"mutation($id: String, $rawPassword: String! ){ resetPassword( id: $id, rawPassword: $rawPassword ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\",\r\n" +
                "    \"rawPassword\" : \"123123\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expected, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<Boolean>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    @Test
    public void testGeneratedMutationFunction_with_optional_ofNullable() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword("1234124", Optional.ofNullable(null),
                FragmentField.of(Types.User.Fields.id),
                FragmentField.of(User.Fields.firstName),
                FragmentField.of(User.Fields.lastName),
                FragmentField.of(User.Fields.status));

        String expected = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"mutation($id: String ){ resetPassword( id: $id ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expected, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<Boolean>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    @Test
    public void testGeneratedMutationFunction_with_optional() {
        GeneratedGraphqlAPI.Mutation mutation = new GeneratedGraphqlAPI.Mutation();

        GQLQuery gqlQuery = mutation.resetPassword("1234124", Optional.of("123123"),
                FragmentField.of(Types.User.Fields.id),
                FragmentField.of(User.Fields.firstName),
                FragmentField.of(User.Fields.lastName),
                FragmentField.of(User.Fields.status));

        String expected = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"mutation($id: String, $rawPassword: String! ){ resetPassword( id: $id, rawPassword: $rawPassword ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\",\r\n" +
                "    \"rawPassword\" : \"123123\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expected, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<Boolean>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

    @Test
    public void testQueryBuilder() {
        GeneratedGraphqlAPI.GQLQuery gqlQuery = new GeneratedGraphqlAPI.Query().findById("1234124",
                FragmentField.of(Types.User.Fields.id),
                FragmentField.of(User.Fields.firstName),
                FragmentField.of(User.Fields.lastName),
                FragmentField.of(User.Fields.status));

        String expected = "{\r\n" +
                "  \"operationName\" : null,\r\n" +
                "  \"query\" : \"query($id: String ){ findById( id: $id ){ id firstName lastName status } }\",\r\n" +
                "  \"variables\" : {\r\n" +
                "    \"id\" : \"1234124\"\r\n" +
                "  }\r\n" +
                "}";

        Assert.assertEquals(expected, gqlQuery.toString());
        Assert.assertEquals(new TypeReference<User>() {
        }.getType(), gqlQuery.getReturnType().getType());
    }

}
