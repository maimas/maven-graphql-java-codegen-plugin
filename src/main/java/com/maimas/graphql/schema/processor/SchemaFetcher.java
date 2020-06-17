package com.maimas.graphql.schema.processor;

import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.util.HashMap;

public class SchemaFetcher {

    @SneakyThrows
    public static String download(final String gqlUrl, HashMap<String, String> httpHeaders) {
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        final HttpPost post = new HttpPost(gqlUrl);

        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(getSchemaQuery());

        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpHeaders.forEach(post::setHeader);
        post.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(post);
        String responseBody = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException(
                    "ERROR - Generation failed from provided URL '" + gqlUrl + "'. Reason: " + responseBody);
        }

        return responseBody;
    }

    private static InputStream getSchemaQuery() {
        return SchemaFetcher.class.getClassLoader().getResourceAsStream("GraphQL_IntrospectionQuery.json");
    }
}
